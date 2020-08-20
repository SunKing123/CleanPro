package com.xiaoniu.cleanking.scheme.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.fragment.app.Fragment;

import com.xiaoniu.cleanking.scheme.Constant.SchemeConstant;
import com.xiaoniu.cleanking.ui.usercenter.activity.BrowserActivity;
import com.xiaoniu.cleanking.ui.usercenter.activity.UserLoadH5Activity;
import com.xiaoniu.cleanking.utils.LogUtils;

/**
 * deprecation:协议跳转工具类
 * author:ayb
 * time:2017-6-8
 */
public class SchemeUtils {

    /**
     * 是否是协议
     *
     * @param url 协议地址
     * @return 是否为协议
     */
    public static boolean isScheme(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        Uri uri = Uri.parse(url);
        String scheme = uri.getScheme();
        String host = uri.getHost();
        return (TextUtils.equals(SchemeConstant.SCHEME, scheme) && TextUtils.equals(SchemeConstant.HOST, host));
    }

    /**
     * 打开协议
     *
     * @param context 上下文
     * @param scheme  协议地址
     * @return 是否可以打开
     */
    public static boolean openScheme(Context context, String scheme) {
        return openScheme(context, null, scheme, null, -1);
    }


    /**
     * 打开协议
     *
     * @param activity    上下文
     * @param scheme      协议地址
     * @param extras      Bundle参数
     * @param requestCode 启动code
     * @return 是否可以打开
     */
    public static boolean openScheme(Activity activity, String scheme,
                                     Bundle extras, int requestCode) {
        if (TextUtils.isEmpty(scheme)) {
            return false;
        }
        final Uri uri = Uri.parse(scheme);
        String tScheme = uri.getScheme();
        if (!SchemeConstant.SCHEME.equals(tScheme)) {
            return false;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setPackage(activity.getPackageName());
        if (extras != null) {
            intent.putExtras(extras);
        }
        if (ActivityUtils.queryActivityIntent(activity, intent)) {
            if (requestCode >= 0) {
                activity.startActivityForResult(intent, requestCode);
            } else {
                activity.startActivity(intent);
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * 打开协议
     *
     * @param context     上下文
     * @param fragment    上层Fragment
     * @param scheme      协议地址
     * @param extras      Bundle参数
     * @param requestCode 启动code
     * @return 是否可以打开
     */
    public static boolean openScheme(Context context, Fragment fragment,
                                     String scheme, Bundle extras, int requestCode) {
        if (TextUtils.isEmpty(scheme)) {
            return false;
        }
        final Uri uri = Uri.parse(scheme);
        String tScheme = uri.getScheme();
        if (!SchemeConstant.SCHEME.equals(tScheme)) {
            return false;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setPackage(context.getPackageName());
        if (extras != null) {
            intent.putExtras(extras);
        }
        if (ActivityUtils.queryActivityIntent(context, intent)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (requestCode >= 0) {
                if (fragment != null) {
                    fragment.startActivityForResult(intent, requestCode);
                } else if (context instanceof Activity) {
                    ((Activity) context).startActivityForResult(intent, requestCode);
                }
            } else {
                context.startActivity(intent);
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * native和native_new协议跳转下一个页面
     *
     * @param context 上下文
     * @param aClass  Activity类名
     * @param bundle  Bundle参数
     */
    public static void toNextPage(Context context, Class<?> aClass, Bundle bundle) {
        try {
            Intent toIntent = new Intent(context, aClass);
            if (bundle != null) {
                toIntent.putExtras(bundle);
            }
            toIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            if (context instanceof UserLoadH5Activity) {
                ((UserLoadH5Activity) context).startActivityForResult(toIntent, ActivityUtils.REQUEST_CODE_OPEN_BROWSER);
            } else {
                context.startActivity(toIntent);
            }
        } catch (Exception e) {
            LogUtils.e(e.getMessage());
        }
    }

    /**
     * jump跳转下一个页面
     *
     * @param context 上下文
     * @param bundle  Bundle参数
     */
    public static void toNextPage(Context context, Bundle bundle) {
        try {
            Intent toIntent = new Intent(context, UserLoadH5Activity.class);
            toIntent.setPackage(context.getPackageName());
            toIntent.putExtras(bundle);
            ((Activity) context).startActivityForResult(toIntent, ActivityUtils.REQUEST_CODE_OPEN_BROWSER);
        } catch (Exception e) {
            LogUtils.e(e.getMessage());
        }
    }

    /**
     * jump跳转下一个页面
     *
     * @param context 上下文
     * @param bundle  Bundle参数
     */
    public static void toBrowserPage(Context context, Bundle bundle) {
        try {
            Intent toIntent = new Intent(context, BrowserActivity.class);
            toIntent.setPackage(context.getPackageName());
            toIntent.putExtras(bundle);
            context.startActivity(toIntent);
        } catch (Exception e) {
            LogUtils.e(e.getMessage());
        }
    }
}
