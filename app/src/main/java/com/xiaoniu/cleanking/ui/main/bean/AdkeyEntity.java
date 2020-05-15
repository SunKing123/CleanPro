package com.xiaoniu.cleanking.ui.main.bean;

/**
 * @ProjectName: clean
 * @Package: com.xiaoniu.cleanking.ui.main.bean
 * @ClassName: AdkeyEntity
 * @Description:
 * @Author: youkun_zhou
 * @CreateDate: 2020/5/9 10:27
 */

public class AdkeyEntity {

    private String configKey;
    private String advertPosition;

    public AdkeyEntity(String configKey, String advertPosition) {
        this.configKey = configKey;
        this.advertPosition = advertPosition;
    }

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public String getAdvertPosition() {
        return advertPosition;
    }

    public void setAdvertPosition(String advertPosition) {
        this.advertPosition = advertPosition;
    }
}
