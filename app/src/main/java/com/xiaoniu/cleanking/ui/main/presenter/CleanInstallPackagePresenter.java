package com.xiaoniu.cleanking.ui.main.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.ui.main.activity.CleanInstallPackageActivity;
import com.xiaoniu.cleanking.ui.main.bean.AppInfoBean;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.model.MainModel;
import com.xiaoniu.cleanking.utils.InstallManageUtils;

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
public class CleanInstallPackagePresenter extends RxPresenter<CleanInstallPackageActivity, MainModel> {

    private RxAppCompatActivity activity;

    private List<AppInfoBean> apps = new ArrayList<>();

    private List<File> apkFiles = new ArrayList<>();

    @Inject
    public CleanInstallPackagePresenter(RxAppCompatActivity activity) {
        this.activity = activity;
    }


    /**
     * 清空文件缓存
     *
     * @param appInfoBeans
     */
    public void updateCache(List<AppInfoBean> appInfoBeans) {
        apps.clear();
        apkFiles.clear();
        apps.addAll(appInfoBeans);
    }

    public void updateRemoveCache(List<AppInfoBean> appInfoBeans) {
        apps.removeAll(appInfoBeans);
    }

    /**
     * 获取应用安装包信息
     *
     * @return
     */
    public List<AppInfoBean> getApkList(String path) {
        if (apps.size() > 0) {
            apps.clear();
        }
        if (apkFiles.size() > 0) {
            apkFiles.clear();
        }

        SharedPreferences sharedPreferences = activity.getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        Set<String> strings = sharedPreferences.getStringSet(SpCacheConfig.CACHES_KEY_APK, new HashSet<String>());

        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                //先获取缓存文件
                if (strings.size() > 0) {
                    for (String path : strings) {
                        File file = new File(path);
                        apkFiles.add(file);
                    }
                } else {
                    //扫描apk文件
                    scanFile(path);
                }
                for (File file : apkFiles) {
                    AppInfoBean appInfoBean = InstallManageUtils.getUninstallAPKInfo(activity, file.getPath());
                    if (!TextUtils.isEmpty(appInfoBean.name)) {
                        apps.add(appInfoBean);
                    }
                }
                emitter.onNext("");
                emitter.onComplete();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(String value) {

                        mView.updateData(apps);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });


        return apps;
    }


    public void delFile(List<AppInfoBean> list) {
        List<AppInfoBean> files = list;
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {

                for (AppInfoBean appInfoBean : files) {
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
     * 获取已安装和未安装安装包信息
     *
     * @param type 0 已安装 ，1未安装
     */
    public List<AppInfoBean> getApkList(String path, int type) {
        List<AppInfoBean> appInfoAll = apps;
        List<AppInfoBean> lists = new ArrayList<>();
        for (AppInfoBean appInfoBean : appInfoAll) {
            if (type == 0 && appInfoBean.isInstall == true) {
                lists.add(appInfoBean);
            } else if (type == 1 && appInfoBean.isInstall == false) {
                lists.add(appInfoBean);
            }
        }
        return lists;
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
                    } else if (file1.getName().trim().toLowerCase().endsWith(".apk")) {
                        Log.i("test", "fileName=" + file1.getPath());
                        apkFiles.add(file1);
                    }
                }
            }
        }


    }
}
