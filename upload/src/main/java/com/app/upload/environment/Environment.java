package com.app.upload.environment;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Environment {
    private List<String> allowedOrigins = Arrays.asList("http://localhost:5173");
    private String authServiceUrl = "http://localhost:8090/authentication/verify_request";
    private String rabbitMQPublishURL = "http://localhost:8095/publish/put_in_queue";

//    Desktop
//    private String originalVideoPath = "E:" + File.separator + "Project" + File.separator + "Streaming-App-Source-Video";

    // Laptop
    private String originalVideoPath = "D:" + File.separator + "Streaming-App-Data" + File.separator + "Streaming-App-Source-Video";

    private String ffprobePath = "C:/ffmpeg/bin/ffprobe.exe";
    private String ffmpegPath = "C:/ffmpeg/bin/ffmpeg.exe";


    public List<String> getAllowedOrigins() {
        return allowedOrigins;
    }

    public String getOriginalVideoPath() {
        return originalVideoPath;
    }

    public String getFfprobePath() {
        return ffprobePath;
    }

    public String getAuthServiceUrl() {
        return authServiceUrl;
    }

    public String getRabbitMQPublishURL() {
        return rabbitMQPublishURL;
    }

    public String getFfmpegPath() {
        return ffmpegPath;
    }

    //#region Custom environment

    //#endregion
}