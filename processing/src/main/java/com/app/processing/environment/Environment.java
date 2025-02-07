package com.app.processing.environment;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Environment {
    private List<String> allowedOrigins = Arrays.asList("http://localhost:5173");
    private String rabbitMQConsumerURL = "http://localhost:8095/consumer/pull";

    // Desktop
//    private String originalVideoPath = "E:" + File.separator + "Project" + File.separator + "Streaming-App-Source-Video";
//    private String encodedVideoPath = "E:" + File.separator + "Project" + File.separator + "Streaming-App-Resized-Video";

    // Laptop
    private String originalVideoPath = "D:" + File.separator + "Streaming-App-Data" + File.separator + "Streaming-App-Source-Video";
    private String encodedVideoPath = "D:" + File.separator + "Streaming-App-Data" + File.separator + "Streaming-App-Resized-Video";

    private List<String> resolutions = List.of("144p", "240p", "360p", "480p", "720p", "1080p", "1440p", "2160p", "4320p");
    private Map<String, Integer> resolutionHeightMap = Map.of(
            "144p", 144, "240p", 240, "360p", 360,
            "480p", 480, "720p", 720, "1080p", 1080,
            "1440p", 1440, "2160p", 2160, "4320p", 4320
    );

    private String ffprobePath = "C:/ffmpeg/bin/ffprobe.exe";
    private String ffmpegPath = "C:/ffmpeg/bin/ffmpeg.exe";

    // Desktop
//    private String pythonScriptPath = "E:" + File.separator + "Project" + File.separator + "Streaming-App" + File.separator +
//            "processing" + File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator +
//            "com" + File.separator + "app" + File.separator + "processing" + File.separator + "python" + File.separator + "VideoSplitter.py";

    // Laptop
    private String pythonScriptPath = "D:" + File.separator + "Streaming-App" + File.separator + "processing" + File.separator +
            "src" + File.separator + "main" + File.separator + "java" + File.separator + "com" + File.separator +
            "app" + File.separator + "processing" + File.separator + "python" + File.separator + "VideoSplitter.py";


    public List<String> getAllowedOrigins() {
        return allowedOrigins;
    }

    public String getOriginalVideoPath() {
        return originalVideoPath;
    }

    public String getEncodedVideoPath() {
        return encodedVideoPath;
    }

    public List<String> getResolutions() {
        return resolutions;
    }

    public String getFfprobePath() {
        return ffprobePath;
    }

    public Map<String, Integer> getResolutionHeightMap() {
        return resolutionHeightMap;
    }

    public String getFfmpegPath() {
        return ffmpegPath;
    }

    public String getPythonScriptPath() {
        return pythonScriptPath;
    }

    public String getRabbitMQConsumerURL() {
        return rabbitMQConsumerURL;
    }
}