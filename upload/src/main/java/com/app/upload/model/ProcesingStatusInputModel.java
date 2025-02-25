package com.app.upload.model;

public class ProcesingStatusInputModel {
    private Video video;
    private int processing_status;

    public ProcesingStatusInputModel() {

    }

    public ProcesingStatusInputModel(Video video, int processing_status) {
        this.video = video;
        this.processing_status = processing_status;
    }

    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }

    public int getProcessing_status() {
        return processing_status;
    }

    public void setProcessing_status(int processing_status) {
        this.processing_status = processing_status;
    }
}
