package com.geek.push.core;

import android.content.Context;

import com.geek.push.entity.PushCommand;
import com.geek.push.entity.PushMsg;

/**
 * Created by pyt on 2017/3/23.
 */

public interface IPushReceiver {

    /**
     * When you received notice
     *
     * @param context
     * @param msg
     */
    void onReceiveNotification(Context context, PushMsg msg);

    /**
     * When you received the notice by clicking
     *
     * @param context
     * @param msg
     */
    void onReceiveNotificationClick(Context context, PushMsg msg);

    /**
     * When I received passthrough message
     *
     * @param context
     * @param msg
     */
    void onReceiveMessage(Context context, PushMsg msg);

    /**
     * When the client calls to execute the command, the callback
     * such as
     *
     * @param context
     * @param command
     * @see IPushClient#addTag(String)
     * @see IPushClient#deleteTag(String)
     * @see IPushClient#bindAlias(String)
     * @see IPushClient#unBindAlias(String)
     * @see IPushClient#unRegister()
     * @see IPushClient#register()
     */
    void onCommandResult(Context context, PushCommand command);

}
