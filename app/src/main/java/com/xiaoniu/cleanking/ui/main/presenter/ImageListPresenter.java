package com.xiaoniu.cleanking.ui.main.presenter;


import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.ui.main.activity.ImageActivity;
import com.xiaoniu.cleanking.ui.main.model.MainModel;
import com.xiaoniu.cleanking.utils.db.FileTableManager;
import com.xiaoniu.cleanking.utils.prefs.NoClearSPHelper;

import java.util.List;
import java.util.Map;

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
public class ImageListPresenter extends RxPresenter<ImageActivity, MainModel> {

    private final RxAppCompatActivity mActivity;
    @Inject
    NoClearSPHelper mSPHelper;


    @Inject
    public ImageListPresenter(RxAppCompatActivity activity) {
        mActivity = activity;
    }

    public void getSdcardFiles() {
        Observable.create(new ObservableOnSubscribe<List<Map<String, String>>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Map<String, String>>> e) throws Exception {
                e.onNext(FileTableManager.queryAllFiles(AppApplication.getInstance()));
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Map<String, String>>>() {
                    @Override
                    public void accept(List<Map<String, String>> strings) throws Exception {
                        mView.scanSdcardResult(strings);
                    }
                });
    }

}
