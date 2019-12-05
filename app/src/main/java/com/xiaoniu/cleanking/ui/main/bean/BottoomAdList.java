package com.xiaoniu.cleanking.ui.main.bean;

import com.xiaoniu.cleanking.base.BaseEntity;

import java.util.List;

public class BottoomAdList extends BaseEntity {

    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {

        private String id;
        private String switcherName;
        private String switcherKey;
        private String advertPosition;
        private String appName;
        private int showType;
        private int deviceType;
        private int state;
        private int versionSign;
        private int versionCode;
        private List<AdvBottomPicsDTOS> advBottomPicsDTOS;

        public String getId() {
            return id;
        }

        public String getSwitcherName() {
            return switcherName;
        }

        public void setSwitcherName(String switcherName) {
            this.switcherName = switcherName;
        }

        public String getSwitcherKey() {
            return switcherKey;
        }

        public void setSwitcherKey(String switcherKey) {
            this.switcherKey = switcherKey;
        }

        public String getAdvertPosition() {
            return advertPosition;
        }

        public void setAdvertPosition(String advertPosition) {
            this.advertPosition = advertPosition;
        }

        public String getAppName() {
            return appName;
        }

        public void setAppName(String appName) {
            this.appName = appName;
        }

        public int getShowType() {
            return showType;
        }

        public void setShowType(int showType) {
            this.showType = showType;
        }

        public int getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(int deviceType) {
            this.deviceType = deviceType;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public int getVersionSign() {
            return versionSign;
        }

        public void setVersionSign(int versionSign) {
            this.versionSign = versionSign;
        }

        public int getVersionCode() {
            return versionCode;
        }

        public void setVersionCode(int versionCode) {
            this.versionCode = versionCode;
        }

        public List<AdvBottomPicsDTOS> getAdvBottomPicsDTOS() {
            return advBottomPicsDTOS;
        }

        public void setAdvBottomPicsDTOS(List<AdvBottomPicsDTOS> advBottomPicsDTOS) {
            this.advBottomPicsDTOS = advBottomPicsDTOS;
        }
    }

    public class AdvBottomPicsDTOS {
        private String activeId;
        private String imgUrl;
        private String linkUrl;

        public String getActiveId() {
            return activeId;
        }

        public void setActiveId(String activeId) {
            this.activeId = activeId;
        }

        public String getImgUrl() {
            return imgUrl;
        }

        public void setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
        }

        public String getLinkUrl() {
            return linkUrl;
        }

        public void setLinkUrl(String linkUrl) {
            this.linkUrl = linkUrl;
        }
    }
}
