package com.xiaoniu.cleanking.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.xiaoniu.cleanking.utils.update.UpdateUtil;
import com.xiaoniu.common.utils.ToastUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @ProjectName: clean
 * @Package: com.xiaoniu.cleanking.utils
 * @ClassName: OperateDownLoadApkUtil
 * @Description: 运营位apk下载
 * @Author: youkun_zhou
 * @CreateDate: 2020/5/16 14:54
 */

public class OperateDownLoadApkUtil {

    private final String TAG = this.getClass().getSimpleName();
    /**
     * 文件下载保存路径
     */
    private static final String FILE_SAVE_DIR = "download";

    /**
     * 存储权限
     */
    private static final String STORAGE_WRITE = "android.permission.WRITE_EXTERNAL_STORAGE";
    /**
     * 存储权限
     */
    private static final String STORAGE_READ = "android.permission.READ_EXTERNAL_STORAGE";


    /**
     * 下载 url
     *
     * @param context
     * @param realUrl
     */
    public void loadApk(Context context, String realUrl) {

        if (!checkStoragePermission(context)) {
            ToastUtils.showLong("请先打开存储权限");
            return;
        }

        Observable.create(new ObservableOnSubscribe<File>() {
            @Override
            public void subscribe(ObservableEmitter<File> emitter) throws Exception {
                RandomAccessFile accessFile = null;
                HttpURLConnection http = null;
                InputStream inStream = null;
                try {
                    URL sizeUrl = new URL(realUrl);
                    HttpURLConnection sizeHttp = (HttpURLConnection) sizeUrl.openConnection();
                    sizeHttp.setRequestMethod("GET");
                    sizeHttp.connect();
                    long totalSize = sizeHttp.getContentLength();
                    sizeHttp.disconnect();
                    File file = getSavePathByTag(context, new Date().getTime() + "");
                    if (totalSize <= 0) {
                        if (file.exists()) {
                            file.delete();
                        }
                        Log.e(TAG, "文件大小 = " + totalSize + "\t, 终止下载过程");
                        return;
                    }

                    accessFile = new RandomAccessFile(file, "rwd");

                    URL url = new URL(realUrl);
                    http = (HttpURLConnection) url.openConnection();
                    http.setConnectTimeout(10000);
                    http.setRequestProperty("Connection", "Keep-Alive");
                    http.setReadTimeout(10000);
                    http.connect();

                    inStream = http.getInputStream();
                    byte[] buffer = new byte[1024 * 8];
                    int offset;

                    while ((offset = inStream.read(buffer)) != -1) {
                        Log.e(TAG, "文件大小 = " + offset + "\t, 正在下载");
                        accessFile.write(buffer, 0, offset);
                    }
                    emitter.onNext(file);
                    emitter.onComplete();
                    Log.e(TAG, "下载成功");
                } catch (Exception e) {
                    Log.e(TAG, "下载过程发生失败" + e.getMessage());
                    e.printStackTrace();
                    emitter.onError(new Throwable(e.getMessage()));
                } finally {
                    try {
                        if (accessFile != null) {
                            accessFile.close();
                        }
                        if (inStream != null) {
                            inStream.close();
                        }
                        if (http != null) {
                            http.disconnect();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "finally 块  关闭文件过程中 发生异常");
                        e.printStackTrace();
                    }
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<File>() {

                    @Override
                    public void accept(File file) throws Exception {
                        UpdateUtil.install(context, file, false);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    /**
     * 权限检查
     *
     * @param context
     * @return
     */
    public static boolean checkStoragePermission(Context context) {
        if (ContextCompat.checkSelfPermission(context, STORAGE_WRITE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, STORAGE_READ) != PackageManager.PERMISSION_GRANTED) {
            if (context instanceof Activity) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{STORAGE_WRITE, STORAGE_READ}, 1);
            }
            Toast.makeText(context, "请打开存储权限", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * 获取文件
     *
     * @param context
     * @param tag     唯一标识 (如md5)
     * @return String 文件存储目录
     */
    public static File getSavePathByTag(Context context, String tag) {

        //获取存储的文件夹
        String saveDir = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            saveDir = Environment.getExternalStorageDirectory().getPath() + File.separator + FILE_SAVE_DIR;
        } else {
            saveDir = context.getCacheDir().getAbsolutePath();
            if (context.getExternalCacheDir() != null) {
                saveDir = context.getExternalCacheDir().getAbsolutePath();
            }
            File file = new File(saveDir);
            file.mkdirs();
        }
        if (saveDir == null) {
            return null;
        }
        //获取存储的文件
        File file = new File(saveDir, tag + ".apk");
        return file;
    }
}
