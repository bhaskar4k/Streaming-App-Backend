package com.app.upload.service;

import com.app.upload.common.CommonReturn;
import com.app.upload.entity.TLogExceptions;
import com.app.upload.environment.Environment;
import com.app.upload.model.JwtUserDetails;
import com.app.upload.model.TokenRequest;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private LogExceptionsService logExceptionsService;

    public AuthService(){
        this.environment = new Environment();
    }

    public CommonReturn<JwtUserDetails> validateToken(String token) {
        String AUTH_SERVICE_URL = environment.getAuthServiceUrl();

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
            log(null,"validateToken()",e.getMessage());
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
            log(-1L,"getAuthenticatedUserFromContext()",e.getMessage());
            return null;
        }
    }


    private void log(Long t_mst_user_id, String function_name, String exception_msg){
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();

        String full_class_path = stackTraceElements[2].getClassName();
        String class_name = full_class_path.substring(full_class_path.lastIndexOf(".") + 1);

        String full_package_path = full_class_path.substring(0, full_class_path.lastIndexOf("."));
        String package_name = full_package_path.substring(full_package_path.lastIndexOf(".") + 1);

        logExceptionsService.saveLogException(new TLogExceptions(package_name,class_name,function_name,exception_msg,t_mst_user_id));
    }
}
