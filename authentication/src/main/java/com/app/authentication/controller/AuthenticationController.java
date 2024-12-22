package com.app.authentication.controller;

import com.app.authentication.common.CommonApiReturn;
import com.app.authentication.entity.TMstUser;
import com.app.authentication.model.TMstUserModel;
import com.app.authentication.service.TMstUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AuthenticationController {
    @Autowired
    private TMstUserService tMstUserService;

    public AuthenticationController() {
        this.tMstUserService = new TMstUserService();
    }

    @PostMapping("/authentication/do_signup")
    public CommonApiReturn<TMstUser> do_signup(@RequestBody TMstUserModel new_user){
        try{
            if(tMstUserService.alreadyRegistered(new_user.getEmail())){
                return CommonApiReturn.error(400,"The email address [" + new_user.getEmail() + "] is already registered.");
            }

            TMstUser saved_user = tMstUserService.saveProduct(new_user);
            return CommonApiReturn.success("Sign-Up is successful. Please Login now.",saved_user);
        } catch (Exception e) {
            // Log Exception
            return CommonApiReturn.error(400,"Internal Server Error.");
        }
    }

    @PostMapping("/authentication/do_login")
    public boolean do_login(@RequestBody TMstUserModel new_user){
        try{
            tMstUserService.validateUser(new_user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}