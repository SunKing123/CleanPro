package com.xiaoniu.cleanking.ui.main.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.xiaoniu.cleanking.base.BaseEntity;

/**
 * Created by tie on 2017/6/16.
 */

public class UpdateInfoEntity extends BaseEntity {


    /**
     * requestId : 34543543543511
     * timestamp : 1535354186713
     * data : {"versionNumber":"1.0.0","downloadUrl":"http://www.baidu.com","isForcedUpdate":"1","changeCopy":"1"}
     */

    private String requestId;
    private long timestamp;
    private DataBean data;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean implements Parcelable {
        /**
         * versionNumber : 1.0.0
         * downloadUrl : http://www.baidu.com
         * isForcedUpdate : 1
         * changeCopy : 1
         */

        private String versionNumber;
        private String downloadUrl;
        private String isForcedUpdate;
        private String changeCopy;
        /**
         * 首页是否弹窗提示更新1-是 2-否
         */
        private String isPopup;

        protected DataBean(Parcel in) {
            versionNumber = in.readString();
            downloadUrl = in.readString();
            isForcedUpdate = in.readString();
            changeCopy = in.readString();
            isPopup = in.readString();
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

        public String getIsPopup() {
            return isPopup;
        }

        public void setIsPopup(String isPopup) {
            this.isPopup = isPopup;
        }

        public String getVersionNumber() {
            return versionNumber;
        }

        public void setVersionNumber(String versionNumber) {
            this.versionNumber = versionNumber;
        }

        public String getDownloadUrl() {
            return downloadUrl;
        }

        public void setDownloadUrl(String downloadUrl) {
            this.downloadUrl = downloadUrl;
        }

        public String getIsForcedUpdate() {
            return isForcedUpdate;
        }

        public void setIsForcedUpdate(String isForcedUpdate) {
            this.isForcedUpdate = isForcedUpdate;
        }

        public String getChangeCopy() {
            return changeCopy;
        }

        public void setChangeCopy(String changeCopy) {
            this.changeCopy = changeCopy;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(versionNumber);
            dest.writeString(downloadUrl);
            dest.writeString(isForcedUpdate);
            dest.writeString(changeCopy);
            dest.writeString(isPopup);
        }
    }
}
