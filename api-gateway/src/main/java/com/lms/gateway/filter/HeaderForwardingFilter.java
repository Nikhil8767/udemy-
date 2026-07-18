package com.lms.gateway.filter;

import com.lms.gateway.security.JwtAuthenticationToken;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class HeaderForwardingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> {
                    if (securityContext.getAuthentication() instanceof JwtAuthenticationToken jwtAuth) {
                        ServerHttpRequest request = exchange.getRequest().mutate()
                                .headers(httpHeaders -> {
                                    httpHeaders.remove("X-User-Id");
                                    httpHeaders.remove("X-User-Email");
                                    httpHeaders.remove("X-User-Role");
                                    httpHeaders.remove("X-Account-Status");
                                })
                                .header("X-User-Id", jwtAuth.getUserId())
                                .header("X-User-Email", jwtAuth.getName())
                                .header("X-User-Role", jwtAuth.getRole())
                                .header("X-Account-Status", jwtAuth.getAccountStatus())
                                .build();
                        return exchange.mutate().request(request).build();
                    }
                    return exchange;
                })
                .defaultIfEmpty(exchange)
                .flatMap(mutatedExchange -> {
                    if (mutatedExchange == exchange) {
                        // For unauthenticated/public routes, ensure these headers cannot be spoofed by external clients
                        ServerHttpRequest request = exchange.getRequest().mutate()
                                .headers(httpHeaders -> {
                                    httpHeaders.remove("X-User-Id");
                                    httpHeaders.remove("X-User-Email");
                                    httpHeaders.remove("X-User-Role");
                                    httpHeaders.remove("X-Account-Status");
                                })
                                .build();
                        return chain.filter(exchange.mutate().request(request).build());
                    }
                    return chain.filter(mutatedExchange);
                });
    }

    @Override
    public int getOrder() {
        return 0; // High priority to run immediately after authentication filters
    }
}
