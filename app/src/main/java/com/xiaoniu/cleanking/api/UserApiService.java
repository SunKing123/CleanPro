package com.xiaoniu.cleanking.api;

import com.alibaba.fastjson.annotation.JSONField;
import com.xiaoniu.cleanking.base.BaseEntity;
import com.xiaoniu.cleanking.ui.main.bean.AppVersion;
import com.xiaoniu.cleanking.ui.main.bean.FileUploadInfoBean;
import com.xiaoniu.cleanking.ui.main.bean.Patch;
import com.xiaoniu.cleanking.ui.main.bean.UpdateInfoEntity;

import io.reactivex.Flowable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * @author tie
 * @date 2017/5/15.
 */
public interface UserApiService {

    /**
     * 查询app版本更新
     *
     * @return
     */
    @POST("/appVersion/query")
    Flowable<AppVersion> queryAppVersion(@Body RequestBody body);

    @POST("/appVersion/query")
    Flowable<AppVersion> sendVoiceSmsCode(@Body RequestBody body);
    /**
     * 查询app是否有补丁版本
     *
     * @param baseVersionName 基线版本
     * @param clientType
     * @param patchVersion    当前补丁版本
     * @return
     */
    @GET("/gateway/config/patch")
    Flowable<Patch> queryPatch(@Query("baseVersionName") String baseVersionName, @Query("clientType") String clientType, @Query("patchVersion") String patchVersion);


    /**
     * 意见反馈
     */
    @POST("/feedback/save")
    @JSONField
    Flowable<BaseEntity> submitQuestionReport(@Body RequestBody requestBody);


    /**
     * 上传用户头像
     **/
    @Multipart
    @POST("/file/upload")
    Flowable<FileUploadInfoBean> uploadFile(@Part MultipartBody.Part file);


}
