package com.lms.gateway.security;

import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtAuthenticationFilter implements WebFilter {

    private final JwtUtils jwtUtils;

    public JwtAuthenticationFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        boolean isPublic = path.startsWith("/api/v1/auth/register") || 
                           path.startsWith("/api/v1/auth/login") || 
                           path.startsWith("/v3/api-docs") || 
                           path.startsWith("/swagger-ui") || 
                           path.startsWith("/actuator");

        String token = extractToken(exchange.getRequest());

        if (StringUtils.hasText(token)) {
            if (jwtUtils.validateToken(token)) {
                String accountStatus = jwtUtils.getAccountStatus(token);

                if (!isPublic) {
                    if ("SUSPENDED".equalsIgnoreCase(accountStatus)) {
                        return SecurityResponseWriter.writeResponse(exchange, HttpStatus.FORBIDDEN, "Account has been suspended.");
                    } else if ("REJECTED".equalsIgnoreCase(accountStatus)) {
                        return SecurityResponseWriter.writeResponse(exchange, HttpStatus.FORBIDDEN, "Account has been rejected.");
                    } else if (!"ACTIVE".equalsIgnoreCase(accountStatus)) {
                        return SecurityResponseWriter.writeResponse(exchange, HttpStatus.FORBIDDEN, "Access denied.");
                    }
                }

                String email = jwtUtils.getEmail(token);
                String role = jwtUtils.getRole(token);
                String userId = jwtUtils.getUserId(token);
                
                if (path.startsWith("/api/v1/admin/") && !"ROLE_ADMIN".equalsIgnoreCase(role)) {
                    return SecurityResponseWriter.writeResponse(exchange, HttpStatus.FORBIDDEN, "Access denied. Admin role required.");
                }

                JwtAuthenticationToken authentication = new JwtAuthenticationToken(email, token, List.of(new SimpleGrantedAuthority(role)), userId, accountStatus, role);
                
                return chain.filter(exchange)
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
            } else {
                return SecurityResponseWriter.writeResponse(exchange, HttpStatus.UNAUTHORIZED, "Invalid or expired token.");
            }
        }
        
        return chain.filter(exchange);
    }

    private String extractToken(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
