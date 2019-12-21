package com.xiaoniu.cleanking.utils.quick;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.umeng.commonsdk.debug.E;
import com.xiaoniu.cleanking.ui.main.widget.SPUtil;

/**
 * @author : lvdongdong
 * @since 2019/12/19
 */
public class QuickUtils {
    private static ShortcutManager shortcutManager;
    private static QuickUtils quickUtils;
    private Context mActivity;

    public QuickUtils(Context context) {
        this.mActivity = context;
        mPm = context.getPackageManager();
    }

    public static QuickUtils getInstant(Context context) {
        if (quickUtils == null) {
            synchronized (QuickUtils.class) {
                if (quickUtils == null) {
                    quickUtils = new QuickUtils(context);
                }
            }
        }
        return quickUtils;
    }


    public static QuickUtils getQuickUtils() {
        return quickUtils;
    }

    /**
     * 添加桌面图标快捷方式
     *
     * @param name         快捷方式名称
     * @param icon         快捷方式图标
     * @param actionIntent 快捷方式图标点击动作
     */
    public void addShortcut(String name, Bitmap icon, Intent actionIntent) {
        if (SPUtil.getBoolean(mActivity, SPUtil.ISADDQUICK, false)) {
            Log.e("MainsActivity", "快捷方式已存在");
            return;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            try {
                //  创建快捷方式的intent广播
                Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
                // 添加快捷名称
                shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
                //  快捷图标是允许重复(不一定有效)
                shortcut.putExtra("duplicate", false);
                shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON, icon);
                // 添加携带的下次启动要用的Intent信息
                shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, actionIntent);
                // 发送广播
                mActivity.sendBroadcast(shortcut);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        disableMainComponent();
                    }
                }, 5000);
            } catch (Exception e) {
            }
        } else {
            try {
                if (shortcutManager == null) {
                    shortcutManager = (ShortcutManager) mActivity.getSystemService(Context.SHORTCUT_SERVICE);
                }
                if (null == shortcutManager) {
                    // 创建快捷方式失败
                    Log.e("MainsActivity", "Create shortcut failed");
                    return;
                }
                ShortcutInfo shortcutInfo = new ShortcutInfo.Builder(mActivity, name)
                        .setShortLabel(name)
                        .setIcon(Icon.createWithBitmap(icon))
                        .setIntent(actionIntent)
                        .build();
                //当添加快捷方式的确认弹框弹出来时，将被回调
                PendingIntent shortcutCallbackIntent = PendingIntent.getBroadcast(mActivity, 0, new Intent(mActivity, QuickSucessCallbackReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT);

                shortcutManager.requestPinShortcut(shortcutInfo, shortcutCallbackIntent.getIntentSender());
            } catch (Exception e) {
            }

        }
    }

    /**
     *      * 判断快捷方式是否存在
     *      *
     *      * @param context 上下文
     *      * @param title   快捷方式标志，不能和其它应用相同
     *      * @return
     *      
     */
    public static boolean isShortCutExist(Context context, String title) {

        boolean isInstallShortcut = false;

        if (null == context || TextUtils.isEmpty(title))
            return isInstallShortcut;
        String authority = getAuthority();
        final ContentResolver cr = context.getContentResolver();
        if (!TextUtils.isEmpty(authority)) {
            try {
                final Uri CONTENT_URI = Uri.parse(authority);

                Cursor c = cr.query(CONTENT_URI, new String[]{"title", "iconResource"}, "title=?", new String[]{title.trim()}, null);

// XXX表示应用名称。
                if (c != null && c.getCount() > 0) {
                    isInstallShortcut = true;
                }
                if (null != c && !c.isClosed()) c.close();
            } catch (Exception e) {
                Log.e("dong", "isShortCutExist:" + e.getMessage());
            }
        }
        return isInstallShortcut;
    }

    public static String getAuthority() {
        String authority;
        int sdkInt = android.os.Build.VERSION.SDK_INT;
        if (sdkInt < 8) { // Android 2.1.x(API 7)以及以下的
            authority = "com.android.launcher.settings";
        } else if (sdkInt <= 19) {// Android 4.4及以下
            authority = "com.android.launcher2.settings";
        } else {// 4.4以上
            authority = "com.android.launcher3.settings";
        }
        return "content://" + authority + "/favorites?notify=true";
    }


    private PackageManager mPm;

    public void enableComponent(ComponentName componentName) {
        mPm.setComponentEnabledSetting(componentName,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    public void disableComponent(ComponentName componentName) {
        int state = mPm.getComponentEnabledSetting(componentName);
        if (PackageManager.COMPONENT_ENABLED_STATE_DISABLED == state) {
            //已经禁用
            return;
        }
        mPm.setComponentEnabledSetting(componentName,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

    public void disableMainComponent() {
        SPUtil.setBoolean(mActivity, SPUtil.ISADDQUICK, true);
        ComponentName orange1 = new ComponentName(mActivity,
                "com.xiaoniu.cleanking.other");
        enableComponent(orange1);
        ComponentName orange = new ComponentName(mActivity,
                "com.xiaoniu.cleanking.spa1");
        int state = mPm.getComponentEnabledSetting(orange);
        if (PackageManager.COMPONENT_ENABLED_STATE_DISABLED == state) {
            //已经禁用
            return;
        }
        mPm.setComponentEnabledSetting(orange,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);


    }
}
