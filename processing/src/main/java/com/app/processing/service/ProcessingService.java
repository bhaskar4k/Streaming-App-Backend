package com.app.processing.service;

import com.app.processing.common.CommonReturn;
import com.app.processing.common.Util;
import com.app.processing.entity.TLogExceptions;
import com.app.processing.environment.Environment;
import com.app.processing.model.Video;
import com.app.processing.python.PythonInvoker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Service
public class ProcessingService {
    private Environment environment;
    private Util util;
    @Autowired
    private LogExceptionsService logExceptionsService;
    private final PythonInvoker pythonInvoker;

    public ProcessingService(){
        this.environment = new Environment();
        this.util = new Util();
        this.pythonInvoker = new PythonInvoker();
    }


    public boolean encodeVideo(Video video) throws Exception {
        String sourceResolution = getVideoResolution(video.getOriginalFilePath(), video.getTMstUserId());

        List<String> resolutions = environment.getResolutions();
        List<String> validResolutions = getValidResolutions(sourceResolution, resolutions, video.getTMstUserId());

        for (String resolution : validResolutions) {
            if(!encodeIntoMultipleResolutions(video, sourceResolution, resolution)){
                // Have to do something if any resolution fails to encode.
                // Rollback the original file save
                return false;
            }
        }

        return true;
    }

    private boolean encodeIntoMultipleResolutions(Video video, String sourceResolution, String resolution) {
        try {
            String ENCODED_VIDEO_DIR = environment.getEncodedVideoPath() + util.getUserSpecifiedFolder(video.getTMstUserId(), video.getVIDEO_GUID()) + File.separator + resolution + File.separator + "EncodedVideo";
            Files.createDirectories(Paths.get(ENCODED_VIDEO_DIR));

            Path encodedOutputFilePath = Paths.get(ENCODED_VIDEO_DIR, video.getOriginalFileName());

            int targetHeight = Integer.parseInt(resolution.replace("p", ""));
            int sourceHeight = Integer.parseInt(sourceResolution.split("x")[1]);
            int sourceWidth = Integer.parseInt(sourceResolution.split("x")[0]);

            long sourceBitrate = Math.max(sourceHeight * sourceWidth * 70L / 1000, 1000L);
            long targetBitrate = Math.max(sourceBitrate * targetHeight / sourceHeight, 500L);

            String ffmpegPath = environment.getFfmpegPath();

            ProcessBuilder processBuilder = new ProcessBuilder(
                    ffmpegPath,
                    "-i", video.getOriginalFilePath(),
                    "-vf", "scale=-2:" + targetHeight + "" +
                    ",format=yuv420p",
                    "-c:v", "libx264",
                    "-b:v", targetBitrate + "k",
                    "-maxrate", targetBitrate + "k",
                    "-bufsize", (targetBitrate * 2) + "k",
                    "-preset", "fast",
                    "-crf", "23",
                    encodedOutputFilePath.toString()
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
                log(video.getTMstUserId(), "createResolutionCopy()","Process wait error.");
                return false;
            }

            try {
                String pythonScriptPath = environment.getPythonScriptPath();
                String ENCODED_SPLITTED_VIDEO_DIR = environment.getEncodedVideoPath() + util.getUserSpecifiedFolder(video.getTMstUserId(), video.getVIDEO_GUID()) + File.separator + resolution;

                boolean is_chunked = pythonInvoker.runPythonScript(video.getTMstUserId(), pythonScriptPath, encodedOutputFilePath.toString(), ENCODED_SPLITTED_VIDEO_DIR);

                if(is_chunked){
                    File videoObj = new File(encodedOutputFilePath.toString());
                    videoObj.delete();
                    File folderObj = new File(ENCODED_VIDEO_DIR);
                    folderObj.delete();
                }

                return is_chunked;
            } catch (Exception e) {
                log(video.getTMstUserId(),"createResolutionCopy()",e.getMessage());
            }

            return false;
        } catch (Exception e) {
            log(video.getTMstUserId(),"createResolutionCopy()",e.getMessage());
            return false;
        }
    }

    private String getVideoResolution(String filePath, long t_mst_user_id){
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
            log(t_mst_user_id,"getVideoResolution()",e.getMessage());
            return null;
        }
    }

    private List<String> getValidResolutions(String sourceResolution, List<String> resolutions, long t_mst_user_id) {
        try {
            int sourceHeight = Integer.parseInt(sourceResolution.split("x")[1]);
            Map<String, Integer> resolutionHeightMap = environment.getResolutionHeightMap();

            return resolutions.stream().filter(res -> resolutionHeightMap.get(res) <= sourceHeight).collect(Collectors.toList());
        } catch (Exception e) {
            log(t_mst_user_id,"getValidResolutions()",e.getMessage());
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
