package com.geek.push.jpush;

import android.content.Context;

import com.geek.push.GeekPush;
import com.geek.push.PushMsgHandler;
import com.geek.push.log.LogUtils;

import java.util.Iterator;
import java.util.Set;

import cn.jpush.android.api.JPushMessage;
import cn.jpush.android.service.JPushMessageReceiver;

/**
 * Created by pengyuantao on 2017/10/25 18:55.
 */

public class JPushTagOrAliasReceiver extends JPushMessageReceiver {
    private static final String TAG = "pushLog " + JPushTagOrAliasReceiver.class.getSimpleName();

    @Override
    public void onAliasOperatorResult(Context context, JPushMessage jPushMessage) {
        LogUtils.e(TAG, jPushMessage.toString());
        PushMsgHandler.transmitCommandResult(context, jPushMessage.getSequence(), jPushMessage.getErrorCode() == 0 ?
                GeekPush.RESULT_OK : jPushMessage.getErrorCode(), null, jPushMessage.getAlias(), null);
    }

    @Override
    public void onTagOperatorResult(Context context, JPushMessage jPushMessage) {
        if (jPushMessage == null) {
            return;
        }
        LogUtils.e(TAG, jPushMessage.toString());
        StringBuilder builder = new StringBuilder();
        Set<String> tagSet = jPushMessage.getTags();
        if (tagSet != null) {
            Iterator<String> iterator = tagSet.iterator();
            if (iterator != null) {
                while (iterator.hasNext()) {
                    builder.append(iterator.next()).append(",");
                }
            }
        }
        if (builder.length() > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }
        PushMsgHandler.transmitCommandResult(context, jPushMessage.getSequence(), jPushMessage.getErrorCode() == 0 ?
                GeekPush.RESULT_OK : jPushMessage.getErrorCode(), null, builder.toString(), null);
    }

    @Override
    public void onCheckTagOperatorResult(Context context, JPushMessage jPushMessage) {
        super.onCheckTagOperatorResult(context, jPushMessage);
        LogUtils.e(TAG, jPushMessage.toString());
        String error = String.valueOf(jPushMessage.getTagCheckStateResult());
        if (jPushMessage.getErrorCode() != 0 || !jPushMessage.getTagCheckStateResult()) {
            LogUtils.e(TAG, "-----tag未设置----" + jPushMessage.getCheckTag());
        } else {

        }
//        HashMap<String, Object> resultParams = new HashMap<>();
//        resultParams.put("checkTag", jPushMessage.getCheckTag());
//        resultParams.put("tagCheckStateResult", jPushMessage.getTagCheckStateResult());
//        resultParams.put("state", jPushMessage.getTagCheckStateResult() ? "tag已绑定可用" : "tag未设置");

        PushMsgHandler.transmitCommandResult(context, jPushMessage.getSequence(), jPushMessage.getErrorCode() == 0 ?
                GeekPush.RESULT_OK : jPushMessage.getErrorCode(), null, error, jPushMessage.getCheckTag());
    }
}
