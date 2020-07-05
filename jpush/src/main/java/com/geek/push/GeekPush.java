package com.geek.push;

import android.app.Application;

import com.geek.push.core.OnPushRegisterListener;
import com.geek.push.core.PushManager;
import com.geek.push.core.PushResultCode;
import com.geek.push.log.LogUtils;

/**
 * message push access class
 * Created by pyt on 2017/5/9.
 */

public class GeekPush implements PushResultCode {

    /**
     * Initialize Geek Push
     *
     * @param application
     * @param listener
     */
    public static void init(Application application, OnPushRegisterListener listener) {
        PushManager.getInstance().init(application, listener);
    }

    /**
     * Registered push
     */
    public static void register() {
        PushManager.getInstance().register();
    }

    /**
     * Cancel the registration of push
     */
    public static void unRegister() {
        PushManager.getInstance().unRegister();
    }

    /**
     * Binding alias
     *
     * @param alias alias
     */
    public static void bindAlias(String alias) {
        PushManager.getInstance().bindAlias(alias);
    }

    /**
     * Unbundling account
     *
     * @param alias alias
     */
    public static void unBindAlias(String alias) {
        PushManager.getInstance().unBindAlias(alias);
    }

    /**
     * check tag state
     *
     * @param tag Tag
     */
    public static void checkTagState(String tag) {
        PushManager.getInstance().checkTagState(tag);
    }

    /**
     * Add the tag
     *
     * @param tag Tag
     */
    public static void addTag(String tag) {
        PushManager.getInstance().addTag(tag);
    }

    /**
     * Delete the tag
     *
     * @param tag Tag
     */
    public static void deleteTag(String tag) {
        PushManager.getInstance().deleteTag(tag);
    }

    /*
     delete all tag
     */
    public static void clearAllTag(){
        PushManager.getInstance().clearAllTag();
    }

    /**
     * Gets the current push platform type
     *
     * @return
     */
    public static int getPushPlatformCode() {
        return PushManager.getInstance().getPushPlatFormCode();
    }

    /**
     * Gets the current push platform name
     *
     * @return
     */
    public static String getPushPlatformName() {
        return PushManager.getInstance().getPushPlatFormName();
    }

    /**
     * current debug statue
     *
     * @param isDebug
     */
    public static void setDebug(boolean isDebug) {
        LogUtils.setDebug(isDebug);
    }

    public static String getRid() {
        return PushManager.getInstance().getRid();
    }

}
