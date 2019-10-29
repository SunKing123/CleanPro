package com.xiaoniu.cleanking.utils;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Created by lang.chen on 2019/7/1
 */
public class FileSizeUtils {


    /**
     * 转换文件大小
     * 四舍五入
     */
    public static String formatFileSize(long size) {
        if (size <= 0){
            return "0";
        }
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        DecimalFormat decimalFormat=new DecimalFormat("#0.0");
        decimalFormat.setRoundingMode(RoundingMode.HALF_DOWN);
        return decimalFormat.format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];

    }
}
