package com.xiaoniu.cleanking.ui.login.bean;

import com.xiaoniu.cleanking.base.BaseEntity;

/**
 * Created by zhaoyingtao
 * Date: 2020/7/5
 * Describe:
 */
public class LoginDataBean extends BaseEntity {

    /**
     * requestId : 1111111
     * timestamp : 1569320543153
     * code : 200
     * message : 请求成功
     * data : {"userId":"5637684114090889216","userType":2,"phone":"","nickname":"游客4devic","userAvatar":"http://pic.58pic.com/58pic/15/14/29/47e58PICQUR_1024.jpg","openId":"deviceid22222222","token":"5637977468947140608","isLogin":"1"}
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
