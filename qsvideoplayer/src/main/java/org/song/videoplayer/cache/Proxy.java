package org.song.videoplayer.cache;

import android.content.Context;
import android.os.Environment;

import com.danikula.videocache.HttpProxyCacheServer;
import com.danikula.videocache.headers.HeaderInjector;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * Created by song on 2018/9/3
 * Contact with github.com/tohodog
 * 描述
 */

public class Proxy {

    private static HttpProxyCacheServer.Builder builder;
    private static final String INDIVIDUAL_DIR_NAME = "video-cache";
    private static File sCacheDir;

    synchronized static HttpProxyCacheServer getProxy(Context context, final Map<String, String> headers) {
        if (builder == null) {
            File cacheDir = getFolder(context, INDIVIDUAL_DIR_NAME);
            builder = new HttpProxyCacheServer
                    .Builder(context)
                    .maxCacheFilesCount(1);//缓存文件大小
            if (cacheDir != null) {
                sCacheDir = cacheDir;
                builder.cacheDirectory(cacheDir);
            }
            deleteExpireVideo();
        }
        if (headers != null) {
            builder.headerInjector(new HeaderInjector() {
                @Override
                public Map<String, String> addHeaders(String url) {
                    return headers;
                }
            });
        }
        return builder.build();
    }


    public static void setConfig(HttpProxyCacheServer.Builder builder) {
        Proxy.builder = builder;
    }

    public static File getFolder(Context context, String folderName) {
        File file = null;
        try {
            if (folderName.startsWith(File.separator)) {
                file = new File(getSDCardPath() + folderName);
            } else {
                file = new File(getSDCardPath() + File.separator + folderName);
            }
            file.mkdirs();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (file == null || !file.exists()) {
                File cache = context.getExternalCacheDir();
                if (cache == null) {
                    cache = context.getCacheDir();
                }
                if (cache != null) {
                    file = new File(cache, folderName);
                    file.mkdirs();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    public static String getSDCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public static void deleteExpireVideo() {
        File cacheDir = sCacheDir;
        if (cacheDir != null && cacheDir.isDirectory() && cacheDir.exists()) {
            File[] files = cacheDir.listFiles();
            if (files != null && files.length > 0) {
                String todayDate = getDate(System.currentTimeMillis());
                for (File file : files) {
                    String curDate = getDate(file.lastModified());
                    if (!curDate.equals(todayDate)) {
                        file.delete();
                    }
                }
            }
        }
    }

    public static void deleteCacheVideo() {
        try {
            File cacheDir = sCacheDir;
            if (cacheDir != null && cacheDir.isDirectory() && cacheDir.exists()) {
                File[] files = cacheDir.listFiles();
                if (files != null && files.length > 0) {
                    for (File file : files) {
                        String path = file.getAbsolutePath();
                        if (path.endsWith(".download")) {
                            file.delete();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取当前日期-年月日
     *
     * @return
     */
    public static String getDate(long timeMillis) {
        try {
            SimpleDateFormat YYYY_MM_DD_FORMAT = new SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault());
            String date = YYYY_MM_DD_FORMAT.format(new Date(timeMillis));
            return date;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
