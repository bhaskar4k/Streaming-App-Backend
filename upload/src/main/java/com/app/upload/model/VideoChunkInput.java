package com.app.upload.model;

import org.springframework.web.multipart.MultipartFile;

public class VideoChunkInput {
    private MultipartFile chunk;
    private String fileId;
    private Long chunkIndex;
    private Long totalChunks;

    public VideoChunkInput(MultipartFile chunk, String fileId, Long chunkIndex, Long totalChunks) {
        this.chunk = chunk;
        this.fileId = fileId;
        this.chunkIndex = chunkIndex;
        this.totalChunks = totalChunks;
    }

    public MultipartFile getChunk() {
        return chunk;
    }

    public void setChunk(MultipartFile chunk) {
        this.chunk = chunk;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public Long getChunkIndex() {
        return chunkIndex;
    }

    public void setChunkIndex(Long chunkIndex) {
        this.chunkIndex = chunkIndex;
    }

    public Long getTotalChunks() {
        return totalChunks;
    }

    public void setTotalChunks(Long totalChunks) {
        this.totalChunks = totalChunks;
    }
}
