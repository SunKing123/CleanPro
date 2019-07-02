package com.xiaoniu.cleanking.ui.main.presenter;


import android.util.Log;
import android.widget.TextView;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.ui.main.activity.FileManagerHomeActivity;
import com.xiaoniu.cleanking.ui.main.model.MainModel;
import com.xiaoniu.cleanking.utils.CleanAllFileScanUtil;
import com.xiaoniu.cleanking.utils.DeviceUtils;
import com.xiaoniu.cleanking.utils.NumberUtils;
import com.xiaoniu.cleanking.utils.db.FileTableManager;
import com.xiaoniu.cleanking.utils.prefs.NoClearSPHelper;
import com.xiaoniu.cleanking.widget.CircleProgressView;

import java.io.File;
import java.util.ArrayList;
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
public class FileManagerHomePresenter extends RxPresenter<FileManagerHomeActivity, MainModel> {

    private final RxAppCompatActivity mActivity;
    @Inject
    NoClearSPHelper mSPHelper;


    @Inject
    public FileManagerHomePresenter(RxAppCompatActivity activity) {
        mActivity = activity;
    }


    /*
     * 获取内存使用情况
     * @param textView
     * @param circleProgressView
     */
    public void getSpaceUse(TextView textView, CircleProgressView circleProgressView) {
        Observable.create(new ObservableOnSubscribe<String[]>() {
            @Override
            public void subscribe(ObservableEmitter<String[]> e) throws Exception {
                e.onNext(new String[]{DeviceUtils.getFreeSpace(), DeviceUtils.getTotalSpace()});
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String[]>() {
                    @Override
                    public void accept(String[] strings) throws Exception {
                        //String数组第一个是剩余存储量，第二个是总存储量
                        textView.setText("剩余：" + strings[0] + "G 总计：" + strings[1] + "G");
                        int spaceProgress = (int) ((NumberUtils.getFloat(strings[1]) - NumberUtils.getFloat(strings[0])) * 100 / NumberUtils.getFloat(strings[1]));
                        circleProgressView.startAnimProgress(spaceProgress, 700);
                    }
                });
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
