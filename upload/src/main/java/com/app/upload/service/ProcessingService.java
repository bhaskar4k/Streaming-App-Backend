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


    public boolean encodeIntoMultipleResolutions(String VIDEO_GUID, String filePath, String originalFilename, String sourceResolution, String resolution, JwtUserDetails userDetails) throws IOException, InterruptedException {
        try {
            String OUTPUT_DIR = environment.getEncodedVideoPath() + util.getUserSpecifiedFolder(userDetails, VIDEO_GUID) + File.separator + resolution;
            Files.createDirectories(Paths.get(OUTPUT_DIR));

            String outputFileName = originalFilename + ".mp4";
            Path outputFilePath = Paths.get(OUTPUT_DIR, outputFileName);

            int targetHeight = Integer.parseInt(resolution.replace("p", ""));
            int sourceHeight = Integer.parseInt(sourceResolution.split("x")[1]);
            int sourceWidth = Integer.parseInt(sourceResolution.split("x")[0]);

            long sourceBitrate = Math.max(sourceHeight * sourceWidth * 70L / 1000, 1000L);
            long targetBitrate = Math.max(sourceBitrate * targetHeight / sourceHeight, 500L);

            String ffmpegPath = environment.getFfmpegPath();

            ProcessBuilder processBuilder = new ProcessBuilder(
                    ffmpegPath,
                    "-i", filePath,
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
                String pythonScriptPath = "E:\\Project\\Streaming-App\\upload\\src\\main\\java\\com\\app\\upload\\python\\VideoSplitter.py";
                String videoFilePath = "E:\\Project\\Vid\\Amkash.mp4";
                String outputFolderPath = "E:\\Project\\Vid\\JOD";                 // Chunk duration in seconds

                pythonInvoker.runPythonScript(pythonScriptPath, videoFilePath, outputFolderPath);

//                if(splitFile(originalFilename,resolution,outputFilePath.toString(), OUTPUT_DIR + File.separator + "Chunks", userDetails)){
//                    return true;
//                }else{
//                    return false;
//                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        } catch (Exception e) {
            log(userDetails.getT_mst_user_id(),"createResolutionCopy()",e.getMessage());
            return false;
        }
    }

    public boolean splitFile(String originalFilename, String resolution, String inputFilePath, String outputDirPath, JwtUserDetails userDetails) throws IOException {
        try {
            File outputDir = new File(outputDirPath);
            if (!outputDir.exists() && !outputDir.mkdirs()) {
                throw new IOException("Failed to create output directory: " + outputDirPath);
            }

            String[] probeDurationCommand = {
                    environment.getFfprobePath(),
                    "-v", "error",
                    "-show_entries", "format=duration",
                    "-of", "default=noprint_wrappers=1:nokey=1",
                    inputFilePath
            };

            ProcessBuilder probeBuilder = new ProcessBuilder(probeDurationCommand);
            probeBuilder.redirectErrorStream(true);
            Process probeProcess = probeBuilder.start();

            String duration;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(probeProcess.getInputStream()))) {
                duration = reader.readLine().trim();
            }

            int exitCode = probeProcess.waitFor();
            if (exitCode != 0) {
                throw new IOException("FFprobe process failed to get video duration");
            }

            double totalDuration = Double.parseDouble(duration);
            int chunkSize = 5;
            int totalChunks = (int) Math.ceil(totalDuration / chunkSize);

            String outputFileName = outputDirPath + File.separator + "%06d.mp4";

            String[] command = {
                    environment.getFfmpegPath(),
                    "-i", inputFilePath,
                    "-c", "copy",
                    "-map", "0",
                    "-f", "segment",
                    "-segment_time", "5",
                    "-segment_start_number", "0",
                    "-reset_timestamps", "1",
                    outputFileName
            };

            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

            try {
                exitCode = process.waitFor();
                if (exitCode != 0) {
                    throw new IOException("FFmpeg process failed with exit code " + exitCode);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException("FFmpeg process was interrupted", e);
            }

            System.out.println("Video splitting completed. Total chunks: " + totalChunks);
            return true;
        } catch (Exception e) {
            log(userDetails.getT_mst_user_id(), "splitFile()", e.getMessage());
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
