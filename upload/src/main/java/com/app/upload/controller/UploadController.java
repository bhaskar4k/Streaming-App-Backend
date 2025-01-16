package com.app.upload.controller;

import com.app.authentication.model.ValidatedUserDetails;
import com.app.upload.common.CommonReturn;
import com.app.upload.model.JwtUserDetails;
import com.app.upload.model.TokenRequest;
import com.app.upload.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/upload")
public class UploadController {
    @Autowired
    public AuthService authService;
    @GetMapping("/temp1")
    public String temp(){
        return "Upload";
    }

    @PostMapping("/upload")
    public CommonReturn<JwtUserDetails> upload(@RequestHeader("Authorization") String authorization) {
        String token = authorization.replace("Bearer ", "");

        return authService.validateToken(token);
    }
}
