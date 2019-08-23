package com.xiaoniu.cleanking.ui.tool.wechat.bean;

import com.xiaoniu.common.widget.xrecyclerview.MultiItemInfo;

import java.io.File;

public class CleanWxChildInfo extends MultiItemInfo<CleanWxChildInfo> {
    public File file;
    public int fileType;
    public int Days;
    public boolean canLoadPic = true;
    public String stringDay;
}