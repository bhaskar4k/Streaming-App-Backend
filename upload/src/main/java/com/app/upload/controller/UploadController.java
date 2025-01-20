package com.app.upload.controller;

import com.app.authentication.model.ValidatedUserDetails;
import com.app.upload.common.CommonReturn;
import com.app.upload.environment.Environment;
import com.app.upload.model.JwtUserDetails;
import com.app.upload.model.TokenRequest;
import com.app.upload.service.AuthService;
import com.app.upload.service.UploadService;
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
    @Autowired
    private UploadService uploadService;
    public Environment environment;

    public UploadController(){
        this.environment = new Environment();
    }

    @PostMapping("/upload")
    public CommonReturn<Boolean> upload(@RequestHeader("Authorization") String authorization, @RequestPart("video") MultipartFile file) {
        String token = authorization.replace("Bearer ", "");

        CommonReturn<JwtUserDetails> post_validated_request = authService.validateToken(token);
        if(post_validated_request.getStatus()!=200){
            return CommonReturn.error(post_validated_request.getStatus(),post_validated_request.getMessage());
        }

        try {
            boolean isVideoUploadDoneAndSuccessful = uploadService.uploadAndProcessVideo(file,post_validated_request.getData());

            if(isVideoUploadDoneAndSuccessful){
                // Emit websocket push notification
                return CommonReturn.success("Video has been uploaded successfully", true);
            }

            return CommonReturn.error(400,"Video upload failed.");
        } catch (Exception e) {
            return CommonReturn.error(400,"Internal Server Error.");
        }
    }
}
