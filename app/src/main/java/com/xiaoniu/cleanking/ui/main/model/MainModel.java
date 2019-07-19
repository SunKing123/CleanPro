package com.xiaoniu.cleanking.ui.main.model;


import com.google.gson.Gson;
import com.xiaoniu.cleanking.api.UserApiService;
import com.xiaoniu.cleanking.base.BaseModel;
import com.xiaoniu.cleanking.ui.main.bean.AppVersion;
import com.xiaoniu.cleanking.ui.main.bean.AuditSwitch;
import com.xiaoniu.cleanking.ui.main.bean.Patch;
import com.xiaoniu.cleanking.ui.main.bean.UpdateInfoEntity;
import com.xiaoniu.cleanking.utils.AndroidUtil;
import com.xiaoniu.cleanking.utils.net.Common4Subscriber;
import com.xiaoniu.cleanking.utils.net.RxUtil;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * @author tie
 * @date 2017/5/15
 */
public class MainModel extends BaseModel {

    private final RxAppCompatActivity mActivity;

    @Inject
    UserApiService mService;

    @Inject
    public MainModel(RxAppCompatActivity activity) {
        mActivity = activity;
    }

    public void queryAppVersion(Common4Subscriber<AppVersion> commonSubscriber) {
        Gson gson = new Gson();
        Map<String, Object> map = new HashMap<>();
        map.put("os", "android");
        map.put("platform", "1");
        map.put("appVersion", AndroidUtil.getAppVersionName());
        String json = gson.toJson(map);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        mService.queryAppVersion(body).compose(RxUtil.<AppVersion>rxSchedulerHelper(mActivity))
                .subscribeWith(commonSubscriber);
    }

    /**
     * 热修复补丁查询
     *
     * @param maps
     * @param commonSubscriber
     */
    public void getPatch(Map<String, String> maps, Common4Subscriber<Patch> commonSubscriber) {
        String baseVersionName = maps.get("baseVersionName");
        String clientType = maps.get("clientType");
        String patchVersion = maps.get("patchVersion");

        mService.queryPatch(baseVersionName, clientType, patchVersion).compose(RxUtil.<Patch>rxSchedulerHelper(mActivity))
                .subscribeWith(commonSubscriber);
    }

    public void queryAuditSwitch(Common4Subscriber<AuditSwitch> commonSubscriber) {
        Gson gson = new Gson();
        Map<String, Object> map = new HashMap<>();
        map.put("channel", AndroidUtil.getMarketId());
        map.put("appVersion", AndroidUtil.getAppVersionName());
        String json = gson.toJson(map);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        mService.queryAuditSwitch(body).compose(RxUtil.<AuditSwitch>rxSchedulerHelper(mActivity))
                .subscribeWith(commonSubscriber);
    }

}