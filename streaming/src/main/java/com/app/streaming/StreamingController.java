package com.app.streaming;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RestController
public class StreamingController {

    @GetMapping("/streaming/temp")
    public String temp(){
        return "Streaming";
    }
}
