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
            boolean ok = processingService.encodeVideo(video);

            if(ok){
                return CommonReturn.success("Video Processed Successfully",true);
            }else{
                return CommonReturn.error(400,"Error In Video Processing.");
            }
        } catch (Exception e) {
            return CommonReturn.error(400,"Internal Server Error.");
        }
    }
}
