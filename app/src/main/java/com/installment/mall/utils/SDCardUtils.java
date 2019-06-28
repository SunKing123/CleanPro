package com.installment.mall.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by fengpeihao on 2017/8/1.
 */

public final class SDCardUtils {

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

    private SDCardUtils() {

        throw new UnsupportedOperationException("u can't instantiate me...");

    }


    /**
     * 判断SD卡是否可用
     *
     * @return true : 可用<br>false : 不可用
     */

    public static boolean isSDCardEnable() {

        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());

    }


    /**
     * 获取SD卡路径
     * <p>
     * <p>先用shell，shell失败再普通方法获取，一般是/storage/emulated/0/</p>
     *
     * @return SD卡路径
     */

    public static String getSDCardPath() {

        if (!isSDCardEnable()) return null;

        String cmd = "cat /proc/mounts";

        Runtime run = Runtime.getRuntime();

        BufferedReader bufferedReader = null;

        try {

            Process p = run.exec(cmd);

            bufferedReader = new BufferedReader(new InputStreamReader(new BufferedInputStream(p.getInputStream())));

            String lineStr;

            while ((lineStr = bufferedReader.readLine()) != null) {

                if (lineStr.contains("sdcard") && lineStr.contains(".android_secure")) {

                    String[] strArray = lineStr.split(" ");

                    if (strArray.length >= 5) {

                        return strArray[1].replace("/.android_secure", "") + File.separator;

                    }

                }

                if (p.waitFor() != 0 && p.exitValue() == 1) {

                    break;

                }

            }

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

            try {
                if (bufferedReader != null)
                    bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return Environment.getExternalStorageDirectory().getPath() + File.separator;

    }


    /**
     * 获取SD卡data路径
     *
     * @return SD卡data路径
     */

    public static String getDataPath() {

        if (!isSDCardEnable()) return null;

        return Environment.getExternalStorageDirectory().getPath() + File.separator + "data" + File.separator;

    }

    /**
     * 获得SD卡总大小
     *
     * @return
     */
    public static String getSDTotalSize() {
        try {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return byte2FitMemorySize(totalBlocks * blockSize);
        } catch (Exception e) {
            return "";
        }
    }


    /**
     * 获取SD卡剩余空间
     *
     * @return SD卡剩余空间
     */

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)

    public static String getFreeSpace() {
        try {
            if (!isSDCardEnable()) return null;

            StatFs stat = new StatFs(getSDCardPath());

            long blockSize, availableBlocks;

//        availableBlocks = stat.getAvailableBlocksLong();


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                availableBlocks = stat.getAvailableBlocksLong();
                blockSize = stat.getBlockSizeLong();
            } else {
                availableBlocks = stat.getBlockSize();
                blockSize = stat.getBlockSize();
            }


            return byte2FitMemorySize(availableBlocks * blockSize);
        } catch (Exception e) {
            return "";
        }

    }

    /**
     * 字节数转合适内存大小
     * <p>
     * <p>保留3位小数</p>
     *
     * @param byteNum 字节数
     * @return 合适内存大小
     */

    @SuppressLint("DefaultLocale")

    public static String byte2FitMemorySize(final long byteNum) {

        if (byteNum < 0) {

            return "shouldn't be less than zero!";

        } else if (byteNum < KB) {

            return String.format("%.3fB", (double) byteNum + 0.0005);

        } else if (byteNum < MB) {

            return String.format("%.3fKB", (double) byteNum / KB + 0.0005);

        } else if (byteNum < GB) {

            return String.format("%.3fMB", (double) byteNum / MB + 0.0005);

        } else {

            return String.format("%.3fGB", (double) byteNum / GB + 0.0005);

        }

    }


    /**
     * 获取SD卡信息
     *
     * @return SDCardInfo
     */

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)

    public static String getSDCardInfo() {

        if (!isSDCardEnable()) return null;

        SDCardInfo sd = new SDCardInfo();

        sd.isExist = true;

        StatFs sf = new StatFs(Environment.getExternalStorageDirectory().getPath());

        sd.totalBlocks = sf.getBlockCountLong();

        sd.blockByteSize = sf.getBlockSizeLong();

        sd.availableBlocks = sf.getAvailableBlocksLong();

        sd.availableBytes = sf.getAvailableBytes();

        sd.freeBlocks = sf.getFreeBlocksLong();

        sd.freeBytes = sf.getFreeBytes();

        sd.totalBytes = sf.getTotalBytes();

        return sd.toString();

    }


    public static class SDCardInfo {

        boolean isExist;

        long totalBlocks;

        long freeBlocks;

        long availableBlocks;

        long blockByteSize;

        long totalBytes;

        long freeBytes;

        long availableBytes;


        @Override

        public String toString() {

            return "isExist=" + isExist +

                    "\ntotalBlocks=" + totalBlocks +

                    "\nfreeBlocks=" + freeBlocks +

                    "\navailableBlocks=" + availableBlocks +

                    "\nblockByteSize=" + blockByteSize +

                    "\ntotalBytes=" + totalBytes +

                    "\nfreeBytes=" + freeBytes +

                    "\navailableBytes=" + availableBytes;

        }

    }

}
