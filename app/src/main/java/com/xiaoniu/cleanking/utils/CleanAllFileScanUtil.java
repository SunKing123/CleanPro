package com.xiaoniu.cleanking.utils;

import android.os.Environment;
import android.widget.TextView;

import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.ui.main.bean.FileEntity;
import com.xiaoniu.cleanking.ui.main.bean.Image;
import com.xiaoniu.cleanking.utils.db.FileDBManager;
import com.xiaoniu.cleanking.utils.db.FileTableManager;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 扫描sd卡工具类
 */
public class CleanAllFileScanUtil {
    /******************** 存储相关常量 ********************/
    /**
     * Byte与Byte的倍数
     */
    public static final int BYTE = 1;
    /**
     * KB与Byte的倍数
     */
    public static final int KB = 1024;
    /**
     * MB与Byte的倍数
     */
    public static final int MB = 1048576;
    /**
     * GB与Byte的倍数
     */
    public static final int GB = 1073741824;

    public static final String[] imageFormat = new String[]{".jpg", ".png", ".gif"};
    //    public static final String[] videoFormat = new String[]{".mp4", ".mov", ".mkv", ".avi",".wmv",".m4v",".mpg",".vob",".webm",".ogv",".3gp",".flv",".f4v",".swf"};
    public static final String[] videoFormat = new String[]{".mp4", ".3gp"};
    public static final String[] musicFormat = new String[]{".mp3"};
    public static final String[] apkFormat = new String[]{".apk"};

    public static List<FileEntity> clean_image_list ;


    /* access modifiers changed from: private */
    public AtomicInteger currentProgress;
    /* access modifiers changed from: private */
    public List<File> fileTemp;
    /* access modifiers changed from: private */
    public fileCheckByScan scanListener;
    private final int threadCount = 3;
    /* access modifiers changed from: private */
    public int totalFolderCount;

    public interface fileCheckByScan {
        void getFileByScan(File file);

        void scanProgress(int i, int i2);
    }

    public void scanAllFiles(fileCheckByScan filecheckbyscan, final String... strArr) {
        this.scanListener = filecheckbyscan;
        ThreadTaskUtil.executeNormalTask("-CleanUnusedPackageFragment-scanAllDiskForSize10Front2-385--", new Runnable() {
            public void run() {
                int i;
//                Logger.m6924i(Logger.TAG, Logger.ZYTAG, "CleanAllFileScanUtil-run-28--全局扫描开始 " + System.currentTimeMillis());
                CleanAllFileScanUtil.this.totalFolderCount = 0;
                CleanAllFileScanUtil.this.currentProgress = new AtomicInteger();
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
                CleanAllFileScanUtil.this.fileTemp = new ArrayList();
                File[] listFiles = file.listFiles();
                if (listFiles != null) {
                    for (File file2 : listFiles) {
                        if (file2 != null) {
                            if (file2.isDirectory()) {
                                File[] listFiles2 = file2.listFiles();
                                if (listFiles2 != null) {
                                    for (File file3 : listFiles2) {
                                        if (file3 != null) {
                                            if (file3.isDirectory()) {
                                                File[] listFiles3 = file3.listFiles();
                                                if (listFiles3 != null) {
                                                    for (File file4 : listFiles3) {
                                                        if (file4 != null) {
                                                            CleanAllFileScanUtil.this.fileTemp.add(file4);
                                                        }
                                                    }
                                                }
                                            } else {
                                                CleanAllFileScanUtil.this.fileTemp.add(file3);
                                            }
                                        }
                                    }
                                }
                            } else {
                                CleanAllFileScanUtil.this.fileTemp.add(file2);
                            }
                        }
                    }
                    CleanAllFileScanUtil.this.totalFolderCount = CleanAllFileScanUtil.this.fileTemp.size();
                    int i2 = 0;
                    while (i2 < CleanAllFileScanUtil.this.fileTemp.size()) {
                        if (((File) CleanAllFileScanUtil.this.fileTemp.get(i2)).isFile()) {
                            CleanAllFileScanUtil.this.currentProgress.getAndIncrement();
                            if (CleanAllFileScanUtil.this.scanListener != null) {
                                if (strArr != null) {
//                                    String[] strArr = strArr;
                                    int length = strArr.length;
                                    int i3 = 0;
                                    while (true) {
                                        if (i3 >= length) {
                                            break;
                                        }
                                        String str = strArr[i3];
                                        if (((File) CleanAllFileScanUtil.this.fileTemp.get(i2)).length() > 10 && ((File) CleanAllFileScanUtil.this.fileTemp.get(i2)).getAbsolutePath().endsWith(str)) {
                                            CleanAllFileScanUtil.this.scanListener.getFileByScan((File) CleanAllFileScanUtil.this.fileTemp.get(i2));
                                            break;
                                        }
                                        i3++;
                                    }
                                } else {
                                    CleanAllFileScanUtil.this.scanListener.getFileByScan((File) CleanAllFileScanUtil.this.fileTemp.get(i2));
                                }
                                CleanAllFileScanUtil.this.scanListener.scanProgress(CleanAllFileScanUtil.this.currentProgress.get(), CleanAllFileScanUtil.this.totalFolderCount);
                            }
                            CleanAllFileScanUtil.this.fileTemp.remove(i2);
                            i = i2 - 1;
                        } else {
                            i = i2;
                        }
                        i2 = i + 1;
                    }
//                    Logger.m6924i(Logger.TAG, Logger.ZYTAG, "CleanAllFileScanUtil-run-98-- " + System.currentTimeMillis());
                    for (int i4 = 0; i4 < 3; i4++) {
                        ThreadTaskUtil.executeNormalTask("-CleanAllFileScanUtil-run-105-- ", new Runnable() {
                            public void run() {
                                while (CleanAllFileScanUtil.this.fileTemp != null && CleanAllFileScanUtil.this.fileTemp.size() > 0) {
                                    File access$400 = CleanAllFileScanUtil.this.getWaitingList();
                                    if (access$400 != null) {
                                        CleanAllFileScanUtil.this.innerListFiles(access$400, CleanAllFileScanUtil.this.scanListener, strArr);
                                    }
                                    CleanAllFileScanUtil.this.currentProgress.getAndIncrement();
                                    if (CleanAllFileScanUtil.this.scanListener != null) {
                                        CleanAllFileScanUtil.this.scanListener.scanProgress(CleanAllFileScanUtil.this.currentProgress.get(), CleanAllFileScanUtil.this.totalFolderCount);
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public void innerListFiles(File file, fileCheckByScan filecheckbyscan, String... strArr) {
        File[] listFiles = file.listFiles();
        if (listFiles != null) {
            try {
                if (listFiles.length == 0) {
                    file.delete();
                    return;
                }
            } catch (Exception e) {
            }
            for (File file2 : listFiles) {
                if (file2.isDirectory()) {
                    if (!file2.getAbsolutePath().toLowerCase().contains("/tencent/micromsg") || file2.getName().length() < 30) {
                        innerListFiles(file2, filecheckbyscan, strArr);
                    }
                } else if (filecheckbyscan != null) {
                    if (strArr != null) {
                        int length = strArr.length;
                        int i = 0;
                        while (true) {
                            if (i >= length) {
                                break;
                            }
                            String str = strArr[i];
                            if (file2.length() > 10 && file2.getAbsolutePath().endsWith(str)) {
                                filecheckbyscan.getFileByScan(file2);
                                break;
                            }
                            i++;
                        }
                    } else {
                        filecheckbyscan.getFileByScan(file2);
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public synchronized File getWaitingList() {
        File file;
        if (this.fileTemp == null || this.fileTemp.size() <= 0) {
            file = null;
        } else {
            file = (File) this.fileTemp.get(0);
            this.fileTemp.remove(0);
        }
        return file;
    }

    /**
     * 字节数转合适大小
     * <p>保留3位小数</p>
     *
     * @param byteNum 字节数
     * @return 1...1024 unit
     */
    public static String byte2FitSize(long byteNum) {
        if (byteNum < 0) {
            return "shouldn't be less than zero!";
        } else if (byteNum < KB) {
            return String.format(Locale.getDefault(), "%.0fB", (double) byteNum);
        } else if (byteNum < MB) {
            return String.format(Locale.getDefault(), "%.0fKB", (double) byteNum / KB);
        } else if (byteNum < GB) {
            return String.format(Locale.getDefault(), "%.0fMB", (double) byteNum / MB);
        } else {
            return String.format(Locale.getDefault(), "%.0fGB", (double) byteNum / GB);
        }
    }

    public static String byte2FitSizeOne(long byteNum) {
        if (byteNum < 0) {
            return "shouldn't be less than zero!";
        } else if (byteNum < KB) {
            return String.format(Locale.getDefault(), "%.1fB", (double) byteNum);
        } else if (byteNum < MB) {
            return String.format(Locale.getDefault(), "%.1fKB", (double) byteNum / KB);
        } else if (byteNum < GB) {
            return String.format(Locale.getDefault(), "%.1fMB", (double) byteNum / MB);
        } else {
            return String.format(Locale.getDefault(), "%.1fGB", (double) byteNum / GB);
        }
    }

    public static String byte2FitSizeTwo(long byteNum, TextView textView) {
        if (byteNum < 0) {
            return "shouldn't be less than zero!";
        } else if (byteNum < KB) {
            textView.setText("B");
            return String.format(Locale.getDefault(), "%.1f", (double) byteNum);
        } else if (byteNum < MB) {
            textView.setText("KB");
            return String.format(Locale.getDefault(), "%.1f", (double) byteNum / KB);
        } else if (byteNum < GB) {
            textView.setText("MB");
            return String.format(Locale.getDefault(), "%.1f", (double) byteNum / MB);
        } else {
            textView.setText("GB");
            return String.format(Locale.getDefault(), "%.1f", (double) byteNum / GB);
        }
    }
    /**
     * 多个数组赋值
     *
     * @param first
     * @param rest
     * @param <T>
     * @return
     */
    public static <T> T[] arrayConcatAll(T[] first, T[]... rest) {
        int totalLength = first.length;
        for (T[] array : rest) {
            totalLength += array.length;
        }
        T[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (T[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

    public static void scanSdcardFiles() {
        String[] fileArray = new String[]{};
        fileArray = CleanAllFileScanUtil.arrayConcatAll(CleanAllFileScanUtil.imageFormat, CleanAllFileScanUtil.videoFormat, CleanAllFileScanUtil.musicFormat, CleanAllFileScanUtil.apkFormat);
        List<File> listFiles = new ArrayList<>();
        CleanAllFileScanUtil cleanAllFileScanUtil = new CleanAllFileScanUtil();
        cleanAllFileScanUtil.scanAllFiles(new CleanAllFileScanUtil.fileCheckByScan() {
            @Override
            public void getFileByScan(File file) {
                listFiles.add(file);
            }

            @Override
            public void scanProgress(int i, int i2) {
                if (i == i2) {
                    FileTableManager.insertBySql(AppApplication.getInstance(), listFiles);
                    EventBus.getDefault().postSticky(new MessageEvent(EventBusTags.UPDATE_FILE_MANAGER));
                }
            }
        }, fileArray);
    }
}