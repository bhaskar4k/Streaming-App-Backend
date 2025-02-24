package com.app.authentication.enums;

public class UIEnum {
    public enum IsActive {
        ACTIVE(1),
        IN_ACTIVE(0);

        private final int value;

        IsActive(int value) {
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
