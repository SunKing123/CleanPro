package com.xiaoniu.cleanking.ui.main.bean;

import com.xiaoniu.cleanking.base.BaseEntity;

/**
 * @author zhengzhihao
 * @date 2020/7/6 12
 * @mail：zhengzhihao@hellogeek.com
 */
public class BubbleCollected extends BaseEntity {


    /**
     * data : {"collected":true,"uuid":"4ac7f670625445dfb76539102c80adf2","locationNum":2,"goldCount":75,"canDouble":1}
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * collected : true
         * uuid : 4ac7f670625445dfb76539102c80adf2
         * locationNum : 2
         * goldCount : 75
         * canDouble : 1
         */

        private boolean collected;
        private String uuid;
        private int locationNum;
        private int goldCount;
        private int canDouble;
        private int totalGoldCount;
        private int doubledMagnification;
        private String iconUrl;

        public String getIconUrl() {
            return iconUrl;
        }

        public void setIconUrl(String iconUrl) {
            this.iconUrl = iconUrl;
        }

        public int getDoubledMagnification() {
            return doubledMagnification;
        }

        public void setDoubledMagnification(int doubledMagnification) {
            this.doubledMagnification = doubledMagnification;
        }

        public int getTotalGoldCount() {
            return totalGoldCount;
        }

        public void setTotalGoldCount(int totalGoldCount) {
            this.totalGoldCount = totalGoldCount;
        }

        public boolean isCollected() {
            return collected;
        }

        public void setCollected(boolean collected) {
            this.collected = collected;
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public int getLocationNum() {
            return locationNum;
        }

        public void setLocationNum(int locationNum) {
            this.locationNum = locationNum;
        }

        public int getGoldCount() {
            return goldCount;
        }

        public void setGoldCount(int goldCount) {
            this.goldCount = goldCount;
        }

        public int getCanDouble() {
            return canDouble;
        }

        public void setCanDouble(int canDouble) {
            this.canDouble = canDouble;
        }
    }
}
