package com.app.authentication.controller;

import com.app.authentication.common.CommonApiReturn;
import com.app.authentication.entity.TMstUser;
import com.app.authentication.model.TMstUserModel;
import com.app.authentication.service.LoginSignUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AuthenticationController {
    @Autowired
    private LoginSignUpService loginSignUpService;

    public AuthenticationController() {
        this.loginSignUpService = new LoginSignUpService();
    }

    @PostMapping("/authentication/do_signup")
    public CommonApiReturn<TMstUser> do_signup(@RequestBody TMstUserModel new_user){
        try{
            if(loginSignUpService.alreadyRegistered(new_user.getEmail())){
                return CommonApiReturn.error(400,"The email address [" + new_user.getEmail() + "] is already registered.");
            }

            TMstUser saved_user = loginSignUpService.saveProduct(new_user);
            return CommonApiReturn.success("Sign-Up is successful. Please Login now.",saved_user);
        } catch (Exception e) {
            // Log Exception
            return CommonApiReturn.error(400,"Internal Server Error.");
        }
    }

    @PostMapping("/authentication/do_login")
    public CommonApiReturn<TMstUser> do_login(@RequestBody TMstUserModel new_user){
        try{
            TMstUser validated_user = loginSignUpService.validateUser(new_user);
            return CommonApiReturn.success("Login is successful.",validated_user);
        } catch (Exception e) {
            // Log Exception
            return CommonApiReturn.error(400,"Internal Server Error.");
        }
    }
}