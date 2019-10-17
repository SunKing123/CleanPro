package com.xiaoniu.cleanking.ui.main.bean;

import com.xiaoniu.cleanking.base.BaseEntity;

import java.util.List;


public class SwitchInfoList extends BaseEntity {

    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * configKey : ID19070801051408093330944156320000
         * isOpen : false
         * switcherName : 开屏广告-android
         */

        private boolean isOpen;
        private String configKey;
        private String switcherName;
        private String advertPosition;
        private String versions;
        private String advertId;

        public boolean isOpen() {
            return isOpen;
        }

        public String getConfigKey() {
            return configKey;
        }

        public String getSwitcherName() {
            return switcherName;
        }

        public String getAdvertPosition() {
            return advertPosition;
        }

        public String getVersions() {
            return versions;
        }

        public String getAdvertId() {
            return advertId;
        }
    }
}
