package com.app.middleware.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
public class MiddlewareController {

    @GetMapping("/hi")
    public String hifunction(){
        return "Tora Maik Chodo";
    }
}
