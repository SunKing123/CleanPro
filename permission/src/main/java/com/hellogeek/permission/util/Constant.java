package com.hellogeek.permission.util;

import android.os.Environment;

import com.hellogeek.permission.R2;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 常用常量
 * Created by tie on 2017/2/18.
 */

public class Constant {

    private static final String DEST_FILE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
    public static final String VIDEO_PATH = DEST_FILE_DIR + "zuilaidian/video/";

    public static final String SUCCESS = "200";

    public static final String COMMON_SUCCESS = "000000";
    public static boolean inExecution = false;

    //用户中心跳转通用H5 Activity字段
    //标题
    public static final String Title = "activity_title";
    public static final String TAG = "splash";

    //标题
    public static final String NoTitle = "NoTitle";
    //链接
    public static final String URL = "webview_url";

    /**
     * 广播跳转到首页标识
     */
    public static final String TAG_TURN_MAIN = "TAG_TURN_MAIN";


    public static final int PERMISSION_FLOAT = 1;//悬浮窗
    public static final int PERMISSION_LOCK_SHOW = 2;//锁屏
    public static final int PERMISSION_SYSTEM_SETTING = 3;//修改系统设置
    public static final int PERMISSION_STARTUP = 4;//自启动
    public static final int PERMISSION_NOTIFY = 5;//通知管理
    public static final int PERMISSION_ACTIVITY = 6;//后台弹出


    public static final int HUAWEI = 1;
    public static final int XIAOMI = 2;
    public static final int OPPO = 3;
    public static final int VIVO = 4;
    public static final int MEIZU = 5;
    public static final int SANXING = 6;
    public static final int LESHI = 7;


    public static final String NOVICE_GUIDE_1 = "NOVICE_GUIDE_1";
    public static final String NOVICE_GUIDE_2 = "NOVICE_GUIDE_2";
    public static final String NOVICE_GUIDE_3 = "NOVICE_GUIDE_3";
    public static final String NOVICE_GUIDE_4 = "NOVICE_GUIDE_4";


    public static final String PERMISSION_ALLOW = "PERMISSION_ALLOW";
    public static final String PERMISSION_AUTO_ALLOW = "PERMISSION_AUTO_ALLOW";


    /**
     * 各厂商ROM 版本KEY
     */
    public static final String ROM_HUAWEI = "ro.build.version.emui";
    public static final String ROM_XIAOMI = "ro.miui.ui.version.name";
    public static final String ROM_OPPO = "ro.build.version.opporom";
    public static final String ROM_VIVO = "ro.vivo.rom.version";
    public static final String ROM_MEIZU = "ro.build.display.id";
    public static final String ROM_LESHI = "ro.letv.release.version";
    public static final String ROM_SANXING = "ro.build.version.samsung";


    public static final String PROVIDER_SELFSTARTING = "permission_selfstarting";
    public static final String PROVIDER_SYSTEMSETTING = "permission_systemsetting";
    public static final String PROVIDER_REPLACEACLLPAGE = "permission_replaceacllpage";
    public static final String PROVIDER_NOTIFICATIONBAR = "permission_notificationbar";
    public static final String PROVIDER_NOTICEOFTAKEOVER = "permission_noticeoftakeover";
    public static final String PROVIDER_BACKSTAGEPOPUP = "permission_backstagepopup";
    public static final String PROVIDER_LOCKDISPLAY = "permission_lockdisplay";
    public static final String PROVIDER_SUSPENDEDTOAST = "permission_suspendedtoast";

    public static final String PROVIDER_PERMISSIONALLOPEN = "permission_isallOpen";
    public static final String PROVIDER_NECESSARY_PERMISSIONALLOPEN = "permission_necessary_isallOpen";

    // 应用使用情况
    public static final String PACKAGE_USAGE_STATS = "permission_packageusagestats";

    /**
     * 白名单
     */
    public static final List<String> WHITE_LIST = Arrays.asList(
            "com.geek.jk.weather",
            "com.xujin.weather",
            "com.yitong.weather",
            "com.geek.jk.calendar.app",
            "com.geek.luck.calendar.app",
            "com.xiaoniu",
            "com.xiaoniu.cleanking"
    );

}
