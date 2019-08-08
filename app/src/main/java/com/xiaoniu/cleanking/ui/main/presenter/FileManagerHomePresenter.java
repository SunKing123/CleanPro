package com.xiaoniu.cleanking.ui.main.presenter;


import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.TextView;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.ui.main.activity.FileManagerHomeActivity;
import com.xiaoniu.cleanking.ui.main.bean.FileEntity;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.model.MainModel;
import com.xiaoniu.cleanking.utils.DeviceUtils;
import com.xiaoniu.cleanking.utils.NumberUtils;
import com.xiaoniu.cleanking.utils.db.FileTableManager;
import com.xiaoniu.cleanking.utils.prefs.NoClearSPHelper;
import com.xiaoniu.cleanking.widget.CircleProgressView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
                        textView.setText("已用：" + String.format("%.1f", (Double.valueOf(strings[1]) - Double.valueOf(strings[0])))+ "GB/" +  String.format("%.1f",  Double.valueOf(strings[1]))+ "GB");
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
//                        mView.scanSdcardResult(strings);
                    }
                });
    }

    /**
     * 读取手机中所有图片信息
     */
    public void getPhotos(Activity mActivity) {
        Observable.create(new ObservableOnSubscribe<List<FileEntity>>() {
            @Override
            public void subscribe(ObservableEmitter<List<FileEntity>> e) throws Exception {
                List<FileEntity> mediaBeen = new ArrayList<>();
                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                String[] projImage = {MediaStore.Images.Media._ID
                        , MediaStore.Images.Media.DATA
                        , MediaStore.Images.Media.SIZE
                        , MediaStore.Images.Media.DISPLAY_NAME};
                Cursor mCursor = mActivity.getContentResolver().query(mImageUri,
                        projImage,
                        MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[]{"image/jpeg", "image/png"},
                        MediaStore.Images.Media.DATE_MODIFIED + " desc");

                if (mCursor != null) {
                    while (mCursor.moveToNext()) {
                        // 获取图片的路径
                        String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        int size = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Images.Media.SIZE)) ;
                        String displayName = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                        //用于展示相册初始化界面
                        mediaBeen.add(new FileEntity(size + "", path));
                    }
                    mCursor.close();
                }

                e.onNext(mediaBeen);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<FileEntity>>() {
                    @Override
                    public void accept(List<FileEntity> strings) throws Exception {
                        mView.getPhotoInfo(strings);
                    }
                });
    }



    /**
     * 获取视频文件总大小
     * @return size
     */
    public long getVideoTotalSize(){
        long size=0L;

        SharedPreferences sharedPreferences = mActivity.getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        Set<String> strings = sharedPreferences.getStringSet(SpCacheConfig.CACHES_KEY_VIDEO, new HashSet<String>());

        for(String path:strings){
            File file=new File(path);
            if(null!=file){
                size+=file.length();
            }
        }
        return  size;
    }

    /**
     * 获取音乐文件总大小
     * @return size
     */
    public long getMusicTotalSize(){
        long size=0L;

        SharedPreferences sharedPreferences = mActivity.getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        Set<String> strings = sharedPreferences.getStringSet(SpCacheConfig.CACHES_KEY_MUSCI, new HashSet<String>());

        for(String path:strings){
            File file=new File(path);
            if(null!=file){
                size+=file.length();
            }
        }
        return  size;
    }

    /**
     * 获取APK文件总大小
     * @return size
     */
    public long getAPKTotalSize(){
        long size=0L;

        SharedPreferences sharedPreferences = mActivity.getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        Set<String> strings = sharedPreferences.getStringSet(SpCacheConfig.CACHES_KEY_APK, new HashSet<String>());

        for(String path:strings){
            File file=new File(path);
            if(null!=file){
                size+=file.length();
            }
        }
        return  size;
    }
}
