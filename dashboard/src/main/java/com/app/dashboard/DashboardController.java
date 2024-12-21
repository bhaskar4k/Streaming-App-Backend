package com.app.dashboard;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RestController
public class DashboardController {

    @GetMapping("/dashboard/temp")
    public String temp(){
        return "Dashboard";
    }
}
