package com.xiaoniu.cleanking.api;

import com.xiaoniu.cleanking.base.BaseEntity;

import io.reactivex.Flowable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.POST;


public interface BigDataApiService {

    /**
     *
     * 通用埋点
     *
     * @return
     */
    @POST("/apis/v1/dataprobe")
    Flowable<BaseEntity> statistics(@Body RequestBody body);

}
