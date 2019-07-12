package com.xiaoniu.cleanking.ui.main.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.ui.main.activity.CleanMusicManageActivity;
import com.xiaoniu.cleanking.ui.main.activity.CleanVideoManageActivity;
import com.xiaoniu.cleanking.ui.main.bean.VideoFileCollenctionBean;
import com.xiaoniu.cleanking.ui.main.bean.VideoInfoBean;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.model.MainModel;
import com.xiaoniu.cleanking.utils.DateUtils;
import com.xiaoniu.cleanking.utils.MusicFileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 视频清理
 * Created by lang.chen on 2019/7/2
 */
public class CleanVideoManagePresenter extends RxPresenter<CleanVideoManageActivity, MainModel> {

    private RxAppCompatActivity activity;

    private List<VideoInfoBean> videoInfoBeans = new ArrayList<>();

    private List<File> files = new ArrayList<>();

    @Inject
    public CleanVideoManagePresenter(RxAppCompatActivity activity) {
        this.activity = activity;
    }


    /**
     * 清空文件缓存
     *
     * @param appInfoBeans
     */
    public void updateCache(List<VideoInfoBean> appInfoBeans) {
        videoInfoBeans.clear();
        files.clear();
        videoInfoBeans.addAll(appInfoBeans);


    }

    public void updateRemoveCache(List<VideoInfoBean> appInfoBeans) {
        videoInfoBeans.removeAll(appInfoBeans);

        //更新本地缓存
        Set<String> strings=new HashSet<>();
        for (VideoInfoBean file:videoInfoBeans){
            strings.add(file.path);
        }
        SharedPreferences sharedPreferences = activity.getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putStringSet(SpCacheConfig.CACHES_KEY_VIDEO,strings);
        editor.commit();

    }
    /**
     * 获取文件
     *
     * @return
     */
    public List<VideoInfoBean> getFlieList(String path) {
        if (videoInfoBeans.size() > 0) {
            videoInfoBeans.clear();
        }
        if (files.size() > 0) {
            files.clear();
        }
        SharedPreferences sharedPreferences = activity.getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        Set<String> strings = sharedPreferences.getStringSet(SpCacheConfig.CACHES_KEY_VIDEO, new HashSet<String>());
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {

                //先获取缓存文件
                if (strings.size() > 0) {
                    for (String path : strings) {
                        File file = new File(path);
                        if(file.length()>0){
                            files.add(file);
                        }
                    }
                } else {
                    //扫描视频文件
                    //scanViodeFile(path);
                }
                List<VideoFileCollenctionBean> lists=new ArrayList<>();

                //找出相同时间的文件

                Set<String> set=new TreeSet<String>((o1, o2) -> o2.compareTo(o1));

                for (File file : files) {
                    String time= DateUtils.timestampToPatternTime(file.lastModified(), "yyyy-MM-dd");
                    set.add(time);
                }

                for (String l : set) {
                    VideoFileCollenctionBean videoFileCollenctionBean = new VideoFileCollenctionBean();
                    lists.add(videoFileCollenctionBean);
                    VideoInfoBean videoInfoBean=new VideoInfoBean();
                    videoInfoBean.date=l;
                    videoInfoBeans.add(videoInfoBean);
                    for (File file : files) {
                        String time = DateUtils.timestampToPatternTime(file.lastModified(), "yyyy-MM-dd");
                        if (time.equals(l)) {
                            VideoInfoBean bean = new VideoInfoBean();
                            bean.path = file.getPath();
                            bean.name = file.getName();
                            bean.date=time;
                            bean.packageSize = file.length();
                            bean.itemType=1;
                            videoFileCollenctionBean.lists.add(bean);
                            videoInfoBeans.add(bean);
                        }
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

                        mView.updateData(videoInfoBeans);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });


        return videoInfoBeans;
    }


    public void delFile(List<VideoInfoBean> list) {
        List<VideoInfoBean> files = list;
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {

                for (VideoInfoBean appInfoBean : files) {
                    File file = new File(appInfoBean.path);
                    if (null != file) {
                        file.delete();
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
     * mp4    mov    mkv    avi    wmv    m4v    mpg    vob    webm    ogv    3gp    flv    f4v    swf    gif
     * @param path
     */
    private  void scanViodeFile(String path){
        File file = new File(path);
        if (file.isDirectory()) {
            File[] f = file.listFiles();
            if (null != f) {
                for (File file1 : f) {
                    String fileName=file1.getName().toLowerCase();
                    if (file1.isDirectory()) {
                        scanViodeFile(path + "/" + file1.getName());
                    } else if (fileName.endsWith(".mp4")
                            ||fileName.equals(".mov")
                            || fileName.equals(".mkv")
                            ||fileName.equals(".avi")
                            ||fileName.equals(".wmv")
                            ||fileName.equals(".m4v")
                            ||fileName.equals(".mpg")
                            ||fileName.equals(".vob")
                            ||fileName.equals(".webm")
                            ||fileName.equals(".ogv")
                            ||fileName.equals(".3gp")
                            ||fileName.equals(".flv")
                            ||fileName.equals(".f4v")
                            ||fileName.equals(".swf")
                            && file.length()!=0) {
                        files.add(file1);
                    }
                }
            }
        }
    }
}
