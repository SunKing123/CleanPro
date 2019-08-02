package com.xiaoniu.cleanking.ui.main.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.ui.main.activity.CleanVideoManageActivity;
import com.xiaoniu.cleanking.ui.main.activity.WXCleanImgActivity;
import com.xiaoniu.cleanking.ui.main.bean.FileChildEntity;
import com.xiaoniu.cleanking.ui.main.bean.FileInfoEntity;
import com.xiaoniu.cleanking.ui.main.bean.FileTitleEntity;
import com.xiaoniu.cleanking.ui.main.bean.VideoFileCollenctionBean;
import com.xiaoniu.cleanking.ui.main.bean.VideoInfoBean;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.fragment.WXImgChatFragment;
import com.xiaoniu.cleanking.ui.main.model.MainModel;
import com.xiaoniu.cleanking.utils.DateUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
 * 图片清理
 * Created by lang.chen on 2019/7/2
 */
public class WXCleanImgPresenter extends RxPresenter<WXCleanImgActivity, MainModel> {

    RxAppCompatActivity activity;

    private static final String TAG = "WXCleanImg.class";
    /**
     * 微信根目录
     */
    private String wxRootPath = Environment.getExternalStorageDirectory().getPath() + "/tencent/MicroMsg";


    /**
     * 图片存储路径
     */
    private String path;



    /**
     * 聊天图片
     */
    private List<FileTitleEntity> listsChat = new ArrayList<>();
    /**
     * 相机图片
     */
    private List<FileTitleEntity> listsCamera = new ArrayList<>();

    /**
     * 保存图片
     */
    private List<FileTitleEntity> listsSaveList = new ArrayList<>();


    /**
     * 相机获取的图片
     */

    @Inject
    public WXCleanImgPresenter(RxAppCompatActivity activity) {
        this.activity = activity;
    }


    /**
     * 获取缓存文件目录
     * <p>
     * 根据目录 长度20位 并且包含emoji目录
     */
    private String getPath(String wxRootPath) {
        File fileRoot = new File(wxRootPath);
        if (fileRoot.isDirectory()) {
            File[] files = fileRoot.listFiles();
            if (null != files) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        String fileName = file.getName();
                        if (fileName.length() > 20) {
                            File[] filesNext = file.listFiles();
                            if (null != filesNext) {
                                for (File f : filesNext) {
                                    if (f.getName().equals("emoji")) {
                                        Log.i("TAG", file.getPath());
                                        return file.getPath();

                                    }
                                }
                            }

                        }
                    }
                }
            }
        }
        return null;
    }


    /**
     * 获取聊天图片保存目录
     * <p>
     * 初始化 一级目录标题
     */
    public void init() {
        path = getPath(wxRootPath);
        String[] titles = activity.getResources().getStringArray(R.array.wx_file_titles);
        for (int i = 0; i < titles.length; i++) {
            //聊天图片
            FileTitleEntity fileEntity = new FileTitleEntity();
            fileEntity.title = titles[i];
            fileEntity.type = i;
            fileEntity.id = String.valueOf(i);
            listsChat.add(fileEntity);
            //拍摄图片
            FileTitleEntity fileEntityCamera = new FileTitleEntity();
            fileEntityCamera.title = titles[i];
            fileEntityCamera.type = i;
            fileEntityCamera.id = String.valueOf(i);
            listsCamera.add(fileEntityCamera);
            //保存图片
            FileTitleEntity fileEntitySaveList = new FileTitleEntity();
            fileEntityCamera.title = titles[i];
            fileEntityCamera.type = i;
            fileEntityCamera.id = String.valueOf(i);
            listsSaveList.add(fileEntitySaveList);
        }

    }


    public void start() {

        getImgChat();
        getImgCamera();
    }


    private void getImgChat() {

        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {

                scanAllImgChat(path);
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
                      totalFileSize(listsChat);
                       mView.updateImgChat(listsChat);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                        mView.cancelLoadingDialog();
                    }
                });

    }


    private  void totalFileSize(List<FileTitleEntity> lists){
        if(null==lists ||  lists.size()==0){
            return;
        }
        for(FileTitleEntity fileTitleEntity: lists){
            long size=0L;
            for(FileChildEntity fileChildEntity:fileTitleEntity.lists){
                size+=fileChildEntity.size;
            }
            fileTitleEntity.size=size;
        }
    }
    /**
     * 获取相机图片
     */
    private void getImgCamera() {

        String pathLocal=wxRootPath+"/WeiXin";
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {

                scanAllImgCamera(pathLocal);
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
                        totalFileSize(listsCamera);
                        mView.updateImgCamera(listsCamera);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                        mView.cancelLoadingDialog();
                    }
                });

    }




    /**
     * 扫描聊天中的图片，包括缩略图
     *
     * @param path
     */
    private void scanAllImgChat(String path) {
        File fileRoot = new File(path);
        if (fileRoot.isDirectory()) {
            File[] files = fileRoot.listFiles();
            if (null != files) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        scanAllImgChat(path + "/" + file.getName());
                    }else if (file.getName().startsWith("th_")){
                        FileChildEntity fileChildEntity = new FileChildEntity();
                        fileChildEntity.name = file.getName()+".jpg";
                        fileChildEntity.path = file.getPath();
                        fileChildEntity.size = file.length();
                        Log.i(TAG,"file="+file.getName()+",path"+file.getPath()+",time="+file.lastModified());
                            listsChat.get(FileTitleEntity.Type.TH).lists.add(fileChildEntity);
                    } else if (file.getName().endsWith(".jpg") || file.getName().endsWith(".png")) {
                        FileChildEntity fileChildEntity = new FileChildEntity();
                        fileChildEntity.name = file.getName();
                        fileChildEntity.path = file.getPath();
                        fileChildEntity.size = file.length();
                        Log.i(TAG,"file="+file.getName()+",time="+file.lastModified());
                        if (file.getName().contains("th_")) {
                            listsChat.get(FileTitleEntity.Type.TH).lists.add(fileChildEntity);
                        } else if (DateUtils.isSameDay(System.currentTimeMillis(), file.lastModified())) {
                            //是否为今天
                            listsChat.get(FileTitleEntity.Type.TODAY).lists.add(fileChildEntity);
                        } else if (DateUtils.isYesterday(file.lastModified())) {
                            //是否为昨天
                            listsChat.get(FileTitleEntity.Type.YESTERDAY).lists.add(fileChildEntity);
                        } else if (DateUtils.isSameMonth(System.currentTimeMillis(), file.lastModified())) {
                            //是否为同一个月
                            listsChat.get(FileTitleEntity.Type.MONTH).lists.add(fileChildEntity);
                        }
//                        else if(){
//                            //是否为半年内
//                        }
                    }
                }
            }
        }
    }

    /**
     * 扫描聊天中的图片，包括缩略图
     *
     * @param path
     */
    private void scanAllImgCamera(String path) {
        File fileRoot = new File(path);
        if (fileRoot.isDirectory()) {
            File[] files = fileRoot.listFiles();
            if (null != files) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        scanAllImgCamera(path + "/" + file.getName());
                    } else if (file.getName().endsWith(".jpg") || file.getName().endsWith(".png")) {
                        FileChildEntity fileChildEntity = new FileChildEntity();
                        fileChildEntity.name = file.getName();
                        fileChildEntity.path = file.getPath();
                        fileChildEntity.size = file.length();
                        Log.i(TAG,"filename="+fileChildEntity.path);
                         if (file.getName().startsWith("wx_camera") && DateUtils.isSameDay(System.currentTimeMillis(), file.lastModified())) {
                            //是否为今天
                            listsCamera.get(FileTitleEntity.Type.TODAY).lists.add(fileChildEntity);
                        } else if ( file.getName().startsWith("wx_camera") && DateUtils.isYesterday(file.lastModified())) {
                            //是否为昨天
                             listsCamera.get(FileTitleEntity.Type.YESTERDAY).lists.add(fileChildEntity);
                        } else if (file.getName().startsWith("wx_camera") && DateUtils.isSameMonth(System.currentTimeMillis(), file.lastModified())) {
                            //是否为同一个月
                             listsCamera.get(FileTitleEntity.Type.MONTH).lists.add(fileChildEntity);
                        } else if(file.getName().startsWith("wx_camera")){
                            //是否为半年内
                        }

                    }
                }
            }
        }
    }


}
