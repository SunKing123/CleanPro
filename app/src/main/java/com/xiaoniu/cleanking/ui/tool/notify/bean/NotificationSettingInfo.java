package com.xiaoniu.cleanking.ui.tool.notify.bean;

import android.graphics.Bitmap;

import java.io.Serializable;

public class NotificationSettingInfo implements Serializable {
    public String appName = "";
    public String pkg = "";
    public Bitmap icon;
    public boolean selected;

}
