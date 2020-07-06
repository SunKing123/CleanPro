package com.xiaoniu.cleanking.ui.login.bean;


import com.xiaoniu.cleanking.base.BaseEntity;

public class IsPhoneBindBean extends BaseEntity {


    /**
     * requestId : 75aa5841bbfb4489905f64039f3385f4
     * timestamp : 1569035897087
     * code : 200
     * message : 请求成功
     * data : {"isBinded":"1"}
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

    public static class DataBean extends BaseEntity {
        /**
         * isBinded : 1
         */

        private String isBinded;

        public String getIsBinded() {
            return isBinded;
        }

        public void setIsBinded(String isBinded) {
            this.isBinded = isBinded;
        }
    }
}
