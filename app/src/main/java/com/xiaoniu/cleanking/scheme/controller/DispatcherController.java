package com.xiaoniu.cleanking.scheme.controller;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.xiaoniu.cleanking.app.Constant;
import com.xiaoniu.cleanking.scheme.Constant.SchemeConstant;
import com.xiaoniu.cleanking.scheme.utils.Parameters;
import com.xiaoniu.cleanking.scheme.utils.SchemeUtils;
import com.xiaoniu.cleanking.scheme.utils.UrlUtils;
import com.xiaoniu.cleanking.utils.LogUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * deprecation:协议分发器
 * author:ayb
 * time:2017-6-8
 */
public class DispatcherController {
    //native协议键值Map[注意:添加Activity需要放在静态代码块]
    public static Map<String, Class<?>> sNativeClassMap = new HashMap<>();

    private static DispatcherController sInstance = new DispatcherController();

    public static DispatcherController getInstance(){
        return sInstance;
    }

    private DispatcherController(){
    }

    public boolean dispatch(final Activity activity, Intent intent){
        Uri uri = intent.getData();
        if (uri == null) {
            return false;
        }
        String allUrl = uri.toString();
        if (TextUtils.isEmpty(allUrl)) {
            return false;
        }
        String path = uri.getPath();
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        if (!SchemeUtils.isScheme(allUrl)) {
            return false;
        }
        Parameters parameters = UrlUtils.getParamsFromUrl(allUrl);
        Set<String> keys = parameters.getParameterNames();
        if (TextUtils.equals(SchemeConstant.JUMP,path)){
            String H5Url = parameters.getParameter(SchemeConstant.H5_URL);
            String isNoTitleStr = parameters.getParameter(SchemeConstant.H5_IS_NO_TITLE);
            String title = parameters.getParameter(SchemeConstant.H5_TITLE);
            boolean isNoTitle = TextUtils.equals("1",isNoTitleStr);
            Bundle bundle = new Bundle();
            bundle.putString(Constant.URL, H5Url);
            if (!TextUtils.isEmpty(title)){
                bundle.putString(Constant.Title, title);
            }
            bundle.putBoolean(Constant.NoTitle, isNoTitle);
            SchemeUtils.toNextPage(activity, bundle);
            return true;
        }else if (TextUtils.equals(SchemeConstant.NATIVE,path)){
            String name = parameters.getParameter(SchemeConstant.NATIVE_NAME);
            final Bundle bundle = new Bundle();
            final Class<?> activityClass = sNativeClassMap.get(name);
            if (activityClass == null) {
                return false;
            }
            if (keys != null) {
                for (String key : keys) {
                    if (!key.equals(SchemeConstant.NATIVE_NAME)) {
                        String value = parameters.getParameter(key);
                        bundle.putString(key, value);
                    }
                }
            }
            SchemeUtils.toNextPage(activity,activityClass,bundle);
        }else if (TextUtils.equals(SchemeConstant.NATIVE_NO_PARAMS,path)){
            String activityName = parameters.getParameter(SchemeConstant.ANDROID_NAME);
            try {
                String className = activity.getPackageName() + ".ui." + activityName;
                final Class clazz = Class.forName(className);
                final Bundle bundle = new Bundle();
                if (keys != null) {
                    for (String key : keys) {
                        String value = parameters.getParameter(key);
                        bundle.putString(key, value);
                    }
                }
                SchemeUtils.toNextPage(activity,clazz,bundle);
            }catch (Exception e){
                LogUtils.e(e.getMessage());
            }
        }
        return false;
    }

}
