package com.xiaoniu.cleanking.ui.main.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.ui.main.activity.CleanInstallPackageActivity;
import com.xiaoniu.cleanking.ui.main.activity.CleanMusicManageActivity;
import com.xiaoniu.cleanking.ui.main.bean.AppInfoBean;
import com.xiaoniu.cleanking.ui.main.bean.MusciInfoBean;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.model.MainModel;
import com.xiaoniu.cleanking.utils.InstallManageUtils;
import com.xiaoniu.cleanking.utils.MusicFileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
 * Created by lang.chen on 2019/7/2
 */
public class CleanMusicFilePresenter extends RxPresenter<CleanMusicManageActivity, MainModel> {

    private RxAppCompatActivity activity;

    private List<MusciInfoBean> musciInfoBeans = new ArrayList<>();

    private List<File> files = new ArrayList<>();

    @Inject
    public CleanMusicFilePresenter(RxAppCompatActivity activity) {
        this.activity = activity;
    }


    /**
     * 清空文件缓存
     *
     * @param appInfoBeans
     */
    public void updateCache(List<MusciInfoBean> appInfoBeans) {
        musciInfoBeans.clear();
        files.clear();
        musciInfoBeans.addAll(appInfoBeans);


    }

    public void updateRemoveCache(List<MusciInfoBean> appInfoBeans) {
        musciInfoBeans.removeAll(appInfoBeans);

        //更新本地缓存
        Set<String> strings=new HashSet<>();
        for (MusciInfoBean file:musciInfoBeans){
            strings.add(file.path);
        }
        SharedPreferences sharedPreferences = activity.getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putStringSet(SpCacheConfig.CACHES_KEY_MUSCI,strings);
        editor.commit();

    }
    /**
     * 获取应用安装包信息
     *
     * @return
     */
    public List<MusciInfoBean> getMusicList(String path) {
        if (musciInfoBeans.size() > 0) {
            musciInfoBeans.clear();
        }
        if (files.size() > 0) {
            files.clear();
        }
        SharedPreferences sharedPreferences = activity.getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        Set<String> strings = sharedPreferences.getStringSet(SpCacheConfig.CACHES_KEY_MUSCI, new HashSet<String>());
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {

                //先获取缓存文件
                if (strings.size() > 0) {
                    for (String path : strings) {
                        File file = new File(path);
                        files.add(file);
                    }
                } else {
                    //扫描apk文件
                    scanFile(path);
                }
                for (File file : files) {
                    MusciInfoBean musciInfoBean = new MusciInfoBean();
                    musciInfoBean.name = file.getName();
                    musciInfoBean.packageSize = file.length();
                    musciInfoBean.path = file.getPath();
                    musciInfoBean.time = MusicFileUtils.getPlayDuration(file.getPath());
                    musciInfoBeans.add(musciInfoBean);
                }
                emitter.onNext("");
                emitter.onComplete();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())//回调在主线程
                .subscribeOn(Schedulers.io())//执行在io线程
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(String value) {

                        mView.updateData(musciInfoBeans);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });


        return musciInfoBeans;
    }


    public void delFile(List<MusciInfoBean> list) {
        List<MusciInfoBean> files = list;
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {

                for (MusciInfoBean appInfoBean : files) {
                    File file = new File(appInfoBean.path);
                    if (null != file) {
                        file.delete();
                    }
                }
                emitter.onNext("");
                emitter.onComplete();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())//回调在主线程
                .subscribeOn(Schedulers.io())//执行在io线程
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(String value) {

                        mView.updateDelFileView(files);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });

    }


    /**
     * 文件扫描
     */
    private void scanFile(String path) {

        File file = new File(path);
        if (file.isDirectory()) {
            File[] f = file.listFiles();
            if (null != f) {
                for (File file1 : f) {
                    if (file1.isDirectory()) {
                        scanFile(path + "/" + file1.getName());
                    } else if (file1.getName().endsWith(".mp3")) {
                        Log.i("test", "fileName=" + file1.getPath());
                        files.add(file1);
                    }
                }
            }
        }


    }
}
