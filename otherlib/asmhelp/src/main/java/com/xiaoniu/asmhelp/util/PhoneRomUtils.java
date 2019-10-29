package com.xiaoniu.asmhelp.util;

import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Desc:
 * <p>
 *
 * @author: ZhouTao
 * Date: 2019/7/2
 * Copyright: Copyright (c) 2016-2020
 * Company: @小牛科技
 * Update
 */
public class PhoneRomUtils {
    private static final String MANUFACTURER = Build.MANUFACTURER.toLowerCase();
    private static final String MODEL = android.os.Build.MODEL.toLowerCase();

    public static final int HUAWEI = 1;
    public static final int XIAOMI = 2;
    public static final int OPPO = 3;
    public static final int VIVO = 4;
    public static final int MEIZU = 5;
    public static final int SANXING = 6;
    public static final int LESHI = 7;
    public static final int SMARTISAN = 8;
    public static final int LG = 9;
    public static final int ZTE = 10;
    public static final int KUPAI = 11;
    public static final int LENOVO = 12;
    public static final int SONY = 13;
    public static final int ONEPLUS = 14;


    public static String getPhoneManufacturer() {
        return MANUFACTURER;
    }

    public static String getPhoneModel() {
        return MODEL;
    }

    public static int getPhoneType() {
        try {
            if (MANUFACTURER.contains("huawei")) {
                return HUAWEI;
            } else if (MANUFACTURER.contains("xiaomi")) {
                return XIAOMI;
            } else if (MANUFACTURER.contains("oppo")) {
                return OPPO;
            } else if (MANUFACTURER.contains("vivo")) {
                return VIVO;
            } else if (MANUFACTURER.contains("meizu")) {
                return MEIZU;
            } else if (MANUFACTURER.contains("samsung")) {
                return SANXING;
            } else if (MANUFACTURER.contains("letv") || MANUFACTURER.contains("lemobile")) {
                return LESHI;
            } else if (MANUFACTURER.contains("smartisan")) {
                return SMARTISAN;
            } else if (MANUFACTURER.contains("lg")) {
                return LG;
            } else if (MANUFACTURER.contains("zte")) {
                return ZTE;
            } else if (MANUFACTURER.contains("yulong")) {
                return KUPAI;
            } else if (MANUFACTURER.contains("lenovo")) {
                return LENOVO;
            } else if (MANUFACTURER.contains("sony")) {
                return SONY;
            } else if (MANUFACTURER.contains("oneplus")) {
                return ONEPLUS;
            } else {
                return 0;
            }
        } catch (Exception e) {
            return 0;
        }
    }

    public static String getRom(String str, String str2) {
        try {
            Object get = Class.forName("android.os.SystemProperties").getMethod("get", new Class[]{String.class, String.class}).invoke(null, new Object[]{str, str2});
            if (TextUtils.equals(get.toString(), "unkonw")) {
                Properties property = getProperty();
                get = property.get(str);
                if (get == null) {
                    return "unkonw";
                }
            }
            return (String) get;
        } catch (Exception unused) {
            return "unkonw";
        }
    }

    public static Properties getProperty() {
        Properties properties = new Properties();
        try {
            Process p = Runtime.getRuntime().exec("getprop");
            properties.load(p.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    public static String getMiuiVersion() {

        String propName = "ro.miui.ui.version.name";
        String line;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(
                    new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return line;
    }
}
