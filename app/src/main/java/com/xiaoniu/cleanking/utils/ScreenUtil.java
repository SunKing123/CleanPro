package com.xiaoniu.cleanking.utils;

import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * @Description:
 * @Author: Xiaodong.He
 * @Date: 2020/5/9
 */
public class ScreenUtil {

    /**
     * @return 获取屏幕的高 单位：px
     */
    public static int getScreenHeightPx(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        if (windowManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                windowManager.getDefaultDisplay().getRealMetrics(dm);
            }
            return dm.heightPixels;
        }
        return 0;
    }


    /**
     * dp转px
     *
     * @param context
     * @param dp
     * @return
     */
    public static float dpToPixel(Context context, float dp) {
        return dp * (getDisplayMetrics(context).densityDpi / 160F);
    }

    public static DisplayMetrics getDisplayMetrics(Context context) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(
                Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(
                displaymetrics);
        return displaymetrics;
    }


    /**
     * 获取状态栏高度
     *
     * @param context context
     * @return 状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        if (context == null) {
            return 0;
        }
        // 获得状态栏高度
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

}
