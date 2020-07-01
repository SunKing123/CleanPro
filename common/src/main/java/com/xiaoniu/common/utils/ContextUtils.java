package com.xiaoniu.common.utils;


import android.app.Application;
import android.content.Context;

/**
 * <p>Utils初始化相关 </p>
 */
public class ContextUtils {

    private static Context sContext;

    private static Application application;

    private static int adBid;
    private ContextUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }


    public static void initAdBid(int bid) {
        adBid = bid;
    }

    public static int getAdBid() {
        return adBid;
    }
    /**
     * 初始化工具类
     *
     *
     */
    public static void initApplication(Application app) {
        application = app;
    }

    /**
     * 获取ApplicationContext
     *
     * @return ApplicationContext
     */
    public static Application getApplication() {
        return application;
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