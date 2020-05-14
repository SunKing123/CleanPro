package com.hellogeek.permission.manufacturer;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.NotificationManagerCompat;
import android.telecom.TelecomManager;
import android.text.TextUtils;
import android.util.Log;


import com.hellogeek.permission.bean.CustomSharedPreferences;
import com.hellogeek.permission.server.AccessibilityServiceMonitor;
import com.hellogeek.permission.util.Constant;
import com.hellogeek.permission.util.NodeInfoUtil;
import com.hellogeek.permission.util.PhoneRomUtils;
import com.hellogeek.permission.widget.GuidePWindowView;
import com.hellogeek.permission.widget.SemiAutomaticToastGuide;
import com.hellogeek.permission.widget.floatwindow.FloatingWindow;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;

public class ManfacturerBase {
    private AccessibilityService accessibilityService;
    private Context mContext;
    private int count;
    private ArrayList<String> list = new ArrayList<>();
    public String SIGN1 = "sign1";
    public String SIGN2 = "sign2";
    public String SIGN3 = "sign3";
    public String SIGN4 = "sign4";


    public ArrayList<String> getList() {
        return list;
    }

    public void deductList() {
        if (list.size() > 0) {
            if (list.size() == 1) {
                list.remove(1);
            } else {
                list.remove(list.size() - 1);
            }
        }
    }

    public void addSIGN(String sign) {
        list.add(sign);
    }

    public void clearList() {
        if (list != null && list.size() > 0) {
            list.clear();
        }
        NodeInfoUtil.scrollNum=0;
        NodeInfoUtil.direction=0;
    }

    public ManfacturerBase(Context context) {
        this.mContext = context;
//        EventBus.getDefault().register(this);
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void removeHandler(RemoveEvent event) {
//        if (backHandler != null) {
//            if (backHandler.hasMessages(2)) {
//                backHandler.removeCallbacksAndMessages(null);
//            }
//        } else {
//        }
//    }

    private boolean isAutoFixActivity;
    @SuppressLint("HandlerLeak")
    protected Handler backHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            count++;
            if (count > 4) {
                return;
            }
            accessibilityService = (AccessibilityService) msg.obj;
            switch (msg.what) {
                case 1:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        if (Constant.inExecution) {
                            accessibilityService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                        }
//                        Log.i("permissionService", "GLOBAL_ACTION_BACK");
                    }

                    break;
                case 2:
                    ActivityManager am = (ActivityManager) mContext.getSystemService(ACTIVITY_SERVICE);
                    ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
                    if (!cn.getPackageName().equalsIgnoreCase(mContext.getPackageName()) || (!TextUtils.isEmpty(getTopActivityName(mContext)) && !getTopActivityName(mContext).equalsIgnoreCase(mContext.getPackageName()))) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            if (Constant.inExecution) {
                                accessibilityService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                            }
                            Log.i("permissionService", "GLOBAL_ACTION_BACK");
                        }
                        Message message = new Message();
                        message.obj = accessibilityService;
                        message.what = 2;
                        if (!hasMessages(2)) {
                            sendMessageDelayed(message, 600);
                        }
                    } else if (count == 1 || isAutoFixActivity) {
                        Log.e("permission", "count=" + count + "    isAutoFixActivity=" + isAutoFixActivity);
                        isAutoFixActivity = true;
                        if (!CustomSharedPreferences.getBooleanValue(mContext, CustomSharedPreferences.isActivity)) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                if (Constant.inExecution) {
                                    accessibilityService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                                }
                                Log.i("permissionService", "GLOBAL_ACTION_BACK");
                            }
                            Message message = new Message();
                            message.obj = accessibilityService;
                            message.what = 2;
                            if (!hasMessages(2)) {
                                sendMessageDelayed(message, 600);
                            }
                        } else {
                            count = 0;
                            isAutoFixActivity = false;
                        }
                    }

                    break;
            }

        }
    };

    //得到栈顶Activity的名字，注意此处要进行判断，Android在5.0以后Google把getRunningTasks的方法给屏蔽掉了，所以要分开处理
    private static String getTopActivityName(Context context) {
        String topActivityPackageName;
        ActivityManager manager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //此处要判断用户的安全权限有没有打开，如果打开了就进行获取栈顶Activity的名字的方法
            //当然，我们的要求是如果没打开就不获取了，要不然跳转会影响用户的体验
            if (isSecurityPermissionOpen(context)) {
                UsageStatsManager mUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
                long endTime = System.currentTimeMillis();
                long beginTime = endTime - 1000 * 60 * 2;
                UsageStats recentStats = null;

                List<UsageStats> queryUsageStats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, beginTime, endTime);
                if (queryUsageStats == null || queryUsageStats.isEmpty()) {
                    return null;
                }

                for (UsageStats usageStats : queryUsageStats) {
                    if (recentStats == null || recentStats.getLastTimeUsed() < usageStats.getLastTimeUsed()) {
                        recentStats = usageStats;
                    }
                }
                topActivityPackageName = recentStats.getPackageName();
                return topActivityPackageName;
            } else {
                return "";
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfos = manager.getRunningTasks(1);
            if (taskInfos.size() > 0)
                topActivityPackageName = taskInfos.get(0).topActivity.getPackageName();
            else
                return "";
            return topActivityPackageName;
        }
    }

    //判断用户对应的安全权限有没有打开
    private static boolean isSecurityPermissionOpen(Context context) {
        long endTime = System.currentTimeMillis();
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getApplicationContext().getSystemService("usagestats");
        List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, 0, endTime);
        if (queryUsageStats == null || queryUsageStats.isEmpty()) {
            return false;
        }
        return true;
    }


    /**
     * 检测 Huawei 悬浮窗权限
     */
    public static int checkFloatWindowPermission(Context context) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 19) {
            return checkOp(context, 24); //OP_SYSTEM_ALERT_WINDOW = 24;
        }
        return 1;
    }

    public void back(AccessibilityService service) {
        clearList();
        CustomSharedPreferences.setValue(mContext, CustomSharedPreferences.isActivity, false);
        Message message = new Message();
        message.obj = service;
        message.what = 2;
        backHandler.sendMessageDelayed(message, 0);
    }

    public void backOnlyOne(AccessibilityService service) {
        clearList();
        Message message = new Message();
        message.obj = service;
        message.what = 1;
        backHandler.sendMessageDelayed(message, 0);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static int checkOp(Context context, int op) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 19) {
            AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            try {
                Class clazz = AppOpsManager.class;
                Method method = clazz.getDeclaredMethod("checkOp", int.class, int.class, String.class);
                return ((Integer) method.invoke(manager, op, Binder.getCallingUid(), context.getPackageName())).intValue();
            } catch (Exception e) {
            }
        } else {
        }
        return 1;
    }


    /**
     * Android M 以上检查是否是系统默认电话应用
     *
     * @return
     */
    public static boolean isDefaultPhoneCallApp(Context mContext) {
        if (PhoneRomUtils.getPhoneModel().equalsIgnoreCase("NTS-AL00") || PhoneRomUtils.getPhoneModel().equalsIgnoreCase("NCE-AL10")
                || PhoneRomUtils.getPhoneModel().equalsIgnoreCase("vivo Y67A") || PhoneRomUtils.getPhoneModel().equalsIgnoreCase("vivo Y66")
                || PhoneRomUtils.getPhoneModel().equalsIgnoreCase("PLK-TL01H")) {
            return true;
        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            TelecomManager manger = (TelecomManager) mContext.getSystemService(Context.TELECOM_SERVICE);
            if (manger != null) {
                String name = manger.getDefaultDialerPackage();
                return TextUtils.equals(name, mContext.getPackageName());
            }
        } else {
            return isNotificationListenerEnableds(mContext);
        }
        return false;
    }


    /**
     * 协助检测系统拨号页面
     * 是否通知栏权限可用
     *
     * @return
     */
    public static boolean isNotificationListenerEnableds(Context mContext) {
        try {
            String packageName = mContext.getPackageName();
            String str1 = Settings.Secure.getString(mContext.getContentResolver()
                    , "enabled_notification_listeners");
            if (!TextUtils.isEmpty(str1)) {
                String[] strings = str1.split(":");
                for (String string : strings) {
                    ComponentName localComponentName
                            = ComponentName.unflattenFromString(string);
                    if ((localComponentName == null)
                            || (!TextUtils.equals(packageName, localComponentName.getPackageName()))) {
                    } else {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * vivo 小米 有效
     * 是否通知栏权限可用
     *
     * @return
     */
    public static boolean isNotificationListenerEnabled(Context mContext) {
        try {

            if (PhoneRomUtils.getPhoneModel().equalsIgnoreCase("vivo X7")) {
                NotificationManagerCompat manager = NotificationManagerCompat.from(mContext);
                return manager.areNotificationsEnabled();
            }
            String packageName = mContext.getPackageName();
            String str1 = Settings.Secure.getString(mContext.getContentResolver()
                    , "enabled_notification_listeners");
            if (!TextUtils.isEmpty(str1)) {
                String[] strings = str1.split(":");
                for (String string : strings) {
                    ComponentName localComponentName
                            = ComponentName.unflattenFromString(string);
                    if ((localComponentName == null)
                            || (!TextUtils.equals(packageName, localComponentName.getPackageName()))) {
                    } else {
                        return true;
                    }
                }
            }
            NotificationManagerCompat manager = NotificationManagerCompat.from(mContext);
            return manager.areNotificationsEnabled();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return false;
    }


    /**
     * 锁屏显示 1 关闭 0 开启
     *
     * @param context
     * @return
     */
    public static int getVivoLockedScreenPermissionStatus(Context context) {
        String packageName = context.getPackageName();
//        Uri uri2 = Uri.parse("content://com.iqoo.secure.provider.secureprovider/start_bg_activity");
        Uri uri2 = Uri.parse("content://com.vivo.permissionmanager.provider.permission/control_locked_screen_action");
        String selection = "pkgname = ?";
        String[] selectionArgs = new String[]{packageName};
        try {
            Cursor cursor = context
                    .getContentResolver()
                    .query(uri2, null, selection, selectionArgs, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int currentmode = cursor.getInt(cursor.getColumnIndex("currentstate"));
                    cursor.close();
                    return currentmode;
                } else {
                    cursor.close();
                    return 1;
                }
            } else {
                return getVivoLockedScreenPermissionStatusOld(context);
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return 1;
    }

    public static int getVivoLockedScreenPermissionStatusOld(Context context) {
        String packageName = context.getPackageName();
//        String packageName = "com.hellogeek.zuilaidian";
//        Uri uri2 = Uri.parse("content://com.iqoo.secure.provider.secureprovider/start_bg_activity");
        Uri uri2 = Uri.parse("content://com.iqoo.secure.provider.secureprovider/control_locked_screen_action");
        String selection = "pkgname = ?";
        String[] selectionArgs = new String[]{packageName};
        try {
            Cursor cursor = context
                    .getContentResolver()
                    .query(uri2, null, selection, selectionArgs, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int currentmode = cursor.getInt(cursor.getColumnIndex("currentstate"));
                    cursor.close();
                    return currentmode;
                } else {
                    cursor.close();
                    return 1;
                }
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return 1;
    }


    public static boolean isFloatPermissionOpen(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context is null");
        }

        try {
            String packageName = context.getPackageName();
            Uri uri = Uri.parse("content://com.iqoo.secure.provider.secureprovider/allowfloatwindowapp");
            String selection = "pkgname = ?";
            String[] selectionArgs = new String[]{packageName};
            Cursor cursor = context
                    .getContentResolver()
                    .query(uri, null, selection, selectionArgs, null);
            if (cursor != null) {
                cursor.getColumnNames();
                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex("currentlmode");
                    if (columnIndex == -1) {
                        return false;
                    }
                    int currentmode = cursor.getInt(columnIndex);
                    cursor.close();
                    return currentmode == 0;
                } else {
                    cursor.close();
                    return getFloatPermissionStatus2(context);
                }

            } else {
                return getFloatPermissionStatus2(context);
            }
        } catch (Exception e) {

        }

        return false;
    }

    /**
     * vivo比较新的系统获取方法
     *
     * @param context
     * @return
     */
    private static boolean getFloatPermissionStatus2(Context context) {
        try {
            String packageName = context.getPackageName();
            Uri uri2 = Uri.parse("content://com.vivo.permissionmanager.provider.permission/float_window_apps");
            String selection = "pkgname = ?";
            String[] selectionArgs = new String[]{packageName};
            Cursor cursor = context
                    .getContentResolver()
                    .query(uri2, null, selection, selectionArgs, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int currentMode = cursor.getInt(cursor.getColumnIndex("currentmode"));
                    cursor.close();
                    return currentMode == 0;
                } else {
                    cursor.close();
                    return false;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public static void floaWindow(Context mContext, Rect rect) {
        FloatingWindow mFloatWindow = new FloatingWindow(mContext);
        GuidePWindowView callInWindowView = new GuidePWindowView(mContext);
        callInWindowView.setWindowDismissListener(() -> mFloatWindow.dismiss());
        mFloatWindow.setContentView(callInWindowView);
        mFloatWindow.show();
        ((GuidePWindowView) mFloatWindow.getContentView()).setData(rect.top, rect.bottom);
//
    }

    public static void floaWindowToast(Context mContext, Rect rect) {
        new SemiAutomaticToastGuide().make(rect, mContext);
    }


}
