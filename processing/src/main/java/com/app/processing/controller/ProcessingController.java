package com.app.processing.controller;


import com.app.processing.common.CommonReturn;
import com.app.processing.job.ProcessVideoJob;
import com.app.processing.model.Video;
import com.app.processing.service.ProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/processing")
public class ProcessingController {
    @Autowired
    private ProcessingService processingService;
    @Autowired
    private ProcessVideoJob processVideoJob;

    @GetMapping("/process_video")
    public CommonReturn<Boolean> process_video() {
        try {
            processVideoJob.startPolling();
            return CommonReturn.success("Ok",true);
        } catch (Exception e) {
            return CommonReturn.error(400,"Internal Server Error.");
        }
    }
}
