package com.xiaoniu.cleanking.keeplive.utils;

import android.content.Context;
import android.content.Intent;

public class ToActivity {
    /**
     * 跳转到对应activity
     */
    public static void toActivity(Context context, String fullName) {
        if (fullName != null && fullName.length() > 0) {
            try {
                Intent intent = new Intent(context,Class.forName(fullName));
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
