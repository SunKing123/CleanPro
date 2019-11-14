package com.xiaoniu.cleanking.ui.main.bean;

import com.xiaoniu.cleanking.base.BaseEntity;

/**
 * 互动式广告
 */
public class VirusLlistEntity extends BaseEntity {

    private int icon;
    private String name;

    public VirusLlistEntity(int icon, String name) {
        this.icon = icon;
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }
}
