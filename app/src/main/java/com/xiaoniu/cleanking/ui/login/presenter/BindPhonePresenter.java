package com.xiaoniu.cleanking.ui.login.presenter;

import android.app.Application;
import android.text.TextUtils;

import com.chuanglan.shanyan_sdk.OneKeyLoginManager;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.RxLifecycleUtils;
import com.xiaoniu.cleanking.ui.login.bean.RequestPhoneBean;
import com.xiaoniu.cleanking.ui.login.contract.BindPhoneContract;
import com.xiaoniu.cleanking.utils.user.UserHelper;
import com.xiaoniu.common.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;

import static com.xiaoniu.cleanking.utils.user.UserHelper.BIND_PHONE_SUCCESS;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 07/02/2020 18:03
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@ActivityScope
public class BindPhonePresenter extends BasePresenter<BindPhoneContract.Model, BindPhoneContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    AppManager mAppManager;

    @Inject
    public BindPhonePresenter(BindPhoneContract.Model model, BindPhoneContract.View rootView) {
        super(model, rootView);
    }

    public void getPhoneNumFromShanYan(String token) {
        mModel.getPhoneNumFromShanYan(token).subscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {
                })
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(new ErrorHandleSubscriber<RequestPhoneBean>(mErrorHandler) {
                    @Override
                    public void onNext(RequestPhoneBean phoneBean) {
                        if (phoneBean != null){
                            RequestPhoneBean.DataBean dataBean = phoneBean.getData();
                            if (dataBean != null && !TextUtils.isEmpty(dataBean.getPhone())){
                                UserHelper.init().setUserPhoneNum(dataBean.getPhone());
                                EventBus.getDefault().post(BIND_PHONE_SUCCESS);
                                OneKeyLoginManager.getInstance().finishAuthActivity();
                                ToastUtils.showShort("绑定成功");
                            }else{
                                OneKeyLoginManager.getInstance().finishAuthActivity();
                            }
                        }
                        if (mRootView != null) {
                            mRootView.bindPhoneSuccess();
                        }
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mErrorHandler = null;
        this.mAppManager = null;
        this.mApplication = null;
    }
}
