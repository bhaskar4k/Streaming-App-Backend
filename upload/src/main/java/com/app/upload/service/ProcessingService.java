package com.app.upload.service;

import com.app.upload.entity.TLogExceptions;
import com.app.upload.environment.Environment;
import com.app.upload.model.JwtUserDetails;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ProcessingService {
    private Environment environment;
    @Autowired
    private LogExceptionsService logExceptionsService;
    private int CHUNK_SIZE;

    public ProcessingService(){
        this.environment = new Environment();
        this.CHUNK_SIZE = environment.getChunkSize();
    }

    public boolean encodeIntoMultipleResolutions(String filePath, String originalFilename, String sourceResolution, String resolution, String outputDir, JwtUserDetails userDetails) throws IOException, InterruptedException {
        try {
            outputDir += "/"+resolution;
            String outputFileName = originalFilename + "_" + resolution + ".mp4";

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

            try {
                if(splitFile(originalFilename,resolution,outputFilePath.toString(), outputDir+"/Chunks", userDetails)){
                    return true;
                }else{
                    return false;
                }
            } catch (IOException e) {
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

            int targetBitrateKbps = getBitrateInKbps(inputFilePath, userDetails);
            int chunkDurationSeconds = (5 * 1024 * 8) / targetBitrateKbps;

            // FFmpeg command
            String outputFilePattern = outputDirPath + File.separator + "chunk_%03d_" + originalFilename + "_" + resolution + ".mp4";
            String[] command = {
                    environment.getFfmpegPath(),
                    "-i", inputFilePath,
                    "-c", "copy",
                    "-map", "0",
                    "-f", "segment",
                    "-segment_time", String.valueOf(chunkDurationSeconds),
                    "-reset_timestamps", "1",
                    outputFilePattern
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
                int exitCode = process.waitFor();
                if (exitCode != 0) {
                    throw new IOException("FFmpeg process failed with exit code " + exitCode);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException("FFmpeg process was interrupted", e);
            }

            System.out.println("Video splitting completed.");
            return true;
        } catch (Exception e) {
            log(userDetails.getT_mst_user_id(),"splitFile()",e.getMessage());
            return false;
        }
    }

    private Integer getBitrateInKbps(String inputFilePath, JwtUserDetails userDetails) throws IOException {
        try {
            String[] bitrateCommand = {
                    environment.getFfmpegPath(),
                    "-i", inputFilePath,
                    "-hide_banner"
            };
            ProcessBuilder processBuilder = new ProcessBuilder(bitrateCommand);
            Process process = processBuilder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("bitrate:")) {
                        String[] parts = line.split("bitrate:");
                        String bitratePart = parts[1].trim().split(" ")[0];
                        return Integer.parseInt(bitratePart.replace("kb/s", "").trim());
                    }
                }
            }

            throw new IOException("Failed to determine video bitrate.");
        } catch (Exception e) {
            log(userDetails.getT_mst_user_id(),"getBitrateInKbps()",e.getMessage());
            return 0;
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
