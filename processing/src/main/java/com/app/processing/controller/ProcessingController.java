package com.app.processing.controller;


import com.app.processing.common.CommonReturn;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/processing")
public class ProcessingController {
    @PostMapping("/crate_multiple_resolutions_of_video")
    public CommonReturn<Boolean> crate_multiple_resolutions_of_video() {
        try {
            return CommonReturn.error(400,"Video process done.");
        } catch (Exception e) {
            return CommonReturn.error(400,"Internal Server Error.");
        }
    }
}
