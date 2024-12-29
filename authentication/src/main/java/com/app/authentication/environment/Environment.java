package com.app.authentication.environment;

import java.util.Arrays;
import java.util.List;

public class Environment {
    private String privateKey1 = "AHeavyEncryptionKeyThatIsWrittenByBhaskar";
    private String privateKey2 = "ValorantIsAStressfullGameButEnjoyableAsWell";
    private String encryptionPadding="SoftwareDevelopmentIsVeryInterestingAreaIfyouLoveILoveMyExGF";
    private List<Integer> encryptionNumber = Arrays.asList(23,56,75,86,45,12,43,65,24,86,37,24,2,456,346,746,347,246,343,6676,6546,9656,456463,34353,43543,733434);
    private List<String> allowedOrigins = Arrays.asList("http://localhost:5173");

    public String getPrivateKey1() {
        return this.privateKey1;
    }

    public String getPrivateKey2() {
        return this.privateKey2;
    }

    public String getEncryptionPadding() {
        return this.encryptionPadding;
    }

    public List<Integer> getEncryptionNumber() {
        return this.encryptionNumber;
    }

    public List<String> getAllowedOrigins() {
        return allowedOrigins;
    }
}