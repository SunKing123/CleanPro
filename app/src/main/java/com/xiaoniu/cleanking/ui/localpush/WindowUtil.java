package com.xiaoniu.cleanking.ui.localpush;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.comm.jksdk.utils.DisplayUtil;
import com.xiaoniu.cleanking.R;


import static android.content.Context.WINDOW_SERVICE;

/**
 * Created by manji
 * Date：2018/9/29 下午4:29
 * Desc：
 */
public class WindowUtil {

    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;
    private View mView;


    private Point point = new Point();
    private Rect mDeleteRect = new Rect();
    private static final int mViewWidth = 100;
    private static final int mCancelViewSize = 200;
    private int statusBarHeight = 0;

    private WindowUtil() {

    }

    private static class SingletonInstance {
        @SuppressLint("StaticFieldLeak")
        private static final WindowUtil INSTANCE = new WindowUtil();
    }

    public static WindowUtil getInstance() {
        return SingletonInstance.INSTANCE;
    }

    public void showWindowIfHavePermission(Context context, LocalPushConfigModel data) {
        if (RomUtils.checkFloatWindowPermission(context)) {
            showWindow(context);
        }
    }

    @SuppressLint("CheckResult")
    private void showWindow(Context context) {
        if (null == mWindowManager && null == mView) {
            mWindowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
            mView = LayoutInflater.from(context).inflate(R.layout.dialog_local_push_layout, null);
            mWindowManager.getDefaultDisplay().getSize(point);
            initListener(context);
            mLayoutParams = new WindowManager.LayoutParams();
            WindowManager.LayoutParams mCancelViewLayoutParams = new WindowManager.LayoutParams();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
                mCancelViewLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
                mCancelViewLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            }


            mLayoutParams.format = PixelFormat.RGBA_8888;   //窗口透明
            mLayoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;  //窗口位置
            mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
          //  mLayoutParams.width = DisplayUtil.dip2px(context, 300);
           mLayoutParams.width= (int) (DisplayUtil.getScreenWidth(context)*0.9);
          //  mLayoutParams.height = DisplayUtil.dip2px(context, 150);
            mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            // 可以修改View的初始位置
//            mLayoutParams.x = 0;
//            mLayoutParams.y = 0;
            mWindowManager.addView(mView, mLayoutParams);
        }
    }

    public void dismissWindow() {
        if (mWindowManager != null && mView != null) {
            mWindowManager.removeViewImmediate(mView);
        }
    }

    private void initListener(final Context context) {
        mView.setOnClickListener(v -> {
               dismissWindow();
        });

    }

    private boolean isRemove(int centerX, int centrY) {
        return mDeleteRect.contains(centerX, centrY);
    }

    private void updateViewLayout() {
        if (null != mView && null != mLayoutParams) {
            mWindowManager.updateViewLayout(mView, mLayoutParams);
        }
    }

    public void hideWindow() {
        if (mView != null) {
            mView.setVisibility(View.GONE);
        }
    }

    public void visibleWindow() {
        if (mView != null) {
            mView.setVisibility(View.VISIBLE);
        }

    }

}