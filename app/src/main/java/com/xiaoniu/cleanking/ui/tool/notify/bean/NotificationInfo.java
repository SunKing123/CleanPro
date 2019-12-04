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

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPkg() {
        return pkg;
    }

    public void setPkg(String pkg) {
        this.pkg = pkg;
    }

    public CharSequence getTitle() {
        return title;
    }

    public void setTitle(CharSequence title) {
        this.title = title;
    }

    public CharSequence getContent() {
        return content;
    }

    public void setContent(CharSequence content) {
        this.content = content;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public PendingIntent getIntent() {
        return intent;
    }

    public void setIntent(PendingIntent intent) {
        this.intent = intent;
    }
}
