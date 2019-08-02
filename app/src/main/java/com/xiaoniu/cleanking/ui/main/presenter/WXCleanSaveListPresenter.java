package com.xiaoniu.cleanking.ui.main.presenter;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.trello.rxlifecycle2.components.RxFragment;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.BaseModel;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.ui.main.activity.WXCleanImgActivity;
import com.xiaoniu.cleanking.ui.main.bean.FileChildEntity;
import com.xiaoniu.cleanking.ui.main.bean.FileTitleEntity;
import com.xiaoniu.cleanking.ui.main.bean.VideoInfoBean;
import com.xiaoniu.cleanking.ui.main.fragment.WXImgSaveListFragment;
import com.xiaoniu.cleanking.ui.main.model.CleanMainModel;
import com.xiaoniu.cleanking.ui.main.model.MainModel;
import com.xiaoniu.cleanking.utils.DateUtils;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
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
public class WXCleanSaveListPresenter extends RxPresenter<WXImgSaveListFragment, CleanMainModel> {


    Activity activity;
    /**
     * 微信根目录
     */
    private String wxRootPath = Environment.getExternalStorageDirectory().getPath() + "/tencent/MicroMsg";

    /**
     * 保存图片
     */
    private List<FileTitleEntity> listsSaveList = new ArrayList<>();


    @Inject
    public WXCleanSaveListPresenter() {
    }


    public void start() {
        getImgCamera();
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

            listsSaveList.add(fileEntity);
        }

    }


    /**
     * 获取相机图片
     */
    private void getImgCamera() {

        String pathLocal = wxRootPath + "/WeiXin";



        Flowable.create(new FlowableOnSubscribe<String>() {
            @Override
            public void subscribe(FlowableEmitter<String> e) throws Exception {
                scanAllImgCamera(pathLocal);
                e.onNext("");
                e.onComplete();
            }
          }, BackpressureStrategy.ERROR)
                .observeOn(AndroidSchedulers.mainThread())//回调在主线程
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        s.request(Long.MAX_VALUE);
                    }

                    @Override
                    public void onNext(String s) {
                        totalFileSize(listsSaveList);
                        mView.updateImgSaveList(listsSaveList);
                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onComplete() {

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

                        /**
                         * 保存的图片
                         */
                        if (file.getName().startsWith("mmexport") && DateUtils.isSameDay(System.currentTimeMillis(), file.lastModified())) {
                            //是否为今天
                            listsSaveList.get(FileTitleEntity.Type.TODAY).lists.add(fileChildEntity);
                        } else if (file.getName().startsWith("mmexport") && DateUtils.isYesterday(file.lastModified())) {
                            //是否为昨天
                            listsSaveList.get(FileTitleEntity.Type.YESTERDAY).lists.add(fileChildEntity);
                        } else if (file.getName().startsWith("mmexport") && DateUtils.isSameMonth(System.currentTimeMillis(), file.lastModified())) {
                            //是否为同一个月
                            listsSaveList.get(FileTitleEntity.Type.MONTH).lists.add(fileChildEntity);
                        } else if (file.getName().startsWith("mmexport")) {
                            //是否为半年内
                            listsSaveList.get(FileTitleEntity.Type.YEAR_HALF).lists.add(fileChildEntity);
                        }

                    }
                }
            }
        }
    }


}
