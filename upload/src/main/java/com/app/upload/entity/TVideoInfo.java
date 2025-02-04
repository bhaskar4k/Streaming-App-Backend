package com.app.upload.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Entity
@Table(name = "t_video_info")
public class TVideoInfo {
    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private String guid;
    @Column(length = 1000)
    private String original_filename;
    private Long size;
    private String extension;
    private String source_resolution;
    private Double duration;
    private Long no_of_chunks;
    private Long t_mst_user_id;
    @Column(nullable = false, columnDefinition = "int default 1")
    private int is_active = 1;
    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime trans_datetime = LocalDateTime.now();

    public TVideoInfo(String guid, String original_filename, Long size, String extension, String source_resolution, Double duration, Long no_of_chunks, Long t_mst_user_id) {
        this.guid = guid;
        this.original_filename = original_filename;
        this.size = size;
        this.extension = extension;
        this.source_resolution = source_resolution;
        this.duration = duration;
        this.no_of_chunks = no_of_chunks;
        this.t_mst_user_id = t_mst_user_id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getOriginal_filename() {
        return original_filename;
    }

    public void setOriginal_filename(String original_filename) {
        this.original_filename = original_filename;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getSource_resolution() {
        return source_resolution;
    }

    public void setSource_resolution(String source_resolution) {
        this.source_resolution = source_resolution;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }

    public Long getNo_of_chunks() {
        return no_of_chunks;
    }

    public void setNo_of_chunks(Long no_of_chunks) {
        this.no_of_chunks = no_of_chunks;
    }

    public Long getT_mst_user_id() {
        return t_mst_user_id;
    }

    public void setT_mst_user_id(Long t_mst_user_id) {
        this.t_mst_user_id = t_mst_user_id;
    }

    public int getIs_active() {
        return is_active;
    }

    public void setIs_active(int is_active) {
        this.is_active = is_active;
    }

    public LocalDateTime getTrans_datetime() {
        return trans_datetime;
    }

    public void setTrans_datetime(LocalDateTime trans_datetime) {
        this.trans_datetime = trans_datetime;
    }
}