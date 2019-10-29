package com.xiaoniu.cleanking.ui.tool.wechat.presenter;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.ui.main.bean.FileChildEntity;
import com.xiaoniu.cleanking.ui.main.bean.FileTitleEntity;
import com.xiaoniu.cleanking.ui.main.model.CleanMainModel;
import com.xiaoniu.cleanking.ui.tool.wechat.fragment.WXFileFragment;
import com.xiaoniu.common.utils.DateUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
 * 图片清理
 * Created by lang.chen on 2019/7/2
 */
public class WXCleanFilePresenter extends RxPresenter<WXFileFragment, CleanMainModel> {


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
     * 相机获取的图片
     */

    @Inject
    public WXCleanFilePresenter() {
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
    public void init(Context context) {
        path = getPath(wxRootPath);
        String[] titles = context.getResources().getStringArray(R.array.wx_file_titles);
        for (int i = 0; i < titles.length; i++) {
            //聊天图片
            FileTitleEntity fileEntity = new FileTitleEntity();
            fileEntity.title = titles[i];
            fileEntity.type = i;
            fileEntity.id = String.valueOf(i);
            listsChat.add(fileEntity);
        }

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


    public void start() {

        getImgChat();
       // getImgCamera();
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
                        //mView.updateImgCamera(listsCamera);
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
     * 导入文件
     * @param files
     */
    //文件的总大小
    private  int mFileTotalSize=0;
    //文件读写的大小
    private  int mFileReadSize=0;
    public   void copyFile(List<File> files ){
        mFileTotalSize=0;
        mFileReadSize=0;
       //相册路径
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString()+"/img_cl";

        File file=new File(path);
        if(!file.exists()){
            file.mkdir();
        }

        for(File fileSize: files){
            mFileTotalSize+=fileSize.length();
        }
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {


                for(File file: files){
                    File fileCopy=new File(path,file.getName());
                    copyFileUsingFileStreams(file,fileCopy,emitter);

                }
                emitter.onComplete();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())//回调在主线程
                .subscribeOn(Schedulers.io())//执行在io线程
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Integer value) {

                        mView.copySuccess(value);
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
     * 文件拷贝
     *
     */
    private  void copyFileUsingFileStreams(File source, File dest,ObservableEmitter<Integer> emitter)
            throws IOException {
        InputStream input = null;
        OutputStream output = null;
        try {
            input = new FileInputStream(source);
            output = new FileOutputStream(dest);
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buf)) > 0) {
                output.write(buf, 0, bytesRead);
                mFileReadSize+=bytesRead;
                int progress = (int) (mFileReadSize * 1.0f / mFileTotalSize * 100);
                emitter.onNext(progress);
            }
        } finally {
            input.close();
            output.close();
        }
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
                        fileChildEntity.parentId=FileTitleEntity.Type.TH;
                        Log.i(TAG,"file="+file.getName()+",path"+file.getPath()+",time="+file.lastModified());
                            listsChat.get(FileTitleEntity.Type.TH).lists.add(fileChildEntity);
                    } else if (file.getName().endsWith(".jpg") || file.getName().endsWith(".png")) {
                        FileChildEntity fileChildEntity = new FileChildEntity();
                        fileChildEntity.name = file.getName();
                        fileChildEntity.path = file.getPath();
                        fileChildEntity.size = file.length();

                        Log.i(TAG,"file="+file.getName()+",time="+file.lastModified());
                        if (file.getName().contains("th_")) {
                            fileChildEntity.parentId=FileTitleEntity.Type.TH;
                            listsChat.get(FileTitleEntity.Type.TH).lists.add(fileChildEntity);
                        } else if (DateUtils.isSameDay(System.currentTimeMillis(), file.lastModified())) {
                            //是否为今天
                            fileChildEntity.parentId=FileTitleEntity.Type.TODAY;
                            listsChat.get(FileTitleEntity.Type.TODAY).lists.add(fileChildEntity);
                        } else if (DateUtils.isYesterday(file.lastModified())) {
                            //是否为昨天
                            fileChildEntity.parentId=FileTitleEntity.Type.YESTERDAY;
                            listsChat.get(FileTitleEntity.Type.YESTERDAY).lists.add(fileChildEntity);
                        } else if (DateUtils.isSameMonth(System.currentTimeMillis(), file.lastModified())) {
                            //是否为同一个月
                            fileChildEntity.parentId=FileTitleEntity.Type.MONTH;
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
