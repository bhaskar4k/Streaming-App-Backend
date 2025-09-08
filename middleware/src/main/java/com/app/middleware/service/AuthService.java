package com.app.middleware.service;

import com.app.middleware.common.CommonReturn;
import com.app.middleware.environment.Environment;
import com.app.middleware.model.JwtUserDetails;
import com.app.middleware.model.TokenRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
@Component
public class AuthService {
    private Environment environment;

    public AuthService(){
        this.environment = new Environment();
    }

    public CommonReturn<JwtUserDetails> validateToken(String token) {
        String AUTH_SERVICE_URL = environment.getAuthServiceUrl();
        String MIDDLEWARE_ORIGIN = environment.getMiddlewareOrigin();

        RestTemplate restTemplate = new RestTemplate();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            headers.set(HttpHeaders.ORIGIN, MIDDLEWARE_ORIGIN);
            headers.set("X-Client-Origin", MIDDLEWARE_ORIGIN);

            TokenRequest new_request = new TokenRequest(token);
            HttpEntity<TokenRequest> entity = new HttpEntity<>(new_request, headers);

            ResponseEntity<CommonReturn<JwtUserDetails>> response = restTemplate.exchange(
                    AUTH_SERVICE_URL,
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<CommonReturn<JwtUserDetails>>() {}
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                CommonReturn<JwtUserDetails> tokenData = response.getBody();
                if (tokenData != null && tokenData.getStatus()==200) {
                    return tokenData;
                }
            }

            return CommonReturn.error(401,"Invalid Or Expired Or Unauthorized JWT Token.");
        } catch (Exception e) {
            System.out.println(e);
            return CommonReturn.error(400,"Internal Server Error.");
        }
    }

    public JwtUserDetails getAuthenticatedUserFromContext() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.isAuthenticated()) {
                Object principal = authentication.getPrincipal();
                if (principal instanceof JwtUserDetails) {
                    return (JwtUserDetails) principal;
                }else{
                    return null;
                }
            }else{
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }
}
