package com.xiaoniu.cleanking.ad.bean;

/**
 * @ProjectName: clean
 * @Package: com.xiaoniu.cleanking.ad.bean
 * @ClassName: AdBean
 * @Description:
 * @Author: youkun_zhou
 * @CreateDate: 2020/5/9 15:15
 */

public class AdRequestBean {
    //联盟id
    private String advertId;
    //联盟类型
    private String advertSource;

    public AdRequestBean(String advertId, String advertSource) {
        this.advertId = advertId;
        this.advertSource = advertSource;
    }

    public String getAdvertId() {
        return advertId;
    }

    public void setAdvertId(String advertId) {
        this.advertId = advertId;
    }

    public String getAdvertSource() {
        return advertSource;
    }

    public void setAdvertSource(String advertSource) {
        this.advertSource = advertSource;
    }
}
