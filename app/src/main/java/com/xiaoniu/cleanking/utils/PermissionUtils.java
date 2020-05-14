package com.xiaoniu.cleanking.utils;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

/**
 * @author \XiLei
 * @date 2019/10/19.
 * description：权限管理类
 */
public class PermissionUtils {

    private static String TAG = PermissionUtils.class.getName();

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


    /**
     * @param context     上下文
     * @param permissions 权限列表
     * @return boolean 是否具备
     * @method checkPermission
     * @description 检测是否具备当前权限
     * @date: 2020/5/9 11:46
     * @author: LiDing
     */
    public static boolean checkPermission(Context context, String[] permissions) {
        PackageManager packageManager = context.getPackageManager();
        String packageName = context.getPackageName();

        for (String permission : permissions) {
            int per = packageManager.checkPermission(permission, packageName);
            if (PackageManager.PERMISSION_DENIED == per) {
                Log.w(TAG, "required permission not granted . permission = " + permission);
                return false;
            }
        }
        return true;
    }


}
