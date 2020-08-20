package com.xiaoniu.cleanking.ui.main.bean;

import com.xiaoniu.cleanking.base.BaseEntity;

import java.util.List;

public class DaliyTaskListData extends BaseEntity {

    /**
     * data : [{"id":1,"appName":"gj_clean","positionCode":"opearte_page_main","taskName":"测试","taskIcon":"https://getvideo-test.oss-cn-shanghai.aliyuncs.com/clean/banner/image/121ebfa5b4ab4619b0dfb0f33b0c4797.jpeg","mainTitle":"测试文案","subtitleTitle":"测试标题","goldIcon":"https://getvideo-test.oss-cn-shanghai.aliyuncs.com/clean/banner/image/18cac2e52b504a62b34f5374ca03c42a.jpeg","goldNum":100,"doubledMagnification":21,"linkType":1,"linkUrl":"111","versionCodes":"40,42","state":1,"isCollect":0},{"id":18,"appName":"gj_clean","positionCode":"opearte_page_add_game","taskName":"测试2222","taskIcon":"https://getvideo-test.oss-cn-shanghai.aliyuncs.com/clean/banner/image/02872aed14c04ddd95e6c68d3722fece.jpg","mainTitle":"测试文案51222","subtitleTitle":"测试标题1222","goldIcon":"https://getvideo-test.oss-cn-shanghai.aliyuncs.com/clean/banner/image/b3fd9af6ade54594ac7c869466e1b5b3.jpg","goldNum":50,"doubledMagnification":2,"linkType":1,"linkUrl":"111","versionCodes":"14,40,42","state":1,"isCollect":0}]
     * timestamp : 1597816856340
     */

    private long timestamp;
    private List<DaliyTaskListEntity> data;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public List<DaliyTaskListEntity> getData() {
        return data;
    }

    public void setData(List<DaliyTaskListEntity> data) {
        this.data = data;
    }


}
