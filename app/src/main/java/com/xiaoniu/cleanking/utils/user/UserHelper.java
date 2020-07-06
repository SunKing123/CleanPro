package com.xiaoniu.cleanking.utils.user;

import android.text.TextUtils;

import com.xiaoniu.cleanking.ui.login.bean.UserInfoBean;
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
     * 用户是否登录
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
        implPreferencesHelper.setClientId(userInfo.userId);
        implPreferencesHelper.setCustomerId(userInfo.openId);
        implPreferencesHelper.setUserName(userInfo.nickname);
        implPreferencesHelper.setNickName(userInfo.nickname);
        implPreferencesHelper.setToken(userInfo.token);
        implPreferencesHelper.setWxLoginSuccess(true);
        implPreferencesHelper.setOpenID(userInfo.openId);
        implPreferencesHelper.setPhoneNum(userInfo.phone);
        EventBus.getDefault().post("loginSuccessRefreshUserInfo");
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
//        AppSPreferencesHelper.init().del(AppConstant.USER_INFO);
//        AppSPreferencesHelper.init().del(USER_SESSION);
//        AppSPreferencesHelper.init().del(USER_ID_TOKEN);
        //关闭推送渠道
//        PushServiceFactory.getCloudPushService().turnOffPushChannel(null);
    }
}
