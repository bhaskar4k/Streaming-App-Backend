package com.app.authentication.jwt;

import com.app.authentication.common.CommonReturn;
import com.app.authentication.environment.ApiEndpointInfo;
import com.app.authentication.model.JwtUserDetails;
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
import java.util.Arrays;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private Jwt jwt;
    private final ObjectMapper objectMapper;
    private final ApiEndpointInfo apiEndpointInfo;

    private final List<String> EXCLUDED_URLS;

    public JwtAuthenticationFilter() {
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
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().write("{\"message\": \"No JWT Token Found.\"}");
                response.getWriter().flush();
                return;
            }

            boolean is_authenticated = true;

            if (StringUtils.hasText(token)) {
                try {
                    if (jwt.validateToken(token)) {
                        String subject = jwt.extractSubject(token);

                        if (StringUtils.hasText(subject)) {
                            JwtUserDetails extractedUserObject = objectMapper.readValue(subject, JwtUserDetails.class);

                            if (extractedUserObject != null) {
                                JwtAuthenticationToken authentication = new JwtAuthenticationToken(extractedUserObject, token, null);
                                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                                SecurityContextHolder.getContext().setAuthentication(authentication);
                            } else {
                                is_authenticated = false;
                            }
                        } else {
                            is_authenticated = false;
                        }
                    } else {
                        is_authenticated = false;
                    }
                } catch (Exception e) {
                    is_authenticated = false;
                }
            }

            if(is_authenticated){
                filterChain.doFilter(request, response);
            }else{
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().write("{\"message\": \"Invalid Or Expired Or Unauthorized JWT Token.\"}");
                response.getWriter().flush();
            }
        } catch (Exception e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("{\"message\": \"Invalid Or Expired Or Unauthorized JWT Token.\"}");
            response.getWriter().flush();
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
}
