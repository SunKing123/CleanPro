package com.xiaoniu.cleanking.ui.main.bean;

import java.util.HashSet;

/**
 * 已安装的app
 */
public class InstalledApp {

    public static final int APK_USER = 1;

    public static final int APK_SYS = 0;

    public int uid;
    public String packageName = "";

    public String versionName = "";

    public int versionCode;

    public String appName = "";

    public String size = "";

    public long totalFileSize = 0;

    public long lastUpdateTime = 0;

    public HashSet<String> signatures;

    /**
     * Only work for Notification clean settings list right now.
     */
    public boolean isSignedWithSysSignature;

    /**
     * 0代表系统应用，1代表用户安装应用
     */
    public int flag;
    public int flags;

    public int store;

    public String storeLocation;
}
