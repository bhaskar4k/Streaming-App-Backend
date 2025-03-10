package com.app.upload.model;

import org.springframework.core.io.ByteArrayResource;

import java.time.LocalDateTime;

public class ManageVideoDetails {
    private Long t_video_info_id;
    private String video_title;
    private String video_description;
    private int is_public;
    private int thumbnail_uploaded;
    private String base64EncodedImage;
    private LocalDateTime trans_datetime;
    private int processing_status;

    public ManageVideoDetails(){

    }

    public ManageVideoDetails(Long t_video_info, String video_title, String video_description, int is_public, int thumbnail_uploaded, String base64EncodedImage, LocalDateTime trans_datetime, int processing_status) {
        this.t_video_info_id = t_video_info;
        this.video_title = video_title;
        this.video_description = video_description;
        this.is_public = is_public;
        this.thumbnail_uploaded = thumbnail_uploaded;
        this.base64EncodedImage = base64EncodedImage;
        this.trans_datetime = trans_datetime;
        this.processing_status = processing_status;
    }

    public Long getT_video_info_id() {
        return t_video_info_id;
    }

    public void setT_video_info_id(Long t_video_info_id) {
        this.t_video_info_id = t_video_info_id;
    }

    public String getVideo_title() {
        return video_title;
    }

    public void setVideo_title(String video_title) {
        this.video_title = video_title;
    }

    public String getVideo_description() {
        return video_description;
    }

    public void setVideo_description(String video_description) {
        this.video_description = video_description;
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

    public String getBase64EncodedImage() {
        return base64EncodedImage;
    }

    public void setBase64EncodedImage(String base64EncodedImage) {
        this.base64EncodedImage = base64EncodedImage;
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
