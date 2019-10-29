package com.xiaoniu.cleanking.ui.tool.notify.bean;

import android.app.PendingIntent;
import android.graphics.Bitmap;

import java.io.Serializable;

public class NotificationInfo implements Serializable {
    public String appName = "";
    public String pkg = "";
    public CharSequence title = "";
    public CharSequence content = "";
    public Bitmap icon;
    public long time;
    public PendingIntent intent;
}
