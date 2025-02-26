package com.app.upload.model;

import java.time.LocalDateTime;

public class ManageVideoDetails {
    private Long t_video_info;
    private String GUID;
    private String video_title;
    private int is_public;
    private int thumbnail_uploaded;
    private LocalDateTime trans_datetime;
    private int processing_status;

    public ManageVideoDetails(){

    }

    public ManageVideoDetails(Long t_video_info, String GUID, String video_title, int is_public, int thumbnail_uploaded, LocalDateTime trans_datetime, int processing_status) {
        this.t_video_info = t_video_info;
        this.GUID = GUID;
        this.video_title = video_title;
        this.is_public = is_public;
        this.thumbnail_uploaded = thumbnail_uploaded;
        this.trans_datetime = trans_datetime;
        this.processing_status = processing_status;
    }

    public Long getT_video_info() {
        return t_video_info;
    }

    public void setT_video_info(Long t_video_info) {
        this.t_video_info = t_video_info;
    }

    public String getGUID() {
        return GUID;
    }

    public void setGUID(String GUID) {
        this.GUID = GUID;
    }

    public String getVideo_title() {
        return video_title;
    }

    public void setVideo_title(String video_title) {
        this.video_title = video_title;
    }

    public int getIs_public() {
        return is_public;
    }

    public void setIs_public(int is_public) {
        this.is_public = is_public;
    }

    public int getThumbnail_uploaded() {
        return thumbnail_uploaded;
    }

    public void setThumbnail_uploaded(int thumbnail_uploaded) {
        this.thumbnail_uploaded = thumbnail_uploaded;
    }

    public LocalDateTime getTrans_datetime() {
        return trans_datetime;
    }

    public void setTrans_datetime(LocalDateTime trans_datetime) {
        this.trans_datetime = trans_datetime;
    }

    public int getProcessing_status() {
        return processing_status;
    }

    public void setProcessing_status(int processing_status) {
        this.processing_status = processing_status;
    }
}
