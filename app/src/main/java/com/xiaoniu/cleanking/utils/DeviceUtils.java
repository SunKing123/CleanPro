package com.xiaoniu.cleanking.utils;

import android.content.Context;
import android.os.Environment;
import android.os.IBinder;
import android.os.StatFs;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.xiaoniu.cleanking.app.AppApplication;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ii.                                         ;9ABH,
 * SA391,                                    .r9GG35&G
 * &#ii13Gh;                               i3X31i;:,rB1
 * iMs,:,i5895,                         .5G91:,:;:s1:8A
 * 33::::,,;5G5,                     ,58Si,,:::,sHX;iH1
 * Sr.,:;rs13BBX35hh11511h5Shhh5S3GAXS:.,,::,,1AG3i,GG
 * .G51S511sr;;iiiishS8G89Shsrrsh59S;.,,,,,..5A85Si,h8
 * :SB9s:,............................,,,.,,,SASh53h,1G.
 * .r18S;..,,,,,,,,,,,,,,,,,,,,,,,,,,,,,....,,.1H315199,rX,
 * ;S89s,..,,,,,,,,,,,,,,,,,,,,,,,....,,.......,,,;r1ShS8,;Xi
 * i55s:.........,,,,,,,,,,,,,,,,.,,,......,.....,,....r9&5.:X1
 * 59;.....,.     .,,,,,,,,,,,...        .............,..:1;.:&s
 * s8,..;53S5S3s.   .,,,,,,,.,..      i15S5h1:.........,,,..,,:99
 * 93.:39s:rSGB@A;  ..,,,,.....    .SG3hhh9G&BGi..,,,,,,,,,,,,.,83
 * G5.G8  9#@@@@@X. .,,,,,,.....  iA9,.S&B###@@Mr...,,,,,,,,..,.;Xh
 * Gs.X8 S@@@@@@@B:..,,,,,,,,,,. rA1 ,A@@@@@@@@@H:........,,,,,,.iX:
 * ;9. ,8A#@@@@@@#5,.,,,,,,,,,... 9A. 8@@@@@@@@@@M;    ....,,,,,,,,S8
 * X3    iS8XAHH8s.,,,,,,,,,,...,..58hH@@@@@@@@@Hs       ...,,,,,,,:Gs
 * r8,        ,,,...,,,,,,,,,,.....  ,h8XABMMHX3r.          .,,,,,,,.rX:
 * :9, .    .:,..,:;;;::,.,,,,,..          .,,.               ..,,,,,,.59
 * .Si      ,:.i8HBMMMMMB&5,....                    .            .,,,,,.sMr
 * SS       :: h@@@@@@@@@@#; .                     ...  .         ..,,,,iM5
 * 91  .    ;:.,1&@@@@@@MXs.                            .          .,,:,:&S
 * hS ....  .:;,,,i3MMS1;..,..... .  .     ...                     ..,:,.99
 * ,8; ..... .,:,..,8Ms:;,,,...                                     .,::.83
 * s&: ....  .sS553B@@HX3s;,.    .,;13h.                            .:::&1
 * SXr  .  ...;s3G99XA&X88Shss11155hi.                             ,;:h&,
 * iH8:  . ..   ,;iiii;,::,,,,,.                                 .;irHA
 * ,8X5;   .     .......                                       ,;iihS8Gi
 * 1831,                                                 .,;irrrrrs&@
 * ;5A8r.                                            .:;iiiiirrss1H
 * :X@H3s.......                                .,:;iii;iiiiirsrh
 * r#h:;,...,,.. .,,:;;;;;:::,...              .:;;;;;;iiiirrss1
 * ,M8 ..,....,.....,,::::::,,...         .     .,;;;iiiiiirss11h
 * 8B;.,,,,,,,.,.....          .           ..   .:;;;;iirrsss111h
 * i@5,:::,,,,,,,,.... .                   . .:::;;;;;irrrss111111
 * 9Bi,:,,,,......                        ..r91;;;;;iirrsss1ss1111
 */

public class DeviceUtils {

    public static int getScreenWidth() {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) AppApplication.getInstance().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    public static int getScreenHeight() {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) AppApplication.getInstance().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    public static int dip2px(float dpValue) {
        final float scale = AppApplication.getInstance().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(float pxValue) {
        final float scale = AppApplication.getInstance().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * @param spVal
     * @return 根据设备的分辨率从 ps 的单位 转成为 px(像素)
     */
    public static int sp2px(int spVal) {
        return Math.round(spVal * AppApplication.getInstance().getResources().getDisplayMetrics().density);
    }

    /**
     * @param pxVal
     * @return 根据设备的分辨率从 px(像素) 的单位 转成为 sp
     */
    public static int px2sp(int pxVal) {
        return Math.round(pxVal / AppApplication.getInstance().getResources().getDisplayMetrics().scaledDensity);
    }

    public static boolean isNum(String str) {
        try {
            new BigDecimal(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String formartTimeToSec(String intMs) {
        if ("".equals(intMs)) {
            intMs = "0";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm");
        String date = sdf.format(new Date(
                (int) Double.parseDouble(intMs) * 1000L));

        return date;
    }

    public static String formartTimeToMis(String intMs) {
        if ("".equals(intMs)) {
            intMs = "0";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = sdf.format(new Date(
                (int) Double.parseDouble(intMs) * 1000L));

        return date;
    }

    public static String convertFileSize(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;
        if (size >= gb) {
            return String.format("%.1f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (size >= kb) {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else {
            return String.format("%d B", size);
        }
    }

    public static int getStatusBarHeight() {
        Class<?> c;
        Object obj;
        Field field;
        int x, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = AppApplication.getInstance().getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }

    public static void hideSoftInput(IBinder windowToken) {
        InputMethodManager inputMethodManager = (InputMethodManager) AppApplication.getInstance()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0);
    }

    public static void showSoftInput(IBinder windowToken) {
        InputMethodManager inputMethodManager = (InputMethodManager) AppApplication.getInstance()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInputFromInputMethod(windowToken, 0);
    }

    private static long lastClickTime;

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 800) {
            return true;
        }
        lastClickTime = time;
        return false;
    }


    private static long lastOperateTime;

    public static boolean isFastOperate() {
        long time = System.currentTimeMillis();
        long timeD = time - lastOperateTime;
        if (0 < timeD && timeD < 100) {
            return true;
        }
        lastOperateTime = time;
        return false;
    }

    private static long recordDelayTime;

    public static boolean isDelayInFo() {
        long time = System.currentTimeMillis();
        long timeD = time - recordDelayTime;
        if (0 < timeD && timeD < 3000) {
            return true;
        }
        recordDelayTime = time;
        return false;
    }

    /**
     * 根据文字长度换行平分文字
     *
     * @param s
     * @return
     */
    public static String getText(String s) {
        if (s.length() > 30) {
            return s.substring(0, s.length() / 2) + "\n" + s.substring(s.length() / 2);
        }
        return s;
    }

    //    获取总的内存空间并控制显示
    public static String getTotalSpace() {
        String mToalS = "";
        try {
            StatFs sf = new StatFs(Environment.getExternalStorageDirectory().getPath());
            float i = 1024 * 1024 * 1024;
            float bytes = 0;
            bytes = sf.getTotalBytes() / i;

            DecimalFormat df = new DecimalFormat("0.00");//格式化小数
            mToalS = df.format(bytes);
        } catch (Exception e) {
        }
        return mToalS;
    }

    //    获取剩余的内存空间并控制显示
    public static String getFreeSpace() {
        String mFreeS = "";
        try {
            StatFs sf = new StatFs(Environment.getExternalStorageDirectory().getPath());
            float i = 1024 * 1024 * 1024;
            float bytes = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                bytes = sf.getFreeBytes() / i;
            }
            DecimalFormat df = new DecimalFormat("0.00");//格式化小数
            mFreeS = df.format(bytes);
        } catch (Exception e) {
        }

        return mFreeS;
    }

}
