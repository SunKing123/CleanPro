package com.xiaoniu.common.utils;


import android.content.Context;

/**
 * <p>Utils初始化相关 </p>
 */
public class ContextUtils {

    private static Context sContext;

    private ContextUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 初始化工具类
     *
     * @param context 上下文
     */
    public static void init(Context context) {
        sContext = context;
    }

    /**
     * 获取ApplicationContext
     *
     * @return ApplicationContext
     */
    public static Context getContext() {
        return sContext;
    }
}