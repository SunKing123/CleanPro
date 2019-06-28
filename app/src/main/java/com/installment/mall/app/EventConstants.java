package com.installment.mall.app;

/**
 * EventBus发送的字符串常量标识
 */
public class EventConstants {

    /**
     * 还款订单信息
     */
    public static final String LEND_DETAIL = "lend_detail";

    /**
     * 认证进度信息
     */
    public static final String AUTH_INFO = "authentication_activity";

    /**
     * 认证失败
     */
    public static final String AUTH_FAILED = "auth_failed";

    /**
     * 上传通讯录
     */
    public static final String UPLOAD_CONTACTS = "upload_contacts";

    /**
     * 上传成功
     */
    public static final String UPLOAD_SUCCESS = "upload_success";

    /**
     * 上传失败
     */
    public static final String UPLOAD_FAILED = "upload_failed";

    /**
     * 银行卡绑定页面关闭或者刷新通知
     */
    public static final String BIND_CLOSE_REFRESH = "close_or_refresh";
    /**
     * 关闭银行卡管理页面
     */
    public static final String FINISH_BANK_MANAGER_ACTIVITY = "finish_bank_manager_activity";

    /**
     *  还款银行卡刷新
     * */
    public static final String REFRESH_REPAYMENT = "refresh_repayment";

    /**
     * 首页小牛喜报fragment刷新
     * */
    public static final String HOME_NOTICE_FRAGMENT = "homenoticefragment";

    /**
     * 刷新
     * */
    public static final String REFRESH = "refresh";
    /**
    * 暂停
    **/
    public static final String PAUSE = "pause";
    /**
    * 我的资料页
    **/
    public static final String COMPLETE_AUTH_ACTIVITY = "CompleteAuthActivity";

    /**
     * 跳转首页
     */
    public static final String TO_MAIN_ACTIVITY = "to_MainActivity";

    /**
    * 小牛喜报
    **/
    public static final String HOME_XB_NOTICE_FRAGMENT = "homexbnoticefragment";
    /**
    * 还款详情
    **/
    public static final String REPAYMENT_DETAIL = "repayment_detail";
    /**
    * 支付失败
    **/
    public static final String PAYFAIL = "payFail";
    /**
    * 微信支付成功
    **/
    public static final String WECHATPAYSUCCESS = "weChatPaySuccess";
    /**
    * 更新限制
    **/
    public static final String UPDATE_LIMIT = "update_limit";
    /**
    * 认证中心
    **/
    public static final String  AUTHENTICATION_CENTER_ACTIVITY = "AuthenticationCenterActivity";
    /**
    * 借钱
    **/
    public static final String BORROW_MONEY = "borrow_money";

}
