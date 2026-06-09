package com.project.extension.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

@Component
public class RateLimitingFilter implements Filter {

    // Cache com TTL + tamanho máximo: buckets de IPs ociosos são removidos automaticamente,
    // evitando crescimento ilimitado de memória (DoS por consumo de heap). O expireAfterAccess
    // é maior que a janela de refill para não evictar um IP que ainda está em rate-limit.
    private final Cache<String, Bucket> loginBuckets = Caffeine.newBuilder()
            .maximumSize(50_000)
            .expireAfterAccess(Duration.ofMinutes(10))
            .build();
    private final Cache<String, Bucket> solicitacoesBuckets = Caffeine.newBuilder()
            .maximumSize(50_000)
            .expireAfterAccess(Duration.ofMinutes(30))
            .build();

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String path = request.getServletPath();
        String method = request.getMethod();

        if ("POST".equalsIgnoreCase(method) && path.endsWith("/auth/login")) {
            String ip = resolveClientIp(request);
            Bucket bucket = loginBuckets.get(ip, k -> buildLoginBucket());
            if (!bucket.tryConsume(1)) {
                response.setStatus(429);
                response.setContentType("application/json");
                response.getWriter().write("{\"message\":\"Muitas tentativas de login. Aguarde antes de tentar novamente.\"}");
                return;
            }
        } else if ("POST".equalsIgnoreCase(method) && path.endsWith("/solicitacoes")) {
            String ip = resolveClientIp(request);
            Bucket bucket = solicitacoesBuckets.get(ip, k -> buildSolicitacoesBucket());
            if (!bucket.tryConsume(1)) {
                response.setStatus(429);
                response.setContentType("application/json");
                response.getWriter().write("{\"message\":\"Muitas solicitações. Aguarde antes de tentar novamente.\"}");
                return;
            }
        }

        chain.doFilter(req, res);
    }

    private Bucket buildLoginBucket() {
        // 5 tentativas por minuto por IP
        return Bucket.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(5)
                        .refillGreedy(5, Duration.ofMinutes(1))
                        .build())
                .build();
    }

    private Bucket buildSolicitacoesBucket() {
        // 3 solicitações por 10 minutos por IP
        return Bucket.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(3)
                        .refillGreedy(3, Duration.ofMinutes(10))
                        .build())
                .build();
    }

    private String resolveClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
