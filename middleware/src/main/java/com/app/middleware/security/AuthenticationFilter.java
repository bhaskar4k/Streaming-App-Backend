package com.app.middleware.security;

import com.app.middleware.environment.ApiEndpointInfo;
import com.app.middleware.common.CommonReturn;
import com.app.middleware.model.JwtUserDetails;
import com.app.middleware.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    public AuthService authService;
    private final ObjectMapper objectMapper;
    private final ApiEndpointInfo apiEndpointInfo;

    private final List<String> EXCLUDED_URLS;

    public AuthenticationFilter() {
        this.objectMapper = new ObjectMapper();
        this.apiEndpointInfo = new ApiEndpointInfo();
        this.EXCLUDED_URLS = apiEndpointInfo.getUnauthenticatedEndpoints();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            if (isExcludedUrl(request.getRequestURI())) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = getTokenFromRequest(request);

            if(token == null) {
                handleNoJwtTokenFound(response);
                return;
            }

            boolean is_authenticated = true;
            JwtUserDetails userDetails = null;

            if (StringUtils.hasText(token)) {
                try {
                    CommonReturn<JwtUserDetails> post_validated_request = authService.validateToken(token);
                    if(post_validated_request.getStatus()!=200){
                        is_authenticated = false;
                    } else {
                        userDetails = post_validated_request.getData();
                        JwtAuthenticationToken authentication = new JwtAuthenticationToken(userDetails, token, null);
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        request.setAttribute("JwtDetails", userDetails);
                    }
                } catch (Exception e) {
                    is_authenticated = false;
                }
            }

            if(is_authenticated){
                filterChain.doFilter(request, response);
            }else{
                handleUnauthorized(response);
            }
        } catch (Exception e) {
            handleUnauthorized(response);
        }
    }

    private boolean isExcludedUrl(String requestUri) {
        return EXCLUDED_URLS.stream().anyMatch(requestUri::startsWith);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }

    private void handleNoJwtTokenFound(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write("{\"message\": \"No JWT Token Found.\"}");
        response.getWriter().flush();
    }

    private void handleUnauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write("{\"message\": \"Invalid Or Expired Or Unauthorized JWT Token.\"}");
        response.getWriter().flush();
    }
}
