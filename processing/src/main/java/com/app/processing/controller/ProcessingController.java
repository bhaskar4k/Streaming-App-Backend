package com.app.processing.controller;


import com.app.processing.common.CommonReturn;
import com.app.processing.model.Video;
import com.app.processing.service.ProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/processing")
public class ProcessingController {
    @Autowired
    private ProcessingService processingService;

    @PostMapping("/process_video")
    public CommonReturn<Boolean> process_video(@RequestBody Video video) {
        try {
            boolean value = processingService.encodeVideo(video);
            return CommonReturn.success("OK",value);
        } catch (Exception e) {
            return CommonReturn.error(400,"Internal Server Error.");
        }
    }
}
