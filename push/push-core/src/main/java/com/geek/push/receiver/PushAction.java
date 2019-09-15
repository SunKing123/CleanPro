package com.geek.push.receiver;

/**
 * Record all news push related Action
 * Created by pyt on 2017/5/12.
 */
public interface PushAction {
    String RECEIVE_NOTIFICATION = "com.geek.push.ACTION_RECEIVE_NOTIFICATION";
    String RECEIVE_NOTIFICATION_CLICK = "com.geek.push.ACTION_RECEIVE_NOTIFICATION_CLICK";
    String RECEIVE_MESSAGE = "com.geek.push.ACTION_RECEIVE_MESSAGE";
    String RECEIVE_COMMAND_RESULT = "com.geek.push.ACTION_RECEIVE_COMMAND_RESULT";
}
