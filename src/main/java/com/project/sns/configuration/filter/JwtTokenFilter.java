package com.project.sns.configuration.filter;

import com.project.sns.model.User;
import com.project.sns.service.UserService;
import com.project.sns.util.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final String key;
    private final UserService userService;

    /**
     * Jwt token filter
     * @param  request
     * @param  response
     * @param  filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        /**
         * 요청값의 header에 token이 들어있고, 그 token을 읽어서 인증 수행
         * token의 claims 속 userName 찾아야 함
         */

        // 1. header 꺼내기
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        // jwt 토큰이 Bearer 토큰에 들어있고, 헤더가 Bearer로 시작함
        if (header == null || !header.startsWith("Bearer ")) {
            log.error("Error occurs while getting header. Header is null or invalid");
            filterChain.doFilter(request, response);
            return;
        }

        // 2. header에서 토큰만 꺼내기
        try {
            final String token = header.split(" ")[1].trim();

            // 3. 유효한 토큰인지 검증
            if (JwtTokenUtils.isExpired(token, key)) {
                log.error("Key is Expired");
                filterChain.doFilter(request, response);
                return;
            }

            // 4. 토큰에서 유저 네임 가져오기
            String userName = JwtTokenUtils.getUserName(token, key);

            // 5. 유저 네임이 유효한지 검증
            User user = userService.loadUserByUserName(userName);

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    user, // principal
                    null, // credentials
                    user.getAuthorities() // authorities
            );

            // 6. Response 조립
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        } catch (RuntimeException e) {
            log.error("Error occurs while validating. {}", e.toString());
            filterChain.doFilter(request, response);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
