package com.xiaoniu.cleanking.ui.main.bean;


import com.xiaoniu.cleanking.base.BaseEntity;

/**
 * deprecation:用户实体类
 * author:ayb
 * time:2018/9/20
 */
public class AppVersion extends BaseEntity {

    private DataBean data;


    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        public String appType;
        public String changeDesc;
        public String changeLog;
        public String code;
        public String downloadUrl;
        public String changeProperties;
        public String md5;
        public String popup;
        public String state;

        public String getAppType() {
            return appType;
        }

        public void setAppType(String appType) {
            this.appType = appType;
        }

        public String getChangeDesc() {
            return changeDesc;
        }

        public void setChangeDesc(String changeDesc) {
            this.changeDesc = changeDesc;
        }

        public String getChangeLog() {
            return changeLog;
        }

        public void setChangeLog(String changeLog) {
            this.changeLog = changeLog;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getDownloadUrl() {
            return downloadUrl;
        }

        public void setDownloadUrl(String downloadUrl) {
            this.downloadUrl = downloadUrl;
        }

        public String getChangeProperties() {
            return changeProperties;
        }

        public void setChangeProperties(String changeProperties) {
            this.changeProperties = changeProperties;
        }

        public String getMd5() {
            return md5;
        }

        public void setMd5(String md5) {
            this.md5 = md5;
        }

        public String getPopup() {
            return popup;
        }

        public void setPopup(String popup) {
            this.popup = popup;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public String getVersionNumber() {
            return versionNumber;
        }

        public void setVersionNumber(String versionNumber) {
            this.versionNumber = versionNumber;
        }

        public String tag;
        public String versionNumber;

    }



}
