package com.xiaoniu.cleanking.ui.main.bean;

import com.xiaoniu.cleanking.base.BaseEntity;


public class CheckAdConfigUpdateEntity extends BaseEntity {

    private DataBean data;


    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private int isUpdate; //	1 需要更新，0-不需要更新

        public int getIsUpdate() {
            return isUpdate;
        }

        public void setIsUpdate(int isUpdate) {
            this.isUpdate = isUpdate;
        }
    }

}
