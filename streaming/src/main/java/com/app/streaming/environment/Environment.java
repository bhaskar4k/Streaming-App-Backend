package com.app.streaming.environment;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Environment {
//    Desktop
    private String originalVideoPath = "E:" + File.separator + "Project" + File.separator + "Streaming-App-Source-Video";
    private String encodedVideoPath = "E:" + File.separator + "Project" + File.separator + "Streaming-App-Resized-Video";
    private String originalThumbnailPath = "E:" + File.separator + "Project" + File.separator + "Streaming-App-Thumbnail";

//    Laptop
//    private String originalVideoPath = "D:" + File.separator + "Streaming-App-Data" + File.separator + "Streaming-App-Source-Video";
//    private String encodedVideoPath = "D:" + File.separator + "Streaming-App-Data" + File.separator + "Streaming-App-Resized-Video";
//    private String originalThumbnailPath = "D:" + File.separator + "Streaming-App-Data" + File.separator + "Streaming-App-Thumbnail";

    private String ffprobePath = "C:/ffmpeg/bin/ffprobe.exe";
    private String ffmpegPath = "C:/ffmpeg/bin/ffmpeg.exe";

    private List<String> resolutions = List.of("144p", "240p", "360p", "480p", "720p", "1080p", "1440p", "2160p", "4320p");
    private Map<String, Integer> resolutionHeightMap = Map.of(
            "144p", 144, "240p", 240, "360p", 360,
            "480p", 480, "720p", 720, "1080p", 1080,
            "1440p", 1440, "2160p", 2160, "4320p", 4320
    );

    public String getOriginalVideoPath() {
        return originalVideoPath;
    }

    public String getOriginalThumbnailPath() {
        return originalThumbnailPath;
    }

    public String getEncodedVideoPath() {
        return encodedVideoPath;
    }

    public String getFfprobePath() {
        return ffprobePath;
    }

    public String getFfmpegPath() {
        return ffmpegPath;
    }

    public List<String> getResolutions() {
        return resolutions;
    }

    public Map<String, Integer> getResolutionHeightMap() {
        return resolutionHeightMap;
    }
}