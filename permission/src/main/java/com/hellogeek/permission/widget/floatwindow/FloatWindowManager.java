package com.hellogeek.permission.widget.floatwindow;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Desc:悬浮窗管理器
 * <p>
 * Author: AnYaBo
 * Date: 2019/7/1
 * Copyright: Copyright (c) 2016-2020
 * Company: @小牛科技
 * Update Comments:
 * @author anyabo
 */
public class FloatWindowManager implements IFloatingWindowMgr {
    private static FloatWindowManager sFWManager;
    private List<IFloatingWindow> mWindows = new ArrayList<>();

    public boolean haveWindow() {
        return mWindows.size() > 0;
    }

    private FloatWindowManager() {

    }

    public static FloatWindowManager getFWManager() {
        if (sFWManager == null) {
            sFWManager = new FloatWindowManager();
        }
        return sFWManager;
    }

    @Override
    public boolean showWindow(IFloatingWindow window) {
        try {
            if (window != null) {
                Context context = window.getContext();
                WindowManager wgr = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                View contentView = window.getContentView();
                WindowManager.LayoutParams layoutParams = window.getLayoutParams();
                if (wgr != null && !window.isShowing() && contentView != null && layoutParams != null) {
                    contentView.setFitsSystemWindows(true);
                    wgr.addView(contentView, layoutParams);
                    mWindows.add(window);
                    return true;
                }
            }
        }catch (Exception e){
            Log.e("44444444",e.getMessage());
        }
        return false;
    }

    @Override
    public void hideWindow(IFloatingWindow window) {
        if (window != null && window.isShowing()) {
            Context context = window.getContext();
            WindowManager wgr = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            View contentView = window.getContentView();
            if (wgr != null) {
                wgr.removeView(contentView);
            }
            if (mWindows.contains(window)) {
                mWindows.remove(window);
            }
        }

    }

    @Override
    public void removeAllWindow() {
        for (IFloatingWindow window : mWindows) {
            hideWindow(window);
        }
    }
}
