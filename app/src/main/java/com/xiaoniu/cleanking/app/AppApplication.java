package com.xiaoniu.cleanking.app;

import com.tencent.tinker.loader.app.TinkerApplication;
import com.tencent.tinker.loader.shareutil.ShareConstants;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by admin on 2017/7/13.
 */

public class AppApplication extends TinkerApplication {
    private static AppApplication sInstance;
    public static boolean isFromHome;
    public static String mapCoordinate;//坐标
    public static String provinceName;//省名
    public static String cityName;//市名
    public static String cityAreaName;//区（县）名
    public static Set<Integer> popSet = new HashSet<>();//运营弹窗是否一弹过位置统计
    public static String officialAccountContent;
    public static String officialAccountName;
    public static String AuditSwitch = "AuditSwitch";
    public static boolean isAudit;//是否市场审核中

    public AppApplication() {
        super(
                //tinkerFlags, which types is supported
                //dex only, library only, all support
                ShareConstants.TINKER_ENABLE_ALL,
                // This is passed as a string so the shell application does not
                // have a binary dependency on your ApplicationLifeCycle class.
                "com.xiaoniu.cleanking.hotfix.ApplicationDelegate");
        sInstance = this;
    }

    /**
     * @return App 全局上下文
     */
    public static AppApplication getInstance() {
        return sInstance;
    }
}
