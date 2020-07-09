package com.xiaoniu.common.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.UUID;

public class DeviceUtils {
    private static String sIMEI;
    private static String sMAC;
    private static String sAndroidId;
    private static String sBrand;
    private static String sModel;
    private static String sUUID;

    @SuppressLint("MissingPermission")
    public static String getIMEI() {
        if (!TextUtils.isEmpty(sIMEI)) {
            return sIMEI;
        }
        try {
            TelephonyManager tm = (TelephonyManager) ContextUtils.getContext().getSystemService(Context.TELEPHONY_SERVICE);
            if (Build.VERSION.SDK_INT >= 26) {
                sIMEI = tm.getImei();
            } else if (Build.VERSION.SDK_INT >= 23) {
                sIMEI = tm.getDeviceId(0);
            } else {
                sIMEI = tm.getDeviceId();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sIMEI;
    }

    public static String getMAC() {
        if (sMAC == null) {

        }
        return sMAC;
    }

    public static String getAndroidId() {
        if (sAndroidId != null) {
            return sAndroidId;
        }
        try {
            sAndroidId = Settings.Secure.getString(ContextUtils.getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            if (sAndroidId == null) {
                sAndroidId = "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sAndroidId;
    }


    public static String getBrand() {
        if (sBrand == null) {
            sBrand = Build.BRAND;
        }
        return sBrand;
    }

    public static String getModel() {
        if (sModel == null) {
            sModel = Build.MODEL;
            if (sModel != null) {
                sModel = sModel.trim().replaceAll("\\s*", "");
            } else {
                sModel = "";
            }
        }
        return sModel;
    }

    /**
     * 获取sdk版本
     */
    public static int getSDKVersion() {
        return android.os.Build.VERSION.SDK_INT;
    }

    public static int getScreenWidth() {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) ContextUtils.getContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    public static int getScreenHeight() {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) ContextUtils.getContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    private static String udid = "";
    private static String mac = "";
    private static String sImei;
    public final static String KEY_DEVICE_ID = "UserDeviceId";

    @SuppressLint("MissingPermission")
    public static String getUdid() {
        if (!checkUdidValid()) {
            SharedPreferences sharepre = PreferenceManager.getDefaultSharedPreferences(ContextUtils.getContext());
            udid = sharepre.getString(KEY_DEVICE_ID, "");
            if (!checkUdidValid()) {
                try {
                    TelephonyManager tm = (TelephonyManager) ContextUtils.getContext().getSystemService(Context.TELEPHONY_SERVICE);
                    udid = "" + tm.getDeviceId();
                } catch (Exception e) {
                    udid = "";
                }

                if (!checkUdidValid()) {
                    if (isEffective(mac)) {
                        udid = "MAC" + mac.replace(':', '0').replace('.', '0');
                    } else {
                        udid = "";
                    }
                    if (!checkUdidValid()) {
                        try {
                            udid = "" + Settings.Secure.getString(ContextUtils.getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                        } catch (Exception e) {
                            udid = "";
                        }
                        if (!checkUdidValid()) {
                            getRandomUdidFromFile();
                        }
                    }
                }

                if (checkUdidValid()) {
                    SharedPreferences.Editor editor = sharepre.edit();
                    editor.putString("UserDeviceId", udid);
                    editor.commit();
                }
            }
        }

        return udid;
    }

    private static boolean checkUdidValid() {
        if (udid != null && !"".equals(udid) && !"null".equals(udid)
                && !"NULL".equals(udid) && !checkUdidZero()
                && !"9774d56d682e549c".equals(udid))
        // SDK version 2.2, some devices have the same id
        {
            int len = 10 - udid.length();
            for (int i = 0; i < len; i++) {
                udid = "0" + udid;
            }
            return true;
        } else {
            return false;
        }
    }

    private static boolean checkUdidZero() {
        try {
            int val = Integer.parseInt(udid);
            return val == 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isEffective(String string) {
        return !((string == null) || ("".equals(string)) || (" ".equals(string))
                || ("null".equals(string)) || ("\n".equals(string)));
    }

    public synchronized static void getRandomUdidFromFile() {
        File installation = new File(ContextUtils.getContext().getFilesDir(), "INSTALLATION");
        try {
            if (!installation.exists()) {
                writeInstallationFile(installation);
            }
            udid = readInstallationFile(installation);
            Log.e("getRandomUId",udid);
        } catch (Exception e) {
            udid = "";
            Log.e("getRandomUId","Exception()");
        }
    }

    private static void writeInstallationFile(File installation)
            throws IOException {
        FileOutputStream out = new FileOutputStream(installation);
        String id = UUID.randomUUID().toString();
        out.write(id.getBytes());
        out.close();
    }

    private static String readInstallationFile(File installation)
            throws IOException {
        RandomAccessFile f = new RandomAccessFile(installation, "r");
        byte[] bytes = new byte[(int) f.length()];
        f.readFully(bytes);
        f.close();
        return new String(bytes);
    }
}
