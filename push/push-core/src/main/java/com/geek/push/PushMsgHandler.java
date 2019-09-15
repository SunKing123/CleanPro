package com.geek.push;

import android.content.Context;
import android.os.Parcelable;

import com.geek.push.entity.PushCommand;
import com.geek.push.entity.PushMsg;
import com.geek.push.log.LogUtils;
import com.geek.push.receiver.PushAction;
import com.geek.push.receiver.TransmitDataManager;

import java.util.Map;

/**
 * The message the repeater
 * Push platform message to forward a third party
 * Created by pyt on 2017/5/10.
 */

public class PushMsgHandler {

    private static final String TAG = "pushLog " + PushMsgHandler.class.getSimpleName();

    /**
     * Repeater instructions operating results
     *
     * @param context
     * @param type
     * @param resultCode
     * @param extraMsg   额外信息
     * @param error      错误信息
     * @see GeekPush#TYPE_ADD_TAG
     * @see GeekPush#TYPE_DEL_TAG
     * @see GeekPush#TYPE_AND_OR_DEL_TAG
     * @see GeekPush#TYPE_REGISTER
     * @see GeekPush#TYPE_UNREGISTER
     * @see GeekPush#TYPE_BIND_ALIAS
     * @see GeekPush#TYPE_UNBIND_ALIAS
     * @see GeekPush#TYPE_CHECK_TAG
     * @see GeekPush#RESULT_ERROR
     * @see GeekPush#RESULT_OK
     */
    public static void transmitCommandResult(Context context, int type, int resultCode, String registerId
            , String extraMsg, String error) {
        transmit(context, PushAction.RECEIVE_COMMAND_RESULT
                , new PushCommand(type, resultCode, registerId, extraMsg, error));
    }

    /**
     * Repeater passthrough message
     *
     * @param context
     * @param msg
     * @param extraMsg
     */
    public static void transmitMessage(Context context, String msg
            , String extraMsg, Map<String, String> keyValue) {
        transmit(context, PushAction.RECEIVE_MESSAGE
                , new PushMsg(0, null, null, msg, extraMsg, keyValue));
    }

    /**
     * Repeater the notification bar click event
     *
     * @param context
     * @param notifyId
     * @param title
     * @param content
     * @param extraMsg
     */
    public static void transmitNotificationClick(Context context, int notifyId, String title
            , String content, String extraMsg, Map<String, String> keyValue) {
        transmit(context, PushAction.RECEIVE_NOTIFICATION_CLICK
                , new PushMsg(notifyId, title, content, null, extraMsg, keyValue));
    }

    /**
     * Repeater notice
     *
     * @param context
     * @param notifyId
     * @param title
     * @param content
     * @param extraMsg
     */
    public static void transmitNotification(Context context, int notifyId, String title
            , String content, String extraMsg, Map<String, String> keyValue) {
        transmit(context, PushAction.RECEIVE_NOTIFICATION
                , new PushMsg(notifyId, title, content, null, extraMsg, keyValue));
    }

    /**
     * The main method to repeater information
     *
     * @param context
     * @param action
     * @param data
     */
    private static void transmit(Context context, String action, Parcelable data) {
        LogUtils.d(TAG, "-----handler-----" + action + "\ndata:" + data.toString());
        TransmitDataManager.sendPushData(context, action, data);
    }
}
