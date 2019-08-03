package com.xiaoniu.cleanking.ui.tool.wechat.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CleanWxFourItemInfo implements MultiItemEntity {
    private int Days;
    private List<CleanWxItemInfo> fourItem = Collections.synchronizedList(new ArrayList());
    private int itemType = 11;

    public List<CleanWxItemInfo> getFourItem() {
        return this.fourItem;
    }

    public int getItemType() {
        return this.itemType;
    }

    public void setItemType(int i) {
        this.itemType = i;
    }

    public int getDays() {
        return this.Days;
    }

    public void setDays(int i) {
        this.Days = i;
    }

    public String toString() {
        return "CleanWxItemInfo{, Days=" + this.Days + '}';
    }
}