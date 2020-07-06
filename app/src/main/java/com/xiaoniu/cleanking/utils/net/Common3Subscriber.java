package com.xiaoniu.cleanking.utils.net;

import com.xiaoniu.cleanking.constant.Constant;
import com.xiaoniu.cleanking.base.BaseEntity;

import io.reactivex.subscribers.ResourceSubscriber;

/**
 * Created z on 2017/2/23.
 * 网赚版本接口
 */

public abstract class Common3Subscriber<T extends BaseEntity> extends CommonSubscriber<T>  {

    @Override
    public void onComplete() {

    }

    @Override
    public void onError(Throwable e) {
        netConnectError();
    }

    @Override
    public void onNext(T t) {
        if (!Constant.SUCCESS_V3.equals(t.code)) {
            showExtraOp(t.code,t.msg);
        }else{
            getData(t);
        }

    }

    //解析Code不为200 失败后的DataBean字段
    public abstract void showExtraOp(String code,String message);

}
