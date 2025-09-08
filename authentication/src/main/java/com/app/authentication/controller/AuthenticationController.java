package com.app.authentication.controller;

import com.app.authentication.common.CommonReturn;
import com.app.authentication.entity.TLogExceptions;
import com.app.authentication.model.JwtUserDetails;
import com.app.authentication.model.TokenRequest;
import com.app.authentication.model.TMstUserModel;
import com.app.authentication.model.ValidatedUserDetails;
import com.app.authentication.security.EncryptionDecryption;
import com.app.authentication.service.AuthService;
import com.app.authentication.service.LogExceptionsService;
import com.app.authentication.service.LoginSignUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/authentication")
public class AuthenticationController extends BaseController {
    @Autowired
    private LoginSignUpService loginSignUpService;
    @Autowired
    private AuthService authService;
    @Autowired
    private LogExceptionsService logExceptionsService;
    private EncryptionDecryption encryptionDecryption;

    public AuthenticationController() {
        this.encryptionDecryption=new EncryptionDecryption();
    }

    @PostMapping("/do_signup")
    public CommonReturn<Boolean> do_signup(@RequestBody TMstUserModel new_user){
        try{
            return loginSignUpService.saveUser(new_user);
        } catch (Exception e) {
            log("do_signup()",e.getMessage());
            return CommonReturn.error(400,"Internal Server Error.");
        }
    }

    @PostMapping("/do_login")
    public CommonReturn<ValidatedUserDetails> do_login(@RequestBody TMstUserModel cur_user){
        try{
            return loginSignUpService.validateUser(cur_user);
        } catch (Exception e) {
            log("do_login()",e.getMessage());
            return CommonReturn.error(400,"Internal Server Error.");
        }
    }

    @Transactional
    @PostMapping("/logout")
    public CommonReturn<Boolean> logout() {
        try {
            JwtUserDetails details = getJwtUserDetails();
            Boolean res = authService.do_logout(details);

            if(res){
                return CommonReturn.success("Successfully logged out.", true);
            }

            return CommonReturn.error(400,"Internal Server Error.");
        } catch (Exception e) {
            log("logout()",e.getMessage());
            return CommonReturn.error(400,"Internal Server Error.");
        }
    }

    @PostMapping("/verify_request")
    public CommonReturn<JwtUserDetails> verify_request(@RequestBody TokenRequest tokenRequest) {
        try {
            String JWT = tokenRequest.getToken();

            if(authService.isJwtAuthenticated(JWT)){
                return CommonReturn.success("Authentication is successful.", authService.getAuthenticatedUserFromJwt(JWT));
            }

            return CommonReturn.error(401,"Invalid Or Expired Or Unauthorized JWT Token.");
        } catch (Exception e) {
            log("verify_request()",e.getMessage());
            return CommonReturn.error(400,"Internal Server Error.");
        }
    }

    @MessageMapping("/send-message")
    @SendTo("/topic/broadcast")
    public CommonReturn<String> get_mesagge_into_web_socket(String message) throws Exception {
        try{
            return CommonReturn.success("Message into websocket from frontend.", message);
        } catch (Exception e) {
            log("get_mesagge_into_web_socket()",e.getMessage());
            return CommonReturn.error(400,"Internal Server Error.");
        }
    }


    private void log(String function_name, String exception_msg){
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();

        String full_class_path = stackTraceElements[2].getClassName();
        String class_name = full_class_path.substring(full_class_path.lastIndexOf(".") + 1);

        String full_package_path = full_class_path.substring(0, full_class_path.lastIndexOf("."));
        String package_name = full_package_path.substring(full_package_path.lastIndexOf(".") + 1);

        logExceptionsService.saveLogException(new TLogExceptions(package_name,class_name,function_name,exception_msg));
    }
}