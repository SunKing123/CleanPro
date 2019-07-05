package com.xiaoniu.cleanking.ui.main.presenter;


import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.ui.main.activity.PhoneAccessActivity;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.model.MainModel;
import com.xiaoniu.cleanking.utils.FileQueryUtils;
import com.xiaoniu.cleanking.utils.prefs.NoClearSPHelper;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by tie on 2017/5/15.
 */
public class PhoneAccessPresenter extends RxPresenter<PhoneAccessActivity, MainModel> {

    private final RxAppCompatActivity mActivity;
    @Inject
    NoClearSPHelper mSPHelper;


    @Inject
    public PhoneAccessPresenter(RxAppCompatActivity activity) {
        mActivity = activity;
    }


    //获取到可以加速的应用名单
    public void getAccessList() {
        mView.showLoadingDialog();
        Observable.create(new ObservableOnSubscribe<ArrayList<FirstJunkInfo>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<FirstJunkInfo>> e) throws Exception {
                //获取到可以加速的应用名单
                FileQueryUtils mFileQueryUtils = new FileQueryUtils();
                ArrayList<FirstJunkInfo> listInfo = mFileQueryUtils.getRunningProcess();
                e.onNext(listInfo);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ArrayList<FirstJunkInfo>>() {
                    @Override
                    public void accept(ArrayList<FirstJunkInfo> strings) throws Exception {
                        mView.cancelLoadingDialog();
                        mView.getAccessList(strings);
                    }
                });
    }
}
