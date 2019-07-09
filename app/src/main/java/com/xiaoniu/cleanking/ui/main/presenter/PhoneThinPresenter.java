package com.xiaoniu.cleanking.ui.main.presenter;

import android.animation.ObjectAnimator;
import android.os.StatFs;
import android.telephony.mbms.FileInfo;
import android.util.Log;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.ui.main.activity.PhoneThinActivity;
import com.xiaoniu.cleanking.ui.main.bean.AppInfoBean;
import com.xiaoniu.cleanking.ui.main.bean.FileInfoEntity;
import com.xiaoniu.cleanking.ui.main.model.MainModel;

import java.io.File;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

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
public class PhoneThinPresenter extends RxPresenter<PhoneThinActivity, MainModel> {

    private RxAppCompatActivity activity;

    /**
     * 扫描目录，共计文件大小
     */
    private long mFileTotalSize = 0;

    @Inject
    public PhoneThinPresenter(RxAppCompatActivity activity) {
        this.activity = activity;
    }


    public void scanFile(String path) {

        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                scanDirectory(path, e);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String o) {
                        mView.updateText(o,mFileTotalSize);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                        mView.onComplete();

                    }
                });
    }


    private void scanDirectory(String path, ObservableEmitter<String> e) {
        File file = new File(path);
        if (file.isDirectory()) {
            File[] f = file.listFiles();
            if (null != f) {
                for (File file1 : f) {
                    String fileName = file1.getName().toLowerCase();
                    e.onNext(fileName);
                    if (file1.isDirectory()) {
                        scanDirectory(path + "/" + file1.getName(),e);
                    } else {
                        mFileTotalSize += file1.length();
                    }
                }
            }
        }
    }


    /**
     * 计算百分比
     *
     * @param num   当前大小
     * @param total 总大小
     * @param scale 保留小数点
     * @return
     */
    public String accuracy(double num, double total, int scale) {
        DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
        //可以设置精确几位小数
        df.setMaximumFractionDigits(scale);
        //模式 例如四舍五入
        df.setRoundingMode(RoundingMode.HALF_UP);
        double accuracy_num = num / total * 100;
        return df.format(accuracy_num) + "";
    }


    /**
     * 获取存储大小
     *
     * @param path
     * @return
     */
    public long queryStorageSize(String path) {
        StatFs statFs = new StatFs(path);

        //存储块总数量
        long blockCount = statFs.getBlockCount();
        //块大小
        long blockSize = statFs.getBlockSize();

        return blockSize * blockCount;

    }


    public long getFileSize() {
        return this.mFileTotalSize;
    }

}
