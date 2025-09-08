package com.app.dashboard.controller;

import com.app.dashboard.common.CommonReturn;
import com.app.dashboard.entity.TLogExceptions;
import com.app.dashboard.environment.Environment;
import com.app.dashboard.model.JwtUserDetails;
import com.app.dashboard.model.Layout;
import com.app.dashboard.service.DashboardService;
import com.app.dashboard.service.LogExceptionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/dashboard")
public class DashboardController extends BaseController {
    @Autowired
    private LogExceptionsService logExceptionsService;
    @Autowired
    private DashboardService dashboardService;

    public Environment environment;

    public DashboardController(){
        this.environment = new Environment();
    }

    @GetMapping("/menu")
    public CommonReturn<List<Layout>> get_layout_menu(){
        try {
            JwtUserDetails post_validated_request = getJwtUserDetails();
            List<Layout> menus = dashboardService.getLayoutMenu(post_validated_request);

            if(menus!=null){
                return CommonReturn.success("Layout menu has fetched successfully for the user", menus);
            }
            return CommonReturn.error(400,"Internal Server Error.");
        } catch (Exception e) {
            log("validateToken()",e.getMessage());
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
