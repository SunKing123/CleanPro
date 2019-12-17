package com.xiaoniu.cleanking.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

public class PhoneInfoUtils {


//    /**
//     * 获取当前手机系统版本号
//     *
//     * @return 系统版本号
//     */
//    public static String getSystemVersion() {
//        return Build.DISPLAY;
//        //return android.os.Build.VERSION.RELEASE;
//
//    }
//
//
//    /**
//     * 获取手机型号
//     *
//     * @return 手机型号
//     */
//    public static String getSystemModel() {
//        return Build.MODEL;
//    }
//
//    /**
//     * 获取手机厂商
//     *
//     * @return 手机厂商
//     */
//    public static String getDeviceBrand() {
//        return Build.BRAND;
//    }
//
//
//    /**
//     * 获取SN
//     *
//     * @return
//     */
//    public static String getSn(Context ctx) {
//        String serial = null;
//        try {
//            Class<?> c = Class.forName("android.os.SystemProperties");
//            Method get = c.getMethod("get", String.class);
//            serial = (String) get.invoke(c, "ro.serialno");
//
//        } catch (Exception ignored) {
//
//        }
//
//        return serial;
//    }

    /**
     * 系统4.0的时候
     * 获取手机IMEI 或者MEID
     *
     * @return 手机IMEI
     */
    @SuppressLint("MissingPermission")
    public static String getImeiOrMeid(Context ctx) {
        try {
            TelephonyManager manager = (TelephonyManager) ctx.getSystemService(Activity.TELEPHONY_SERVICE);
            if (manager != null) {
                return manager.getDeviceId();
            }
        }catch (RuntimeException e){
            e.printStackTrace();
        }

        return null;
    }

//    /**
//     * 拿到imei或者meid后判断是有多少位数
//     *
//     * @param ctx
//     * @return
//     */
//    public static int getNumber(Context ctx) {
//        return getImeiOrMeid(ctx).trim().length();
//    }


    /**
     *  5.0统一使用这个获取IMEI IMEI2 MEID
     *
     * @param ctx
     * @return
     */
    @SuppressLint("MissingPermission")
    @TargetApi(Build.VERSION_CODES.M)
    public static String getImeiAndMeid(Context ctx) {
        try {
            TelephonyManager mTelephonyManager = (TelephonyManager) ctx.getSystemService(Activity.TELEPHONY_SERVICE);
            mTelephonyManager.getDeviceId(0);
            return  mTelephonyManager.getDeviceId(0);
        }catch (RuntimeException e){
            e.printStackTrace();
        }
      return "";
    }

    @SuppressLint("MissingPermission")
    @TargetApi(Build.VERSION_CODES.O)
    public static String getIMEIforO(Context context) {
        String imei1 = "";
        try {
            TelephonyManager tm = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE));
            imei1 = tm.getImei(0);
        }catch (RuntimeException e){
            e.printStackTrace();
        }
        return imei1;
    }


//    /**
//     * 获取版本号
//     *
//     * @param context
//     * @return
//     */
//    public static int getVerCode(Context context) {
//        int vercoe = 0;
//
//        try {
//            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
//            vercoe = packageInfo.versionCode;
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//
//        }
//        return vercoe;
//    }




    public static String getIMEI(Context ctx) {
        String imei = "";
        try {

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {  //4.0以下 直接获取
                imei = getImeiOrMeid(ctx);
            } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) { //5。0，6。0系统
                imei = getImeiAndMeid(ctx);
            } else {
                imei = getIMEIforO(ctx);

            }

        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return imei;
    }


    /**
     * 获取设备Id
     *
     * @param context
     * @return
     */
    public static String getAndroidId(Context context) {
        String androidID = "";
        try {
            androidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {

        }
        return androidID;
    }

    /**
     * 获取设备sn
     * @return
     */
    public static String getSN() {
        String sn = "";
        if (Build.VERSION.SDK_INT < 28) {
            sn = Build.SERIAL;
        }
        return sn;
    }

    /**
     * 获取uuid（和牛数保持一致的UUID）
     * @param context
     * @return
     */
    public static String getUUID(Context context){
        String uuid = getAndroidId(context);
        String sn =  getSN();
        if (!TextUtils.isEmpty(sn) && !Build.UNKNOWN.equalsIgnoreCase(sn)) {
            uuid = uuid + "_" + sn;
        }
        return uuid;
    }


}
