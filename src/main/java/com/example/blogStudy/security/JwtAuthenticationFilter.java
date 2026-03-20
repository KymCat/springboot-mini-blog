package com.example.blogStudy.security;

import com.example.blogStudy.exception.CustomException;
import com.example.blogStudy.exception.ErrorCode;
import com.example.blogStudy.jwt.JwtProvider;
import com.example.blogStudy.jwt.redis.BlacklistTokenService;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final BlacklistTokenService blacklistTokenService;

    @Resource(name = "handlerExceptionResolver")
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            String authorizationHeader = request.getHeader("Authorization");

            // 헤더 없으면 통과
            if (authorizationHeader == null) {
                filterChain.doFilter(request, response);
                return;
            }

            // 토큰 추출
            if (!authorizationHeader.startsWith("Bearer "))
                throw new CustomException(ErrorCode.INVALID_AUTH_HEADER);

            String accessToken = authorizationHeader.substring(7);    // Authorization: Bearer <토큰>

            // 블랙 리스트 확인
            if (blacklistTokenService.isBlackList(accessToken))
                throw new CustomException(ErrorCode.INVALID_TOKEN);

            // 유효한 액세스 토큰인지 확인
            if (!jwtProvider.validateToken(accessToken))
                throw new CustomException(ErrorCode.INVALID_TOKEN);

            String userId = jwtProvider.getUserId(accessToken);
            String nickName = jwtProvider.getNickName(accessToken);

            CustomUserDetails customUserDetails = new CustomUserDetails(userId, nickName);

            // Authentication 객체 생성
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            customUserDetails,
                            null,   // Password, Jwt 인증은 필요없음
                            AuthorityUtils.NO_AUTHORITIES   // 권한
                    );

            // SecurityContextHolder 에 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 다음 필터로 넘김
            filterChain.doFilter(request, response);
        } catch (Exception customException) {
            SecurityContextHolder.clearContext();
            handlerExceptionResolver.resolveException(request, response, null, customException);
        }
    }
}
