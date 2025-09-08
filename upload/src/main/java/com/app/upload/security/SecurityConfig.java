package com.app.upload.security;

import com.app.upload.environment.Environment;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Configuration
public class SecurityConfig {

    private final Environment environment;
    private final List<String> EXCLUDED_URLS;

    public SecurityConfig() {
        this.environment = new Environment();
        this.EXCLUDED_URLS = environment.getUnauthenticatedEndpoints();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                // Add the custom filter to the security chain
                .addFilterBefore(originValidationFilter(), org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(environment.getAllowedOrigins());
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public OncePerRequestFilter originValidationFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                    throws ServletException, IOException {
                if (isExcludedUrl(request.getRequestURI())) {
                    filterChain.doFilter(request, response);
                    return;
                }

                String originHeader = request.getHeader("Origin");
                String allowedOrigin = environment.getAllowedOrigins().get(0); // Assuming only one allowed origin

                // Check if the request is an HTTP request and if the origin is correct
                if (originHeader == null || !originHeader.equals(allowedOrigin)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("Forbidden: Invalid Origin.");
                    return; // Stop the filter chain
                }

                filterChain.doFilter(request, response);
            }
        };
    }

    private boolean isExcludedUrl(String requestUri) {
        return EXCLUDED_URLS.stream().anyMatch(requestUri::startsWith);
    }
}