package br.com.teste.config;

import br.com.teste.security.filter.ApiKeyAuthFilter;
import br.com.teste.security.filter.ApiKeyRateLimitFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<ApiKeyAuthFilter> disableApiKeyAuthFilterAutoRegistration(ApiKeyAuthFilter filter) {
        FilterRegistrationBean<ApiKeyAuthFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<ApiKeyRateLimitFilter> disableApiKeyRateLimitFilterAutoRegistration(ApiKeyRateLimitFilter filter) {
        FilterRegistrationBean<ApiKeyRateLimitFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }
}