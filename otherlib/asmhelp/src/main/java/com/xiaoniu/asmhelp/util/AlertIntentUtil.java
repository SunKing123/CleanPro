package com.xiaoniu.asmhelp.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;

/**
 * Desc:
 * <p>
 *
 * @author: ZhouTao
 * Date: 2019/7/2
 * Copyright: Copyright (c) 2016-2020
 * Company: @小牛科技
 * Update
 */
public class AlertIntentUtil {
    /* renamed from: a */
    private static final String MANUFACTURER = Build.MANUFACTURER.toLowerCase();

    /* renamed from: a */
    public void start(Context context, int i) {
        Intent intent;
        if (MANUFACTURER.contains("huawei")) {
            intent = gethuawei(context);
        } else if (MANUFACTURER.contains("xiaomi")) {
            intent = getxiaomi(context);
        } else if (MANUFACTURER.contains("oppo")) {
            intent = getoppo(context);
        } else if (MANUFACTURER.contains("vivo")) {
            intent = getvivo(context);
        } else if (MANUFACTURER.contains("meizu")) {
            intent = getmeizu(context);
        } else {
            intent = getot(context);
        }
        try {
            startActivityForResult(context, intent, i);
        } catch (Exception unused) {
            startActivityForResult(context, getot(context), i);
        }
    }

    public void startActivityForResult(Context context, Intent intent, int i) {
        if (context instanceof Activity) {
            ((Activity) context).startActivityForResult(intent, i);
            return;
        }
        intent.setFlags(268435456);
        context.startActivity(intent);
    }

    private Intent getoppo(Context context) {
        Intent intent = new Intent();
        intent.putExtra("packageName", context.getPackageName());
        intent.setClassName("com.color.safecenter", "com.color.safecenter.permission.floatwindow.FloatWindowListActivity");
        if (queryIntentActivities(context, intent)) {
            return intent;
        }
        intent.setClassName("com.coloros.safecenter", "com.coloros.safecenter.sysfloatwindow.FloatWindowListActivity");
        if (queryIntentActivities(context, intent)) {
            return intent;
        }
        intent.setClassName("com.oppo.safe", "com.oppo.safe.permission.PermissionAppListActivity");
        return intent;
    }

    private Intent getmeizu(Context context) {
        Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
        intent.putExtra("packageName", context.getPackageName());
        intent.setComponent(new ComponentName("com.meizu.safe", "com.meizu.safe.security.AppSecActivity"));
        return intent;
    }

    private static Intent getot(Context context) {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        return intent;
    }

    private Intent getvivo(Context context) {
        Intent intent = new Intent();
        intent.putExtra("packagename", context.getPackageName());
        //6.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.SoftPermissionDetailActivity"));
        } else {
            intent.setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.MainGuideActivity"));
        }
        if (queryIntentActivities(context, intent)) {
            return intent;
        }

        return intent;
    }

    private Intent gethuawei(Context context) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity"));
        if (queryIntentActivities(context, intent)) {
            return intent;
        }
        intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.addviewmonitor.AddViewMonitorActivity"));
        if (queryIntentActivities(context, intent)) {
            return intent;
        }
        intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.notificationmanager.ui.NotificationManagmentActivity"));
        return intent;
    }

    private Intent getxiaomi(Context context) {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.putExtra("extra_pkgname", context.getPackageName());
        if (queryIntentActivities(context, intent)) {
            return intent;
        }
        intent.setPackage("com.miui.securitycenter");
        if (queryIntentActivities(context, intent)) {
            return intent;
        }
        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
        return intent;
    }

    private static boolean queryIntentActivities(Context context, Intent intent) {
        return context.getPackageManager().queryIntentActivities(intent, 65536).size() > 0;
    }


}
