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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean extends BaseEntity {
        /**
         * userId : 4233063489985318913
         * phone : 18955310615
         * nickname : zhangsan
         * userAvatar : zhangsan
         * openId : abcdefghijklmn123456
         * token : 4399563222974205952
         */

        private String userId;
        private String phone;
        private String nickname;
        private String userAvatar;
        private String openId;
        private String token;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getUserAvatar() {
            return userAvatar;
        }

        public void setUserAvatar(String userAvatar) {
            this.userAvatar = userAvatar;
        }

        public String getOpenId() {
            return openId;
        }

        public void setOpenId(String openId) {
            this.openId = openId;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}
