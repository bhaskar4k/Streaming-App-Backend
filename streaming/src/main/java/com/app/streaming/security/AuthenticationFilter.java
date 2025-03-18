package com.app.streaming.security;

import com.app.streaming.common.CommonReturn;
import com.app.streaming.model.JwtUserDetails;
import com.app.streaming.service.AuthService;
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

@Component
public class AuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    public AuthService authService;
    private final ObjectMapper objectMapper;

    public AuthenticationFilter() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = getTokenFromRequest(request);

            if(token == null) {
                handleNoJwtTokenFound(response);
                return;
            }

            boolean is_authenticated = true;

            if (StringUtils.hasText(token)) {
                try {
                    CommonReturn<JwtUserDetails> post_validated_request = authService.validateToken(token);
                    if(post_validated_request.getStatus()!=200){
                        is_authenticated = false;
                    } else {
                        JwtAuthenticationToken authentication = new JwtAuthenticationToken(post_validated_request.getData(), token, null);
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
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
