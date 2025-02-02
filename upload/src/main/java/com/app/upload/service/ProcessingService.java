package com.app.upload.service;

import com.app.upload.common.Util;
import com.app.upload.entity.TLogExceptions;
import com.app.upload.environment.Environment;
import com.app.upload.model.JwtUserDetails;
import com.app.upload.python.PythonInvoker;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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


    public boolean encodeIntoMultipleResolutions(String VIDEO_GUID, String originalFilePath, String outputFileName, String sourceResolution, String resolution, JwtUserDetails userDetails) throws IOException, InterruptedException {
        try {
            String OUTPUT_DIR = environment.getEncodedVideoPath() + util.getUserSpecifiedFolder(userDetails, VIDEO_GUID) + File.separator + resolution;
            Files.createDirectories(Paths.get(OUTPUT_DIR));

            Path outputFilePath = Paths.get(OUTPUT_DIR, outputFileName);

            int targetHeight = Integer.parseInt(resolution.replace("p", ""));
            int sourceHeight = Integer.parseInt(sourceResolution.split("x")[1]);
            int sourceWidth = Integer.parseInt(sourceResolution.split("x")[0]);

            long sourceBitrate = Math.max(sourceHeight * sourceWidth * 70L / 1000, 1000L);
            long targetBitrate = Math.max(sourceBitrate * targetHeight / sourceHeight, 500L);

            String ffmpegPath = environment.getFfmpegPath();

            ProcessBuilder processBuilder = new ProcessBuilder(
                    ffmpegPath,
                    "-i", originalFilePath,
                    "-vf", "scale=-2:" + targetHeight + "" +
                    ",format=yuv420p",
                    "-c:v", "libx264",
                    "-b:v", targetBitrate + "k",
                    "-maxrate", targetBitrate + "k",
                    "-bufsize", (targetBitrate * 2) + "k",
                    "-preset", "fast",
                    "-crf", "23",
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

            try {
                String pythonScriptPath = environment.getPythonScriptPath();
                String videoFilePath = outputFilePath.toString();
                String outputFolderPath = OUTPUT_DIR + File.separator + "Chunks";

                return pythonInvoker.runPythonScript(userDetails, pythonScriptPath, videoFilePath, outputFolderPath);
            } catch (Exception e) {
                log(userDetails.getT_mst_user_id(),"createResolutionCopy()",e.getMessage());
            }

            return false;
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
