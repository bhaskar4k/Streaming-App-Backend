package com.app.authentication.controller;

import com.app.authentication.entity.TMstUser;
import com.app.authentication.service.TMstUserService;
import com.app.common.utils.CommonReturn.CommonReturn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AuthenticationController {
    @Autowired
    private TMstUserService tMstUserService;

    @PostMapping("/authentication/do_signup")
    public CommonReturn<TMstUser> do_signup(@RequestBody TMstUser t_mst_user){
        TMstUser created_user = tMstUserService.saveUser(t_mst_user);
        return CommonReturn.success(created_user);
    }
}