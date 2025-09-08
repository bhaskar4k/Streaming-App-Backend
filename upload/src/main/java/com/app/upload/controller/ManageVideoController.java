package com.app.upload.controller;

import com.app.upload.common.CommonReturn;
import com.app.upload.entity.TLogExceptions;
import com.app.upload.model.JwtUserDetails;
import com.app.upload.model.ManageVideoDetails;
import com.app.upload.service.LogExceptionsService;
import com.app.upload.service.ManageVideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/manage_video")
public class ManageVideoController extends BaseController {
    @Autowired
    private LogExceptionsService logExceptionsService;
    @Autowired
    private ManageVideoService manageVideoService;

    public ManageVideoController(){
    }


    @GetMapping("/get_uploaded_video_list")
    public CommonReturn<List<ManageVideoDetails>> get_uploaded_video_list(){
        JwtUserDetails post_validated_request = getJwtUserDetails();

        try {
            List<ManageVideoDetails> details = manageVideoService.do_get_uploaded_video_list(post_validated_request);

            return CommonReturn.success("Video details have been fetched.", details);
        } catch (Exception e) {
            log("get_uploaded_video_list()",e.getMessage());
            return CommonReturn.error(400,"Internal Server Error.");
        }
    }

    @GetMapping("/download_video/{guid:.+}")
    public ResponseEntity<Resource> download_video(@PathVariable String guid){
        JwtUserDetails post_validated_request = getJwtUserDetails();

        try {
            return manageVideoService.do_download_video(guid,post_validated_request);
        } catch (Exception e) {
            log("download_video()",e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/delete_video")
    public CommonReturn<Boolean> delete_video(@RequestBody Map<String, Long> requestBody){
        JwtUserDetails post_validated_request = getJwtUserDetails();

        try {
            Long t_video_info_id = requestBody.get("t_video_info_id");
            Boolean res = manageVideoService.do_delete_video(t_video_info_id, post_validated_request);
            if(res) return CommonReturn.success("Video has been deleted successfully.", true);

            return CommonReturn.error(400,"Failed to delete video.");
        } catch (Exception e) {
            log("delete_video()",e.getMessage());
            return CommonReturn.error(400,"Internal Server Error.");
        }
    }

    @GetMapping("/get_deleted_video_list")
    public CommonReturn<List<ManageVideoDetails>> get_deleted_video_list(){
        JwtUserDetails post_validated_request = getJwtUserDetails();

        try {
            List<ManageVideoDetails> details = manageVideoService.do_get_deleted_video_list(post_validated_request);

            return CommonReturn.success("Video details have been fetched.", details);
        } catch (Exception e) {
            log("get_uploaded_video_list()",e.getMessage());
            return CommonReturn.error(400,"Internal Server Error.");
        }
    }

    @PostMapping("/restore_video")
    public CommonReturn<Boolean> restore_video(@RequestBody Map<String, Long> requestBody){
        JwtUserDetails post_validated_request = getJwtUserDetails();

        try {
            Long t_video_info_id = requestBody.get("t_video_info_id");
            Boolean res = manageVideoService.do_restore_video(t_video_info_id, post_validated_request);
            if(res) return CommonReturn.success("Video has been restored successfully.", true);

            return CommonReturn.error(400,"Failed to restore video.");
        } catch (Exception e) {
            log("restore_video()",e.getMessage());
            return CommonReturn.error(400,"Internal Server Error.");
        }
    }

    @GetMapping("/get_a_single_video_info/{guid:.+}")
    public CommonReturn<ManageVideoDetails> get_a_single_video_info(@PathVariable String guid){
        JwtUserDetails post_validated_request = getJwtUserDetails();

        try {
            ManageVideoDetails data = manageVideoService.do_get_a_single_video_info(post_validated_request, guid);

            return CommonReturn.success("Video details have been fetched.", data);
        } catch (Exception e) {
            log("get_a_single_video_info()",e.getMessage());
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
