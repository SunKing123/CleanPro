package com.xiaoniu.cleanking.utils;

import android.support.annotation.StringRes;

import com.xiaoniu.cleanking.app.AppApplication;

/**
 * 关于资源操作方法的工具类
 * Created by lihao on 2019/03/06
 */
public final class ResourceUtils {

    private ResourceUtils() {
    }

    /**
     * 获取字符串资源值。
     *
     * @param res 资源 ID
     * @return 字符串资源值
     */
    public static String getString(@StringRes int res) {
        return AppApplication.getInstance().getString(res);
    }

    /**
     * 获取字符串资源值。
     *
     * @param res 资源 ID
     * @param args 格式化参数
     * @return 字符串资源值
     */
    public static String getString(@StringRes int res, Object... args) {
        return AppApplication.getInstance().getString(res, args);
    }

    /**
     * 获取字符串数组资源。
     *
     * @param array 字符串数组资源 ID
     * @return 字符串数组资源
     */
    public static String[] getStringArray(int array) {
        return  AppApplication.getInstance().getResources().getStringArray(array);
    }
}