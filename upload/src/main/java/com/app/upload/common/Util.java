package com.app.upload.common;

import com.app.upload.entity.TLogExceptions;
import com.app.upload.model.JwtUserDetails;
import com.app.upload.service.LogExceptionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

public class Util {
    @Autowired
    private LogExceptionsService logExceptionsService;


    public String getrandomGUID(){
        return UUID.randomUUID().toString();
    }

    public String getUserSpecifiedFolder(JwtUserDetails userDetails, String uniqueID){
        try {
            return File.separator + "UserID-" + userDetails.getT_mst_user_id() + File.separator + uniqueID;
        } catch (Exception e) {
            log(userDetails.getT_mst_user_id(),"getUserSpecifiedFolder()",e.getMessage());
            return null;
        }
    }

    public String getFileNameWithoutExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            return originalFilename;
        }
        return originalFilename.substring(0, originalFilename.lastIndexOf('.'));
    }

    public String getFileExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();

        String fileExtension = "";
        if (originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        }

        return fileExtension;
    }

    private void log(Long t_mst_user_id, String function_name, String exception_msg){
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();

        String full_class_path = stackTraceElements[2].getClassName();
        String class_name = full_class_path.substring(full_class_path.lastIndexOf(".") + 1);

        String full_package_path = full_class_path.substring(0, full_class_path.lastIndexOf("."));
        String package_name = full_package_path.substring(full_package_path.lastIndexOf(".") + 1);

        logExceptionsService.saveLogException(new TLogExceptions(package_name,class_name,function_name,exception_msg,t_mst_user_id));
    }
}
