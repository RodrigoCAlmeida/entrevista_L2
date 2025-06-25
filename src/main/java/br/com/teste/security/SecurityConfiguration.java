package br.com.teste.security;

import br.com.teste.security.filter.ApiKeyAuthFilter;
import br.com.teste.security.filter.ApiKeyRateLimitFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final ApiKeyAuthExtractor apiKeyAuthExtractor;
    private final UnauthorizedHandler unauthorizedHandler;

    public SecurityConfiguration(
            ApiKeyAuthExtractor apiKeyAuthExtractor,
            UnauthorizedHandler unauthorizedHandler
    ) {
        this.apiKeyAuthExtractor = apiKeyAuthExtractor;
        this.unauthorizedHandler = unauthorizedHandler;
    }

    @Bean
    public ApiKeyAuthFilter apiKeyAuthFilter() {
        return new ApiKeyAuthFilter(apiKeyAuthExtractor);
    }

    @Bean
    public ApiKeyRateLimitFilter apiKeyRateLimitFilter() {
        return new ApiKeyRateLimitFilter();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(AbstractHttpConfigurer::disable)
                .exceptionHandling(configurer -> configurer.authenticationEntryPoint(unauthorizedHandler))
                .authorizeHttpRequests(registry -> registry
                        .requestMatchers(
                                "/public/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/favicon.ico",
                                "/error"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(apiKeyRateLimitFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(apiKeyAuthFilter(), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            throw new UnsupportedOperationException("UserDetailsService is not used.");
        };
    }
}
