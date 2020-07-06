package com.xiaoniu.cleanking.api;

import com.xiaoniu.cleanking.ui.login.bean.LoginDataBean;
import com.xiaoniu.cleanking.ui.main.bean.AppVersion;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

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


}
