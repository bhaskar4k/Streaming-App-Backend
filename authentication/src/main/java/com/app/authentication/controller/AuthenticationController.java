package com.app.authentication.controller;

import com.app.authentication.common.CommonApiReturn;
import com.app.authentication.entity.TLogExceptions;
import com.app.authentication.entity.TMstUser;
import com.app.authentication.model.TMstUserModel;
import com.app.authentication.security.EncryptionDecryption;
import com.app.authentication.service.LogExceptionsService;
import com.app.authentication.service.LoginSignUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


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
    public CommonApiReturn<TMstUser> do_signup(@RequestBody TMstUserModel new_user){
        try{
            if(loginSignUpService.alreadyRegistered(new_user.getEmail())){
                return CommonApiReturn.error(400,"The email address [" + new_user.getEmail() + "] is already registered.");
            }

            TMstUser saved_user = loginSignUpService.saveUser(new_user);
            return CommonApiReturn.success("Sign-Up is successful. Please Login now.",saved_user);
        } catch (Exception e) {
            logExceptionsService.saveLogException(new TLogExceptions("controller","AuthenticationController","do_signup()",e.getMessage()));
            return CommonApiReturn.error(400,"Internal Server Error.");
        }
    }

    @PostMapping("/authentication/do_login")
    public CommonApiReturn<TMstUser> do_login(@RequestBody TMstUserModel cur_user){
        try{
            TMstUser validated_user = loginSignUpService.validateUser(cur_user);
            if(validated_user!=null){
                if(encryptionDecryption.Decrypt(cur_user.getPassword()).equals(encryptionDecryption.Decrypt(validated_user.getPassword()))){
                    return CommonApiReturn.success("Login is successful.",validated_user);
                }
            }

            return CommonApiReturn.error(401,"Incorrect Username or Password.");
        } catch (Exception e) {
            logExceptionsService.saveLogException(new TLogExceptions("controller","AuthenticationController","do_login()",e.getMessage()));
            return CommonApiReturn.error(400,"Internal Server Error.");
        }
    }
}