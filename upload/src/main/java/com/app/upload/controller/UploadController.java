package com.app.upload.controller;

import com.app.authentication.model.ValidatedUserDetails;
import com.app.upload.common.CommonReturn;
import com.app.upload.environment.Environment;
import com.app.upload.model.JwtUserDetails;
import com.app.upload.model.TokenRequest;
import com.app.upload.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/upload")
public class UploadController {
    @Autowired
    public AuthService authService;
    public Environment environment;

    public UploadController(){
        this.environment = new Environment();
    }


    @GetMapping("/temp1")
    public String temp(){
        return "Upload";
    }

    @PostMapping("/upload")
    public CommonReturn<JwtUserDetails> upload(@RequestHeader("Authorization") String authorization, @RequestPart("video") MultipartFile file) {
        String token = authorization.replace("Bearer ", "");

        try {
            String uploadDir = environment.getVideoFilePath();
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            File savedFile = new File(uploadDir + file.getOriginalFilename());
            file.transferTo(savedFile);

            return CommonReturn.success("Ok",null);
        } catch (IOException e) {
            return CommonReturn.error(401,"GG");
        }
    }
}
