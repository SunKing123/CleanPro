package com.xiaoniu.cleanking.ui.main.presenter;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.ui.main.bean.FileChildEntity;
import com.xiaoniu.cleanking.ui.main.bean.FileTitleEntity;
import com.xiaoniu.cleanking.ui.main.fragment.WXImgCameraFragment;
import com.xiaoniu.cleanking.ui.main.model.CleanMainModel;
import com.xiaoniu.cleanking.utils.DateUtils;

import java.io.File;
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
 * Created by lang.chen on 2019/8/3
 */
public class WXImgCameraPresenter  extends RxPresenter<WXImgCameraFragment, CleanMainModel> {


    private  static  final  String TAG="WXImgCamera.class";

    /**
     * 微信根目录
     */
    private String wxRootPath = Environment.getExternalStorageDirectory().getPath() + "/tencent/MicroMsg";

    /**
     * 相机图片
     */
    private List<FileTitleEntity> listsCamera = new ArrayList<>();



    @Inject
    public WXImgCameraPresenter(){

    }
    /**
     * 获取聊天图片保存目录
     * <p>
     * 初始化 一级目录标题
     */
    public void init(Context context) {
        String[] titles = context.getResources().getStringArray(R.array.wx_file_titles);
        for (int i = 0; i < titles.length; i++) {
            FileTitleEntity fileEntity = new FileTitleEntity();
            fileEntity.title = titles[i];
            fileEntity.type = i;
            fileEntity.id = String.valueOf(i);

            listsCamera.add(fileEntity);
        }
        getImgCamera();
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


    public void delFile(List<FileChildEntity> list) {
        List<FileChildEntity> files = list;
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {

                for ( FileChildEntity fileChildEntity : files) {
                    File file = new File(fileChildEntity.path);
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

                        mView.updateDelFileView(list);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });

    }



    public   void totalFileSize(List<FileTitleEntity> lists){
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
