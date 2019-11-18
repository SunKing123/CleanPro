package com.geek.webpage.utils;

import android.content.Context;

/**
 * 代码描述<p>
 *
 * @author anhuiqing
 * @since 2019/6/18 14:07
 */
public class ScreenUtils {
    /**
     * 得到设备屏幕的宽度
     */
    public static int getScreenWidth(Context context) {
        if (context == null) {
            return 1;
        }
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 得到设备屏幕的高度
     */
    public static int getScreenHeight(Context context) {
        if (context == null) {
            return 1;
        }
        return context.getResources().getDisplayMetrics().heightPixels;
    }


    /**

     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)

     */

    public static int dip2px(Context context, float dpValue) {

        final float scale = context.getResources().getDisplayMetrics().density;

        return (int) (dpValue * scale + 0.5f);

    }


    /**

     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp

     */

    public static int px2dip(Context context, float pxValue) {

        final float scale = context.getResources().getDisplayMetrics().density;

        return (int) (pxValue / scale + 0.5f);

    }


}
