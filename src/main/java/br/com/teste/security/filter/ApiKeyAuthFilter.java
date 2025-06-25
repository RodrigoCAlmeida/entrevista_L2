package br.com.teste.security.filter;

import br.com.teste.security.ApiKeyAuthExtractor;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private final ApiKeyAuthExtractor extractor;

    public ApiKeyAuthFilter(ApiKeyAuthExtractor extractor) {
        this.extractor = extractor;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            extractor.extract(request)
                    .ifPresentOrElse(
                            auth -> SecurityContextHolder.getContext().setAuthentication(auth),
                            () -> {
                                SecurityContextHolder.clearContext();
                            }
                    );
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: " + e.getMessage());
        }
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
}
