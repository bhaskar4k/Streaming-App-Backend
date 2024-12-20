package com.app.upload;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RestController
public class UploadController {

    @GetMapping("/upload/temp")
    public String temp(){
        return "Upload";
    }
}
