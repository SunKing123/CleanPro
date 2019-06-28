package com.installment.mall.utils;

/**
 * 牛数sdk埋点事件
 * Created by lihao on 2019/03/06
 */
public enum  NiuDataEvent {

    //v1.0.1新增
//    login01("[下一步]_手机号录入", "login01"),
//    login02("[×]_取消录入手机号", "login02"),
//    login08("[下一步】_密码录入", "login08"),
//    login07("【显示】_密码显示隐藏", "login07"),
//    login05("【注册协议】_点击注册协议", "login05"),
//    login06("【信用授权协议】_点击信用授权协议", "login06"),
//    login11("【点击联系客户】_唤起客服的号码", "login11"),
//    login10("【重新获取】_验证码重新获取", "login10"),
//    login04("[下一步】_密码录入", "login04"),
//    login03("【显示】_密码显示隐藏", "login03"),
//
//    band02("【拍摄】_拍摄银行卡上报", "band02"),
//    band04("【代扣服务协议】_打开协议页面", "band04"),
//    band03("【支持银行】_打开支持银行页面", "band03"),
//    band01("【下一步】_提交银行卡信息", "band01"),
//    band06("【查看支持的银行】_打开支持银行页面", "band06"),
//    band07("【关闭弹窗】_点击返回键关闭弹窗", "band07"),
//    band05("【下一步】_完成银行卡号码确认", "band05"),
//
//    back04("【返回】_退出绑卡页面", "back04"),
//    back05("【返回】_回到绑卡页面", "back05"),
//    band08("【获取验证码】_短信发送验证码", "band08"),
//    band09("【下一步】_短信验证码输入完成", "band09"),
//    back02("【返回】_回到上级页面", "back02"),
//    back01("【返回】_回到上级页面", "back01"),
//    back03("【回退】_回到上级页面", "back03"),


    //v1.0.3新增
    login09("新用户输入验证码", "login09"),
    reg_003("登陆注册页面打开", "reg_003"),
    reg_005("新用户填写手机号", "reg_005"),
    reg_002("新用户获取验证码", "reg_002"),
    reg_001("新用户注册成功", "reg_001"),
    login_001("新用户登录", "login_001"),
    auth01("点击立即申请", "auth01"),
    auth09("身份认证", "auth09"),
    auth12("个人信息", "auth12"),
    auth16("信用认证（提交申请）", "auth16");


    public String eventName;
    public String eventCode;

    NiuDataEvent( String eventName, String eventCode) {
        this.eventName = eventName;
        this.eventCode = eventCode;
    }
}
