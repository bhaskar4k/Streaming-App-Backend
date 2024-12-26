package com.app.authentication.environment;

import java.util.Arrays;
import java.util.List;

public class Environment {
    private String privateKey1;
    private String privateKey2;
    private String encryptionPadding;
    private List<Integer> encryptionNumber;

    public String getPrivateKey1() {
        return "AHeavyEncryptionKeyThatIsWrittenByBhaskar";
    }

    public String getPrivateKey2() {
        return "ValorantIsAStressfullGameButEnjoyableAsWell";
    }

    public String getEncryptionPadding() {
        return "SoftwareDevelopmentIsVeryInterestingAreaIfyouLoveILoveMyExGF";
    }

    public List<Integer> getEncryptionNumber() {
        return Arrays.asList(23,56,75,86,45,12,43,65,24,86,37,24,2,456,346,746,347,246,343,6676,6546,9656,456463,34353,43543,733434);
    }
}