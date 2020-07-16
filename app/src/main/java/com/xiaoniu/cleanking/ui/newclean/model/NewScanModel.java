package com.xiaoniu.cleanking.ui.newclean.model;

import android.annotation.SuppressLint;

import com.google.gson.Gson;
import com.trello.rxlifecycle2.components.support.RxFragment;
import com.xiaoniu.cleanking.api.UserApiService;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.base.BaseModel;
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

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class NewScanModel extends GoldModel {

    private final RxFragment mRxFragment;

    @Inject
    public NewScanModel(RxFragment rxFragment) {
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

    /**
     * 广告开关
     *
     * @param commonSubscriber
     */
    public void getSwitchInfoList(Common4Subscriber<SwitchInfoList> commonSubscriber) {
        Gson gson = new Gson();
        Map<String, Object> map = new HashMap<>();
        map.put("channel", ChannelUtil.getChannel());
        map.put("appVersion", AppUtils.getVersionName(mRxFragment.getActivity(), mRxFragment.getActivity().getPackageName()));
        String json = gson.toJson(map);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        mService.getSwitchInfoList(body).compose(RxUtil.rxSchedulerHelper(mRxFragment)).subscribeWith(commonSubscriber);
    }


    /**
     * 互动式广告开关
     *
     * @param commonSubscriber
     */
    public void getInteractionSwitch(Common4Subscriber<InteractionSwitchList> commonSubscriber) {
        Gson gson = new Gson();
        Map<String, Object> map = new HashMap<>();
        map.put("channel", ChannelUtil.getChannel());
        map.put("appVersion", AppUtils.getVersionName(AppApplication.getInstance(), AppApplication.getInstance().getPackageName()));
        String json = gson.toJson(map);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        mService.getInteractionSwitch(body).compose(RxUtil.rxSchedulerHelper(mRxFragment)).subscribeWith(commonSubscriber);
    }
    /**
     * 推荐列表
     */
    public void getRecommendList( Common4Subscriber<HomeRecommendEntity> commonSubscriber) {
        mService.getRecommendList("opearte_page_main").compose(RxUtil.rxSchedulerHelper(mRxFragment)).subscribeWith(commonSubscriber);
    }
}
