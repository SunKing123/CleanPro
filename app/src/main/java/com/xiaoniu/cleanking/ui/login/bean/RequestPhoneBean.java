package com.xiaoniu.cleanking.ui.login.bean;


import com.xiaoniu.cleanking.base.BaseEntity;

import java.io.Serializable;

public class RequestPhoneBean extends BaseEntity {

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean implements Serializable {
        /**
         * -- phone	string	绑定手机号
         */
        String phone;

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
    }
}
