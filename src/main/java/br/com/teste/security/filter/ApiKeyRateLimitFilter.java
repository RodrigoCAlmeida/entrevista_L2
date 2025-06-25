package br.com.teste.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ApiKeyRateLimitFilter extends OncePerRequestFilter {

    private static final int MAX_REQUESTS = 100;
    private static final long WINDOW_MILLIS = 60_000;

    private final Map<String, RateLimitInfo> requestCounts = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String apiKey = request.getHeader("authorization");

        if (apiKey != null) {
            RateLimitInfo rateLimit = requestCounts.computeIfAbsent(apiKey, k -> new RateLimitInfo());

            synchronized (rateLimit) {
                long now = Instant.now().toEpochMilli();
                if (now - rateLimit.windowStart > WINDOW_MILLIS) {
                    rateLimit.windowStart = now;
                    rateLimit.count = 0;
                }
                rateLimit.count++;
                if (rateLimit.count > MAX_REQUESTS) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("Limite de requisições excedido. Tente novamente mais tarde.");
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/public")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.equals("/swagger-ui.html")
                || path.startsWith("/webjars"); // necessário para alguns estilos JS/CSS
    }
    private static class RateLimitInfo {
        long windowStart = Instant.now().toEpochMilli();
        int count = 0;
    }
}
