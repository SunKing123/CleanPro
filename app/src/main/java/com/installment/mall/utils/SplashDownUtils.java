package com.installment.mall.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.installment.mall.app.AppApplication;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;
import java.text.NumberFormat;

/**
 * Created by fengpeihao on 2018/8/27.
 */

public class SplashDownUtils {

    private static String fileName = "splash.png";

    public static void download(String downloadUrl, final ApkDownloadUtils.DownloadListener downloadListener) {
        FileDownloader.setup(AppApplication.getInstance());
        final String path = getCachePath() + File.separator + fileName;
        delImage();
        BaseDownloadTask task = FileDownloader.getImpl().create(downloadUrl)
                .setPath(path)
                .setListener(new FileDownloadListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        if (downloadListener != null) {
                            float radio = totalBytes == 0 ? 0f : (float) soFarBytes / (float) totalBytes;
                            NumberFormat numberFormat = NumberFormat.getPercentInstance();
                            numberFormat.setMinimumFractionDigits(0);
                            numberFormat.setMaximumFractionDigits(0);
                            String percent = numberFormat.format((double) radio);
                            downloadListener.progress(percent);
                        }
                    }

                    @Override
                    protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                        if (downloadListener != null) {
                            downloadListener.onStart();
                        }
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        if (downloadListener != null) {
                            float radio = totalBytes == 0 ? 0f : (float) soFarBytes / (float) totalBytes;
                            NumberFormat numberFormat = NumberFormat.getPercentInstance();
                            numberFormat.setMinimumFractionDigits(0);
                            numberFormat.setMaximumFractionDigits(0);
                            String percent = numberFormat.format((double) radio);
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
                        if (downloadListener != null) {
                            downloadListener.completed(path);
                        }
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        Log.e("ayb", "error===" + e.getMessage());
                        if (downloadListener != null) {
                            downloadListener.onError(e.getMessage());
                        }
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
                        Log.e("ayb", "warn===" + task.getErrorCause());
                    }
                });

        task.start();
    }

    /**
     * 获取app缓存路径[SDCard/Android/data/你的应用的包名/cache]
     *
     * @return 缓存路径
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

    public static boolean isImageExists(Context context) {
        File file = new File(getCachePath(), fileName);
        return file.exists();
    }

    public static void delImage() {
        File file = new File(getCachePath(), fileName);
        if (file.exists()) {
            file.delete();
        }
    }

    public static String getImagePath(Context context) {
        return getCachePath() + File.separator + fileName;
    }

    public static File getImage(Context context) {
        return new File(getCachePath(), fileName);
    }
}
