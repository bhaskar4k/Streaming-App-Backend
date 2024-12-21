package com.app.authentication.controller;

import com.app.authentication.entity.TMstUser;
import com.app.authentication.service.TMstUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AuthenticationController {
    @Autowired
    private TMstUserService tMstUserService;

    @PostMapping("/authentication/do_signup")
    public String do_signup(@RequestBody TMstUser t_mst_user){
        tMstUserService.saveUser(t_mst_user);
        return "Authentication";
    }
}