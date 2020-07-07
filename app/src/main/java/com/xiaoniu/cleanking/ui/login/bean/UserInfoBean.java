package com.xiaoniu.cleanking.ui.login.bean;

/**
 * Created by zhaoyingtao
 * Date: 2020/7/5
 * Describe:
 */
public class UserInfoBean {

    /**
     * userId : 5637684114090889216
     * userType : 2
     * phone :
     * nickname : 游客4devic
     * userAvatar : http://pic.58pic.com/58pic/15/14/29/47e58PICQUR_1024.jpg
     * openId : deviceid22222222
     * token : 5637977468947140608
     * isLogin : 1
     */

    public String userId;
    // userType 1为微信登录状态 2-游客  绑定手机号只能绑定到微信登录状态
    public int userType;
    public String phone;
    public String nickname;
    public String userAvatar;
    public String openId;
    public String token;
    public String isLogin;

}
