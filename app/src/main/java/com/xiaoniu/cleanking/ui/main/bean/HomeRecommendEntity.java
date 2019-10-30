package com.xiaoniu.cleanking.ui.main.bean;

import com.xiaoniu.cleanking.base.BaseEntity;

import java.util.List;


public class HomeRecommendEntity extends BaseEntity {

    private List<HomeRecommendListEntity> data;

    public List<HomeRecommendListEntity> getData() {
        return data;
    }

    public void setData(List<HomeRecommendListEntity> data) {
        this.data = data;
    }

}
