package com.app.upload.service;

import com.app.upload.common.CommonReturn;
import com.app.upload.model.JwtUserDetails;
import com.app.upload.model.TokenRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Component
public class AuthService {
    public CommonReturn<JwtUserDetails> validateToken(String token) {
        String AUTH_SERVICE_URL = "http://localhost:8090/authentication/verify_request";

        RestTemplate restTemplate = new RestTemplate();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            TokenRequest request = new TokenRequest(token);
            HttpEntity<TokenRequest> entity = new HttpEntity<>(request, headers);

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
            e.printStackTrace();
            return CommonReturn.error(401,"Invalid Or Expired Or Unauthorized JWT Token.");
        }
    }
}
