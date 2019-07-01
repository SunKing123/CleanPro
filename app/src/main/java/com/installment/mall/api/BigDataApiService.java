package com.installment.mall.api;

import com.installment.mall.base.BaseEntity;

import io.reactivex.Flowable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.POST;


public interface BigDataApiService {

    /**
     *
     * 通用埋点
     * @param body
     * @return
     */
    @POST("/apis/v1/dataprobe")
    Flowable<BaseEntity> statistics(@Body RequestBody body);

}
