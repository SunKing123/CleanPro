package com.xiaoniu.cleanking.ui.login.bean;


import com.xiaoniu.cleanking.base.BaseEntity;

public class BindPhoneBean extends BaseEntity {


    /**
     * requestId : 1111111
     * timestamp : 1569034706541
     * code : 200
     * message : 请求成功
     * data : {"userId":"4233063489985318913","phone":"18955310615","nickname":"zhangsan","userAvatar":"zhangsan","openId":"abcdefghijklmn123456","token":"4399563222974205952"}
     */

    private String requestId;
    private long timestamp;

    private UserInfoBean data;

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UserInfoBean getData() {
        return data;
    }

    public void setData(UserInfoBean data) {
        this.data = data;
    }
}
