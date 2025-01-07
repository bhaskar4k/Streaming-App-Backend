package com.app.authentication.controller;

import com.app.authentication.common.CommonReturn;
import com.app.authentication.entity.TLogExceptions;
import com.app.authentication.entity.TMstUser;
import com.app.authentication.model.TMstUserModel;
import com.app.authentication.security.EncryptionDecryption;
import com.app.authentication.service.LogExceptionsService;
import com.app.authentication.service.LoginSignUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;


@RestController
@RequestMapping
public class AuthenticationController {
    @Autowired
    private LoginSignUpService loginSignUpService;
    @Autowired
    private LogExceptionsService logExceptionsService;
    private EncryptionDecryption encryptionDecryption;

    public AuthenticationController() {
        this.encryptionDecryption=new EncryptionDecryption();
    }

    @PostMapping("/authentication/do_signup")
    public CommonReturn<Boolean> do_signup(@RequestBody TMstUserModel new_user){
        try{
            return loginSignUpService.saveUser(new_user);
        } catch (Exception e) {
            log("do_signup()",e.getMessage());
            return CommonReturn.error(400,"Internal Server Error.");
        }
    }

    @PostMapping("/authentication/do_login")
    public CommonReturn<TMstUserModel> do_login(@RequestBody TMstUserModel cur_user){
        try{
            return loginSignUpService.validateUser(cur_user);
        } catch (Exception e) {
            log("do_login()",e.getMessage());
            return CommonReturn.error(400,"Internal Server Error.");
        }
    }

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public String greeting(Object message) throws Exception {
        return "From WS HELLOOOOO!";
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