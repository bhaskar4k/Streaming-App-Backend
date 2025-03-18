package com.app.upload.controller;

import com.app.upload.common.CommonReturn;
import com.app.upload.common.Util;
import com.app.upload.entity.TLogExceptions;
import com.app.upload.environment.Environment;
import com.app.upload.model.JwtUserDetails;
import com.app.upload.model.ManageVideoDetails;
import com.app.upload.service.AuthService;
import com.app.upload.service.LogExceptionsService;
import com.app.upload.service.ManageVideeService;
import com.app.upload.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/manage_video")
public class ManageVideoController {
    @Autowired
    public AuthService authService;
    @Autowired
    private LogExceptionsService logExceptionsService;
    @Autowired
    private ManageVideeService manageVideeService;

    public ManageVideoController(){
    }


    @GetMapping("/get_uploaded_video_list")
    public CommonReturn<List<ManageVideoDetails>> get_uploaded_video_list(){
        JwtUserDetails post_validated_request = authService.getAuthenticatedUserFromContext();

        try {
            List<ManageVideoDetails> details = manageVideeService.do_get_uploaded_video_list(post_validated_request);

            return CommonReturn.success("Video details have been fetched.", details);
        } catch (Exception e) {
            log("get_uploaded_video_list()",e.getMessage());
            return CommonReturn.error(400,"Internal Server Error.");
        }
    }

    @GetMapping("/download_video/{guid:.+}")
    public ResponseEntity<Resource> download_video(@PathVariable String guid){
        JwtUserDetails post_validated_request = authService.getAuthenticatedUserFromContext();

        try {
            return manageVideeService.do_download_video(guid,post_validated_request);
        } catch (Exception e) {
            log("download_video()",e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/delete_video")
    public CommonReturn<Boolean> delete_video(@RequestBody Map<String, Long> requestBody){
        JwtUserDetails post_validated_request = authService.getAuthenticatedUserFromContext();

        try {
            Long t_video_info_id = requestBody.get("t_video_info_id");
            Boolean res = manageVideeService.do_delete_video(t_video_info_id, post_validated_request);
            if(res) return CommonReturn.success("Video has been deleted successfully.", true);

            return CommonReturn.error(400,"Failed to delete video.");
        } catch (Exception e) {
            log("delete_video()",e.getMessage());
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
