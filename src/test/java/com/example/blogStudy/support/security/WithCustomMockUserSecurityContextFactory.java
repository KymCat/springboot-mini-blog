package com.example.blogStudy.support.security;

import com.example.blogStudy.security.CustomUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithCustomMockUserSecurityContextFactory implements
        WithSecurityContextFactory<WithCustomMockUser>
{

    @Override
    public SecurityContext createSecurityContext(WithCustomMockUser annotation) {
        String userId = annotation.userId();
        String nickname = annotation.nickname();

        CustomUserDetails customUserDetails = new CustomUserDetails(userId, nickname);
        // Authentication 객체 생성
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        customUserDetails,
                        null,   // Password, Jwt 인증은 필요없음
                        AuthorityUtils.NO_AUTHORITIES   // 권한
                );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        return context;
    }
}
