package com.xiaoniu.cleanking.ui.main.bean;

import com.google.gson.annotations.SerializedName;
import com.xiaoniu.cleanking.base.BaseEntity;

import java.util.List;


public class RedPacketEntity extends BaseEntity {

    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {

        private String id;
        private String title;
        private int trigger;
        private String location;
        private String remark;
        private String appName;
        private int showType;
        private List<String> imgUrls;
        private List<String> jumpUrls;

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public int getTrigger() {
            return trigger;
        }

        public String getLocation() {
            return location;
        }

        public String getRemark() {
            return remark;
        }

        public String getAppName() {
            return appName;
        }

        public int getShowType() {
            return showType;
        }

        public List<String> getImgUrls() {
            return imgUrls;
        }

        public List<String> getJumpUrls() {
            return jumpUrls;
        }
    }
}
