package com.xiaoniu.cleanking.app;

/**
 * 常用常量
 * Created by tie on 2017/2/18.
 */

public class Constant {

    public static final String SUCCESS = "000000";
    public static final String CODE_REBIND_SUCCESS = "600"; // 已绑定过的卡，重新绑定标识码
    public static final String customerPhone = "400-106-7878";
    public static final String server_Phone = "4001067878";

    public static String APP_IS_LIVE = "";
    //用户中心跳转通用H5 Activity字段
    //标题
    public static final String Title = "activity_title";
    public static final String TAG = "splash";

    //标题
    public static final String NoTitle = "NoTitle";
    //链接
    public static final String URL = "webview_url";
    //Token失效
    public static final String TokenFailure = "1004";
    public static final String TokenIllegality = "1005";

    //发版状态
    public static final String Server_Error = "1100";

    public static final int LOACATION = 666;

    public static final String WECHAT_APP_ID = "wx2440bee27b45a19a";

    //商城页面
    public static final int SHOPPING = 2;
    //提额页面
    public static final int UPQUOTA = 3;
    //借款页面
    public static final int LOAN = 1;
    //活动页面 (赚钱页面)
    public static final int ACTION = 4;
    //我的页面
    public static final int MINE = 5;
    //绑卡页面
    public static final int BIND_CARD = 6;
    //运营商页面
    public static final int OPERATOR = 7;
    //个人信息页面
    public static final int USERINFO = 8;
    //信用认证页面
    public static final int CREDITAUTH = 9;

    /**
     * 小额贷标识
     */
    public static final int LOAN_TYPE_FAST = 1;
    /**
     * 大额贷标识
     */
    public static final int LOAN_TYPE_INSTALLMENT = 2;

    /**
     * 进件审核
     */
    public static final String AUTH_AUDIT = "1";
    /**
     * 借款审核
     */
    public static final String WITHDRAW_AUDIT = "2";
    /**
     * 保险审核
     */
    public static final String PREMIUM_AUDIT = "3";

    /**
     * 自费购买
     */
    public static final int SELF_BUY = 1;
    /**
     * 贷款购买
     */
    public static final int LOANS_BUY = 2;

    /**
     * 顶部banner type
     */
    public static final int BANNER_TOP = 1;
    /**
     * 中部banner type
     */
    public static final int BANNER_CENTER = 2;
    /**
     * 底部banner type
     */
    public static final int BANNER_BOTTOM = 3;
    //banner显示页面位置(1借款页 2商场页 3提额页 4赚钱页 5我的页 6绑卡页 7身份认证页 8个人信息页 9信用认证页)
    /**
     * 借款页面banner type
     */
    public static final int BANNER_LOAN_PAGE = 1;
    /**
     * 商场页面banner type
     */
    public static final int BANNER_SHOPPING_PAGE = 2;
    /**
     * 提额页面banner type
     */
    public static final int BANNER_UP_QUOTA_PAGE = 3;

    /**
     * 赚钱页面banner type
     */
    public static final int BANNER_MAKE_MONEY_PAGE = 4;
    /**
     * 我的页面banner type
     */
    public static final int BANNER_MINE_PAGE = 5;
    /**
     * 绑卡页面banner type
     */
    public static final int BANNER_BIND_CARD_PAGE = 6;
    /**
     * 身份认证页面banner type
     */
    public static final int BANNER_IDENTITY_PAGE = 7;
    /**
     * 个人信息面banner type
     */
    public static final int BANNER_PERSONAL_PAGE = 8;
    /**
     * 信用认证面banner type
     */
    public static final int BANNER_CREDIT_PAGE = 9;
    /**
     * 排队页开关
     */
    public static final int SWITCH_TYPE_QUEUE = 1;
    /**
     * 保险弹窗开关
     */
    public static final int SWITCH_TYPE_PREMIUM = 2;
    /**
     * 灾备用户提现开关
     */
    public static final int SWITCH_TYPE_BACKUPS_LOAN = 3;
    /**
     * 灾备保险开关
     */
    public static final int SWITCH_TYPE_BACKUPS_PREMIUM = 4;

    /**
     * 代表埋点成功状态
     */
    public static final int BURY_SUCCESS_STATE = 1;
    /**
     * 代表埋点失败状态
     */
    public static final int BURY_FAILURE_STATE = 2;

    /**
     * 导流贷款超市
     */
    public static final String DIVERSION_LOAN_SUPERMARKET = "1";
    /**
     * 广播跳转到首页标识
     */
    public static final String TAG_TURN_MAIN = "TAG_TURN_MAIN";

    /**
     * 聚信立
     */
    public static final String JXL = "jxl";
    /**
     * 白骑士
     */
    public static final String BQS = "bqs";

    /**
     * 场景 0-14天借款 1-小额分期
     */
    public static final String SCENE_INSTALLMENT = "1";

    /**
     *  费率路由 - 保费
     */
    public static final String PREMIUM = "1";
    /**
     * 费率路由 - 资产管理
     */
    public static final String ASSET_MANAGEMENT = "2";

    /**运营商开关打开*/
    public static final String OPERATOR_SWITCH_ON = "1";
    /**运营商开关关闭*/
    public static final String OPERATOR_SWITCH_OFF = "0";


    /**
     * QQ专清图片集合
     */
    public static final String PARAMS_QQ_IMG_LIST="params_qq_img_list";

    /**
     * QQ专清视频集合
     */
    public static final String PARAMS_QQ_VIDEO_LIST="params_qq_video_list";

    /** 微信图片清理key*/
    public static final String WX_CACHE_SIZE_IMG="wx_cache_size_img";

    /** 微信视频清理key*/
    public static final String WX_CACHE_SIZE_VIDEO="wx_cache_size_video";

    /**
     * 各厂商ROM 版本KEY
     */
    public static final String ROM_HUAWEI = "ro.build.version.emui";
    public static final String ROM_XIAOMI = "ro.miui.ui.version.name";
    public static final String ROM_OPPO = "ro.build.version.opporom";
    public static final String ROM_VIVO = "ro.vivo.rom.version";
    public static final String ROM_MEIZU = "ro.build.display.id";
    public static final String ROM_LESHI = "ro.letv.release.version";
    public static final String ROM_SANXING = "ro.build.version.samsung";

    public static final int PERMISSION_FLOAT = 1;
    public static final int PERMISSION_LOCK_SHOW = 2;
    public static final int PERMISSION_SYSTEM_SETTING = 3;
    public static final int PERMISSION_STARTUP = 4;
    public static final int PERMISSION_NOTIFY = 5;
    public static final int PERMISSION_ACTIVITY = 6;

    public static final int HUAWEI = 1;
    public static final int XIAOMI = 2;
    public static final int OPPO = 3;
    public static final int VIVO = 4;
    public static final int MEIZU = 5;
    public static final int SANXING = 6;
    public static final int LESHI = 7;

    //定时任务间隔检测时间（分钟）
    public static final int SCAN_SPACE_LONG = 1;

}

