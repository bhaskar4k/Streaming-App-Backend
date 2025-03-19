package com.app.streaming.model;

import java.util.List;

public class VideoInformation {
    private Boolean hasVideo;
    private int chunkCount;
    private List<String> videoResolutions;

    public Boolean getHasVideo() {
        return hasVideo;
    }

    public void setHasVideo(Boolean hasVideo) {
        this.hasVideo = hasVideo;
    }

    public int getChunkCount() {
        return chunkCount;
    }

    public void setChunkCount(int chunkCount) {
        this.chunkCount = chunkCount;
    }

    public List<String> getVideoResolutions() {
        return videoResolutions;
    }

    public void setVideoResolutions(List<String> videoResolutions) {
        this.videoResolutions = videoResolutions;
    }
}
