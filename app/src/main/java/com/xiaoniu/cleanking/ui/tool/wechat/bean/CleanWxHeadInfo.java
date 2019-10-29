package com.xiaoniu.cleanking.ui.tool.wechat.bean;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;

public class CleanWxHeadInfo extends AbstractExpandableItem<CleanWxFourItemInfo> implements MultiItemEntity {
    private int Days;
    private boolean isChecked;
    private String stringDay;
    private String title;
    private int totalNum;

    public int getTotalNum() {
        return this.totalNum;
    }

    public void setTotalNum(int i) {
        this.totalNum = i;
    }

    public String getStringDay() {
        return this.stringDay;
    }

    public void setStringDay(String str) {
        this.stringDay = str;
    }

    public int getDays() {
        return this.Days;
    }

    public void setDays(int i) {
        this.Days = i;
    }

    public int getItemType() {
        return 1;
    }

    public int getLevel() {
        return 0;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String str) {
        this.title = str;
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    public void setChecked(boolean z) {
        this.isChecked = z;
    }
}