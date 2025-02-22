package com.app.dashboard.controller;

import com.app.authentication.common.CommonReturn;
import com.app.dashboard.entity.TLogExceptions;
import com.app.dashboard.environment.Environment;
import com.app.dashboard.model.JwtUserDetails;
import com.app.dashboard.service.AuthService;
import com.app.dashboard.service.LogExceptionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {
    @Autowired
    public AuthService authService;
    @Autowired
    private LogExceptionsService logExceptionsService;

    public Environment environment;

    public DashboardController(){
        this.environment = new Environment();
    }

    @GetMapping("/menu")
    public CommonReturn<JwtUserDetails> get_layout_menu(){
        JwtUserDetails post_validated_request = authService.getAuthenticatedUserFromContext();
        return CommonReturn.success("Baler menu le bara",post_validated_request);
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
