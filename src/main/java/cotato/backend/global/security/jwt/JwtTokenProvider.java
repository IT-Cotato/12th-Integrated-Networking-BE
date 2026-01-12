package cotato.backend.global.security.jwt;

import cotato.backend.common.exception.AppException;
import cotato.backend.common.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;

import java.security.Key;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.security.core.Authentication;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * java.security.Key vs io.jsonwebtoken.security.Keys
 *  java.security.Key (인터페이스)
 *  - Java 표준 라이브러리(JDK)에 포함된 최상위 보안 키 인터페이스
 *  - 모든 암호화 키(비밀키, 공개키, 개인키)가 가져야 할 표준 규격을 정의
 *  - 구체적인 구현체 x, 데이터 타입으로 사용
 *
 *  io.jsonwebtoken.security.Keys (유틸리티 클래스)
 *  - java.security.Key인터페이스를 구현한 실제 키 객체를 쉽게 생성해주는 Factory 역할
 *  - new Keys()로 인스턴스를 만들지 않고, Keys.hmacShaKeyFor()처럼 정적(static) 메서드를 호출하여 사용한다.
 *  - 내부적으로 키 길이 등을 검사하여 안전한 키 객체를 만든다.
 *
 *  JWT는 점.으로 구분된 3개의 JSON 덩어리
 *  HEADER.PAYLOAD.SIGNATURE
 *  - HEADER -> 이 토큰이 어떤 방식으로 서명됐는지 정보
 *  - PAYLOAD -> claim들이 전부 들어가는 곳
 *  - SIGNATURE -> 위 두 개가 정말 서버가 만든 건지 검증하는 서명
 *
 *  eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.                                    -> Header (BaseUrl 인코딩된 JSON)
 * eyJzdWIiOiIxMjMiLCJyb2xlIjoiUk9MRV9VU0VSIiwiZXhwIjoxNzMyMTIwMDAwfQ.       -> Payload (Claims)
 * abCDefGhIJkLmNoPqRsTuVwXyZ1234567890aaaa                                  -> Signature
 *
 * Header - 어떤 알고리짐으로 서명 했는지
 * Header 디코딩 결과
 * {
 *   "alg": "HS256",
 *   "typ": "JWT"
 * }
 * - alg: 서명 알고리즘 (밑에 코드에서 쓴 SignatureAlgorithm.HS256)
 * - typ: 타입 (거의 항상 "JWT")
 * 즉, 이 토큰은 HS256으로 서명된 JWT라는 메타 정보
 * 알고리즘이 같고 typ는 거의 항상 같으므로 대부분의 사용자 토큰의 Header는 똑같음
 * -> 사용자를 구분하는 건 Header가 아니라 Payload(Claims) 쪽
 *
 * Payload - claim이 전부 들어감
 * Payload 디코딩 결과
 * {
 *   "sub": "123",               // subject (우리가 memberId 넣은 것)
 *   "role": "ROLE_USER",        // 우리가 커스텀 claim으로 넣은 거
 *   "iat": 1732100000,          // 발급시간(issued at)
 *   "exp": 1732103600           // 만료시간(expiration)
 * }
 *
 * Signature - JWT가 위조되지 않았는지 증명
 * Signature은 이런 식으로 계산된다
 * signature = HMACSHA256(
 *     base64UrlEncode(header) + "." + base64UrlEncode(payload),
 *     secretKey
 * )
 * 즉,
 * 1. Header JSON -> Base64Url
 * 2. PayLoad JSON -> Base64Url
 * 3. 그 둘을 "."로 이어붙인 문자열에
 * 4. 설정한 secretKey로 HMAC-SHA256 서명
 * 5. 그 결과를 다시 Base64Url -> 이게 맨 마지막 SIGNATURE 부분
 *
 * 서버에서 토큰 검증할 때는:
 * - 토큰에서 header와 payload를 꺼내서
 * - 같은 방식으로 다시 signature를 계산해보고
 * - 토큰에 들어있는 signature와 같으면 정상, 다르면 위조된 것
 */
@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

    /**
     * JWT안에 커스텀 데이터를 넣을 수 있는데, 그걸 claim이라고 함
     * .claim(CLAIM_ROLE, role)
     * - key -> CLAIM_ROLE = "role"
     * - value -> 메서드 파라미터 role (예: "ROLE_USER")
     */
    private static final String CLAIM_ROLE = "role";

    private final JwtProperties jwtProperties;
    private Key key;

    /**
     * @PostConstruct: 의존성 주입이 완료된 직후, 해당 빈이 서비스에 투입되기 전에 자동으로 실행된다.
     * 생성자 시점에서 jwtProperties 같은 의존성 객체의 값이 완전하게 주입되지 않았을 수 있다
     * -> 주입이 확실히 끝난 시점에 안전하게 초기화 로직을 수행하기 위해 사용한다.
     */
    @PostConstruct
    public void init(){
        // 1. 설정으로 받아온 secretKey 문자열을 getBytes()로 바이트 배열로 바꿈
        // 2. Keys.hmacShaKeyFor(...)로 HMAC-SHA256에 사용할 Key 객체를 생성
        // 3. 이걸 this.key에 보관
        // 이렇게 해두면, 이후에 createAccessToken, parseClaims등에서 동일한 Key로 토큰 서명, 토큰 검증을 수행할 수 있음
        System.out.println(jwtProperties.getSecretKey());
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes());
    }

    public String createAccessToken(Long memberId, String role){
        long now = System.currentTimeMillis();
        // *1000 해서 ms 바꿔서 만료 시간 expiry 생성
        // 설정에서 3600초(1시간)로 두어서 3600*1000ms(1시간) 뒤 만료
        Date expiry = new Date(now + jwtProperties.getAccessTokenValidityInSeconds()*1000);

        return Jwts.builder()
                .setSubject(String.valueOf(memberId))
                .claim(CLAIM_ROLE, role)
                .setIssuedAt(new Date(now))  //토큰 발급 시간
                .setExpiration(expiry)       // 토큰 만료 시간
                .signWith(key, SignatureAlgorithm.HS256)
                .compact(); //최종적으로 header.payload.signature 형태의 문자열로 직렬화
    }

    public String createRefreshToken(Long memberId, String role){
        long now = System.currentTimeMillis();
        Date expiry = new Date(now+ jwtProperties.getRefreshTokenValidityInSeconds()*1000);

        return Jwts.builder()
                .setSubject(String.valueOf(memberId))
                .claim(CLAIM_ROLE,role)
                .setIssuedAt(new Date(now))
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    //  토큰이 진짜인지, 아직 유효한지 검사
    public boolean validateToken(String token){
        try{
            parseClaims(token);
            return true; //전부 정상 -> true 리턴
        }catch(JwtException | IllegalArgumentException e){ //하나라도 문제 -> 예외 처리
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
    }

    /**
     * 토큰에서 Spring Security 인증 객체 만들기
     * 전체 흐름
     * 1. 클라이언트가 Authorization: Bearer <JWT> 헤더 붙여서 요청 보냄
     * 2. 스프링 시큐리티 필터 체인 동작
     * 3. 우리 커스텀 JwtAuthenticationFilter가 중간에서
     *    - a. 헤더에서 토큰 꺼냄
     *    - b. jwtTokenProvider.validateToken(token)으로 검증
     *    - c. 통과하면 jwtTokenProvider.getAuthentication(token)으로 Authentication 객체 생성
     *    - d. SecurityContextHolder.getContext().setAuthentication(authentication)에 넣음
     * 4. 나머지 필터/디스패처서블릿/컨트롤러로 요청이 넘어감
     * 5. 컨트롤러 / @PreAuthorize, hasRole() 같은 권한 체크는 SecurityContext 안에 있는 Autentication을 보고 판단
     */
    public Authentication getAuthentication(String token){
        Claims claims = parseClaims(token);  //이미 서명/만료 검증된 상태라고 가정 ( 전에 validateToken(token)으로 검증 해야함)
        String subject = claims.getSubject(); // memberId 꺼내기
        String role = claims.get(CLAIM_ROLE, String.class);  //커스텀 클레임 꺼내기

        /**
         * 주어진 role로 권한 리스트를 만듬 -> authorities = [ SimpleGrantedAuthority("ROLE_USER") ]   // 리스트 안에 권한 객체 1개
         * 비어있는 경우 아무것도 없는 불변 리스트를 반환 -> authorities = []   // 비어있는 리스트, 즉 권한이 하난도 없는 사용자처럼 취급
         * SimpleGrantedAuthority는 String 하나를 감싸놓은 객체
         * -> 즉, 그냥 "ROLE_USER"같은 문자열을 Spring Security가 이해할 수 있는 형태로 감싼 것
         * 굳이 문자열을 객체로 감싸는 이유
         * Spring Security는 Authentication 객체 안에 이 유저가 가진 권한 목록을 Collection<? extends GrantedAuthority> 로 들고 다님
         * Authentication auth = SecurityContextHolder.getContext().getAuthentication();
         * Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
         * - 유연성과 확장성 때문에 인터페이스(GrantedAuthority) + 객체 구조를 쓰는 것
         */
        Collection<SimpleGrantedAuthority> authorities =
                role != null? List.of(new SimpleGrantedAuthority(role)): Collections.emptyList();

        /**
         * JWT에서 꺼낸 정보 (subject, role)를 기반으로, Spring Security가 이해할 수 있는 UserDetails 객체를 만들고,
         * 그걸 Authentication에 태워서 이 요청은 인증된 사용자라고 표시하기 위한 준비 단계
         * User는 Spring Security가 제공하는 UserDetails를 구현한 기본 구현체
         * JWT기반 인증에서는 비밀번호가 의미 없는 이유
         * - 로그인 시에는 이미 백엔드에서 아이디/비밀번호 확인 끝난 뒤에 JWT를 발급해줌
         * - 이후 요청에서는 비밀번호를 다시 확인하는 게 아니라, 토큰 서명/만료만 확인함
         */
        UserDetails principal = new User(
                subject,      // 이 사용자를 식별하는 아이디 (여기서는 memberId 문자열)
                "",           //password (JWT 기반이라 비밀번호는 의미 없어서 빈 값)
                authorities); // 이 사용자가 가진 역할/권한 목록(ROLE_USER, ROLE_ADMIN 등)

        /**
         * Authentication은 인증 요청 또는 인증 결과를 담는 토큰 객체
         * 즉, 이 요청이 [아직 인증 전인지, 인증 후인지, 누가 로그인했고, 권한이 뭔지] 같은 것을 한 번에 들고 있는 상태 덩어리
         * getPrincipal() -> 보통 UserDetails
         * getCredentials() -> 비밀번호, 토큰 같은 자격 정보(인증 후에는 보통 비움/의미없음)
         * getAuthorities() -> 권한 리스트
         * isAuthenticated() -> 이 Authentication이 이미 인증된 상태인지 여부
         */
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
        /**
         * UserDetails와 Authentication의 차이?
         * 1. 역할
         *  - UserDetails
         *  -> 사용자 정보 구조 (아이디, 비밀번호, 권한, 계정 상태 등)
         *  -> DB/외부 시스템의 유저를 스프링 시큐리티가 이해할 수 있는 형태로 감싼 것
         *  - Authentication
         *  -> 인증 상태ㅐ를 담은 토큰 (누가, 무슨 권한으로, 인증됐는지)
         *  -> SecurityContext 안에 들어가서 현재 로그인한 사용자를 대표하는 객체
         * 2. 관계
         *  - Authentication안에 principal로 UserDetails가 들어있는 경우가 대부분
         *
         * 동작 구조
         *  1. JWT 필터가 토큰 파싱
         *  2. claims → subject, role로부터 UserDetails 생성
         *  3. 그걸 principal로 해서 UsernamePasswordAuthenticationToken (Authentication) 생성
         *  4. SecurityContext에 저장
         */
    }

    public Claims parseClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Long getMemberId(String token){
        Claims claims = parseClaims(token);
        return Long.valueOf(claims.getSubject());
    }

}
