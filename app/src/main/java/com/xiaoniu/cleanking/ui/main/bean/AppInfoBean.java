package com.xiaoniu.cleanking.ui.main.bean;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

/**
 * 应用信息
 * Created by lang.chen on 2019/7/1
 */
public class AppInfoBean implements Serializable {

    public Drawable icon;
    public String name;
    public long installTime;
    //安装包大小
    public long packageSize;
    //储存大小
    public long storageSize;
    //包名
    public String packageName="";
    //版本号
    public String versionName;
    //是否已安装
    public boolean isInstall;
    //文件路径
    public String path;
    //是否可选择
    public boolean isSelect;

    @Override
    public String toString() {
        return "AppInfoBean{" +
                ", name='" + name + '\'' +
                ", installTime=" + installTime +
                ", packageSize='" + packageSize + '\'' +
                ", storageSize='" + storageSize + '\'' +
                '}';
    }
}
