package com.xiaoniu.cleanking.ui.localpush;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.comm.jksdk.utils.DisplayUtil;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.utils.GlideUtils;
import com.xiaoniu.cleanking.utils.LogUtils;


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


    public void showWindowWhenDelayTwoSecond(Context context, Long homeTime, LocalPushConfigModel.Item item) {
        long current = System.currentTimeMillis();
        LogUtils.e("=====current:" + current + "  homeTime:" + homeTime);
        long period = current / 1000 - homeTime / 1000;
        LogUtils.e("===距离按下Home已经过去了" + period + "秒");
        if (period >= 3) {
            LogUtils.e("===已经超过3秒，立即弹出");
            showWindow(context, item);
        } else {
            long delay = 3 - period;
            LogUtils.e("===延迟" + delay + "秒弹出");
            new Handler().postDelayed(() -> showWindow(context, item), delay * 1000);
        }

    }

    @SuppressLint("CheckResult")
    private void showWindow(Context context, LocalPushConfigModel.Item item) {
        if (null == mWindowManager && null == mView) {
            mWindowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
            mView = LayoutInflater.from(context).inflate(R.layout.dialog_local_push_layout, null);

            AppCompatImageView icon = mView.findViewById(R.id.logo);
            if (!TextUtils.isEmpty(item.getIconUrl())) {
                GlideUtils.loadImage(context, item.getIconUrl(), icon);
            }

            AppCompatTextView title = mView.findViewById(R.id.title);
            title.setText(item.getTitle());

            AppCompatTextView content = mView.findViewById(R.id.content);
            content.setText(item.getContent());


            AppCompatButton button = mView.findViewById(R.id.button);
            switch (item.getOnlyCode()) {
                case LocalPushType.TYPE_NOW_CLEAR:
                    button.setText("立即清理");
                    break;
                case LocalPushType.TYPE_SPEED_UP:
                    button.setText("一键加速");
                    break;
                case LocalPushType.TYPE_PHONE_COOL:
                    button.setText("一键降温");
                    break;
                case LocalPushType.TYPE_SUPER_POWER:
                    button.setText("立即省电");
                    break;
                default:
                    break;
            }


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
            mLayoutParams.width = (int) (DisplayUtil.getScreenWidth(context) * 0.9);
            //  mLayoutParams.height = DisplayUtil.dip2px(context, 150);
            mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            // 可以修改View的初始位置
//            mLayoutParams.x = 0;
//            mLayoutParams.y = 0;
            mWindowManager.addView(mView, mLayoutParams);
            //更新上次弹框的时间
            LocalPushUtils.getInstance().updateLastPopTime(item.getOnlyCode());
            //更新当天弹框的次数
            LocalPushUtils.getInstance().autoIncrementDayLimit(item.getOnlyCode());
            //五秒后自动消失
            new Handler().postDelayed(this::dismissWindow, 1000 * 5);
        }
    }

    public void dismissWindow() {
        if (mWindowManager != null && mView != null) {
            mWindowManager.removeViewImmediate(mView);
        }
        mView = null;
        mWindowManager = null;
    }

    private void initListener(final Context context) {
        if (mView == null) {
            return;
        }
        mView.setOnClickListener(v -> {
            //点击跳转到相应的页面，并关闭画面
            dismissWindow();
        });

    }

    public boolean isShowing() {
        return mView != null && mView.getVisibility() == View.VISIBLE;
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