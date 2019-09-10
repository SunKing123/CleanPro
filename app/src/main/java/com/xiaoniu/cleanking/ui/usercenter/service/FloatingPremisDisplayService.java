package com.xiaoniu.cleanking.ui.usercenter.service;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;
import com.xiaoniu.cleanking.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 开启权限引导动画
 */
public class FloatingPremisDisplayService extends Service {
    public static boolean isStarted = false;
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private View displayView;

    @Override
    public void onCreate() {
        super.onCreate();
        isStarted = true;
        setWindowParam();
    }

    public void setWindowParam() {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        showFloatingWindow();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    private LottieAnimationView mAnimationView;

    @SuppressLint("ClickableViewAccessibility")
    private void showFloatingWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height =  WindowManager.LayoutParams.MATCH_PARENT;
        displayView = layoutInflater.inflate(R.layout.activity_asm_guide, null);
        mAnimationView = displayView.findViewById(R.id.view_lottie);
        showLottieView();
        if (displayView.isAttachedToWindow())
            windowManager.removeViewImmediate(displayView);
        windowManager.addView(displayView, layoutParams);
        displayView.setOnTouchListener((v, event) -> {
            stopSelf();
            return false;
        });
    }
    /**
     * 显示吸收动画
     */
    private void showLottieView() {
        mAnimationView.useHardwareAcceleration();
        mAnimationView.setAnimation("data_premis.json");
        mAnimationView.setImageAssetsFolder("images");
        mAnimationView.playAnimation();
        mAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimationView.playAnimation();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopView();
    }

    public void stopView() {
        if (displayView != null)
            displayView.setVisibility(View.GONE);
    }

    public int getScreenWidth() {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    public int getScreenHeight() {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    public int dip2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
