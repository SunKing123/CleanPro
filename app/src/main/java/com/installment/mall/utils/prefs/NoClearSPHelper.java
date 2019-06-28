package com.installment.mall.utils.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import com.installment.mall.app.AppApplication;
import com.installment.mall.utils.AndroidUtil;

import javax.inject.Inject;

import static com.installment.mall.utils.prefs.SpConstants.NOCLEARSPHELPER_NAME;

/**
 * Created by fengpeihao on 2017/8/4.
 */

public class NoClearSPHelper {
    private final SharedPreferences mSPrefs;

    @Inject
    public NoClearSPHelper() {
        mSPrefs = AppApplication.getInstance().getSharedPreferences(NOCLEARSPHELPER_NAME, Context.MODE_PRIVATE);
    }

    public void setSplashImgUrl(String value) {
        mSPrefs.edit().putString(SpConstants.SPLASH_IMG_URL, value).apply();
    }

    /**
     * 启动页图片链接
     */
    public String getSplashImgUrl() {
        return mSPrefs.getString(SpConstants.SPLASH_IMG_URL, "");
    }

    public void setUnAuthenLine(String value) {
        mSPrefs.edit().putString(SpConstants.UN_AUTHEN_LINE, value).apply();
    }

    /**
     * 未认证最高额度8000
     *
     * @return
     */
    public String getUnAuthenLine() {
        return mSPrefs.getString(SpConstants.UN_AUTHEN_LINE, "8000");
    }

    public void setPremiumPicUrl(String value) {
        mSPrefs.edit().putString(SpConstants.PREMIUM_PIC_URL, value).apply();
    }

    /**
     * 支付保费弹窗图片链接
     *
     * @return
     */
    public String getPremiumPicUrl() {
        return mSPrefs.getString(SpConstants.PREMIUM_PIC_URL, "");
    }

    public void setLoginBannerUrl(String value) {
        mSPrefs.edit().putString(SpConstants.LOGIN_BANNER_URL, value).apply();
    }

    /**
     * 登录页面banner图片链接
     *
     * @return
     */
    public String getLoginBannerUrl() {
        return mSPrefs.getString(SpConstants.LOGIN_BANNER_URL, "");
    }

    public void setPrintWordInfo(String value) {
        mSPrefs.edit().putString(SpConstants.PRINT_WORD_INFO, value).apply();
    }

    /**
     * 借点钱不求人，有钱安逸花
     */
    public String getPrintWordInfo() {
        return mSPrefs.getString(SpConstants.PRINT_WORD_INFO, "借点钱不求人，有钱安逸花");
    }

    public void setPrePayUrl(String value) {
        mSPrefs.edit().putString(SpConstants.PREMIUM_PAY_PIC_URL, value).apply();
    }

    /**
     * 支付资产管理费弹窗图片链接
     * @param value
     */
    public void setAssetsManagementUrl(String value){
        mSPrefs.edit().putString(SpConstants.ASSETS_MANAGEMENT_URL, value).apply();
    }

    /**
     * 获取支付资产管理费弹窗图片链接
     * @return
     */
    public String getAssetsManagementUrl(){
        return mSPrefs.getString(SpConstants.ASSETS_MANAGEMENT_URL, "");
    }

    /**
     * 借点钱不求人，有钱安逸花
     */
    public String getPrePayUrl() {
        return mSPrefs.getString(SpConstants.PREMIUM_PAY_PIC_URL, "");
    }

    /**
     * 自有商城的url
     *
     * @param value
     */
    public void setMineShopUrl(String value) {
        mSPrefs.edit().putString(SpConstants.MINE_SHOP_URL, value).apply();
    }

    /**
     * 自有商城的url
     */
    public String getMineShopUrl() {
        return mSPrefs.getString(SpConstants.MINE_SHOP_URL, "");
    }

    /**
     * 审核不通过后，是否显示导入页开关 1：显示导流页 0 ：不显示导流页，默认显示审核失败界面
     * @return
     */
    public String getVerifyFailSwitch(){
        return mSPrefs.getString(SpConstants.VERIFY_FAIL_SWITCH, "0");
    }

    public void setVerifyFailSwitch(String value){
        mSPrefs.edit().putString(SpConstants.VERIFY_FAIL_SWITCH, value).apply();
    }

    /**
     * 审核不通过后要显示导流页URL
     * @return
     */
    public String getVerifyFailDiversionUrl(){
        return mSPrefs.getString(SpConstants.VERIFY_FAIL_DIVERSION_URL, "");
    }

    public void setVerifyFailDiversionUrl(String value){
        mSPrefs.edit().putString(SpConstants.VERIFY_FAIL_DIVERSION_URL, value).apply();
    }


    public String getMax_Borrow_days(){
        return mSPrefs.getString(SpConstants.MAX_BORROW_DAYS, "28");
    }

    public void setMax_Borrow_days(String value){
        mSPrefs.edit().putString(SpConstants.MAX_BORROW_DAYS, value).apply();
    }


    public String getMin_day_interest(){
        return mSPrefs.getString(SpConstants.MIN_DAY_INTEREST, "0.05%");
    }

    public void setMin_day_interest(String value){
        mSPrefs.edit().putString(SpConstants.MIN_DAY_INTEREST, value).apply();
    }

    //设置放款中 取消订单开关 1可取消订单  0 只能联系客服
    public void setCancel_the_loan_switch(String value){
        mSPrefs.edit().putString(SpConstants.CAMCEL_THE_LOAN_SWITCH, value).apply();
    }
    //获取放款中 取消订单开关 1可取消订单  0 只能联系客服
    public String getCancel_the_loan_switch(){
       return mSPrefs.getString(SpConstants.CAMCEL_THE_LOAN_SWITCH,"0");
    }
    //设置放款中 取消订单开关 1可取消订单  0 只能联系客服
    public void setCancel_the_loan_to_close_the_prompt(String value){
        mSPrefs.edit().putString(SpConstants.CAMCEL_THE_LOAN_SWITCH_TO_CLOSE_THE_PROMPT, value).apply();
    }
    //获取放款中 取消订单开关 1可取消订单  0 只能联系客服
    public String getCancel_the_loan_to_close_the_prompt(String value){
       return mSPrefs.getString(SpConstants.CAMCEL_THE_LOAN_SWITCH,"0");
    }
    //设置排队页开关
    public void setSwitch_page_wait(boolean value){
        mSPrefs.edit().putBoolean(SpConstants.SWITCH_PAGE_WAIT, value).apply();
    }

    //获取排队页开关状态
    public boolean getSwitch_page_wait(){
       return mSPrefs.getBoolean(SpConstants.SWITCH_PAGE_WAIT,false);
    }

    //设置保险弹窗是否打开
    public void setSwitch_dialog_insurance(boolean value){
        mSPrefs.edit().putBoolean(SpConstants.SWITCH_DIALOG_INSURANCE, value).apply();
    }

    //获取保险弹窗是否打开状态
    public boolean getSwitch_dialog_insurance(){
        return mSPrefs.getBoolean(SpConstants.SWITCH_DIALOG_INSURANCE,false);
    }

    //设置放款中文案
    public void setText_loaning(String value){
        mSPrefs.edit().putString(SpConstants.TEXT_LOANING, value).apply();
    }

    //获取放款中文案
    public String getText_loaning(){
        return mSPrefs.getString(SpConstants.TEXT_LOANING,"");
    }

    //设置放款金额不足排队描述
    public void setPageWaitHint(String value){
        mSPrefs.edit().putString(SpConstants.PAGE_WAIT_HINT, value).apply();
    }

    //获取放款金额不足排队描述
    public String getPageWaitHint(){
        return mSPrefs.getString(SpConstants.PAGE_WAIT_HINT,"");
    }

    //存储用户手机号，用来判断用户是否是第一次上传设备信息
    public void setPhoneDevice(String PhoneDevice) {
        mSPrefs.edit().putString(SpConstants.PHONE_DEVICE, PhoneDevice).apply();
    }

    //存储用户手机号，用来判断用户是否是第一次上传应用列表
    public void setAppInfo(String appInfo) {
        mSPrefs.edit().putString(SpConstants.APP_INFO, appInfo).apply();
    }

    //存储用户手机号，用来判断用户是否是第一次上传通讯录
    public void setContacts(String phone) {
        mSPrefs.edit().putString(SpConstants.CONSTANTS_INFO, phone).apply();
    }

    //存储统计用户激活率
    public void setIsFristStart(boolean isFristStart) {
        mSPrefs.edit().putBoolean("isFristStart", isFristStart).apply();
    }

    //存储第一次进入提额页面
    public void setIsFristQuota(boolean isFristQuota) {
        mSPrefs.edit().putBoolean("isFristQuota", isFristQuota).apply();
    }

    //存储是否是第一上传短息记录
    public void setIsUpdateSms(String phoneNumber) {
        mSPrefs.edit().putString(SpConstants.IS_UPDATE_SMS, phoneNumber).apply();
    }

    //存储是否是第一次上传通话记录
    public void setIsCallLog(String phoneNumber) {
        mSPrefs.edit().putString(SpConstants.IS_CALL_LOG, phoneNumber).apply();
    }

    //获取是否是第一上传通话记录
    public boolean getIsCallLog() {
        return !AndroidUtil.getPhoneNum().equalsIgnoreCase(mSPrefs.getString(SpConstants.IS_CALL_LOG, ""));
    }

    //获取是否是第一上传短息记录
    public boolean getIsUpdateSms() {
        return !AndroidUtil.getPhoneNum().equalsIgnoreCase(mSPrefs.getString(SpConstants.IS_UPDATE_SMS, ""));
    }

    //身份认证页面是否是第一次请求权限状态
    public void setIsPermissionFirst() {
        mSPrefs.edit().putBoolean(SpConstants.IS_PERMISSION_FIRST, false).apply();
    }

    public boolean isPermissionFirst() {
        return mSPrefs.getBoolean(SpConstants.IS_PERMISSION_FIRST, true);
    }

    public void setContactPermission() {
        mSPrefs.edit().putBoolean(SpConstants.CONTACT_PERMISSION, false).apply();
    }

    //联系人权限是否是第一次读取
    public boolean isContactPermissionFirst() {
        return mSPrefs.getBoolean(SpConstants.CONTACT_PERMISSION, true);
    }

    public boolean getIsFristQuota() {
        return mSPrefs.getBoolean("isFristQuota", false);
    }

    public boolean getIsFristStart() {
        return mSPrefs.getBoolean("isFristStart", false);
    }

    public boolean getContacts() {
        return !AndroidUtil.getPhoneNum().equalsIgnoreCase(mSPrefs.getString(SpConstants.CONSTANTS_INFO, ""));
    }

    public boolean isAppInfo() {
        return !AndroidUtil.getPhoneNum().equalsIgnoreCase(mSPrefs.getString(SpConstants.APP_INFO, ""));
    }

    public boolean isPhoneDevice() {
        return !AndroidUtil.getPhoneNum().equalsIgnoreCase(mSPrefs.getString(SpConstants.PHONE_DEVICE, ""));
    }

    //是否是重新进件
    public boolean isAgainAuth(){
        return AndroidUtil.getPhoneNum().equalsIgnoreCase(mSPrefs.getString(SpConstants.IS_AGAIN_AUTH, ""));
    }

    //是否是重新进件
    public void setAgainAuth(String value){
        mSPrefs.edit().putString(SpConstants.IS_AGAIN_AUTH, value).apply();
    }

    /**
     * 设置运营商开关
     *
     * @param operatorSwitch
     */
    public void setOperatorSwitch(String operatorSwitch) {
        mSPrefs.edit().putString(SpConstants.OPERATOR_SWITCH, operatorSwitch).apply();
    }

    /**
     * 设置运营商默认token
     *
     * @param operatorToken
     */
    public void setOperatorToken(String operatorToken) {
        mSPrefs.edit().putString(SpConstants.OPERATOR_TOKEN, operatorToken).apply();
    }

    /**
     * 设置运营商开关
     */
    public String getOperatorSwitch() {
        return mSPrefs.getString(SpConstants.OPERATOR_SWITCH, "0");
    }

    /**
     * 设置运营商默认token
     */
    public String getOperatorToken() {
        return mSPrefs.getString(SpConstants.OPERATOR_TOKEN, "");
    }
}
