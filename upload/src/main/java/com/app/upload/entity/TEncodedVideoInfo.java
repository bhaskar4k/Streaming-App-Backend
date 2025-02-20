package com.app.upload.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Entity
@Table(name = "t_encoded_video_info")
public class TEncodedVideoInfo {
    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private Long t_video_info_id;
    @Column(length = 500)
    private String encoded_resolutions;
    private int processing_status;
    private LocalDateTime processed_at;
    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime trans_datetime = LocalDateTime.now();

    public TEncodedVideoInfo(String encoded_resolutions, int processing_status) {
        this.encoded_resolutions = encoded_resolutions;
        this.processing_status = processing_status;
    }

    public TEncodedVideoInfo(Long t_video_info_id, String encoded_resolutions, int processing_status) {
        this.t_video_info_id = t_video_info_id;
        this.encoded_resolutions = encoded_resolutions;
        this.processing_status = processing_status;
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

    public String getEncoded_resolutions() {
        return encoded_resolutions;
    }

    public void setEncoded_resolutions(String encoded_resolutions) {
        this.encoded_resolutions = encoded_resolutions;
    }

    public int getProcessing_status() {
        return processing_status;
    }

    public void setProcessing_status(int processing_status) {
        this.processing_status = processing_status;
    }

    public LocalDateTime getTrans_datetime() {
        return trans_datetime;
    }

    public void setTrans_datetime(LocalDateTime trans_datetime) {
        this.trans_datetime = trans_datetime;
    }
}
