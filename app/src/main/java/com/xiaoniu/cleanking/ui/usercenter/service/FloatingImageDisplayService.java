package com.xiaoniu.cleanking.ui.usercenter.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.xiaoniu.cleanking.R;

/**
 * Created by dongzhong on 2018/5/30.
 */

public class FloatingImageDisplayService extends Service {
    public static boolean isStarted = false;

    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;

    private View displayView;
    public static int[] imageRes;
    public static int[] imageWidth;
    public static int[] imageHeight;

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
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.width = dip2px(275);
        layoutParams.height = dip2px(206);
        layoutParams.x = getScreenWidth() - dip2px(291);
        layoutParams.y = getScreenHeight() - dip2px(222 + 30);
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

    //    int[] imageRes = new int[]{R.mipmap.icon_per2, R.mipmap.icon_per3, R.mipmap.icon_per4, R.mipmap.icon_per1};
//    int[] imageWidth = new int[]{275, 275, 275, 275};
//    int[] imageHeight = new int[]{186, 186, 206, 143};
    int index = 0;
    ImageView iv_zhankai;
    ImageView imageView;
    ImageView iv_delete;

    private void showFloatingWindow() {
        if (imageRes == null) return;
//        if (Build.VERSION.SDK_INT >= 23 &&Settings.canDrawOverlays(this)) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        displayView = layoutInflater.inflate(R.layout.image_display, null);
//            displayView.setOnTouchListener(new FloatingOnTouchListener());
        imageView = displayView.findViewById(R.id.image_display_imageview);
        final ImageView iv_next = displayView.findViewById(R.id.iv_next);
        final ImageView iv_pre = displayView.findViewById(R.id.iv_pre);
        iv_zhankai = displayView.findViewById(R.id.iv_zhankai);
        final ImageView iv_back = displayView.findViewById(R.id.iv_back);
        iv_delete = displayView.findViewById(R.id.iv_delete);
        iv_zhankai.setVisibility(View.GONE);
        iv_delete.setVisibility(View.GONE);
        index = 0;
        imageView.setImageResource(imageRes[index]);
        setImageParam(imageView, imageWidth[index], imageHeight[index]);
        iv_next.setOnClickListener(view -> {
            if (index == imageRes.length - 1) return;
            index++;
            if (index > imageRes.length - 1) {
                index -= 1;
                return;
            }
            imageView.setImageResource(imageRes[index]);
            setImageParam(imageView, imageWidth[index], imageHeight[index]);
        });

        iv_pre.setOnClickListener(view -> {
            index--;
            if (index < 0) {
                index = 0;
                return;
            }
            imageView.setImageResource(imageRes[index]);
            setImageParam(imageView, imageWidth[index], imageHeight[index]);
        });

        iv_back.setOnClickListener(view -> {
            iv_zhankai.setVisibility(View.VISIBLE);
            iv_delete.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.INVISIBLE);
        });
        iv_zhankai.setOnClickListener(view -> {
            iv_zhankai.setVisibility(View.GONE);
            iv_delete.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
        });
        iv_delete.setOnClickListener(view -> {
            iv_zhankai.setVisibility(View.GONE);
            iv_delete.setVisibility(View.GONE);
            imageView.setVisibility(View.GONE);
            displayView.setVisibility(View.GONE);
            stopService(new Intent(FloatingImageDisplayService.this, FloatingImageDisplayService.class));
        });

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (displayView.isAttachedToWindow())
                    windowManager.removeViewImmediate(displayView);
                windowManager.addView(displayView, layoutParams);
            }
        } catch (NoSuchMethodError error) {
            error.printStackTrace();
        }


//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("ddd", "ddd");
        stopView();
    }

    public void stopView() {
        if (iv_zhankai != null)
            iv_zhankai.setVisibility(View.GONE);
        if (iv_delete != null)
            iv_delete.setVisibility(View.GONE);
        if (iv_delete != null)
            imageView.setVisibility(View.GONE);
        if (displayView != null)
            displayView.setVisibility(View.GONE);
    }

    public void setImageParam(ImageView iv, int widthDp, int heightDp) {
        ConstraintLayout.LayoutParams llp = (ConstraintLayout.LayoutParams) iv.getLayoutParams();
        llp.width = dip2px(widthDp);
        llp.height = dip2px(heightDp);
        iv.setLayoutParams(llp);
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
