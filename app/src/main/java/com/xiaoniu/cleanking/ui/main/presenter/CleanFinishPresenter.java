package com.xiaoniu.cleanking.ui.main.presenter;

import android.annotation.SuppressLint;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.xiaoniu.cleanking.app.ApplicationDelegate;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;
import com.xiaoniu.cleanking.ui.main.model.MainModel;
import com.xiaoniu.cleanking.ui.newclean.activity.NewCleanFinishActivity;
import com.xiaoniu.cleanking.utils.FileQueryUtils;
import com.xiaoniu.cleanking.utils.net.Common4Subscriber;
import com.xiaoniu.cleanking.utils.prefs.NoClearSPHelper;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by tie on 2017/5/15.
 */
public class CleanFinishPresenter extends RxPresenter<NewCleanFinishActivity, MainModel> {

    private final RxAppCompatActivity mActivity;
    @Inject
    NoClearSPHelper mSPHelper;

    @Inject
    public CleanFinishPresenter(RxAppCompatActivity activity) {
        mActivity = activity;
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
                mView.getSwitchInfoListSuccess(switchInfoList);
                if(null != switchInfoList)
                    AppHolder.getInstance().setSwitchInfoMap(switchInfoList.getData());
            }

            @Override
            public void showExtraOp(String message) {
                mView.getSwitchInfoListFail();
            }

            @Override
            public void netConnectError() {
                mView.getSwitchInfoListFail();
                if (null != ApplicationDelegate.getAppDatabase() && null != ApplicationDelegate.getAppDatabase().adInfotDao()) {
                    AppHolder.getInstance().setSwitchInfoMap(ApplicationDelegate.getAppDatabase().adInfotDao().getAll());
                }
            }
        });
    }
    /**
     * 插屏广告开关
     */
    public void getScreentSwitch() {
        mModel.getScreentSwitch(new Common4Subscriber<SwitchInfoList>() {
            @Override
            public void showExtraOp(String code, String message) {

            }

            @Override
            public void getData(SwitchInfoList switchInfoList) {
                mView.getScreentSwitchSuccess(switchInfoList);
            }

            @Override
            public void showExtraOp(String message) {
                mView.getSwitchInfoListFail();
            }

            @Override
            public void netConnectError() {
                mView.getSwitchInfoListFail();
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
                public void reduceSize(long p0) {

                }

                @Override
                public void scanFile(String p0) {

                }

                @Override
                public void totalSize(int p0) {

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
}
