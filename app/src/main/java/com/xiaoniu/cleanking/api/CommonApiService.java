package com.xiaoniu.cleanking.api;

import com.xiaoniu.cleanking.base.BaseEntity;
import com.xiaoniu.cleanking.ui.login.bean.BindPhoneBean;
import com.xiaoniu.cleanking.ui.login.bean.IsPhoneBindBean;
import com.xiaoniu.cleanking.ui.login.bean.LoginDataBean;
import com.xiaoniu.cleanking.ui.login.bean.RequestPhoneBean;
import com.xiaoniu.cleanking.ui.main.bean.AppVersion;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * @author z
 * @date 2020/5/15.
 */
public interface CommonApiService {

    /**
     * 查询app版本更新
     *
     * @return
     */
    @GET("/app/upgrade")
    Observable<AppVersion> queryAppVersion();

    /**
     * 微信登录
     *
     * @param body
     * @return
     */
    @POST("/clean-user/login")
    Observable<LoginDataBean> loginWeiChatApi(@Body RequestBody body);



    /**
     * 绑定微信到游客
     *
     * @param body
     * @return
     */
    @POST("/clean-user/bindWechat")
    Observable<LoginDataBean> bindingWeiChatApi(@Body RequestBody body);

    /**
     * 校验手机号是否被绑定
     *
     * @param phoneNum
     * @return
     */
    @GET("/clean-user/isPhoneBinded")
    Observable<IsPhoneBindBean> checkPhoneBindedApi(@Query("phoneNum") String phoneNum);

    /**
     * 手机号绑定
     *
     * @param body
     * @return
     */
    @POST("/clean-user/bindPhone")
    Observable<BindPhoneBean> phoneBindApi(@Body RequestBody body);

    /**
     * 发送短信验证码
     *
     * @param body
     * @return
     */
    @POST("/clean-user/sendMsg")
    Observable<BaseEntity> sendMsgApi(@Body RequestBody body);
    /**
     * 闪验通过token获取手机号
     */
    @GET("/clean-user/quickBinding")
    Observable<RequestPhoneBean> getPhoneNumFromShanYanApi(@Query("token") String systemType);
}
