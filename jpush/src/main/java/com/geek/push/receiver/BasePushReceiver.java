package com.geek.push.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;

import com.geek.push.core.IPushReceiver;
import com.geek.push.core.PushResultCode;
import com.geek.push.entity.PushCommand;
import com.geek.push.entity.PushMsg;
import com.geek.push.log.LogUtils;


/**
 * Finally unified message push processing Receiver
 * Created by pyt on 2017/5/10.
 */

public abstract class BasePushReceiver extends BroadcastReceiver implements IPushReceiver, PushResultCode {
    private final static String TAG = "pushLog " + BasePushReceiver.class.getSimpleName();

    @Override
    public final void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Parcelable parcelable = TransmitDataManager.parsePushData(intent);
        LogUtils.i(TAG, "action:" + action + " data:" + parcelable.toString());
        if (PushAction.RECEIVE_COMMAND_RESULT.equals(action)) {
            onCommandResult(context, (PushCommand) parcelable);
        } else if (PushAction.RECEIVE_NOTIFICATION.equals(action)) {
//            onReceiveNotification(context, (OnePushMsg) parcelable);
            onReceiveMessage(context, (PushMsg) parcelable);
        } else if (PushAction.RECEIVE_NOTIFICATION_CLICK.equals(action)) {
            onReceiveNotificationClick(context, (PushMsg) parcelable);
        } else if (PushAction.RECEIVE_MESSAGE.equals(action)) {
            onReceiveMessage(context, (PushMsg) parcelable);
        }
    }

    @Deprecated
    @Override
    public void onReceiveNotification(Context context, PushMsg msg) {
        //this is method is not always invoke,if you application is dead ,when you click
        //notification ,this method is not invoke,so don't do important things in this method
    }
}
