package com.xiaoniu.cleanking.ui.main.bean;

import com.xiaoniu.cleanking.base.BaseEntity;

import java.util.List;

/**
 * @author zhengzhihao
 * @date 2020/7/6 12
 * @mail：zhengzhihao@hellogeek.com
 */
public class BubbleConfig extends BaseEntity {


    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 8
         * appName : wk_clean
         * userId : 8718303966116450304
         * goldCount : 7158
         * locationNum : 3
         * goldMaxTotalCount : 99999
         */

        private int id;
        private String appName;
        private String userId;
        private int goldCount;
        private int locationNum;
        private int goldMaxTotalCount;
        private String iconUrl;


        public String getIconUrl() {
            return iconUrl;
        }

        public void setIconUrl(String iconUrl) {
            this.iconUrl = iconUrl;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getAppName() {
            return appName;
        }

        public void setAppName(String appName) {
            this.appName = appName;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public int getGoldCount() {
            return goldCount;
        }

        public void setGoldCount(int goldCount) {
            this.goldCount = goldCount;
        }

        public int getLocationNum() {
            return locationNum;
        }

        public void setLocationNum(int locationNum) {
            this.locationNum = locationNum;
        }

        public int getGoldMaxTotalCount() {
            return goldMaxTotalCount;
        }

        public void setGoldMaxTotalCount(int goldMaxTotalCount) {
            this.goldMaxTotalCount = goldMaxTotalCount;
        }
    }
}
