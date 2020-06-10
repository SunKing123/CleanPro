package com.geek.push.jpush;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.geek.push.GeekPush;
import com.geek.push.PushMsgHandler;
import com.geek.push.cache.PushConfigCache;
import com.geek.push.log.LogUtils;
import com.geek.push.utils.JsonUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by pengyuantao on 2017/10/25 18:26.
 */

public class JPushReceiver extends BroadcastReceiver {
    private final static String TAG = "pushLog " + JPushReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Bundle bundle = intent.getExtras();
        //防止下面的bundle为null
        if (bundle == null) {
            bundle = new Bundle();
        }
        LogUtils.d(TAG, action);
        LogUtils.d(TAG, printBundle(bundle));
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(action)) {
            String token = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            PushMsgHandler.transmitCommandResult(context, GeekPush.TYPE_REGISTER, TextUtils.isEmpty(token)
                    ? GeekPush.RESULT_ERROR : GeekPush.RESULT_OK, token, null, null);
            PushConfigCache.putRid(context, token);
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(action)) {
            // 收到了自定义消息
            String title = bundle.getString(JPushInterface.EXTRA_TITLE);
            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
            String msgId = bundle.getString(JPushInterface.EXTRA_MSG_ID);
            Log.e("pushLog", "onReceiveMessage msgId = " + msgId);
            PushMsgHandler.transmitMessage(context, message, msgId, extra, null);
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(action)) {
            // 收到了通知
            String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
            String content = bundle.getString(JPushInterface.EXTRA_ALERT);
            String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
            String msgId = bundle.getString(JPushInterface.EXTRA_MSG_ID);
            int notifyId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            try {
                PushMsgHandler.transmitNotification(context, notifyId, title, content, msgId, null,
                        JsonUtils.toMap(new JSONObject(extra)));
            } catch (JSONException localJSONException2) {
                localJSONException2.printStackTrace();
                PushMsgHandler.transmitNotification(context, notifyId, title, content, msgId, extra, null);
            }
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(action)) {
            // 用户点击打开了通知
            String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
            String content = bundle.getString(JPushInterface.EXTRA_ALERT);
            String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
            String msgId = bundle.getString(JPushInterface.EXTRA_MSG_ID);
            int notifyId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            try {
                PushMsgHandler.transmitNotificationClick(context, notifyId, title, content, msgId, null,
                        JsonUtils.toMap(new JSONObject(extra)));
            } catch (JSONException localJSONException1) {
                localJSONException1.printStackTrace();
                PushMsgHandler.transmitNotificationClick(context, notifyId, title, content, msgId, extra, null);
            }
        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(action)) {
            if (bundle.getBoolean(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                PushMsgHandler.transmitCommandResult(context, GeekPush.TYPE_REGISTER, GeekPush.RESULT_OK,
                        JPushInterface.getRegistrationID(context), null, null);
            } else {
                PushMsgHandler.transmitCommandResult(context, GeekPush.TYPE_UNREGISTER, GeekPush.RESULT_OK,
                        JPushInterface.getRegistrationID(context), null, null);
            }
            if (!TextUtils.isEmpty(JPushInterface.getRegistrationID(context))) {
                PushConfigCache.putRid(context, JPushInterface.getRegistrationID(context));
            }
        } else {
            LogUtils.i(action + "----" + bundle.keySet());
        }
    }

    // 打印所有的 intent extra 数据
    private String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
                    LogUtils.i(TAG, "This message has no Extra data");
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();
                    while (it.hasNext()) {
                        String myKey = it.next();
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " + json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                    LogUtils.e(TAG, "Get message extra JSON error!");
                }

            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.get(key));
            }
        }
        return sb.toString();
    }
}
