package com.xiaoniu.cleanking.utils.net;

import com.xiaoniu.cleanking.app.Constant;
import com.xiaoniu.cleanking.base.BaseEntity;

/**
 * Created by codeest on 2017/2/23.
 */

public abstract class Common4Subscriber<T extends BaseEntity> extends CommonSubscriber<T> {

    @Override
    public void onComplete() {

    }

    @Override
    public void onError(Throwable e) {
        netConnectError();
    }

    @Override
    public void onNext(T t) {
        if (!Constant.SUCCESS.equals(t.code)) {
            showExtraOp(t.code,t.msg);
        }
        super.onNext(t);
    }
    //解析Code不为200 失败后的DataBean字段
    public abstract void showExtraOp(String code,String message);


}
