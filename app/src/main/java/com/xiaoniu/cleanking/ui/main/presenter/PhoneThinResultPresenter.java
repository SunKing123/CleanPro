package com.xiaoniu.cleanking.ui.main.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.StatFs;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.ui.main.activity.PhoneThinActivity;
import com.xiaoniu.cleanking.ui.main.activity.PhoneThinResultActivity;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.model.MainModel;

import java.io.File;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by lang.chen on 2019/7/9
 */
public class PhoneThinResultPresenter extends RxPresenter<PhoneThinResultActivity, MainModel> {

    private RxAppCompatActivity activity;

    /**
     * 扫描目录，共计文件大小
     */
    private long mFileTotalSize = 0;

    @Inject
    public PhoneThinResultPresenter(RxAppCompatActivity activity) {
        this.activity = activity;
    }


    public long getVideoTotalSize() {
        long totalSize = 0L;

        SharedPreferences sharedPreferences = activity.getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        Set<String> strings = sharedPreferences.getStringSet(SpCacheConfig.CACHES_KEY_VIDEO, new HashSet<String>());
        for (String path : strings) {
            File file = new File(path);
            if (null != file && file.exists()) {
                totalSize += file.length();
            }
        }
        return totalSize;


    }


}
