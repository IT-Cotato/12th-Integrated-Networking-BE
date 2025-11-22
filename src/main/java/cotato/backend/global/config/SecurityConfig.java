package cotato.backend.global.config;

import cotato.backend.global.security.jwt.JwtAccessDeniedHandler;
import cotato.backend.global.security.jwt.JwtAuthenticationEntryPoint;
import cotato.backend.global.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    /**
     * HttpSecurity를 사용해서 애플리케이션의 보안 규칙을 정의하는 메서드
     * 여기서 리턴하는 FilterChain이 모든 요청에 대해 적용된다.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // 1. CSRF 설정
                // - REST API + JWT 조합에서는 보통 세션을 쓰지 않고, 브라우저 기반 폼도 안 쓰기 때문에 CSRF를 꺼버린다.
                .csrf(csrf -> csrf.disable())
                // 2️. CORS 설정
                // - 프론트 도메인과 백엔드 도메인이 다를 때(CORS 이슈) 기본 설정을 사용.
                // - 필요하면 별도 CorsConfigurationSource 빈으로 상세 설정 가능.
                .cors(Customizer.withDefaults())
                // 3️. 세션 관리 전략
                // - STATELESS: 스프링 시큐리티가 HttpSession을 사용해서 로그인 상태를 저장하지 않음
                // - 매 요청마다 JWT로부터 다시 인증 정보를 세팅하는 방식(JWT + REST API에 맞는 전략)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // 4. URL 별 인가(authorization) 규칙
                .authorizeHttpRequests(auth -> auth
                        //인증 없이 누구나 접근 가능한 URL 패턴
                        .requestMatchers(
                                "/api/auth/**" //로그인, 회원가입, 토큰 재발급
                        ).permitAll()
                        //관리자 전용 URL 패턴
//                        .requestMatchers(
//                                "/api/admin/**"
//                        ).hasRole("ADMIN")
                        //위에서 명시한 것 외의 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )
                // 5. 예외 처리 설정
                .exceptionHandling(ex -> ex
                        // 인증되지 않은 사용자가 보호된 자원에 접근했을 때 -> 401 Unauthorized 응답
                        // (예: JWT 없음, 완전 깨진 JWT, SecurityContext에 Authentication이 없는 경우)
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        // 인증은 되었지만 권한이 부족할 때 -> 403 Forbidden 응답
                        // (예: ROLE_USER로 로그인했는데, ADMIN 전용 API에 접근할 때)
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                )
                // 6. JWT 필터 등록
                // - UsernamePasswordAuthenticationFilter(폼 로그인 처리)보다 앞에 JWT 필터를 배치
                // - 요청이 들어오면 먼저 JwtAuthenticationFilter가 Authorization 헤더에서 JWT를 파싱하고,
                //   토큰이 유효하면 SecurityContext에 Authentication을 심어준다.
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // 7. 최종 FilterChain 객체를 빌드해서 스프링에 등록
        return http.build();
    }
}

/**
 * 단계별 흐름
 * 클라이언트가 이 URL로 요청
 * - GET /api/members/me (내 정보 조회)
 * - 헤더: Authorization: Bearer <JWT>
 *
 * 1) 서블릿 필터 체인 → Spring Security FilterChain
 * 요청은 톰캣(내장 서블릿 컨테이너)에 들어오고,
 * 서블릿 필터들을 거쳐서 Spring Security의 FilterChainProxy까지 도달.
 * 그 안에 우리가 구성한 SecurityFilterChain(여러 Filter들)이 차례로 실행됨.
 *
 * 2) JwtAuthenticationFilter 동작
 * addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
 * 때문에, UsernamePasswordAuthenticationFilter보다 먼저 실행됨.
 *  1. doFilterInternal(...) 호출
 *  2. resolveToken(request)로 Authorization 헤더에서 토큰 추출
 *      - 있으면 → "eyJhbGciOi..."
 *      - 없으면 → null
 *  3. 토큰이 있으면:
 *      - jwtTokenProvider.validateToken(token) → 서명/만료시간/형식 검증
 *      - 통과하면 jwtTokenProvider.getAuthentication(token)으로 Authentication 생성
 *      - SecurityContextHolder.getContext().setAuthentication(authentication);
 *          → 이제 이 요청은 인증된 사용자가 됨.
 *  4. filterChain.doFilter(request, response);
 *      → 다음 필터로 진행.
 *
 * 3) AuthorizeHttpRequests 동작 (인가 단계)
 *  authorizeHttpRequests에서 설정한 룰이 이때 적용
 *  요청 URL이 /api/auth/**에 해당하면:
 *  아예 인증 없이도 통과 (permitAll)
 *  그 외 모든 요청:
 *  authenticated() → SecurityContext에 Authentication이 있어야 통과
 *
 *  우리 예시는 GET /api/members/me니까:
 *  - /api/auth/**, /h2-console/**에 해당 X
 *  - anyRequest().authenticated() 룰을 타게 됨
 *  - 방금 JwtAuthenticationFilter에서 Authentication을 세팅했으므로 → 통과
 *
 * 만약 토큰이 없거나, 토큰 검증 실패로 Authentication이 없었다면
 *  → 여기서 인증 안 됨으로 판단되고, 예외가 터져서 JwtAuthenticationEntryPoint로 흘러감 (아래에서 설명).
 *
 * 4) 컨트롤러 진입
 *  인가까지 통과하면, DispatcherServlet → 컨트롤러로 요청이 넘어감.
 *  컨트롤러나 서비스에서: @AuthenticationPrincipal 사용
 *
 * SecurityContextHolder.getContext().getAuthentication() 사용
 * → 둘 다 우리가 세팅한 Authentication 기준으로 현재 로그인 사용자를 알 수 있음.
 */

