package com.xiaoniu.cleanking.api;

import com.xiaoniu.cleanking.ui.main.bean.Patch;
import com.xiaoniu.cleanking.ui.main.bean.UpdateInfoEntity;

import io.reactivex.Flowable;
import retrofit2.http.GET;
import retrofit2.http.Query;

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
    @GET("/gateway/config/version")
    Flowable<UpdateInfoEntity> queryAppVersion();

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

}
