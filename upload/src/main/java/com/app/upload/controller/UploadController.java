package com.app.upload.controller;

import com.app.upload.common.CommonReturn;
import com.app.upload.entity.TLogExceptions;
import com.app.upload.environment.Environment;
import com.app.upload.model.JwtUserDetails;
import com.app.upload.service.AuthService;
import com.app.upload.service.LogExceptionsService;
import com.app.upload.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/upload")
public class UploadController {
    @Autowired
    public AuthService authService;
    @Autowired
    private UploadService uploadService;
    @Autowired
    private LogExceptionsService logExceptionsService;
    public Environment environment;

    public UploadController(){
        this.environment = new Environment();
    }

    @PostMapping("/upload")
    public CommonReturn<Boolean> upload(@RequestHeader("Authorization") String authorization,
                                        @RequestPart("video") MultipartFile file,
                                        @RequestParam("fileId") String fileId) {
        String token = authorization.replace("Bearer ", "");

        CommonReturn<JwtUserDetails> post_validated_request = authService.validateToken(token);
        if(post_validated_request.getStatus()!=200){
            return CommonReturn.error(post_validated_request.getStatus(),post_validated_request.getMessage());
        }

        try {
            boolean isVideoUploadDoneAndSuccessful = uploadService.uploadAndProcessVideo(file,fileId,post_validated_request.getData());

            if(isVideoUploadDoneAndSuccessful){
                // Emit websocket push notification
                return CommonReturn.success("Video has been uploaded successfully", true);
            }

            return CommonReturn.error(400,"Video upload failed.");
        } catch (Exception e) {
            log("upload()",e.getMessage());
            return CommonReturn.error(400,"Internal Server Error.");
        }
    }


    private void log(String function_name, String exception_msg){
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();

        String full_class_path = stackTraceElements[2].getClassName();
        String class_name = full_class_path.substring(full_class_path.lastIndexOf(".") + 1);

        String full_package_path = full_class_path.substring(0, full_class_path.lastIndexOf("."));
        String package_name = full_package_path.substring(full_package_path.lastIndexOf(".") + 1);

        logExceptionsService.saveLogException(new TLogExceptions(package_name,class_name,function_name,exception_msg,0L));
    }
}
