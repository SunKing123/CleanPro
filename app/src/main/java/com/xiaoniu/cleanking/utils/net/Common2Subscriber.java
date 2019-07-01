package com.xiaoniu.cleanking.utils.net;

import io.reactivex.subscribers.ResourceSubscriber;

/**
 * 不做成功验证码校验 成功校验码不一定为200情况调用
 * Created by codeest on 2017/2/23.
 */

public abstract class Common2Subscriber<T> extends ResourceSubscriber<T> {

    @Override
    public void onComplete() {

    }

    @Override
    public void onError(Throwable e) {
        netConnectError();
    }

    @Override
    public void onNext(T t) {
       getData(t);
    }

    public abstract void getData(T t);

    //网络访问失败
    public abstract void netConnectError();
}
