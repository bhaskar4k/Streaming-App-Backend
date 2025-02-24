package com.app.upload.enums;

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

    public enum YesNo {
        YES(1),
        NO(0);

        private final int value;

        YesNo(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
