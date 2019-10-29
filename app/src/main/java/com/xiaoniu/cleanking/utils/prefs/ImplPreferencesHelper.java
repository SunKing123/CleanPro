package com.xiaoniu.cleanking.utils.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import com.xiaoniu.cleanking.app.AppApplication;

import javax.inject.Inject;

import static com.xiaoniu.cleanking.utils.prefs.SpConstants.SHAREDPREFERENCES_NAME;

/**
 * @author: Est <codeest.dev@gmail.com>
 * @date: 2017/4/21
 * @description:
 */

public class ImplPreferencesHelper implements PreferencesHelper {

    private static final boolean DEFAULT_NIGHT_MODE = false;
    private static final boolean DEFAULT_NO_IMAGE = false;
    private static final boolean DEFAULT_AUTO_SAVE = true;

    private static final boolean DEFAULT_LIKE_POINT = false;
    private static final boolean DEFAULT_VERSION_POINT = false;
    private static final boolean DEFAULT_MANAGER_POINT = false;

    private final SharedPreferences mSPrefs;

    @Inject
    public ImplPreferencesHelper() {
        mSPrefs = AppApplication.getInstance().getSharedPreferences(SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public void clear() {
        mSPrefs.edit().clear().apply();
    }

    @Override
    public String getToken() {
        return mSPrefs.getString(SpConstants.TOKEN, "");
    }

    @Override
    public void setToken(String token) {
        mSPrefs.edit().putString(SpConstants.TOKEN, token).apply();
    }

    public void setCustomerId(String customerId) {
        mSPrefs.edit().putString(SpConstants.CUSTOMERID, customerId).apply();
    }

    public void setPhoneNum(String phoneNum) {
        mSPrefs.edit().putString(SpConstants.PHONENUM, phoneNum).apply();
    }

    public void setNickName(String nickName) {
        mSPrefs.edit().putString(SpConstants.NICKNAME, nickName).apply();
    }

    public void setNetIP(String ip) {
        mSPrefs.edit().putString(SpConstants.NETIP, ip).apply();
    }

    public void setLendingTag(boolean isLending) {
        mSPrefs.edit().putBoolean(SpConstants.IS_LENDING, isLending).apply();
    }

    public void setLendedApplicationId(String value) {
        mSPrefs.edit().putString(SpConstants.LENDED_APPLICATION_ID, value).apply();
    }

    @Override
    public String getCustomerId() {
        return mSPrefs.getString(SpConstants.CUSTOMERID, "");
//        return "bb22c38d305e456994701d3ef430e5f6";
    }

    @Override
    public String getPhoneNum() {
        return mSPrefs.getString(SpConstants.PHONENUM, "");
    }

    @Override
    public String getNickName() {
        return mSPrefs.getString(SpConstants.NICKNAME, "");
    }


    public String getNetIp() {
        return mSPrefs.getString(SpConstants.NETIP, "");
    }

    public boolean getLendingTag() {
        return mSPrefs.getBoolean(SpConstants.IS_LENDING, false);
    }

    public String getLendedApplicationId() {
        return mSPrefs.getString(SpConstants.LENDED_APPLICATION_ID, "");
    }

    public void setUserName(String userName) {
        mSPrefs.edit().putString(SpConstants.USER_NAME, userName).apply();
    }

    public String getUserName() {
        return mSPrefs.getString(SpConstants.USER_NAME, "");
    }

    public void setIDNum(String idNum) {
        mSPrefs.edit().putString(SpConstants.ID_NUM, idNum).apply();
    }

    public String getIDNum() {
        return mSPrefs.getString(SpConstants.ID_NUM, "");
    }

    /**
     * 获取费率路由
     * @return
     */
    public String getCustRate(){
        return mSPrefs.getString(SpConstants.CUST_RATE, "1");
    }

    /**
     * 设置费率路由
     * @param value
     */
    public void setCustRate(String value){
        mSPrefs.edit().putString(SpConstants.CUST_RATE, value).apply();
    }

    public void clearSP() {
        SharedPreferences.Editor editor = mSPrefs.edit();
        editor.clear();
        editor.commit();
    }

    public String getClientId() {
        return mSPrefs.getString(SpConstants.CLIENT_ID, "");
    }

    public void setClientId(String clientId) {
        mSPrefs.edit().putString(SpConstants.CLIENT_ID, clientId).apply();
    }
}
