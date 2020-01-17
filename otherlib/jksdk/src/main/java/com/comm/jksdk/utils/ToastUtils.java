package com.comm.jksdk.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

public class ToastUtils {


    private static Handler sHandler = new Handler(Looper.getMainLooper());


    public static void showToast(final Context cxt,final CharSequence content, final int duration, final int gravity) {
        if (TextUtils.isEmpty(content) || cxt == null)
            return;

        sHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    Toast toast = Toast.makeText(cxt, content, duration);
//                    toast.setGravity(gravity, 0, 0);
                    toast.show();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
