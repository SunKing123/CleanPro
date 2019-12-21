package com.xiaoniu.cleanking.utils.quick;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @author : lvdongdong
 * @since 2019/12/19
 */
public class QuickSucessCallbackReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
       QuickUtils.getQuickUtils().disableMainComponent();
    }
}
