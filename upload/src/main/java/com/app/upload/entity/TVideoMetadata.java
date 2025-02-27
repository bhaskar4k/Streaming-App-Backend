package com.app.upload.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Entity
@Table(name = "t_video_metadata")
public class TVideoMetadata {
    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private Long t_video_info_id;
    @Column(length = 1000)
    private String video_title;
    @Column(length = 1000000000)
    private String video_description;
    private int is_public;
    private int thumbnail_uploaded;
    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime trans_datetime = LocalDateTime.now();

    public TVideoMetadata(){

    }

    public TVideoMetadata(int is_public, int thumbnail_uploaded) {
        this.is_public = is_public;
        this.thumbnail_uploaded = thumbnail_uploaded;
    }

    public TVideoMetadata(Long t_video_info_id, String video_title, String video_description, int is_public, int thumbnail_uploaded) {
        this.t_video_info_id = t_video_info_id;
        this.video_title = video_title;
        this.video_description = video_description;
        this.is_public = is_public;
        this.thumbnail_uploaded = thumbnail_uploaded;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getTrans_datetime() {
        return trans_datetime;
    }

    public void setTrans_datetime(LocalDateTime trans_datetime) {
        this.trans_datetime = trans_datetime;
    }
}
