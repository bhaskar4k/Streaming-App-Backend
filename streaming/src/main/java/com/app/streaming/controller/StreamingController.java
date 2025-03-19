package com.app.streaming.controller;

import com.app.streaming.common.CommonReturn;
import com.app.streaming.entity.TLogExceptions;
import com.app.streaming.model.JwtUserDetails;
import com.app.streaming.model.VideoInformation;
import com.app.streaming.service.AuthService;
import com.app.streaming.service.LogExceptionsService;
import com.app.streaming.service.StreamingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/streaming")
public class StreamingController {
    @Autowired
    private LogExceptionsService logExceptionsService;
    @Autowired
    private AuthService authService;
    @Autowired
    private StreamingService streamingService;

    @PostMapping("/get_video_information")
    public CommonReturn<VideoInformation> get_video_information(@RequestBody Map<String, String> requestBody){
        JwtUserDetails post_validated_request = authService.getAuthenticatedUserFromContext();

        try{
            String guid = requestBody.get("guid");
            VideoInformation info = streamingService.do_get_video_information(guid, post_validated_request);

            return CommonReturn.success("Video information fetched", info);
        } catch (Exception e) {
            log("restore_video()",e.getMessage());
            return CommonReturn.error(400,"Internal Server Error.");
        }

    }

    private void log(String function_name, String exception_msg) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();

        String full_class_path = stackTraceElements[2].getClassName();
        String class_name = full_class_path.substring(full_class_path.lastIndexOf(".") + 1);

        String full_package_path = full_class_path.substring(0, full_class_path.lastIndexOf("."));
        String package_name = full_package_path.substring(full_package_path.lastIndexOf(".") + 1);

        logExceptionsService.saveLogException(new TLogExceptions(package_name, class_name, function_name, exception_msg, 0L));
    }
}
