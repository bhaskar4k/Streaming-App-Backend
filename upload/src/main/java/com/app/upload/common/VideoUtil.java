package com.app.upload.common;

import com.app.upload.entity.TLogExceptions;
import com.app.upload.environment.Environment;
import com.app.upload.model.JwtUserDetails;
import com.app.upload.service.LogExceptionsService;
import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Component
public class VideoUtil {
    private Environment environment;
    @Autowired
    private LogExceptionsService logExceptionsService;

    public VideoUtil(){
        this.environment = new Environment();
    }


    public String getVideoResolution(String filePath, JwtUserDetails userDetails){
        try {
            String ffprobePath = environment.getFfprobePath();
            ProcessBuilder processBuilder = new ProcessBuilder(ffprobePath, "-v", "error", "-select_streams", "v:0",
                    "-show_entries", "stream=width,height", "-of", "csv=s=x:p=0", filePath);
            Process process = processBuilder.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String res = reader.readLine();
                int size=res.length();

                if(res.charAt(size-1)=='x'){
                    res = res.substring(0, size-1);
                }

                return res;
            }
        } catch (Exception e) {
            log(userDetails.getT_mst_user_id(),"getVideoResolution()",e.getMessage());
            return null;
        }
    }

    public List<String> getValidResolutions(String sourceResolution, List<String> resolutions, long t_mst_user_id) {
        try {
            int sourceHeight = Integer.parseInt(sourceResolution.split("x")[1]);
            Map<String, Integer> resolutionHeightMap = environment.getResolutionHeightMap();

            return resolutions.stream().filter(res -> resolutionHeightMap.get(res) <= sourceHeight).collect(Collectors.toList());
        } catch (Exception e) {
            log(t_mst_user_id,"getValidResolutions()",e.getMessage());
            return null;
        }
    }

    public Double getVideoDuration(String filePath, JwtUserDetails userDetails) {
        try {
            String ffprobePath = environment.getFfprobePath();
            ProcessBuilder processBuilder = new ProcessBuilder(
                    ffprobePath, "-i",
                    filePath, "-show_entries",
                    "format=duration", "-v",
                    "quiet", "-of", "csv=p=0"
            );
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String duration = reader.readLine();
            process.waitFor();
            return duration != null ? Double.parseDouble(duration) : 0.0;
        } catch (Exception e) {
            log(userDetails.getT_mst_user_id(),"getVideoResolution()",e.getMessage());
            return null;
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
