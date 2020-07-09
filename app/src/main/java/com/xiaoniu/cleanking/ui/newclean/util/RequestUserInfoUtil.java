package com.xiaoniu.cleanking.ui.newclean.util;


import com.xiaoniu.cleanking.api.UserApiService;
import com.xiaoniu.cleanking.app.injector.module.ApiModule;
import com.xiaoniu.cleanking.ui.main.bean.MinePageInfoBean;
import com.xiaoniu.cleanking.ui.tool.notify.event.UserInfoEvent;
import com.xiaoniu.cleanking.utils.net.CommonSubscriber;
import com.xiaoniu.cleanking.utils.user.UserHelper;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.schedulers.Schedulers;

import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;

/**
 * Created by zhaoyingtao
 * Date: 2020/7/9
 * Describe: 公共请求用户金币的类
 */
public class RequestUserInfoUtil {
    /**
     * 获取用户金币并发送EventBus 通知
     */
    public static void getUserCoinInfo() {
        if (!UserHelper.init().isLogin()) {
            return;
        }
        ApiModule.getRetrofit().create(UserApiService.class)
                .getMinePageInfo().subscribeOn(Schedulers.io())
                .observeOn(mainThread())
                .subscribeWith(new CommonSubscriber<MinePageInfoBean>() {
                    @Override
                    public void getData(MinePageInfoBean infoBean) {
                        if (infoBean != null && infoBean.getData() != null) {
                            UserInfoEvent event = new UserInfoEvent();
                            event.infoBean = infoBean.getData();
                            EventBus.getDefault().post(event);
                        }
                    }

                    @Override
                    public void showExtraOp(String message) {

                    }

                    @Override
                    public void netConnectError() {

                    }
                });
    }
}
