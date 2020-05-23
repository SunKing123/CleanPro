package com.xiaoniu.cleanking.ui.newclean.presenter;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.xiaoniu.cleanking.app.ApplicationDelegate;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.ui.main.bean.AppVersion;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.bean.HomeRecommendEntity;
import com.xiaoniu.cleanking.ui.main.bean.ImageAdEntity;
import com.xiaoniu.cleanking.ui.main.bean.InteractionSwitchList;
import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;
import com.xiaoniu.cleanking.ui.newclean.fragment.NewCleanMainFragment;
import com.xiaoniu.cleanking.ui.newclean.model.NewScanModel;
import com.xiaoniu.cleanking.utils.FileQueryUtils;
import com.xiaoniu.cleanking.utils.net.Common4Subscriber;
import com.xiaoniu.cleanking.utils.net.CommonSubscriber;
import com.xiaoniu.cleanking.utils.update.UpdateAgent;
import com.xiaoniu.cleanking.utils.update.listener.OnCancelListener;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class NewCleanMainPresenter extends RxPresenter<NewCleanMainFragment, NewScanModel> {

    @Inject
    public NewCleanMainPresenter() {
    }

    private UpdateAgent mUpdateAgent;

    /**
     * 版本更新
     */
    public void queryAppVersion(final OnCancelListener onCancelListener) {
        mModel.queryAppVersion(new Common4Subscriber<AppVersion>() {

            @Override
            public void getData(AppVersion updateInfoEntity) {
                setAppVersion(updateInfoEntity, onCancelListener);
            }

            @Override
            public void showExtraOp(String code, String message) {
                Toast.makeText(mView.getActivity(), message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void showExtraOp(String message) {
            }

            @Override
            public void netConnectError() {
                if (onCancelListener != null) {
                    onCancelListener.onCancel();
                }
            }
        });
    }

    public void setAppVersion(AppVersion result, OnCancelListener onCancelListener) {
        if (result != null && result.getData() != null) {
            if (TextUtils.equals("1", result.getData().popup)) {
                if (mUpdateAgent == null) {
                    mUpdateAgent = new UpdateAgent(mView.getActivity(), result, () -> {
                        if (onCancelListener != null) {
                            onCancelListener.onCancel();
                        }
                    });
                    mUpdateAgent.check();
                } else {
                    mUpdateAgent.check();
                }
            } else {
                if (onCancelListener != null) {
                    onCancelListener.onCancel();
                }
            }
        }
    }

    /**
     * 底部广告接口
     */
    public void requestBottomAd() {
        mModel.getBottomAd(new CommonSubscriber<ImageAdEntity>() {
            @Override
            public void getData(ImageAdEntity imageAdEntity) {
                if (imageAdEntity == null) return;
                ArrayList<ImageAdEntity.DataBean> dataList = imageAdEntity.getData();
                if (dataList != null && dataList.size() > 0) {
                    mView.showFirstAd(dataList.get(0), 0);
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

    /**
     * 冷启动、热启动、完成页广告开关
     */
    public void getSwitchInfoList() {
        mModel.getSwitchInfoList(new Common4Subscriber<SwitchInfoList>() {
            @Override
            public void showExtraOp(String code, String message) {

            }

            @Override
            public void getData(SwitchInfoList switchInfoList) {
                Log.d("NewCleanMainPresenter", "!--->getSwitchInfoList---getData---");
                if (null != switchInfoList) {
                    AppHolder.getInstance().setSwitchInfoMap(switchInfoList.getData());
                }
            }

            @Override
            public void showExtraOp(String message) {
            }

            @Override
            public void netConnectError() {
                if (null != ApplicationDelegate.getAppDatabase() && null != ApplicationDelegate.getAppDatabase().adInfotDao()) {
                    AppHolder.getInstance().setSwitchInfoMap(ApplicationDelegate.getAppDatabase().adInfotDao().getAll());
                }
            }
        });
    }

    /**
     * 互动式广告开关
     */
    public void getInteractionSwitch() {
        mModel.getInteractionSwitch(new Common4Subscriber<InteractionSwitchList>() {
            @Override
            public void showExtraOp(String code, String message) {

            }

            @Override
            public void getData(InteractionSwitchList switchInfoList) {
                mView.getInteractionSwitchSuccess(switchInfoList);
            }

            @Override
            public void showExtraOp(String message) {
            }

            @Override
            public void netConnectError() {
                mView.getInteractionSwitchFailure();
            }
        });
    }

    /**
     * 获取到可以加速的应用名单Android O以下的获取最近使用情况
     */
    @SuppressLint("CheckResult")
    public void getAccessListBelow() {
//        mView.showLoadingDialog();
        Observable.create((ObservableOnSubscribe<ArrayList<FirstJunkInfo>>) e -> {
            //获取到可以加速的应用名单
            FileQueryUtils mFileQueryUtils = new FileQueryUtils();
            //文件加载进度回调
            mFileQueryUtils.setScanFileListener(new FileQueryUtils.ScanFileListener() {
                @Override
                public void currentNumber() {

                }

                @Override
                public void increaseSize(long p0) {

                }

                @Override
                public void scanFile(String p0) {

                }
            });
            ArrayList<FirstJunkInfo> listInfo = mFileQueryUtils.getRunningProcess();
            if (listInfo == null) {
                listInfo = new ArrayList<>();
            }
            e.onNext(listInfo);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(strings -> {
                    if (mView == null) return;
//                    mView.cancelLoadingDialog();
                    mView.getAccessListBelow(strings);
                });
    }

    /**
     * 推荐列表
     */
    public void getRecommendList() {
        mModel.getRecommendList(new Common4Subscriber<HomeRecommendEntity>() {
            @Override
            public void showExtraOp(String code, String message) {
                mView.getRecommendListFail();
            }

            @Override
            public void getData(HomeRecommendEntity entity) {
                mView.getRecommendListSuccess(entity);
            }

            @Override
            public void showExtraOp(String message) {
                mView.getRecommendListFail();
            }

            @Override
            public void netConnectError() {
                mView.getRecommendListFail();
            }
        });
    }
}
