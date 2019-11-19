package com.xiaoniu.cleanking.ui.main.bean;

import com.xiaoniu.cleanking.base.BaseEntity;

import java.util.List;


public class IconsEntity extends BaseEntity {

    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {

        private String appName;
        private String tabName;
        private int orderNum;
        private String iconImgUrl;
        private String clickIconUrl;

        public String getAppName() {
            return appName;
        }

        public String getTabName() {
            return tabName;
        }

        public int getOrderNum() {
            return orderNum;
        }

        public String getIconImgUrl() {
            return iconImgUrl;
        }

        public String getClickIconUrl() {
            return clickIconUrl;
        }
    }
}
