package com.hellogeek.permission.widget.floatwindow;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;


import com.hellogeek.permission.util.PhoneRomUtils;


/**
 * Desc:悬浮窗基类
 * <p>
 * Author: AnYaBo
 * Date: 2019/7/1
 * Copyright: Copyright (c) 2016-2020
 * Company: @小牛科技
 * Update Comments:
 *
 * @author anyabo
 */
public class FloatingWindow implements IFloatingWindow {

    private Context mContext;
    private View mContentView;
    private WindowManager.LayoutParams mLayoutParams;
    private boolean mIsShowing = false;


    public FloatingWindow(@NonNull Context context) {
        mContext = context;
    }

    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    public View getContentView() {
        return mContentView;
    }

    @Override
    public void setContentView(@NonNull View view) {
        this.mContentView = view;
    }

    @Override
    public void setContentView(int layoutRes) {
        this.mContentView = LayoutInflater.from(mContext).inflate(layoutRes, null);
    }

    @Override
    public <T extends View> T findViewById(int viewId) {
        if (mContentView == null) {
            throw new NullPointerException("please invoke setContentView method first");
        }
        return mContentView.findViewById(viewId);
    }

    @Override
    public void setParams(WindowManager.LayoutParams params) {
        this.mLayoutParams = params;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IFloatingWindow) {
            View contentView = ((IFloatingWindow) obj).getContentView();
            return contentView == getContentView();
        }
        return super.equals(obj);
    }

    @Override
    public void show() {
        if (mIsShowing) {
            return;
        }
        if (mLayoutParams == null) {
            mLayoutParams = getDefaultLayoutParams();
        }
        mIsShowing = FloatWindowManager.getFWManager().showWindow(this);

    }

    @Override
    public void dismiss() {
        FloatWindowManager.getFWManager().hideWindow(this);
        mIsShowing = false;
    }

    @Override
    public boolean isShowing() {
        return mIsShowing;
    }

    @Override
    public WindowManager.LayoutParams getDefaultLayoutParams() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            } else {
                layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
            }
        }

        if (PhoneRomUtils.getPhoneModel().equalsIgnoreCase("PBEM00") || PhoneRomUtils.getPhoneModel().equalsIgnoreCase("V1831A")
                || PhoneRomUtils.getPhoneModel().equalsIgnoreCase("vivo x20A") || PhoneRomUtils.getPhoneModel().equalsIgnoreCase("PBAM00")
                || PhoneRomUtils.getPhoneModel().equalsIgnoreCase("PAFM00")) {
            PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
            layoutParams.flags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM |
                    WindowManager.LayoutParams.FLAG_FULLSCREEN |
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
            if (!pm.isScreenOn()) {
                layoutParams.flags = WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
            }
        } else if (PhoneRomUtils.getPhoneType() == PhoneRomUtils.VIVO || PhoneRomUtils.getPhoneModel().equalsIgnoreCase("OPPO A59m")
                || PhoneRomUtils.getPhoneModel().equalsIgnoreCase("oppo R9s") || PhoneRomUtils.getPhoneModel().equalsIgnoreCase("oppo R11")
                || PhoneRomUtils.getPhoneModel().equalsIgnoreCase("oppo R11S") || PhoneRomUtils.getPhoneModel().equalsIgnoreCase("oppo A59S")
                || PhoneRomUtils.getPhoneModel().equalsIgnoreCase("oppo A57") || PhoneRomUtils.getPhoneModel().equalsIgnoreCase("oppo A83")) {
            layoutParams.flags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM |
                    WindowManager.LayoutParams.FLAG_FULLSCREEN |
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        } else {
            layoutParams.flags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM |
                    WindowManager.LayoutParams.FLAG_FULLSCREEN |
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            layoutParams.flags = WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
        }
        layoutParams.format = PixelFormat.TRANSPARENT;

//        layoutParams.flags = WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD;

        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.x = 0;
        layoutParams.y = 0;
        layoutParams.gravity = Gravity.CENTER;
        return layoutParams;
    }

    @Override
    public WindowManager.LayoutParams getLayoutParams() {
        return mLayoutParams;
    }
}
