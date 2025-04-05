package com.app.streaming.model;

import java.util.List;

public class VideoInformation {
    private Boolean hasVideo;
    private Boolean properlyProcessed;
    private String channel;
    private String title;
    private String description;
    private long chunkCount;
    private double videoDuration;
    private List<String> videoResolutions;
    private String trans_datetime;

    public Boolean getHasVideo() {
        return hasVideo;
    }

    public void setHasVideo(Boolean hasVideo) {
        this.hasVideo = hasVideo;
    }

    public Boolean getProperlyProcessed() {
        return properlyProcessed;
    }

    public void setProperlyProcessed(Boolean properlyProcessed) {
        this.properlyProcessed = properlyProcessed;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getChunkCount() {
        return chunkCount;
    }

    public void setChunkCount(long chunkCount) {
        this.chunkCount = chunkCount;
    }

    public double getVideoDuration() {
        return videoDuration;
    }

    public void setVideoDuration(double videoDuration) {
        this.videoDuration = videoDuration;
    }

    public List<String> getVideoResolutions() {
        return videoResolutions;
    }

    public void setVideoResolutions(List<String> videoResolutions) {
        this.videoResolutions = videoResolutions;
    }

    public String getTrans_datetime() {
        return trans_datetime;
    }

    public void setTrans_datetime(String trans_datetime) {
        this.trans_datetime = trans_datetime;
    }
}
