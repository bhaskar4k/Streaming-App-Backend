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
    private String encoded_filepath;
    @Column(length = 1000)
    private String encoded_filename;
    @Column(length = 500)
    private String encoded_resolutions;
    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime trans_datetime = LocalDateTime.now();

    public TEncodedVideoInfo(String encoded_filepath, String encoded_filename, String encoded_resolutions) {
        this.encoded_filepath = encoded_filepath;
        this.encoded_filename = encoded_filename;
        this.encoded_resolutions = encoded_resolutions;
    }

    public TEncodedVideoInfo(Long t_video_info_id, String encoded_filepath, String encoded_filename, String encoded_resolutions) {
        this.t_video_info_id = t_video_info_id;
        this.encoded_filepath = encoded_filepath;
        this.encoded_filename = encoded_filename;
        this.encoded_resolutions = encoded_resolutions;
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

    public String getEncoded_filepath() {
        return encoded_filepath;
    }

    public void setEncoded_filepath(String encoded_filepath) {
        this.encoded_filepath = encoded_filepath;
    }

    public String getEncoded_filename() {
        return encoded_filename;
    }

    public void setEncoded_filename(String encoded_filename) {
        this.encoded_filename = encoded_filename;
    }

    public String getEncoded_resolutions() {
        return encoded_resolutions;
    }

    public void setEncoded_resolutions(String encoded_resolutions) {
        this.encoded_resolutions = encoded_resolutions;
    }

    public LocalDateTime getTrans_datetime() {
        return trans_datetime;
    }

    public void setTrans_datetime(LocalDateTime trans_datetime) {
        this.trans_datetime = trans_datetime;
    }
}
