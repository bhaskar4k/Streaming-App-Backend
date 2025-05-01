package com.app.streaming.controller;

import com.app.streaming.common.CommonReturn;
import com.app.streaming.entity.TLogExceptions;
import com.app.streaming.model.JwtUserDetails;
import com.app.streaming.model.VideoInformation;
import com.app.streaming.service.AuthService;
import com.app.streaming.service.LogExceptionsService;
import com.app.streaming.service.StreamingService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

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

    @GetMapping("/video_file/{guid}/{filename}")
    public ResponseEntity<Resource> getVideoFile(@PathVariable String guid, @PathVariable String filename) {
        try {
            String fullPath = "D:\\Streaming-App-Data\\Streaming-App-Resized-Video\\" + guid + "\\1080p\\" + filename;
            File file = new File(fullPath);

            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }

            UrlResource resource = new UrlResource(file.toURI());

            return ResponseEntity.ok()
                    .contentType(MediaTypeFactory.getMediaType(file.getName())
                            .orElse(MediaType.APPLICATION_OCTET_STREAM))
                    .contentLength(file.length())
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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
