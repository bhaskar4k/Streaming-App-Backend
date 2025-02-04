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
        return File.separator + "UserID-" + userDetails.getT_mst_user_id() + File.separator + uniqueID;
    }

    public String getFileNameWithoutExtension(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) {
            return originalFilename;
        }
        return originalFilename.substring(0, originalFilename.lastIndexOf('.'));
    }

    public String getFileExtension(String originalFilename) {
        String fileExtension = "";
        if (originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        }

        return fileExtension;
    }
}