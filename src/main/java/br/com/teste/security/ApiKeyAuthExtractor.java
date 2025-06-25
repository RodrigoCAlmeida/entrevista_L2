package br.com.teste.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ApiKeyAuthExtractor {

    @Value("${application.security.api-key}")
    private String apiKey;

    public Optional<Authentication> extract(HttpServletRequest request) {
        String header = request.getHeader("authorization");
        if (header == null || header.isBlank()) {
            return Optional.empty();
        }

        final String prefix = "ApiKey ";
        String providedKey = header.startsWith(prefix) ? header.substring(prefix.length()).trim() : header.trim();

        if (!providedKey.equals(apiKey)) {
            return Optional.empty();
        }

        return Optional.of(new ApiKeyAuth(providedKey, AuthorityUtils.NO_AUTHORITIES));
    }
}
