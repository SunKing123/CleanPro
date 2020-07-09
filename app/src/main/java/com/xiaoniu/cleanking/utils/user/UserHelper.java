package com.xiaoniu.cleanking.utils.user;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.xiaoniu.cleanking.ui.login.activity.LoginWeiChatActivity;
import com.xiaoniu.cleanking.ui.login.bean.UserInfoBean;
import com.xiaoniu.cleanking.ui.newclean.util.RequestUserInfoUtil;
import com.xiaoniu.cleanking.utils.prefs.ImplPreferencesHelper;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by zhaoyingtao
 * Date: 2020/7/5
 * Describe:账户有关管理器
 */

public class UserHelper {
    private volatile static UserHelper userHelper;

    public static UserHelper init() {
        if (userHelper == null) {
            synchronized (UserHelper.class) {
                if (userHelper == null) {
                    userHelper = new UserHelper();
                }
            }
        }
        return userHelper;
    }

    static ImplPreferencesHelper implPreferencesHelper = new ImplPreferencesHelper();

    /**
     * 用户是否登录==游客、微信登录都算登录
     *
     * @return
     */
    public boolean isLogin() {
        if (!TextUtils.isEmpty(implPreferencesHelper.getCustomerId())) {
            return true;
        }
        return false;
    }

    /**
     * 判断用户是否登录
     *
     * @return
     */
    public boolean isWxLogin() {
        return implPreferencesHelper.getWxLoginFlag();
    }


    public String getToken() {
        return implPreferencesHelper.getToken();
    }

    public String getCustomerId() {
        return implPreferencesHelper.getCustomerId();
    }

    public String getNickName() {
        return implPreferencesHelper.getNickName();
    }

    public String getUserHeadPortraitUrl() {
        return implPreferencesHelper.getUserHeadPortraitUrl();
    }

    public String getOpenID() {
        return implPreferencesHelper.getOpenID();
    }

    public String getPhoneNum() {
        return implPreferencesHelper.getPhoneNum();
    }

    /**
     * 存储用户信息
     *
     * @param userInfo
     */
    public void saveUserInfo(UserInfoBean userInfo) {
        if (userInfo == null) {
            return;
        }
        implPreferencesHelper.setClientId(userInfo.userId);
        implPreferencesHelper.setCustomerId(userInfo.userId);
        implPreferencesHelper.setUserName(userInfo.nickname);
        implPreferencesHelper.setNickName(userInfo.nickname);
        implPreferencesHelper.setToken(userInfo.token);
        if (userInfo.userType == 1) {//微信登录
            implPreferencesHelper.setWxLoginSuccess(true);
        } else {
            implPreferencesHelper.setWxLoginSuccess(false);
        }
        implPreferencesHelper.setOpenID(userInfo.openId);
        implPreferencesHelper.setPhoneNum(userInfo.phone);
        implPreferencesHelper.setUserHeadPortraitUrl(userInfo.userAvatar);
        EventBus.getDefault().post("loginSuccessRefreshUserInfo");
        RequestUserInfoUtil.getUserCoinInfo();
    }

    public void setUserPhoneNum(String phoneNum) {
        implPreferencesHelper.setPhoneNum(phoneNum);
    }

    /**
     * 设置app登录状态
     *
     * @param loginState
     * @return
     */
    public void changeLoginState(boolean loginState) {
//        AppSPreferencesHelper.init().putBoolean(IS_LOGIN, loginState);
    }

    /**
     * 清除当前用户信息
     */
    public void clearCurrentUserInfo() {
        changeLoginState(false);
        implPreferencesHelper.clearUserInfo();
        EventBus.getDefault().post("exitRefreshUserInfo");
    }

    public void startToLogin(Context mActivity) {
        mActivity.startActivity(new Intent(mActivity, LoginWeiChatActivity.class));
    }
}
