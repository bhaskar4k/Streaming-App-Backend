package com.app.streaming.common;

import java.io.File;
import java.util.UUID;

public class Util {
    public String getrandomGUID(){
        return UUID.randomUUID().toString();
    }

    public String getUserSpecifiedFolder(String uniqueID){
        return File.separator + uniqueID;
    }

    public String getUserSpecifiedFolderForThumbnail(String uniqueID){
        return File.separator + uniqueID;
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