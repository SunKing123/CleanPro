package com.xiaoniu.cleanking.ui.newclean.model;

import android.annotation.SuppressLint;

import com.google.gson.Gson;
import com.trello.rxlifecycle2.components.support.RxFragment;
import com.xiaoniu.cleanking.api.UserApiService;
import com.xiaoniu.cleanking.base.BaseModel;
import com.xiaoniu.cleanking.ui.main.bean.BubbleCollected;
import com.xiaoniu.cleanking.ui.main.bean.BubbleConfig;
import com.xiaoniu.cleanking.ui.main.bean.HomeRecommendEntity;
import com.xiaoniu.cleanking.ui.main.bean.ImageAdEntity;
import com.xiaoniu.cleanking.ui.main.bean.InteractionSwitchList;
import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;
import com.xiaoniu.cleanking.utils.net.Common4Subscriber;
import com.xiaoniu.cleanking.utils.net.CommonSubscriber;
import com.xiaoniu.cleanking.utils.net.RxUtil;
import com.xiaoniu.common.utils.AppUtils;
import com.xiaoniu.common.utils.ChannelUtil;

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
     * 金币查询
     *
     * @param commonSubscriber
     */
    @SuppressLint("CheckResult")
    public void goleCollect(CommonSubscriber<BubbleCollected> commonSubscriber, FlowableTransformer tt,int locationNum) {
        Gson gson = new Gson();
        Map<String, Object> map = new HashMap<>();
        map.put("locationNum", locationNum);
        String json = gson.toJson(map);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        mService.bubbleCollected(body).compose(tt)
                .subscribeWith(commonSubscriber);
    }



}
