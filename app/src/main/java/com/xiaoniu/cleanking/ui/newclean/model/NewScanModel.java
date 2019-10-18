package com.xiaoniu.cleanking.ui.newclean.model;

import com.google.gson.Gson;
import com.trello.rxlifecycle2.components.support.RxFragment;
import com.xiaoniu.cleanking.api.UserApiService;
import com.xiaoniu.cleanking.base.BaseModel;
import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;
import com.xiaoniu.cleanking.utils.net.Common4Subscriber;
import com.xiaoniu.cleanking.utils.net.RxUtil;
import com.xiaoniu.common.utils.AppUtils;
import com.xiaoniu.common.utils.ChannelUtil;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class NewScanModel extends BaseModel {

    private final RxFragment mRxFragment;
    @Inject
    UserApiService mService;

    @Inject
    public NewScanModel(RxFragment rxFragment) {
        mRxFragment = rxFragment;
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
}
