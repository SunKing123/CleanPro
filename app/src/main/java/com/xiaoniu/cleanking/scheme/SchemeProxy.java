package com.xiaoniu.cleanking.scheme;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.xiaoniu.cleanking.app.Constant;
import com.xiaoniu.cleanking.scheme.Constant.SchemeConstant;
import com.xiaoniu.cleanking.scheme.controller.DispatcherController;
import com.xiaoniu.cleanking.scheme.utils.SchemeUtils;
import com.xiaoniu.cleanking.ui.main.activity.MainActivity;
import com.xiaoniu.cleanking.ui.usercenter.activity.UserLoadH5Activity;

/**
 * deprecation:代理入口
 * author:ayb
 * time:2017-6-8
 */
public class SchemeProxy {

    //静态代码块预加载native协议
    static {
        DispatcherController.sNativeClassMap.put(SchemeConstant.NATIVE_MAIN, MainActivity.class);
    }

    /**
     * 打开协议
     * @param context   上下文
     * @param scheme    协议地址
     */
    public static void openScheme(Context context, String scheme) {
        if (TextUtils.isEmpty(scheme)) {
            return;
        }
        if (SchemeUtils.isScheme(scheme)) {
            SchemeUtils.openScheme(context, scheme);
        } else {
            startBrowserActivity(context,scheme);
        }
    }

    private static void startBrowserActivity(Context context,String scheme){
        Bundle bundle = new Bundle();
        bundle.putString(Constant.URL, scheme);
        bundle.putBoolean(Constant.NoTitle, false);
        Intent intent = new Intent(context, UserLoadH5Activity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 协议分发
     * @param activity   上下文
     * @param intent    意图
     */
    public static void dispatcher(Activity activity, Intent intent){
        if (activity != null && intent != null){
            activity.setIntent(intent);
            DispatcherController.getInstance().dispatch(activity,intent);
        }
    }

    /**
     * 本地协议跳转
     * @param context   上下文
     * @param isNative   是否native_new协议
     * @param nativeSchemeName native的协议名称或者native_new的页面名称
     * @param extraParams   额外参数
     */
    public static void actionToNative(Context context, boolean isNative, String nativeSchemeName, String extraParams){
        String schemeUrl = SchemeConstant.SCHEME +"://"+SchemeConstant.HOST;
        if (isNative){
            schemeUrl = schemeUrl+"/native?name="+nativeSchemeName+extraParams;
        }else {
            schemeUrl = schemeUrl+"/native_new?a_name="+nativeSchemeName+extraParams;
        }
        openScheme(context,schemeUrl);
    }


}
