package com.xiaoniu.cleanking.utils;

import android.app.Activity;
import android.os.Build;
import android.util.Log;
import android.view.DisplayCutout;
import android.view.View;
import android.view.WindowInsets;

import com.xiaoniu.cleanking.app.AppApplication;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * deprecation:刘海屏适配工具
 * author:ayb
 * time:2018/11/12
 */
public class NotchUtils {
    private static final String TAG = "NotchUtils";
    /**
     * 判断是否是刘海屏   小米,华为 oppo vivo
     * @return
     */
    public static boolean hasNotchScreen(Activity activity){
        // 各种品牌
        return getInt("ro.miui.notch") == 1 || hasNotchAtHuawei() || hasNotchAtOPPO()
                || hasNotchAtVivo() || isAndroidP(activity) != null;
    }

    /**
     * 获取不同厂商刘海屏高度
     * @param activity
     * @return
     */
    public static int getNotchHeight(Activity activity){
        //如果有刘海屏就获取刘海屏高度
        if (hasNotchScreen(activity)){
            getHeight();
        }
        return 0;
    }

    private static int getHeight() {
        if (isXiaomi()){
            //获取小米刘海高度
            return getXiaomiNotchSize();
        }else if (isHuawei()){
            //获取华为刘海高度 int[0]值为刘海宽度 int[1]值为刘海高度。
            int[] huaweiNotchSize = getHuaweiNotchSize();
            return huaweiNotchSize[0];
        }else if (isOppo()){
            //获取oppo刘海高度
            //默认返回  oppo官网给的指定高度
            return 80;
        }else if (isVivo()){
            //获取vivo刘海高度  27dp
            //vivo官网给的指定高度 27dp
            return dip2px(27);
        }
        return 0;
    }

    /**
     * dp转换成px
     * @param dpValue
     * @return
     */
    public static int dip2px(float dpValue) {
        final float scale = AppApplication.getInstance().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    /**
     * 华为
     * 获取刘海尺寸：width、height
     int[0]值为刘海宽度 int[1]值为刘海高度。
     * @return
     */
    public static int[] getHuaweiNotchSize() {
        int[] ret = new int[]{0, 0};
        try {
            ClassLoader cl = AppApplication.getInstance().getClassLoader();
            Class hwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = hwNotchSizeUtil.getMethod("getNotchSize");
            ret = (int[]) get.invoke(hwNotchSizeUtil);
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "getNotchSize ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "getNotchSize NoSuchMethodException");
        } catch (Exception e) {
            Log.e(TAG, "getNotchSize Exception");
        } finally {

        }
        return ret;
    }

    public static int getXiaomiNotchSize(){
        int dimensionPixelSize = 0;
        //获取高度
        int resourceId = AppApplication.getInstance().getResources().getIdentifier("notch_height", "dimen", "android");
        if (resourceId > 0) {
            dimensionPixelSize = AppApplication.getInstance().getResources().getDimensionPixelSize(resourceId);
        }
        //获取宽度
        /*int resourceId = context.getResources().getIdentifier("notch_width", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }*/

        return dimensionPixelSize;
    }

    /**
     * Android P 刘海屏判断
     * @param activity
     * @return
     */
    public static DisplayCutout isAndroidP(Activity activity){
        View decorView = activity.getWindow().getDecorView();
        if (decorView != null && Build.VERSION.SDK_INT >= 28){
            WindowInsets windowInsets = decorView.getRootWindowInsets();
            if (windowInsets != null) {
                return windowInsets.getDisplayCutout();
            }
        }
        return null;
    }

    /**
     * 小米刘海屏判断.
     * @return 0 if it is not notch ; return 1 means notch
     * @throws IllegalArgumentException if the key exceeds 32 characters
     */
    public static int getInt(String key) {
        int result = 0;
        if (isXiaomi()){
            try {
                ClassLoader classLoader = AppApplication.getInstance().getClassLoader();
                @SuppressWarnings("rawtypes")
                Class SystemProperties = classLoader.loadClass("android.os.SystemProperties");
                //参数类型
                @SuppressWarnings("rawtypes")
                Class[] paramTypes = new Class[2];
                paramTypes[0] = String.class;
                paramTypes[1] = int.class;
                Method getInt = SystemProperties.getMethod("getInt", paramTypes);
                //参数
                Object[] params = new Object[2];
                params[0] = new String(key);
                params[1] = new Integer(0);
                result = (Integer) getInt.invoke(SystemProperties, params);

            } catch (ClassNotFoundException e) {
                Log.e(TAG, "getInt ClassNotFoundException");
            } catch (NoSuchMethodException e) {
                Log.e(TAG, "getInt ClassNotFoundException");
            } catch (IllegalAccessException e) {
                Log.e(TAG, "getInt IllegalAccessException");
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "getInt IllegalArgumentException");
            } catch (InvocationTargetException e) {
                Log.e(TAG, "getInt InvocationTargetException");
            }
        }
        return result;
    }

    /**
     * 华为刘海屏判断
     * @return
     */
    public static boolean hasNotchAtHuawei() {
        boolean ret = false;
        try {
            ClassLoader classLoader = AppApplication.getInstance().getClassLoader();
            Class hwNotchSizeUtil = classLoader.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = hwNotchSizeUtil.getMethod("hasNotchInScreen");
            ret = (boolean) get.invoke(hwNotchSizeUtil);
            return ret;
        } catch (ClassNotFoundException e) {
            Log.e("","hasNotchAtHuawei ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            Log.e("","hasNotchAtHuawei NoSuchMethodException");
        } catch (Exception e) {
            Log.e("", "hasNotchAtHuawei Exception");
        }
        return ret;
    }
    //是否有刘海
    public static final int VIVO_NOTCH = 0x00000020;
    //是否有圆角
    public static final int VIVO_FILLET = 0x00000008;

    /**
     * VIVO刘海屏判断
     * @return
     */
    public static boolean hasNotchAtVivo() {
        boolean ret = false;
        try {
            ClassLoader classLoader = AppApplication.getInstance().getClassLoader();
            Class FtFeature = classLoader.loadClass("android.util.FtFeature");
            Method method = FtFeature.getMethod("isFeatureSupport", int.class);
            ret = (boolean) method.invoke(FtFeature, VIVO_NOTCH);
            return ret;
        } catch (ClassNotFoundException e) {
            Log.e( "","hasNotchAtVivo ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            Log.e( "", "hasNotchAtVivo NoSuchMethodException");
        } catch (Exception e) {
            Log.e(  "","hasNotchAtVivo Exception");
        }
        return ret;
    }
    /**
     * OPPO刘海屏判断
     * @return
     */
    public static boolean hasNotchAtOPPO() {
        return AppApplication.getInstance().getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
    }
    // 是否是小米手机
    public static boolean isXiaomi() {
        return "Xiaomi".toLowerCase().equals(Build.MANUFACTURER.toLowerCase());
    }
    // 是否是华为手机
    public static boolean isHuawei() {
        return "huawei".toLowerCase().equals(Build.MANUFACTURER.toLowerCase());
    }
    public static boolean isOppo() {
        return "oppo".toLowerCase().equals(Build.MANUFACTURER.toLowerCase());
    }
    public static boolean isVivo() {
        return "vivo".toLowerCase().equals(Build.MANUFACTURER.toLowerCase());
    }

}
