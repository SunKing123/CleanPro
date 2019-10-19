package com.xiaoniu.cleanking.utils;

import android.app.AppOpsManager;
import android.content.Context;
import android.os.Build;

/**
 * @author XiLei
 * @date 2019/10/19.
 * description：权限管理类
 */
public class PermissionUtils {

    /**
     * 获取查看应用使用情况权限
     *
     * @return
     */
    public static boolean isUsageAccessAllowed(Context context) {
        if (Build.VERSION.SDK_INT >= 21) {
            try {
                AppOpsManager manager = ((AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE));
                if (manager == null) return false;
                int mode = manager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.getPackageName());
                return mode == AppOpsManager.MODE_ALLOWED;
            } catch (Throwable ignored) {
            }
            return false;
        }
        return true;
    }
}
