package com.xiaoniu.cleanking.ui.main.bean;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

@Entity(tableName = "HomeRecommend")
public class HomeRecommendListEntity implements Serializable {

    @NotNull
    @PrimaryKey
    private String id;
    private String sort;
    private String positionCode;
    private String name;
    private String content;
    private String iconUrl;
    private String buttonName;
    private String linkType;
    private String linkUrl;

    @NotNull
    public String getId() {
        return id;
    }

    public void setId(@NotNull String id) {
        this.id = id;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public void setPositionCode(String positionCode) {
        this.positionCode = positionCode;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public void setButtonName(String buttonName) {
        this.buttonName = buttonName;
    }

    public void setLinkType(String linkType) {
        this.linkType = linkType;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getPositionCode() {
        return positionCode;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public String getButtonName() {
        return buttonName;
    }

    public String getLinkType() {
        return linkType;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public String getSort() {
        return sort;
    }
}
