package com.app.upload.service;

import com.app.upload.common.Util;
import com.app.upload.entity.TLogExceptions;
import com.app.upload.entity.TVideoInfo;
import com.app.upload.environment.Environment;
import com.app.upload.model.JwtUserDetails;
import com.app.upload.repository.TLogExceptionsRepository;
import com.app.upload.repository.TVideoInfoRepository;
import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Component
public class UploadService {
    private Environment environment;
    private Util util;
    @Autowired
    private LogExceptionsService logExceptionsService;
    @Autowired
    private TVideoInfoRepository tVideoInfoRepository;
    private ProcessingService processingService;

    public UploadService(){
        this.environment = new Environment();
        this.util = new Util();
        this.processingService = new ProcessingService();
    }


    public boolean uploadAndProcessVideo(MultipartFile file, String fileId, JwtUserDetails userDetails) {
        if (file.isEmpty()) {
            return false;
        }

        try {
            String VIDEO_GUID = util.getrandomGUID();
            String ORIGINAL_FILE_DIR = environment.getOriginalVideoPath() + util.getUserSpecifiedFolder(userDetails,VIDEO_GUID);
            Files.createDirectories(Paths.get(ORIGINAL_FILE_DIR));

            long fileSize = file.getSize();
            String originalFilenameWithoutExtension = util.getFileNameWithoutExtension(file);
            String fileExtension = util.getFileExtension(file);
            String originalFilename = file.getOriginalFilename();

            originalFilename = originalFilename.replace(" ","");
            originalFilenameWithoutExtension = originalFilenameWithoutExtension.replace(" ","");

            Path originalFilePath = Paths.get(ORIGINAL_FILE_DIR, originalFilename);
            Files.write(originalFilePath, file.getBytes());

            String sourceResolution = getVideoResolution(originalFilePath.toString(), userDetails);

            TVideoInfo tVideoInfo = new TVideoInfo(VIDEO_GUID, originalFilenameWithoutExtension, fileSize, fileExtension, sourceResolution, userDetails.getT_mst_user_id());

            if(saveVideoDetails(tVideoInfo)){
                List<String> resolutions = environment.getResolutions();
                List<String> validResolutions = getValidResolutions(sourceResolution, resolutions, userDetails);

                for (String resolution : validResolutions) {
                    if(!processingService.encodeIntoMultipleResolutions(VIDEO_GUID, originalFilePath.toString(), originalFilename, sourceResolution, resolution, userDetails)){
                        // Have to do something if any resolution fails to encode.
                        // Rollback the original file save
                    }
                }

                return true;
            }else{
                // Rollback the original file save

                return false;
            }
        } catch (Exception e) {
            log(userDetails.getT_mst_user_id(),"uploadAndProcessVideo()",e.getMessage());
            return false;
        }
    }



    private String getVideoResolution(String filePath, JwtUserDetails userDetails) throws Exception {
        try {
            String ffprobePath = environment.getFfprobePath();
            ProcessBuilder processBuilder = new ProcessBuilder(ffprobePath, "-v", "error", "-select_streams", "v:0",
                    "-show_entries", "stream=width,height", "-of", "csv=s=x:p=0", filePath);
            Process process = processBuilder.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                return reader.readLine();
            }
        } catch (Exception e) {
            log(userDetails.getT_mst_user_id(),"getVideoResolution()",e.getMessage());
            return null;
        }
    }

    private List<String> getValidResolutions(String sourceResolution, List<String> resolutions, JwtUserDetails userDetails) {
        try {
            int sourceHeight = Integer.parseInt(sourceResolution.split("x")[1]);
            Map<String, Integer> resolutionHeightMap = environment.getResolutionHeightMap();

            return resolutions.stream().filter(res -> resolutionHeightMap.get(res) <= sourceHeight).collect(Collectors.toList());
        } catch (Exception e) {
            log(userDetails.getT_mst_user_id(),"getValidResolutions()",e.getMessage());
            return null;
        }
    }

    public boolean saveVideoDetails(TVideoInfo video) {
        try {
            tVideoInfoRepository.save(video);
            return true;
        } catch (Exception e) {
            log(video.getT_mst_user_id(),"saveVideoDetails()",e.getMessage());
            return false;
        }
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