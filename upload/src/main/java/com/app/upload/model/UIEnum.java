package com.app.upload.model;

public class UIEnum {
    public enum ProcessingStatus {
        TO_BE_PROCESSED(1),
        PROCESSING(2),
        PROCESSED(3),
        PROCESSING_FAILED(4);

        private final int value;

        ProcessingStatus(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
