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
            String TEMP_DIR = "E:/Project/Streaming-App-Source-Video";
            String OUTPUT_DIR = "E:/Project/Streaming-App-Resized-Videos";

            Files.createDirectories(Paths.get(TEMP_DIR));
            Files.createDirectories(Paths.get(OUTPUT_DIR));

            Path tempFile = Paths.get(TEMP_DIR, "uploaded_" + file.getOriginalFilename());
            Files.write(tempFile, file.getBytes());

            String sourceResolution = getVideoResolution(tempFile.toString());

            List<String> resolutions = List.of("144p", "240p", "360p", "480p", "720p", "1080p", "1440p", "2160p", "4320p");
            List<String> validResolutions = getValidResolutions(sourceResolution, resolutions);

            for (String resolution : validResolutions) {
                createResolutionCopy(tempFile.toString(), sourceResolution, resolution, OUTPUT_DIR);
            }

            return "Copies created successfully in " + OUTPUT_DIR;
        } catch (Exception e) {
            return "Error processing video: " + e.getMessage();
        }
    }

    private String getVideoResolution(String filePath) throws Exception {
        String ffprobePath = "C:/ffmpeg/bin/ffprobe.exe";
        ProcessBuilder processBuilder = new ProcessBuilder(ffprobePath, "-v", "error", "-select_streams", "v:0",
                "-show_entries", "stream=width,height", "-of", "csv=s=x:p=0", filePath);
        Process process = processBuilder.start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            return reader.readLine();
        }
    }

    private List<String> getValidResolutions(String sourceResolution, List<String> resolutions) {
        int sourceHeight = Integer.parseInt(sourceResolution.split("x")[1]);
        Map<String, Integer> resolutionHeightMap = Map.of(
                "144p", 144, "240p", 240, "360p", 360,
                "480p", 480, "720p", 720, "1080p", 1080,
                "1440p", 1440, "2160p", 2160, "4320p", 4320
        );

        return resolutions.stream()
                .filter(res -> resolutionHeightMap.get(res) <= sourceHeight)
                .collect(Collectors.toList());
    }

    private void createResolutionCopy(String filePath, String sourceResolution, String resolution, String outputDir) throws IOException, InterruptedException {
        try {
            String outputFileName = "output_" + resolution + ".mp4";
            Path outputFilePath = Paths.get(outputDir, outputFileName);

            Files.createDirectories(Paths.get(outputDir));

            int targetHeight = Integer.parseInt(resolution.replace("p", ""));

            int sourceHeight = Integer.parseInt(sourceResolution.split("x")[1]);
            int sourceWidth = Integer.parseInt(sourceResolution.split("x")[0]);

            long sourceBitrate = Math.max(sourceHeight * sourceWidth * 70L / 1000, 1000L);

            long targetBitrate = Math.max(sourceBitrate * targetHeight / sourceHeight, 500L);

            String ffmpegPath = "C:/ffmpeg/bin/ffmpeg.exe";

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
                throw new RuntimeException("FFmpeg failed with exit code: " + exitCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error creating resolution copy for: " + resolution, e);
        }
    }


}
