package com.xiaoniu.cleanking.utils.prefs;

/**
 * @author: Est <codeest.dev@gmail.com>
 * @date: 2017/4/21
 * @description:
 */

public interface PreferencesHelper {

    /**
     * 获取Token
     * @return
     */
    String getToken();

    /**
     * 设置Token
     * @param token
     */
    void setToken(String token);

    /**
     * 获取CustomerId
     */
    String getCustomerId();

    /**
     * 获取手机号
     */
    String getPhoneNum();

    /**
     * 获取昵称
     */
    String getNickName();
}
