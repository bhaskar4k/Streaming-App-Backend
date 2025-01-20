package com.app.upload.service;

import com.app.upload.entity.TLogExceptions;
import com.app.upload.environment.Environment;
import com.app.upload.model.JwtUserDetails;
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
import java.util.stream.Collectors;

@Service
@Component
public class UploadService {
    private Environment environment;
    @Autowired
    private LogExceptionsService logExceptionsService;

    public UploadService(){
        this.environment = new Environment();
    }

    public boolean uploadAndProcessVideo(MultipartFile file, JwtUserDetails userDetails) {
        if (file.isEmpty()) {
            return false;
        }

        try {
            String ORIGINAL_FILE = environment.getOriginalVideoPath();
            String OUTPUT_DIR = environment.getEncodedVideoPath();

            Files.createDirectories(Paths.get(ORIGINAL_FILE));
            Files.createDirectories(Paths.get(OUTPUT_DIR));

            Path tempFile = Paths.get(ORIGINAL_FILE, "uploaded_" + file.getOriginalFilename());
            Files.write(tempFile, file.getBytes());

            String sourceResolution = getVideoResolution(tempFile.toString(), userDetails);

            List<String> resolutions = environment.getResolutions();
            List<String> validResolutions = getValidResolutions(sourceResolution, resolutions, userDetails);

            for (String resolution : validResolutions) {
                if(!createResolutionCopy(tempFile.toString(), sourceResolution, resolution, OUTPUT_DIR, userDetails)){
                    // Have to do something if any chunk fails to encode.
                }
            }

            return true;
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

    private boolean createResolutionCopy(String filePath, String sourceResolution, String resolution, String outputDir, JwtUserDetails userDetails) throws IOException, InterruptedException {
        try {
            String outputFileName = "output_" + resolution + ".mp4";
            Path outputFilePath = Paths.get(outputDir, outputFileName);

            Files.createDirectories(Paths.get(outputDir));

            int targetHeight = Integer.parseInt(resolution.replace("p", ""));

            int sourceHeight = Integer.parseInt(sourceResolution.split("x")[1]);
            int sourceWidth = Integer.parseInt(sourceResolution.split("x")[0]);

            long sourceBitrate = Math.max(sourceHeight * sourceWidth * 70L / 1000, 1000L);

            long targetBitrate = Math.max(sourceBitrate * targetHeight / sourceHeight, 500L);

            String ffmpegPath = environment.getFfmpegPath();

            ProcessBuilder processBuilder = new ProcessBuilder(
                    ffmpegPath, "-i", filePath,
                    "-vf", "scale=-2:" + targetHeight + ",format=yuv420p",
                    "-c:v", "libx264", "-b:v", targetBitrate + "k", "-maxrate", targetBitrate + "k", "-bufsize", (targetBitrate * 2) + "k",
                    "-preset", "fast", "-crf", "23",
                    outputFilePath.toString()
            );

            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                log(userDetails.getT_mst_user_id(),"createResolutionCopy()","Process wait error.");
                return false;
            }

            return true;
        } catch (Exception e) {
            log(userDetails.getT_mst_user_id(),"createResolutionCopy()",e.getMessage());
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
