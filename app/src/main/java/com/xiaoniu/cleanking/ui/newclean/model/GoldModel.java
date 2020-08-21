package com.xiaoniu.cleanking.ui.newclean.model;

import android.annotation.SuppressLint;

import com.google.gson.Gson;
import com.xiaoniu.cleanking.api.UserApiService;
import com.xiaoniu.cleanking.base.BaseModel;
import com.xiaoniu.cleanking.ui.main.bean.BubbleCollected;
import com.xiaoniu.cleanking.ui.main.bean.BubbleConfig;
import com.xiaoniu.cleanking.ui.main.bean.BubbleDouble;
import com.xiaoniu.cleanking.ui.main.bean.DaliyTaskListData;
import com.xiaoniu.cleanking.utils.net.CommonSubscriber;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.FlowableTransformer;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class GoldModel extends BaseModel {
    @Inject
    UserApiService mService;

    /**
     * 金币查询
     *
     * @param commonSubscriber
     */
    @SuppressLint("CheckResult")
    public void getGoleGonfigs(CommonSubscriber<BubbleConfig> commonSubscriber, FlowableTransformer tt) {
        mService.getBubbleConfig().compose(tt)
                .subscribeWith(commonSubscriber);
    }

    /**
     * 金币领取
     *
     * @param commonSubscriber
     */
    @SuppressLint("CheckResult")
    public void goleCollect(CommonSubscriber<BubbleCollected> commonSubscriber, FlowableTransformer tt, int locationNum) {
        Gson gson = new Gson();
        Map<String, Object> map = new HashMap<>();
        map.put("locationNum", locationNum);
        String json = gson.toJson(map);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        mService.bubbleCollected(body).compose(tt)
                .subscribeWith(commonSubscriber);
    }


    /**
     * 金币翻倍
     *
     * @param commonSubscriber
     */
    @SuppressLint("CheckResult")
    public void goldDouble(CommonSubscriber<BubbleDouble> commonSubscriber, FlowableTransformer tt, String uuid, int locationNum, int goldCount, int doubledMagnification,boolean isTask) {
        Gson gson = new Gson();
        Map<String, Object> map = new HashMap<>();
        map.put("locationNum", locationNum);
        map.put("uuid", uuid);
        map.put("goldCount", goldCount);
        map.put("doubledMagnification", doubledMagnification);
        String json = gson.toJson(map);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        if(isTask){ //任务翻版
            mService.daliyTasksDouble(body).compose(tt).subscribeWith(commonSubscriber);
        }else{
            mService.bubbleDouble(body).compose(tt).subscribeWith(commonSubscriber);
        }

    }

    /**
     * 金币查询
     *
     * @param commonSubscriber
     */
    @SuppressLint("CheckResult")
    public void getDaliyTaskList(CommonSubscriber<DaliyTaskListData> commonSubscriber, FlowableTransformer tt) {
        mService.daliyTaskList().compose(tt)
                .subscribeWith(commonSubscriber);
    }



    /**
     * 日常任务领取
     * @param commonSubscriber
     */
    @SuppressLint("CheckResult")
    public void daliyTasksCollect(CommonSubscriber<BubbleCollected> commonSubscriber, FlowableTransformer tt, int locationNum) {
        Gson gson = new Gson();
        Map<String, Object> map = new HashMap<>();
        map.put("locationNum", locationNum);
        String json = gson.toJson(map);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        mService.daliyTasksCollect(body).compose(tt).subscribeWith(commonSubscriber);
    }







}
