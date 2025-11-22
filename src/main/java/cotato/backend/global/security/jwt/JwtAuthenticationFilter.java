package cotato.backend.global.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JwtAuthenticationFilter
 *
 * - 매 요청마다 한 번씩 동작하면서, HTTP 요청 헤더에서 JWT를 꺼내고,
 *   토큰이 유효하다면 SecurityContext에 Authentication을 심어주는 역할
 * - 이 필터를 통과한 이후부터 스프링 시큐리티는 로그인된 사용자로 인식
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // HTTP Authorization 헤더에 들어오는 토큰 앞부분 형식 (상수로 뺌)
    // 예:  "Authorization: Bearer eyJhbGciOi..."
    private static final String BEARER_PREFIX = "Bearer ";

    // 실제 JWT 생성/검증/클레임 추출을 담당하는 컴포넌트
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 이 메서드 안에서 실제 필터링 로직을 구현한다.
     *
     * 전체 흐름:
     *  1. 요청 헤더에서 JWT(Access Token)를 꺼낸다. (resolveToken)
     *  2. 토큰이 존재하면 유효성 검증(validateToken)
     *  3. 검증 통과 시, 토큰에서 Authentication 객체 생성(getAuthentication)
     *  4. SecurityContextHolder에 Authentication을 저장해서, 이후 체인에서 "인증된 사용자"로 인식되게 만든다.
     *  5. 다음 필터로 요청을 넘긴다 (filterChain.doFilter)
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String token = resolveToken(request);

        if (token != null) {
            if (jwtTokenProvider.validateToken(token)) {
                //토큰이 유효하다면, 토큰 안의 클레임(subject, role 등)을 이용해 Spring Security에서 사용하는 Authentication 객체 생성
                Authentication authentication = jwtTokenProvider.getAuthentication(token);

                //  생성한 Authentication을 SecurityContext에 저장
                // ->  이후 컨트롤러나 @PreAuthorize, hasRole() 등에서 "현재 로그인한 사용자" 정보로 사용된다.
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        //  나머지 필터/서블릿/컨트롤러로 요청을 계속 전달
        //  이 줄을 호출하지 않으면, 뒤에 있는 필터나 컨트롤러가 아예 실행되지 않는다.
        filterChain.doFilter(request, response);
    }

    /**
     * - HTTP 요청 헤더에서 JWT 토큰 문자열만 추출하는 역할.
     * - Convention:
     *   Authorization: Bearer <JWT 토큰 문자열>
     *
     * 동작:
     *  1. Authorization 헤더 값을 가져옴
     *  2. 값이 존재하고, "Bearer "로 시작하는지 검사
     *  3. 접두사("Bearer ")를 잘라낸 나머지 부분만 반환
     *  4. 조건을 만족하지 않으면 null 반환 (토큰 없음으로 처리)
     */
    private String resolveToken(HttpServletRequest request) {
        // Authorization 헤더 전체 값 가져오기
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        // 값이 비어있지 않고, "Bearer"로 시작하는지 체크
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            //Bearer 이후 문자열만 잘라서 순수 토큰 부분만 반환
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        // Authorization 헤더가 없거나, 형식이 맞지 않으면 토큰이 없는 것으로 간주
        return null;
    }
}
