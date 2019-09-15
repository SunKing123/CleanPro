package com.geek.push.getui;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.geek.push.GeekPush;
import com.geek.push.PushMsgHandler;
import com.geek.push.utils.JsonUtils;
import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTNotificationMessage;
import com.igexin.sdk.message.GTTransmitMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;

/**
 * Created by pyt on 2017/7/14.
 */

public class GeTuiReceiverService extends GTIntentService {

    private Handler retryRegisterHandler = new Handler();

    @Override
    protected void onHandleIntent(Intent var1) {
        super.onHandleIntent(var1);
    }

    @Override
    public void onReceiveServicePid(Context context, int pid) {
    }


//    {
//        "onePush":true,
//         "title":"通知标题",
//         "content":"通知内容",
//         "extraMsg":"额外信息",
//         "keyValue":{
//            "key1":"value1",
//            "key2":"value2",
//            "key3":"value3"
//           }
//      }

    /**
     * 当收到消息透传
     *
     * @param msg
     */

    @Override
    public void onReceiveMessageData(Context context, GTTransmitMessage msg) {
        Log.i(TAG, "onReceiveMessageData() called with: context = [" + context + "], msg = [" + msg.getPayload() + "]");
        String json = new String(msg.getPayload(), Charset.forName("utf-8"));
        Log.i(TAG, "onReceiveMessageData: " + json);
        try {
            JSONObject jsonObject = new JSONObject(json);
            boolean onePush = jsonObject.getBoolean("onePush");
            if (onePush) {
                String title = jsonObject.getString("title");
                String content = jsonObject.getString("content");
                String extraMsg = jsonObject.getString("extraMsg");
                JSONObject keyValue = jsonObject.getJSONObject("keyValue");
                Log.i(TAG, "title:" + title + "  content:" + content + "  extraMsg:" + extraMsg + " keyValue:" + keyValue);
                PushMsgHandler.transmitNotificationClick(context, 0, title, content, extraMsg, JsonUtils.toMap(keyValue));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            PushMsgHandler.transmitMessage(context, json, null, null);
        }
    }

    @Override
    public void onReceiveClientId(Context context, String clientId) {
        PushMsgHandler.transmitCommandResult(context, GeekPush.TYPE_REGISTER, GeekPush.RESULT_OK, clientId, null, null);
    }

    @Override
    public void onReceiveOnlineState(Context context, boolean online) {
        if (!online) {
            retryRegisterHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    GeekPush.register();
                }
            }, 10000);
        }
    }

    @Override
    public void onReceiveCommandResult(Context context, GTCmdMessage cmdMessage) {
        Log.d(TAG, "onReceiveCommandResult() called with: context = [" + context + "], cmdMessage = [" + cmdMessage + "]");
    }

    @Override
    public void onNotificationMessageArrived(Context context, GTNotificationMessage gtNotificationMessage) {

    }

    @Override
    public void onNotificationMessageClicked(Context context, GTNotificationMessage gtNotificationMessage) {

    }


}
