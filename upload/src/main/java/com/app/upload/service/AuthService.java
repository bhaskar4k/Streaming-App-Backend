package com.app.upload.service;

import com.app.upload.common.CommonReturn;
import com.app.upload.environment.Environment;
import com.app.upload.model.JwtUserDetails;
import com.app.upload.model.TokenRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
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
public class AuthService {
    private Environment environment;

    public AuthService(){
        this.environment = new Environment();
    }

    public CommonReturn<JwtUserDetails> validateToken(String token) {
        String AUTH_SERVICE_URL = environment.getAuthServiceUrl();

        RestTemplate restTemplate = new RestTemplate();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            TokenRequest request = new TokenRequest(token);
            HttpEntity<TokenRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<CommonReturn<JwtUserDetails>> response = restTemplate.exchange(
                    AUTH_SERVICE_URL,
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<CommonReturn<JwtUserDetails>>() {}
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                CommonReturn<JwtUserDetails> tokenData = response.getBody();
                if (tokenData != null && tokenData.getStatus()==200) {
                    return tokenData;
                }
            }

            return CommonReturn.error(401,"Invalid Or Expired Or Unauthorized JWT Token.");
        } catch (Exception e) {
            e.printStackTrace();
            return CommonReturn.error(401,"Invalid Or Expired Or Unauthorized JWT Token.");
        }
    }


    public String uploadAndProcessVideo(MultipartFile file) {
        if (file.isEmpty()) {
            return "File is empty";
        }

        try {
            // Define temporary input directory and output directory
            String TEMP_DIR = "E:/Project/Streaming-App-Source-Video";
            String OUTPUT_DIR = "E:/Project/Streaming-App-Resized-Videos";

            // Ensure both directories exist
            Files.createDirectories(Paths.get(TEMP_DIR));
            Files.createDirectories(Paths.get(OUTPUT_DIR));

            // Save the uploaded file temporarily
            Path tempFile = Paths.get(TEMP_DIR, "uploaded_" + file.getOriginalFilename());
            Files.write(tempFile, file.getBytes());

            // Extract original resolution
//            String sourceResolution = getVideoResolution(tempFile.toString());
            String sourceResolution = "1920x1080";

            // Define the desired resolutions
            List<String> resolutions = List.of("144p", "240p", "360p", "480p", "720p", "1080p", "2k", "4k");
            List<String> validResolutions = getValidResolutions(sourceResolution, resolutions);

            // Generate copies in the output directory
            for (String resolution : validResolutions) {
                createResolutionCopy(tempFile.toString(), resolution, OUTPUT_DIR);
            }

            return "Copies created successfully in " + OUTPUT_DIR;
        } catch (Exception e) {
            return "Error processing video: " + e.getMessage();
        }
    }

    private String getVideoResolution(String filePath) throws Exception {
        String ffprobePath = "C:/ffmpeg/bin"; // Use the absolute path to ffprobe
        ProcessBuilder processBuilder = new ProcessBuilder(ffprobePath, "-v", "error", "-select_streams", "v:0",
                "-show_entries", "stream=width,height", "-of", "csv=s=x:p=0", filePath);
        Process process = processBuilder.start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            return reader.readLine(); // Example: "1920x1080"
        }
    }

    private List<String> getValidResolutions(String sourceResolution, List<String> resolutions) {
        int sourceHeight = Integer.parseInt(sourceResolution.split("x")[1]);
        Map<String, Integer> resolutionHeightMap = Map.of(
                "144p", 144, "240p", 240, "360p", 360,
                "480p", 480, "720p", 720, "1080p", 1080,
                "2k", 1440, "4k", 2160
        );

        return resolutions.stream()
                .filter(res -> resolutionHeightMap.get(res) <= sourceHeight)
                .collect(Collectors.toList());
    }

    private void createResolutionCopy(String filePath, String resolution, String outputDir) throws IOException, InterruptedException {
        try {
            // Define output file path
            String outputFileName = "output_" + resolution + ".mp4";
            Path outputFilePath = Paths.get(outputDir, outputFileName);

            // Ensure the output directory exists
            Files.createDirectories(Paths.get(outputDir));

            int height = Integer.parseInt(resolution.replace("p", ""));
            String ffmpegPath = "C:/ffmpeg/bin/ffmpeg.exe"; // Use ffmpeg here, not ffprobe

            // Use FFmpeg to resize and save in the output directory
            ProcessBuilder processBuilder = new ProcessBuilder(
                    ffmpegPath, "-i", filePath, "-vf", "scale=-1:" + height, "-c:v", "libx264", "-crf", "23", "-preset", "fast", outputFilePath.toString()
            );

            // Redirect error stream to help with debugging
            processBuilder.redirectErrorStream(true);

            // Start the process
            Process process = processBuilder.start();

            // Log the output for debugging
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

            // Wait for the process to finish
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("FFmpeg failed with exit code: " + exitCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error creating resolution copy for: " + resolution, e);
        }
    }

}
