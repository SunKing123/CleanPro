package com.xiaoniu.cleanking.ui.main.bean;


import com.xiaoniu.cleanking.base.BaseEntity;

/**
 * deprecation审核
 * author:ayb
 * time:2018/9/20
 */
public class AuditSwitch extends BaseEntity {
//    状态（0=隐藏，1=显示）
    private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
