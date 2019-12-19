package com.xiaoniu.cleanking.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.xiaoniu.cleanking.BuildConfig;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.debug.DebugActivity;
import com.xiaoniu.common.utils.DisplayUtils;

/**
 * Desc:
 * <p>
 * Author: AnYaBo
 * Date: 2019/8/26
 * Copyright: Copyright (c) 2016-2020
 * Company: @小牛科技
 * Update Comments:
 */
public class AppConfig {

    public static void showDebugWindow(Activity activity) {
        if (!BuildConfig.DEBUG) {
            return;
        }
        try {
            WindowManager windowManager = (WindowManager) activity.
                    getSystemService(Context.WINDOW_SERVICE);
            ImageView imageView = new ImageView(activity.getApplicationContext());
            imageView.setImageResource(R.drawable.debug_logo);
            WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION;
            params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            params.format = PixelFormat.TRANSLUCENT;
            params.width = DisplayUtils.dip2px(42);
            params.height = DisplayUtils.dip2px(42);
            params.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
            try {
                windowManager.addView(imageView, params);
            } catch (Exception e) {
                return;
            }
            imageView.setOnClickListener(view ->
                    activity.startActivity(new Intent(view.getContext(), DebugActivity.class)));
            imageView.setOnTouchListener(new View.OnTouchListener() {
                float lastX, lastY;
                int oldOffsetX, oldOffsetY;
                int tag = 0;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    final int action = event.getAction();
                    float x = event.getX();
                    float y = event.getY();
                    if (tag == 0) {
                        oldOffsetX = params.x;
                        oldOffsetY = params.y;
                    }
                    if (action == MotionEvent.ACTION_DOWN) {
                        lastX = x;
                        lastY = y;
                    } else if (action == MotionEvent.ACTION_MOVE) {
                        params.x += (int) (x - lastX) / 3;
                        params.y += (int) (y - lastY) / 3;
                        tag = 1;
                        windowManager.updateViewLayout(imageView, params);
                    } else if (action == MotionEvent.ACTION_UP) {
                        int newOffsetX = params.x;
                        int newOffsetY = params.y;
                        if (Math.abs(oldOffsetX - newOffsetX) <= 20
                                && Math.abs(oldOffsetY - newOffsetY) <= 20) {
                            activity.startActivity(new Intent(imageView.getContext(), DebugActivity.class));
                        } else {
                            tag = 0;
                        }
                    }
                    return true;
                }
            });
        } catch (Exception e) {
            Log.e("AppConfig", e.getMessage());
        }
    }

}
