package com.installment.mall.ui.main.bean;

import com.installment.mall.base.BaseEntity;

/**
 * Created by sahara on 2017/4/25.
 */

public class Patch extends BaseEntity {


    /**
     * data : {"baseVersionName":"1.3.0","encryption":"adfadfasdfasdf","patchVersion":"1.0","url":"http://","versionCode":7,"versionName":"1.3.0"}
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
         * baseVersionName : 1.3.0
         * encryption : adfadfasdfasdf
         * patchVersion : 1.0
         * url : http://
         * versionCode : 7
         * versionName : 1.3.0
         */

        private String baseVersionName;
        private String patchEncryption;
        private String patchVersion;
        private String patchUrl;
        private int versionCode;
        private String baseVersion;

        public String getBaseVersionName() {
            return baseVersionName;
        }

        public void setBaseVersionName(String baseVersionName) {
            this.baseVersionName = baseVersionName;
        }

        public String getPatchEncryption() {
            return patchEncryption;
        }

        public void setPatchEncryption(String patchEncryption) {
            this.patchEncryption = patchEncryption;
        }

        public String getPatchVersion() {
            return patchVersion;
        }

        public void setPatchVersion(String patchVersion) {
            this.patchVersion = patchVersion;
        }

        public String getPatchUrl() {
            return patchUrl;
        }

        public void setPatchUrl(String patchUrl) {
            this.patchUrl = patchUrl;
        }

        public int getVersionCode() {
            return versionCode;
        }

        public void setVersionCode(int versionCode) {
            this.versionCode = versionCode;
        }

        public String getBaseVersion() {
            return baseVersion;
        }

        public void setBaseVersion(String baseVersion) {
            this.baseVersion = baseVersion;
        }
    }
}
