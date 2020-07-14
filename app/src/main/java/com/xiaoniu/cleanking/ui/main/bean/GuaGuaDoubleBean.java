package com.xiaoniu.cleanking.ui.main.bean;

import com.xiaoniu.cleanking.base.BaseEntity;

/**
 * Created by zhaoyingtao
 * Date: 2020/7/14
 * Describe:
 */
public class GuaGuaDoubleBean extends BaseEntity {


    /**
     * requestId : bf1a07446d4b4c85b0723b82f47e6120
     * timestamp : 1593755300782
     * data : {"area":2,"gold":20,"date":"20200703","appName":1,"userId":"6793823640115736571"}
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

    public static class DataBean {
        /**
         * area : 2
         * gold : 20
         * date : 20200703
         * appName : 1
         * userId : 6793823640115736571
         */

        private String area;
        private int gold;
        private String date;
        private String appName;
        private String userId;

        public String getArea() {
            return area;
        }

        public void setArea(String area) {
            this.area = area;
        }

        public int getGold() {
            return gold;
        }

        public void setGold(int gold) {
            this.gold = gold;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
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
    }
}
