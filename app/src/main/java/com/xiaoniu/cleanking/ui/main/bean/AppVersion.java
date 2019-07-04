package com.xiaoniu.cleanking.ui.main.bean;


import android.os.Parcel;
import android.os.Parcelable;

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

    public static class DataBean implements Parcelable {
        public String appType;
        public String changeDesc;
        public String changeLog;
        public String code;
        public String downloadUrl;
        public String forcedUpdate;
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

        public String getForcedUpdate() {
            return forcedUpdate;
        }

        public void setForcedUpdate(String forcedUpdate) {
            this.forcedUpdate = forcedUpdate;
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


        protected DataBean(Parcel in) {
            appType = in.readString();
            changeDesc = in.readString();
            changeLog = in.readString();
            code = in.readString();
            downloadUrl = in.readString();
            forcedUpdate = in.readString();
            md5 = in.readString();
            popup = in.readString();
            state = in.readString();
            tag = in.readString();
            versionNumber = in.readString();
        }

        public static final Creator<DataBean> CREATOR = new Creator<DataBean>() {
            @Override
            public DataBean createFromParcel(Parcel in) {
                return new DataBean(in);
            }

            @Override
            public DataBean[] newArray(int size) {
                return new DataBean[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(appType);
            dest.writeString(changeDesc);
            dest.writeString(changeLog);
            dest.writeString(code);
            dest.writeString(downloadUrl);
            dest.writeString(forcedUpdate);
            dest.writeString(md5);
            dest.writeString(popup);
            dest.writeString(state);
            dest.writeString(tag);
            dest.writeString(versionNumber);
        }
    }



}
