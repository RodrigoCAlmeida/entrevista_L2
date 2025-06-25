package br.com.teste.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public final class ApiKeyAuth extends AbstractAuthenticationToken {

    private final String apiKey;

    public ApiKeyAuth(String apiKey, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.apiKey = apiKey;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return apiKey;
    }

    public String getApiKey() {
        return apiKey;
    }

    @Override
    public String toString() {
        return "ApiKeyAuth[authenticated=" + isAuthenticated() + ", authorities=" + getAuthorities() + "]";
    }
}
