package com.xiaoniu.cleanking.ui.main.model;

import android.annotation.SuppressLint;

import com.google.gson.Gson;
import com.trello.rxlifecycle2.components.support.RxFragment;
import com.xiaoniu.cleanking.api.UserApiService;
import com.xiaoniu.cleanking.base.BaseModel;
import com.xiaoniu.cleanking.ui.main.bean.ImageAdEntity;
import com.xiaoniu.cleanking.utils.net.CommonSubscriber;
import com.xiaoniu.cleanking.utils.net.RxUtil;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class CleanMainModel extends BaseModel {

    private final RxFragment mRxFragment;

    @Inject
    UserApiService mService;

    @Inject
    public CleanMainModel(RxFragment rxFragment) {
        mRxFragment = rxFragment;
    }


    /**
     * 首页广告查询
     *
     * position 1-首页广告位
     * @param commonSubscriber
     */
    @SuppressLint("CheckResult")
    public void getBottomAd(CommonSubscriber<ImageAdEntity> commonSubscriber) {

        Gson gson = new Gson();
        Map<String, Object> map = new HashMap<>();
        map.put("position", "2");
        String json = gson.toJson(map);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);


        mService.queryBottomAd(body).compose(RxUtil.<ImageAdEntity>rxSchedulerHelper(mRxFragment))
                .subscribeWith(commonSubscriber);
    }
}
