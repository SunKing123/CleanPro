package com.xiaoniu.cleanking.ui.main.bean;

import com.xiaoniu.cleanking.base.BaseEntity;

import java.util.List;

/**
 * 插屏广告数据
 */
public class InsertAdSwitchInfoList extends BaseEntity {

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
         * internalAdRate 当内部插屏该条数据配置返回“展示频次”参数为1,2,3,4  则代表当日第一次、第二次、第三次、第四次启动app时（不分冷热启动）内部插屏显示
         */

        private boolean isOpen;
        private String configKey;
        private String switcherName;
        private String advertPosition;
        private String versions;
        private String advertId;
        private String secondAdvertId;
        private int displayTime;
        private int showRate = 3;
        private String internalAdRate;

        public String getInternalAdRate() {
            return internalAdRate;
        }

        public void setInternalAdRate(String internalAdRate) {
            this.internalAdRate = internalAdRate;
        }

        public int getDisplayTime() {
            return displayTime;
        }

        public void setDisplayTime(int displayTime) {
            this.displayTime = displayTime;
        }


        public String getSecondAdvertId() {
            return secondAdvertId;
        }

        public int getShowRate() {
            return showRate;
        }

        public boolean isOpen() {
            return isOpen;
        }

        public void setOpen(boolean open) {
            isOpen = open;
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

        public void setAdvertId(String advertId) {
            this.advertId = advertId;
        }
    }
}
