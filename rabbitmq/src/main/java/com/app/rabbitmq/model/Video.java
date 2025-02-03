package com.app.rabbitmq.model;

public class Video {
    private String VIDEO_GUID;
    private String originalFilePath;
    private String originalFileName;
    private long tMstUserId;

    public Video(String VIDEO_GUID, String originalFilePath, String originalFileName, long tMstUserId) {
        this.VIDEO_GUID = VIDEO_GUID;
        this.originalFilePath = originalFilePath;
        this.originalFileName = originalFileName;
        this.tMstUserId = tMstUserId;
    }

    public String getVIDEO_GUID() {
        return VIDEO_GUID;
    }

    public void setVIDEO_GUID(String VIDEO_GUID) {
        this.VIDEO_GUID = VIDEO_GUID;
    }

    public String getOriginalFilePath() {
        return originalFilePath;
    }

    public void setOriginalFilePath(String originalFilePath) {
        this.originalFilePath = originalFilePath;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public long getTMstUserId() {
        return tMstUserId;
    }

    public void setTMstUserId(long tMstUserId) {
        this.tMstUserId = tMstUserId;
    }
}
