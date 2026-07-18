package com.lms.frontend.config;

import com.lms.frontend.security.JwtAuthenticationToken;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
public class FeignClientInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        String url = requestTemplate.url();
        if (url.contains("/login") || url.contains("/register")) {
            return;
        }
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            requestTemplate.header("Authorization", "Bearer " + jwtAuth.getToken());
        }
    }
}
