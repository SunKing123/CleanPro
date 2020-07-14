package com.xiaoniu.cleanking.ui.main.bean;


import com.xiaoniu.cleanking.base.BaseEntity;

/**
 * deprecation:用户/token校验
 * author:ayb
 * time:2018/9/20
 */
public class CheckUserTokenBean extends BaseEntity {

    private boolean data;


    public boolean getData() {
        return data;
    }

    public void setData(boolean data) {
        this.data = data;
    }
}
