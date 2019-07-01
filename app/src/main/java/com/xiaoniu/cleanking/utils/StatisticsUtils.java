package com.xiaoniu.cleanking.utils;

import com.xiaoniu.cleanking.api.BigDataApiService;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.app.injector.module.ApiModule;
import com.xiaoniu.cleanking.base.BaseEntity;
import com.xiaoniu.cleanking.utils.net.Common2Subscriber;
import com.xiaoniu.cleanking.utils.net.RxUtil;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * 埋点统计工具类
 * Created by fengpeihao on 2018/1/24.
 */

public class StatisticsUtils {

    public static final int STATE_REPAYMENT = 0; //正常还款
    public static final int STATE_EXTENSION = 1; //续期还款
    public static final int STATE_OVERDUE = 2; //逾期还款

    /**
     * sdk 自定义埋点
     *
     * @param event
     */
    public static void trackEvent(NiuDataEvent event) {
//        NiuDataAPI.trackEvent(event.eventCode, event.eventName);
    }


    /**
     * @param event 事件
     */
    public static void burying(BuryEvent event) {
        Gson gson = new Gson();
        String json = gson.toJson(getParameter(event));
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);

        requestServer(body, null);
    }

    /**
     * @param event    事件
     * @param listener 请求回调
     */
    public static void burying(BuryEvent event, OnResponseListener listener) {
        Gson gson = new Gson();
        String json = gson.toJson(getParameter(event));
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);

        requestServer(body, listener);
    }

    /**
     * @param event 事件
     * @param state 是否成功 1 成功 2失败
     */
    public static void burying(BuryEvent event, int state) {
        Gson gson = new Gson();
        String json = gson.toJson(getParameter(event, state));
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);

        requestServer(body, null);
    }

    /**
     * @param type      1 人脸识别认证时长 2 身份证正面认证时长 3 身份证反面认证时长  4 个人信息填写时长
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param event     事件
     */
    public static void burying(int type, long startTime, long endTime, BuryEvent event) {
        Gson gson = new Gson();
        String json = gson.toJson(getParameter(type, startTime, endTime, event));
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);

        requestServer(body, null);
    }

    /**
     * 上传数据（短信息、通话记录、通讯录、app应用列表）
     *
     * @param event     事件
     * @param core_data 数据
     */
    public static void uploadData(BuryEvent event, Object core_data, int type, OnResponseListener listener) {
        Gson gson = new Gson();
        String json = gson.toJson(getParameter(event, core_data, type));
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);

        requestServer(body, listener);
    }

    private static Map<String, Object> getParameter(BuryEvent event, int status) {
        Map<String, Object> map = new HashMap<>();
        map.put("message_id", event.messageId);
        map.put("product_name", AndroidUtil.getAppNum());
        map.put("status", status);
        map.put("customer_id", AndroidUtil.getCustomerId());
        map.put("phone_num", AndroidUtil.getPhoneNum());
        map.put("data", getData(event));
        return map;
    }

    private static Map<String, Object> getParameter(BuryEvent event, Object core_data, int type) {
        Map<String, Object> map = new HashMap<>();
        map.put("message_id", event.messageId);
        map.put("product_name", AndroidUtil.getAppNum());
        map.put("customer_id", AndroidUtil.getCustomerId());
        map.put("phone_num", AndroidUtil.getPhoneNum());
        map.put("jsonInfo", core_data);
        map.put("type", type);
        map.put("data", getData(event));
        return map;
    }

    private static Map<String, Object> getParameter(BuryEvent event) {
        Map<String, Object> map = new HashMap<>();
        map.put("message_id", event.messageId);
        map.put("product_name", AndroidUtil.getAppNum());
        map.put("customer_id", AndroidUtil.getCustomerId());
        map.put("phone_num", AndroidUtil.getPhoneNum());
        map.put("data", getData(event));
        return map;
    }

    private static Map<String, Object> getParameter(int type, long startTime, long endTime, BuryEvent event) {
        Map<String, Object> map = new HashMap<>();
        map.put("message_id", event.messageId);
        map.put("product_name", AndroidUtil.getAppNum());
        map.put("customer_id", AndroidUtil.getCustomerId());
        map.put("phone_num", AndroidUtil.getPhoneNum());
        map.put("type", type);
        map.put("startTime", startTime);
        map.put("endTime", endTime);
        map.put("data", getData(event));
        return map;
    }

    /**
     * 获取data 数据
     *
     * @param event
     * @return
     */
    public static Map<String, Object> getData(BuryEvent event) {
        Map<String, Object> map = new HashMap<>();
        map.put("uuid", AndroidUtil.getUdid());
        map.put("page_name", event.page_name);
        map.put("even_name", event.even_name);
        map.put("even_code", event.even_code);
        map.put("title", event.title);
        map.put("req_time", System.currentTimeMillis());
        map.put("appVersion", AndroidUtil.getAppVersionName());// APP版本号
        map.put("marketName", AndroidUtil.getMarketId());//APP渠道名
        map.put("osSystem", "Android");//手机操作系统：iOS、Android、其他
        return map;
    }

    /**
     * 请求服务
     */
    private static void requestServer(RequestBody body, final OnResponseListener listener) {
        ApiModule apiModule = new ApiModule(AppApplication.getInstance());
        BigDataApiService bigDataApiService = apiModule.provideBigDataApiService();
        bigDataApiService.statistics(body).compose(RxUtil.<BaseEntity>rxSchedulerHelper()).subscribeWith(new Common2Subscriber<BaseEntity>() {
            @Override
            public void getData(BaseEntity baseEntity) {
                if (baseEntity != null && "200".equals(baseEntity.code)) {
                    if (listener != null) listener.onSuccess(new Gson().toJson(baseEntity));
                } else {
                    if (listener != null) listener.onError(new Gson().toJson(baseEntity));
                }
            }

            @Override
            public void netConnectError() {
                if (listener != null) listener.onError("网络异常");
            }
        });
    }

    public enum BuryEvent {
        INPUT_PHONE_NUMBER("快速注册登陆页面", "【下一步】_手机号码录入", "login01", "手机号输入后跳转至下一步", "250003"),
        INPUT_PASSWORD("输入登陆密码页面", "【下一步】_手机登录密码录入", "login02", "手机登录密码录入后跳转至下一步", "250003"),
        MODIFY_PASSWORD("注册设置密码页面", "【忘记密码】", "login05", "用户主动发起登录密码修改", "250003"),
        INPUT_VERIFY_CODE("输入验证码页面", "【下一步】_手机验证码录入", "login03", "手机验证码输入后跳转至下一步", "250003"),
        NEW_USER_SETTINGS_PASSWORD("注册设置密码页面", "【下一步】_设置密码", "login04", "手机号码首次注册后设置密码跳转至下一步", "250003"),
        MAIN_AUTH_CLICK("主页-申请借款页", "【去申请】", "auth01", "登录成功后申请借款", "250003"),
        IDENTITY_AUTH_BACK("认证流程页-身份认证", "身份认证阶段返回button", "auth02", "身份认证未通过后返回首页", "250003"),
        FACE_VERIFY_CLICK("认证流程页-身份认证", "【用户人脸识别】", "auth03", "点击上传脸部识别信息", "250003"),
        FACE_FRONT_VERIFY_CLICK("认证流程页-身份认证", "【身份证正面】", "auth04", "点击上传身份证人像面", "250003"),
        BACK_ID_CARD_CLICK("认证流程页-身份认证", "【返回】", "auth28", "返回身份证识别页面", "250003"),
        FINISH_ID_CARD_CLICK("认证流程页-身份认证", "【确认无误】", "auth29", "完成身份认证页面", "250003"),
        IDENTITY_AUTH_BACK_CLICK("认证流程页-身份认证", "【身份证反面】", "auth05", "点击上传身份证国徽面", "250003"),
        IDENTITY_AUTH_NEXT_STEP_CLICK("认证流程页-身份认证", "【下一步】_身份认证", "auth06", "身份认证完毕后跳转至下一步", "250003"),
        PERSON_INFO_BACK_CLICK("认证流程页-个人信息", "个人信息录入阶段返回button", "auth07", "个人信息录入中返回首页", "250003"),
        PERSON_INFO_NEXT_STEP_CLICK("认证流程页-个人信息", "【下一步】_个人信息", "auth08", "个人信息录入后跳转至下一步", "250003"),
        CREDIT_AUTH_BACK_CLICK("认证流程页-信用认证", "信用认证阶段返回button", "auth09", "信用认证未通过后返回首页", "250003"),
        EMERGENCY_CONTACT_AUTH_CLICK("认证流程页-信用认证", "【紧急联系人】认证", "auth10", "申请人点击填写紧急联系人", "250003"),
        SAVE_EMERGENCY_CONTACT_CLICK("填写紧急联系人页", "【保存】_紧急联系人", "auth11", "提交直系亲属及其他联系人信息", "250003"),
        MOBILE_OPERATOR_CLICK("认证流程页-信用认证", "【手机运营商】认证", "auth14", "申请人授权获取通话详单", "250003"),
        MOBILE_OPERATOR_CUSTOMER_SERVICE_CLICK("手机运营商认证页面", "【手机运营商客服电话】", "auth15", "申请人忘记服务密码快速查运营商客服电话", "250003"),
        MOBILE_OPERATOR_NEXT_STEP_CLICK("手机运营商认证页面", "【下一步】_手机运营商认证", "auth16", "认证手机运营商服务密码", "250003"),
        MOBILE_OPERATOR_CODE_AUTH_CLICK("手机运营商认证页面", "【去认证】_手机运营商短信随机码验证", "auth17", "验证手机运营商短信随机验证码", "250003"),
        SUBMITTED_AUTH_INFORMATION_CLICK("认证流程页-信用认证", "【提交资料】", "auth18", "所有信息填写完毕后提交审核", "250003"),
        CREDIT_AGAIN_AUTH_BACK_CLICK("认证流程页-信用认证", "复贷用户信用认证阶段返回button", "auth19", "复贷用户信用认证未通过后返回首页", "250003"),
        MOBILE_OPERATOR_AGAIN_AUTH_CLICK("手机运营商认证页面", "【手机运营商】复贷用户认证", "auth22", "复贷用户授权获取通话详单", "250003"),
        MOBILE_OPERATOR_AGAIN_USER_AUTH_CLICK("手机运营商认证页面", "【去认证】_复贷用户手机运营商认证", "auth23", "复贷用户认证手机运营商服务密码", "250003"),
        MOBILE_OPERATOR_AGAIN_USER_CODE_CLICK("手机运营商认证页面", "【去认证】_复贷用户手机运营商短信随机码验证", "auth24", "复贷用户验证手机运营商短信随机验证码", "250003"),
        MOBILE_OPERATOR_AGAIN_RESUBMIT_CLICK("手机运营商认证页面", "【提交资料】", "auth25", "复贷用户重新提交信用认证资料", "250003"),
        MINE_BORROWING_RECORD("我的页面", "借款记录", "card24", "", "250003"),
        MINE_HELP_CENTER("我的页面", "帮助中心", "card26", "", "250003"),
        MINE_CONTACT_CUSTOMER_SERVICE("我的页面", "联系客服", "card27", "", "250003"),
        MINE_MODIFY_PASSWORD("我的页面", "修改登录密码", "card28", "", "250003"),
        MINE_ABOUT_ME("我的页面", "关于我们", "card29", "", "250003"),
        MINE_ADVICE_FEEDBACK("我的页面", "意见反馈", "card30", "", "250003"),
        MINE_BANK_CARD("我的页面", "我的银行卡", "card16", "添加提现或者还款银行卡", "250003"),
        MINE_CHECK_MY_PROFILE("我的资料页面", "已完善资料", "card13", "查看我的资料", "250003"),
        MINE_PERSONAL_INFORMATION("我的资料页面", "个人信息（修改资料）", "card14", "修改个人资料", "250003"),
        SAVE_PERSONAL_INFORMATION("个人资料页面", "保存资料", "card15", "", "250003"),
        MINEPAGEGOTOAUTH("我的资料页面", "个人资料去完善", "info01", "补充个人资料、手机运营商、更多认证", "250003"),
        PASS_MODIFY01("输入密码页", "修改登录密码", "pass_modify01", "修改现有密码或者忘记密码后重新设置", "250003"),
        PASS_MODIFY02("设置密码页", "【下一步】_修改密码", "pass_modify02", "用户主动发起登录密码修改", "250003"),


        QuJieKuan("借款页面", "去借款", "CLICK_C_01", "去借款", "230001"),
        LiJiShenQing("借款页面", "立即申请", "CLICK_C_001", "立即申请", "230001"),
        BaoFeiLiJiGouMai("保费页面", "保费立即购买", "CLICK_C_02", "保费立即购买", "230002"),
        BaoFeiZanBuXuYao("保费页面", "保费暂不需要", "CLICK_C_03", "保费暂不需要", "230003"),

        BaoFeiGuanBi("保费页面", "保费关闭", "CLICK_C_04", "保费关闭", "230004"),
        BaoFeiZhiFu("保费页面", "保费支付", "CLICK_C_05", "保费支付", "230005"),
        HuiYuanLiJiGouMai("会员页", "会员立即购买", "CLICK_C_06", "会员立即购买", "230006"),

        HuiYuanZanBuXuYao("会员页", "会员暂不需要", "CLICK_C_07", "会员暂不需要", "230007"),
        HuiYuanGuanBi("会员页面", "会员关闭", "CLICK_C_08", "会员关闭", "230008"),
        HuiYuanZhiFu("会员页面", "会员支付", "CLICK_C_09", "会员支付", "230009"),

        TabJiekuan("导航页面", "借款", "CLICK_C_10", "借款", "2300010"),
        TabShangcheng("导航页面", "商城", "CLICK_C_11", "商城", "2300011"),
        TabTie("导航页面", "提额", "CLICK_C_12", "提额", "2300012"),
        TabZhuanqian("导航页面", "赚钱", "CLICK_C_13", "赚钱", "2300013"),
        TabWode("导航页面", "我的", "CLICK_C_14", "我的", "2300014"),
        JiekuanBanner("导航页面", "借款BANNER", "CLICK_C_15", "借款BANNER", "2300015"),
        RenLianShiBieDianJi("身份认证页面", "人脸认证点击", "CLICK_B_03", "人脸认证点击量统计", "200009"),
        ShenFenZhengZhengMianDianJi("身份认证页面", "身份证正面认证点击", "CLICK_B_04", "身份证正面认证点击量统计", "200009"),
        ShenFenFanZhengMianDianJi("身份认证页面", "身份证反面认证点击", "CLICK_B_05", "身份证反面认证点击量统计", "200009"),

        RenLianRenZheng("身份认证页面", "face++人脸认证", "TIME_A_01", "人脸认证时长统计", "200001"),
        ShenFenZhengZhengMian("身份认证页面", "身份证正面认证", "TIME_A_02", "身份证正面认证时长统计", "200001"),
        ShenFenZhengFanMian("身份认证页面", "身份证反面认证", "TIME_A_03", "人脸认证时长统计", "200001"),
        GeRenXinXi("个人信息页面", "个人信息保存", "TIME_A_04", "个人信息保存时长统计", "200001"),
        LiJiGouMai("借款排队页面", "立即购买按钮点击量", "CLICK_B_01", "提现", "210001"),
        SmsInbox("", "", "", "获取手机短信", "200005"),
        CallLog("", "", "", "获取手机通话记录", "200003"),
        AppList("", "", "", "获取手机APP列表", "200004"),
        AddressBook("", "", "", "获取手机通讯录", "200002"),
        DeviceInfo("", "", "", "获取手机设备信息", "210002"),

        LOAN_FAIL_CONTACTS_SERVICE("提现失败页面", "【在线咨询】", "auth27", "转至人工咨询", "250003"),
        GOTO_LOAN_UNBIND("借款页", "【去借款】", "menu01.1", "首页借款按钮-用户未绑卡", "250003"),
        GOTO_LOAN_BIND("借款页", "【去借款】", "menu01.2", "首页借款按钮-用户已绑卡", "250003"),
        ONE_KEY_BUY("一键购买保费弹窗", "【认购保险并提现】", "ins01", "使用贷款抵扣保费并放款", "250003"),
        CLOSE_ONE_KEY_BUY("一键购买保费弹窗", "提现弹窗关闭button", "ins02", "不考虑提现并关闭弹窗", "250003"),
        LOAN_FILE_TRY_AGAIN("提现失败页", "【再试一次】_提现失败", "loan03", "其他原因导致的放款失败", "250003"),
        AUTH_AUDIT_CONTACTS_SERVICE("进件资料审核中页面", "【联系客服】", "loan13", "点击后转在线人工客服页面", "250003"),
        REFUSED_NEXT("被拒导流管家页", "【立即拿钱】", "auth26", "用户被拒导流至管家后点击“立即拿钱”", "250003"),
        ActivatingQuantity("激活页面", "激活点击量", "CLICK_B_02", "激活统计", "250003"),
        REPAYMENT_EXECUTE("待还款页面", "【立即还款】", "repay01", "客户发起还款请求", "250003"),
        REPAYMENT_MODE_BACK("选择还款方式页面", "选择还款方式页返回button", "repay02", "在选择还款方式页面点击返回", "250003"),
        REPAYMENT_EXTENSION("待还款页面", "【申请延期】", "repay03", "由于无法按时还款需要申请延期还款", "250003"),
        REPAYMENT_EXTENSION_BACK("延期页面", "申请延期返回button", "repay04", "不考虑延期还款返回至立即还款页面", "250003"),
        REPAYMENT_EXTENSION_CONFIRM("延期页面", "【确认延期】", "repay05", "确认延期还款需要支付的费用", "250003"),
        REPAYMENT_MODE_BANK_PAY("选择还款方式页面", "【一键支付】", "repay06", "点击一键还款", "250003"),
        REPAYMENT_MODE_BANK_PAY_DIALOG_CLICK("选择还款方式页面", "【一键支付】", "repay07", "点击进行银行卡支付", "250003"),
        BIND_STEP2_CONFIRM("绑卡第二步页面", "【下一步】_绑定银行卡", "band01", "银行卡信息校验成功后跳转至下一步", "250003"),
        BIND_CONFIRM_NEXT("扫描银行卡确认页面", "【下一步】_银行卡号图像认证", "band02", "认证需要绑定的银行卡片信息", "250003"),
        BIND_STEP2_BACK("绑卡第二步页面", "绑定银行卡阶段返回button", "band04", "银行卡未成功绑定返回首页", "250003"),
        BANK_MANAGE_SET_DEFAULT("银行卡详情页面", "设为默认提现", "card01", "设置某银行卡为提现银行卡", "250003"),
        BIND_STEP1_NEXT("绑卡第一步页面", "【下一步】_绑定银行卡", "card02", "银行卡信息校验成功后跳转至下一步", "250003"),
        BIND_STEP1_CLICK_CAMERA("绑卡第一步页面", "【下一步】_银行卡号图像认证", "card03", "认证需要绑定的银行卡片信息", "250003"),
        BIND_STEP1_CLICK_PROTOCOL("绑卡第一步页面", "【代扣服务协议】_绑卡", "card04", "查询代扣服务协议", "250003"),
        BIND_STEP1_BACK("绑卡第一步页面", "绑定银行卡阶段返回button", "card05", "银行卡未成功绑定返回", "250003"),
        BANK_MANAGE_ADD_BANK("银行卡管理界面", "提现银行卡界面添加银行卡button", "card18", "添加新银行卡", "250003"),
        BANK_MANAGE_ADD_BANK_WINDOW("银行卡管理界面右上角弹窗", "添加银行卡（右上角button）", "card19", "添加新银行卡", "250003"),
        BANK_MANAGE_SET_ORDER_WINDOW("银行卡管理界面右上角弹窗", "设置还款顺序（右上角button）", "card20", "调整还款银行卡的先后顺序", "250003"),
        BANK_DETAIL_DELETE_BANK("银行卡详情页面", "解除绑定", "card21", "确认解除已绑定的银行卡", "250003"),
        BANK_MANAGE_WITHDRAW_CLICK("银行卡详情页面", "提现银行卡TAB切换", "card17", "提现银行卡TAB切换", "250003"),
        BANK_MANAGE_REPAYMENT_CLICK("银行卡详情页面", "还款银行卡TAB切换", "card22", "还款银行卡TAB切换", "250003"),
        BANK_MANAGE_REPAYMENT_ADD_BANK("银行卡管理界面", "还款银行卡界面添加银行卡button", "card23", "添加新银行卡", "250003"),
        BANK_MANAGE_DELETE_BIND_BUTTON_CLICK("解绑弹窗", "解绑银行卡弹窗解绑按钮", "card25", "解除绑定", "250003");


        String page_name;
        String even_name;
        String even_code;
        String title;
        String messageId;

        BuryEvent(String page_name, String even_name, String even_code, String title, String messageId) {
            this.page_name = page_name;
            this.even_name = even_name;
            this.even_code = even_code;
            this.title = title;
            this.messageId = messageId;
        }
    }

    public interface OnResponseListener {
        void onSuccess(String result);

        void onError(String result);
    }

    //    public static Map<String, String> getData() {
//        Map<String, String> map = new HashMap<>();
//        map.put("pageType", "Android页面");//数据页面类型：iOS页面、Android页面、h5页面
//        map.put("appVersion", AndroidUtil.getAppVersionName(AppApplication.getInstance()));// APP版本号
//        map.put("deviceSerialNumber", AndroidUtil.getUdid(AppApplication.getInstance()));//手机设备序列号
//        map.put("deviceType", AndroidUtil.getSystemModel());//手机型号
//        map.put("ip", AndroidUtil.getHostIP());//手机ip地址
//        map.put("latitude", AndroidUtil.getLatitude(AppApplication.getInstance()));//纬度
//        map.put("longitude", AndroidUtil.getLongitude(AppApplication.getInstance()));//经度
//        map.put("marketName", AndroidUtil.getMarketId(AppApplication.getInstance()));//APP渠道名
//        map.put("networkType", NetWorkUtils.getNetWorkType(AppApplication.getInstance()));//联网方式：wifi、4G、3G、2G、其他
//        map.put("osSystem", "Android");//手机操作系统：iOS、Android、其他
//        map.put("osVersion", AndroidUtil.getAndroidSDKVersion() + "");//手机操作系统版本号
//        map.put("osResolution", DeviceUtils.getScreenHeight(AppApplication.getInstance()) + "*" + DeviceUtils.getScreenWidth(AppApplication.getInstance()));//手机分辨率(1920*1080...)
//        return map;
//    }
}
