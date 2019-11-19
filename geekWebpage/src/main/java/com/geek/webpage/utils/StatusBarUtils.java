package com.geek.webpage.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.DrawableRes;

import java.lang.reflect.Field;

public class StatusBarUtils {

    /**
     * 获取状态栏高度
     */
    public static int getStatusBarHeight(Context activity) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, sbar = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sbar = activity.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return sbar;
    }

    /**
     * 动态设置状态栏高度
     */
    public static void setStatusBarViewHeight(View view, int height) {
        if (height < 0 || view == null)
            return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.height = height;
            view.setLayoutParams(params);
        }
    }

    /**
     * 自动设置状态栏高度
     */
    public static void setStatusBarViewHeight(Activity context, int id) {
        int height = getStatusBarHeight(context);
        View view = context.findViewById(id);
        if (height < 0 || view == null)
            return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.height = height;
            view.setLayoutParams(params);
        }
    }

    /**
     * 自动设置状态栏高度,背景，是否可见状态
     */
    public static void setStatusBarState(Activity context, int id, boolean isVisible, @DrawableRes int resource) {
        int height = getStatusBarHeight(context);
        View view = context.findViewById(id);
        if (height < 0 || view == null)
            return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.height = height;
            view.setLayoutParams(params);
        }
        setStatusBarViewVisibility(view, isVisible);
        if (resource != -1) {
            setStatusBarViewBackGroundResource(view, resource);
        }
    }

    /**
     * 自动设置状态栏高度,背景，是否可见状态
     */
    public static void setStatusBarState(Activity context,View view, boolean isVisible, @DrawableRes int resource) {
        int height = getStatusBarHeight(context);
        if (height < 0 || view == null)
            return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.height = height;
            view.setLayoutParams(params);
        }
        setStatusBarViewVisibility(view, isVisible);
        if (resource != -1) {
            setStatusBarViewBackGroundResource(view, resource);
        }
    }

    // 状态栏显示隐藏设置
    public static void setStatusBarViewVisibility(View view) {
        if (view == null) {
            return;
        }
        // 注释掉状态栏view
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    // 状态栏显示隐藏设置
    public static void setStatusBarViewVisibility(View view, boolean isVisible) {
        if (view == null) {
            return;
        }
        // 注释掉状态栏view
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && isVisible) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    // 状态栏背景设置
    public static void setStatusBarViewBackGroundResource(View view, @DrawableRes int resource) {
        if (view == null) {
            return;
        }
        view.setBackgroundResource(resource);
    }

}

