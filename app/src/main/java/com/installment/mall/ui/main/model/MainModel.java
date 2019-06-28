package com.installment.mall.ui.main.model;


import com.installment.mall.api.UserApiService;
import com.installment.mall.base.BaseModel;
import com.installment.mall.ui.main.bean.Patch;
import com.installment.mall.ui.main.bean.UpdateInfoEntity;
import com.installment.mall.utils.net.Common4Subscriber;
import com.installment.mall.utils.net.RxUtil;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.util.Map;

import javax.inject.Inject;

/**
 *
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

    public void queryAppVersion(Common4Subscriber<UpdateInfoEntity> commonSubscriber) {
        mService.queryAppVersion().compose(RxUtil.<UpdateInfoEntity>rxSchedulerHelper(mActivity))
                .subscribeWith(commonSubscriber);
    }

    /**
     * 热修复补丁查询
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
}