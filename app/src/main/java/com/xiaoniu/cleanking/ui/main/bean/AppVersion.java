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
        public int alertTime; //	提示周期，0-每次，1-一天一次，2-两天一次，3-三天一次
        public boolean doubleCheck; //是否二次挽留
        public int downloadEnv; //静默下载环境，0-WIFI，1-WIFI+G网
        public String downloadUrl;
        public boolean isPopup; //是否弹窗
        public String upgradeType; //更新类型，1-常规更新，2-强制更新
        public String content;
        public String remark;
        public String versionNumber;

        public String getVersionNumber() {
            return versionNumber;
        }

        public int getAlertTime() {
            return alertTime;
        }

        public boolean isDoubleCheck() {
            return doubleCheck;
        }

        public int getDownloadEnv() {
            return downloadEnv;
        }

        public String getDownloadUrl() {
            return downloadUrl;
        }

        public boolean isPopup() {
            return isPopup;
        }

        public String getUpgradeType() {
            return upgradeType;
        }

        public String getContent() {
            return content;
        }

        public String getRemark() {
            return remark;
        }
    }



}
