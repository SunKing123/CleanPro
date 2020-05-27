package com.xiaoniu.cleanking.ui.newclean.model;

import android.content.Context;

import com.google.gson.Gson;
import com.trello.rxlifecycle2.components.support.RxFragment;
import com.xiaoniu.cleanking.api.UserApiService;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.app.injector.module.ApiModule;
import com.xiaoniu.cleanking.mvp.BaseModel;
import com.xiaoniu.cleanking.mvp.DefaultRetrofitProxyImpl;
import com.xiaoniu.cleanking.mvp.IBaseModel;
import com.xiaoniu.cleanking.mvp.IRetrofitProxy;
import com.xiaoniu.cleanking.mvp.RepositoryManager;
import com.xiaoniu.cleanking.ui.main.bean.InteractionSwitchList;
import com.xiaoniu.cleanking.ui.newclean.contact.CleanMainTopViewContact;
import com.xiaoniu.cleanking.utils.net.Common4Subscriber;
import com.xiaoniu.cleanking.utils.net.RxUtil;
import com.xiaoniu.common.utils.AppUtils;
import com.xiaoniu.common.utils.ChannelUtil;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by xinxiaolong on 2020/5/27.
 * email：xinxiaolong123@foxmail.com
 */
public class CleanMainTopViewModel implements CleanMainTopViewContact.Model {

    private RxFragment mRxFragment;

    public CleanMainTopViewModel(RxFragment fragment){
        mRxFragment=fragment;
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void getInteractionSwitch(Common4Subscriber<InteractionSwitchList> commonSubscriber) {
        Gson gson = new Gson();
        Map<String, Object> map = new HashMap<>();
        map.put("channel", ChannelUtil.getChannel());
        map.put("appVersion", AppUtils.getVersionName(mRxFragment.getActivity(), mRxFragment.getActivity().getPackageName()));
        String json = gson.toJson(map);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        new ApiModule(AppApplication.getInstance()).provideHomeService().getInteractionSwitch(body).compose(RxUtil.rxSchedulerHelper(mRxFragment)).subscribeWith(commonSubscriber);
    }
}
