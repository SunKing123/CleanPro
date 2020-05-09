package com.xiaoniu.cleanking.ui.main.bean;

import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;

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

    @Entity(tableName = "AdInfo",primaryKeys = {"configKey", "advertPosition"})
    public static class DataBean {
        /**
         * configKey : ID19070801051408093330944156320000
         * isOpen : false
         * switcherName : 开屏广告-android
         */
        private boolean isOpen;
        @NonNull
        private String configKey;
        private String switcherName;
        @NonNull
        private String advertPosition;
        private String versions;
        private String advertId;
        private String secondAdvertId;
        private int showRate;

        public String getSecondAdvertId() {
            return secondAdvertId;
        }

        public int getShowRate() {
            return showRate;
        }

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

        public void setOpen(boolean open) {
            isOpen = open;
        }

        public void setConfigKey(String configKey) {
            this.configKey = configKey;
        }

        public void setSwitcherName(String switcherName) {
            this.switcherName = switcherName;
        }

        public void setAdvertPosition(String advertPosition) {
            this.advertPosition = advertPosition;
        }

        public void setVersions(String versions) {
            this.versions = versions;
        }

        public void setAdvertId(String advertId) {
            this.advertId = advertId;
        }

        public void setSecondAdvertId(String secondAdvertId) {
            this.secondAdvertId = secondAdvertId;
        }

        public void setShowRate(int showRate) {
            this.showRate = showRate;
        }
    }
}
