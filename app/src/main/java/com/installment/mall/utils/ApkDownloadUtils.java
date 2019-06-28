package com.installment.mall.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.installment.mall.app.AppApplication;
import com.installment.mall.utils.update.UpdateFileProvider;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;
import java.text.NumberFormat;

/**
 * deprecation:文件下载工具类
 * author:ayb
 * time:2018/4/4
 */
public class ApkDownloadUtils {

    //正常流程没问题
    //假如正在下载退出来,然后再进来,如果没下载好,就继续弹出来下载进度
    //假如下载好了,本地
    public static void download(final Context context, String downloadUrl, String fileName, final DownloadListener downloadListener) {
        FileDownloader.setup(context);
        final String path = getCachePath() + File.separator + fileName + ".apk";
        BaseDownloadTask task = FileDownloader.getImpl().create(downloadUrl)
                .setPath(path)
                .setListener(new FileDownloadListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        if (downloadListener != null){
                            float radio = totalBytes == 0 ? 0f : (float)soFarBytes / (float)totalBytes;
                            NumberFormat numberFormat = NumberFormat.getPercentInstance();
                            numberFormat.setMinimumFractionDigits(0);
                            numberFormat.setMaximumFractionDigits(0);
                            String percent = numberFormat.format((double)radio);
                            downloadListener.progress(percent);
                        }
                    }
                    @Override
                    protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                        if (downloadListener != null){
                            downloadListener.onStart();
                        }
                    }
                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        if (downloadListener != null){
                            float radio = totalBytes == 0 ? 0f : (float)soFarBytes / (float)totalBytes;
                            NumberFormat numberFormat = NumberFormat.getPercentInstance();
                            numberFormat.setMinimumFractionDigits(0);
                            numberFormat.setMaximumFractionDigits(0);
                            String percent = numberFormat.format((double)radio);
                            downloadListener.progress(percent);
                        }
                    }
                    @Override
                    protected void blockComplete(BaseDownloadTask task) {
                    }
                    @Override
                    protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {
                    }
                    @Override
                    protected void completed(BaseDownloadTask task) {
                        if (downloadListener != null){
                            downloadListener.completed(path);
                        }
                        changeApkFileMode(new File(path));
                        installAPK(path, new InstallListener() {
                            @Override
                            public void onSuccess() {
                            }
                            @Override
                            public void onFail(Exception e) {
//                                Toaster.toast("安装异常"+e.getMessage());
                            }
                        });
                    }
                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                    }
                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        Log.e("ayb", "error===" + e.getMessage());
                        if (downloadListener != null){
                            downloadListener.onError(e.getMessage());
                        }
                    }
                    @Override
                    protected void warn(BaseDownloadTask task) {
                        Log.e("ayb", "warn===" + task.getErrorCause());
                    }
                });
//        if (task.isReusedOldFile()){
//
//        }else {
            task.start();
//        }
    }

    /**
     * 获取app缓存路径[SDCard/Android/data/你的应用的包名/cache]
     * @return  缓存路径
     */
    private static String getCachePath() {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            //外部存储可用
            cachePath = AppApplication.getInstance().getExternalCacheDir().getPath();
        } else {
            //外部存储不可用
            cachePath = AppApplication.getInstance().getCacheDir().getPath();
        }
        return cachePath;
    }

    /**
     * 安装APK工具类
     * @param filePath 文件路径
     * @param listener 安装界面成功调起的监听
     */
    private static void installAPK(String filePath, InstallListener listener) {
        try {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);
            File apkFile = new File(filePath);
            Uri apkUri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                String authority = AppApplication.getInstance().getPackageName() + ".updatefileprovider";
                apkUri = UpdateFileProvider.getUriForFile(AppApplication.getInstance(), authority, apkFile);
            } else {
                apkUri = Uri.fromFile(apkFile);
            }
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            AppApplication.getInstance().startActivity(intent);
            if (listener != null) {
                listener.onSuccess();
            }
        } catch (Exception e) {
            if (listener != null) {
                listener.onFail(e);
            }
        }
    }

    /**
     * 处理部分[APK放到data/data/下面提示解析失败]
     * 参考博客地址:http://blog.csdn.net/lonely_fireworks/article/details/27693073
     * @param file  文件路径
     */
    private static void changeApkFileMode(File file) {
        try {
            //apk放在缓存目录时，低版本安装提示权限错误，需要对父级目录和apk文件添加权限
            String cmd1 = "chmod 777 " + file.getParent();
            Runtime.getRuntime().exec(cmd1);

            String cmd = "chmod 777 " + file.getAbsolutePath();
            Runtime.getRuntime().exec(cmd);
        } catch (Exception e) {
        }
    }

    public interface DownloadListener{
        void onStart();
        void progress(String percent);
        void completed(String filePath);
        void onError(String errorMsg);
    }

    //安装监听
    public interface InstallListener {
        void onSuccess();

        void onFail(Exception e);
    }

}
