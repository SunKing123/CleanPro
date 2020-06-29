package com.xiaoniu.common.utils;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

public class ToastUtils {


    private static Handler sHandler = new Handler(Looper.getMainLooper());

    /**
     * 短时间显示Toast
     *
     * @param message
     */
    public static void showShort(final CharSequence message) {
        showToast(message, Toast.LENGTH_SHORT, Gravity.CENTER);
    }


    /**
     * 短时间显示Toast
     *
     * @param strResId
     */
    public static void showShort(final int strResId) {
        showToast(ContextUtils.getContext().getResources().getText(strResId), Toast.LENGTH_SHORT, Gravity.CENTER);
    }

    /**
     * 长时间显示Toast
     *
     * @param message
     */
    public static void showLong(final CharSequence message) {
        showToast(message, Toast.LENGTH_LONG, Gravity.CENTER);
    }

    /**
     * 长时间显示Toast
     *
     * @param strResId
     */
    public static void showLong(final int strResId) {
        showToast(ContextUtils.getContext().getResources().getText(strResId), Toast.LENGTH_LONG, Gravity.CENTER);
    }

    private static Toast toast;
    private static void showToast(final CharSequence content, final int duration, final int gravity) {
        if (TextUtils.isEmpty(content) || ContextUtils.getContext() == null)
            return;
        if (toast == null) {
            toast = Toast.makeText(ContextUtils.getContext(), content, duration);
        }
        toast.cancel();
        toast.setText(content);
        toast.setDuration(duration);
        sHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                    toast.show();
            }
        }, 50);
    }
}
