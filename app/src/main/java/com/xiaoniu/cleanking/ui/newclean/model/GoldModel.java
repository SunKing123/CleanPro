package com.xiaoniu.cleanking.ui.newclean.model;

import android.annotation.SuppressLint;

import com.google.gson.Gson;
import com.trello.rxlifecycle2.components.support.RxFragment;
import com.xiaoniu.cleanking.api.UserApiService;
import com.xiaoniu.cleanking.base.BaseModel;
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

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class GoldModel extends BaseModel {

    private final RxFragment mRxFragment;
    @Inject
    UserApiService mService;

    @Inject
    public GoldModel(RxFragment rxFragment) {
        mRxFragment = rxFragment;
    }

    /**
     * 首页广告查询
     *
     * position 1-首页广告位
     * @param commonSubscriber
     */
    @SuppressLint("CheckResult")
    public void getGoleGonfigs(CommonSubscriber<BubbleConfig> commonSubscriber) {
        mService.getBubbleConfig().compose(RxUtil.<BubbleConfig>rxSchedulerHelper(mRxFragment))
                .subscribeWith(commonSubscriber);
    }


}
