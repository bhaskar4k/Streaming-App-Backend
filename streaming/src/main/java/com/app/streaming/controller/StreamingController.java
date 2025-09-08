package com.app.streaming.controller;

import com.app.streaming.common.CommonReturn;
import com.app.streaming.entity.TLogExceptions;
import com.app.streaming.environment.Environment;
import com.app.streaming.model.JwtUserDetails;
import com.app.streaming.model.VideoInformation;
import com.app.streaming.service.LogExceptionsService;
import com.app.streaming.service.StreamingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/streaming")
public class StreamingController {
    @Autowired
    private LogExceptionsService logExceptionsService;
    @Autowired
    private StreamingService streamingService;
    private Environment environment;

    public StreamingController(){
        this.environment = new Environment();
    }

    @GetMapping("/get_video_information_for_streaming/{guid}")
    public CommonReturn<VideoInformation> get_video_information(@PathVariable String guid){
//        JwtUserDetails post_validated_request = authService.getAuthenticatedUserFromContext();
        JwtUserDetails post_validated_request = null;

        try{
            VideoInformation info = streamingService.do_get_video_information(guid, post_validated_request);

            return CommonReturn.success("Video information fetched", info);
        } catch (Exception e) {
            log("restore_video()",e.getMessage());
            return CommonReturn.error(400,"Internal Server Error.");
        }
    }

    @GetMapping("/get_video_file_chunks_in_batch/{guid}/{index}")
    public ResponseEntity<byte[]> get_video_file_chunks_in_batch(@PathVariable String guid, @PathVariable int index) {
        try {
            Path path = Paths.get(environment.getEncodedVideoPath(), guid, "1440p", index + ".mp4");
            byte[] chunk = Files.readAllBytes(path);

            return ResponseEntity.ok().body(chunk);
        } catch (Exception e) {
            log("get_video_file_chunks_in_batch()",e.getMessage());
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
