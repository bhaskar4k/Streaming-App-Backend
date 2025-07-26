package com.app.streaming.controller;

import com.app.streaming.common.CommonReturn;
import com.app.streaming.entity.TLogExceptions;
import com.app.streaming.environment.Environment;
import com.app.streaming.model.JwtUserDetails;
import com.app.streaming.model.VideoInformation;
import com.app.streaming.service.AuthService;
import com.app.streaming.service.LogExceptionsService;
import com.app.streaming.service.StreamingService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/streaming")
public class StreamingController {
    @Autowired
    private LogExceptionsService logExceptionsService;
    @Autowired
    private AuthService authService;
    @Autowired
    private StreamingService streamingService;
    private Environment environment;

    public StreamingController(){
        this.environment = new Environment();
    }

    @GetMapping("/get_video_information_for_streaming/{guid}")
    public CommonReturn<VideoInformation> get_video_information(@PathVariable String guid){
        JwtUserDetails post_validated_request = authService.getAuthenticatedUserFromContext();

        try{
            VideoInformation info = streamingService.do_get_video_information(guid, post_validated_request);

            return CommonReturn.success("Video information fetched", info);
        } catch (Exception e) {
            log("restore_video()",e.getMessage());
            return CommonReturn.error(400,"Internal Server Error.");
        }
    }

    @GetMapping("/get_video_file_chunks_in_batch/{guid}/{index}")
    public ResponseEntity<byte[]> get_video_file_chunks_in_batch(@PathVariable String guid, @PathVariable int index) {
        try {
            Path videoDir = Paths.get(environment.getEncodedVideoPath(), guid, "1440p");

            // Collect input file names
            List<String> inputFiles = new ArrayList<>();
            for (int i = index; i < index + 3; i++) {
                Path chunk = videoDir.resolve(i + ".mp4");
                if (Files.exists(chunk)) {
                    inputFiles.add(i + ".mp4");
                }
            }

            if (inputFiles.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            // Build FFmpeg command
            List<String> cmd = new ArrayList<>();
            cmd.add(environment.getFfmpegPath());
            for (String file : inputFiles) {
                cmd.add("-i");
                cmd.add(file);
            }

            StringBuilder filter = new StringBuilder();
            for (int i = 0; i < inputFiles.size(); i++) {
                filter.append("[").append(i).append(":v:0][").append(i).append(":a:0]");
            }
            filter.append("concat=n=").append(inputFiles.size()).append(":v=1:a=1[outv][outa]");

            cmd.add("-filter_complex");
            cmd.add(filter.toString());
            cmd.add("-map");
            cmd.add("[outv]");
            cmd.add("-map");
            cmd.add("[outa]");
            cmd.add("-f");
            cmd.add("mp4");
            cmd.add("-movflags");
            cmd.add("+faststart");
            cmd.add("merged_output.mp4");

            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.directory(videoDir.toFile());
            pb.redirectErrorStream(true);
            Process process = pb.start();

            // Optional: log FFmpeg output
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("FFmpeg: " + line);
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

            Path outputPath = videoDir.resolve("merged_output.mp4");
            if (!Files.exists(outputPath)) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

            byte[] fileBytes = Files.readAllBytes(outputPath);
            Files.deleteIfExists(outputPath); // cleanup

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"merged.mp4\"")
                    .body(fileBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    private void log(String function_name, String exception_msg) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();

        String full_class_path = stackTraceElements[2].getClassName();
        String class_name = full_class_path.substring(full_class_path.lastIndexOf(".") + 1);

        String full_package_path = full_class_path.substring(0, full_class_path.lastIndexOf("."));
        String package_name = full_package_path.substring(full_package_path.lastIndexOf(".") + 1);

        logExceptionsService.saveLogException(new TLogExceptions(package_name, class_name, function_name, exception_msg, 0L));
    }
}
