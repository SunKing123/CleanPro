package com.xiaoniu.cleanking.utils.net;

import com.xiaoniu.cleanking.app.Constant;
import com.xiaoniu.cleanking.base.BaseEntity;

import io.reactivex.subscribers.ResourceSubscriber;

/**
 *
 * @author codeest
 * @date 2017/2/23
 */
public abstract class CommonSubscriber<T extends BaseEntity> extends ResourceSubscriber<T> {

    @Override
    public void onComplete() {

    }

    @Override
    public void onError(Throwable e) {
        netConnectError();
    }

    @Override
    public void onNext(T t) {
        if (Constant.SUCCESS.equals(t.code) || Constant.AD_SUCCESS.equals(t.code)) {
            //成功
            getData(t);
//        } else if (Constant.TokenFailure.equals(t.code) || Constant.TokenIllegality.equals(t.code)) {
//            Intent intent = new Intent(AppApplication.getInstance(), CheckLoginActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            AndroidUtil.clearUserInfo();
//            NiuDataAPI.logout();
//            AppManager.getAppManager().finishAllActivity();
//            AppApplication.getInstance().startActivity(intent);
//            JPushInterface.setAlias(AppApplication.getInstance(), "", null);
        }else {
//            失败
            showExtraOp(t.msg);
        }
    }

    public abstract void getData(T t);

    /**
     * 解析Code不为200 失败后的DataBean字段
     * @param message 失败信息
     */
    public abstract void showExtraOp(String message);

    /**
     * 网络访问失败
     */
    public abstract void netConnectError();

}
