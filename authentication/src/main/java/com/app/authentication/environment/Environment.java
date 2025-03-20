package com.app.authentication.environment;

import java.util.Arrays;
import java.util.List;

public class Environment {
    private String privateKey1 = "AHeavyEncryptionKeyThatIsWrittenByBhaskar";
    private String privateKey2 = "ValorantIsAStressfullGameButEnjoyableAsWell";
    private String encryptionPadding="SoftwareDevelopmentIsVeryInterestingAreaIfyouLoveILoveMyExGF";
    private List<Integer> encryptionNumber = Arrays.asList(23,56,75,86,45,12,43,65,24,86,37,24,2,456,346,746,347,246,343,6676,6546,9656,456463,34353,43543,733434);
    private List<String> allowedOrigins = Arrays.asList("http://localhost:5173");
    private Long maximumLoginDevice = 2L;
    private String secretKey = "yg45eg-g56yw4r-hj45g-fy6awe-g54fw";
//    private Long jwtExpireTime = (1000L * 60L * 60L * 10L * 300L);
    private Long jwtExpireTime = (60000L);

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

    public Long getMaximumLoginDevice() {
        return maximumLoginDevice;
    }

    public String getSecretKey() {
            return secretKey;
    }

    public Long getJwtExpireTime() {
        return jwtExpireTime;
    }



    //#region Custom environment
    public String getDeviceEndpoint(Long t_mst_user_id, Long device_count){
        return "/u"+t_mst_user_id.toString()+"/d"+device_count.toString();
    }
    //#endregion
}