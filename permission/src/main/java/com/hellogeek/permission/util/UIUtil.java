package com.hellogeek.permission.util;

import android.content.Context;

import java.text.DecimalFormat;

/**
 * 博客: http://hackware.lucode.net
 * Created by hackware on 2016/6/26.
 */
public final class UIUtil {

    public static int dip2px(Context context, double dpValue) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * density + 0.5);
    }

    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

//    //整数相除 保留小数点后边
//    public static float division(int a, int b) {
//        String result = "";
//        float num = (float) a / b;
//        DecimalFormat df = new DecimalFormat("0.00");
//        result = df.format(num);
//        return result;
//
//    }

}