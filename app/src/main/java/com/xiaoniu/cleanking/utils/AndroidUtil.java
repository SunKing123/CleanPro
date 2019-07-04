package com.xiaoniu.cleanking.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.xiaoniu.cleanking.BuildConfig;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.utils.encypt.Base64;
import com.xiaoniu.cleanking.utils.prefs.ImplPreferencesHelper;
import com.leon.channel.helper.ChannelReaderUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.MediaType;
import okhttp3.RequestBody;


/**
 * android系统相关常用操作
 * 说明：提供一些android系统常用操作：如系统版本，图片操作等
 */

public class AndroidUtil {
    private static String mac = "";

    /**
     * 获取sdk版本
     *
     * @return
     */
    public static int getAndroidSDKVersion() {
        return android.os.Build.VERSION.SDK_INT;
    }
    public static int getVersionCode() {
        return BuildConfig.VERSION_CODE;
    }
    /**
     * 返回当前程序版本名
     */
    public static String getAppVersionName(Context context) {
        String versionName = "0.0.0";
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            // 这里的context.getPackageName()可以换成你要查看的程序的包名
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            //Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }
    /**
     * 返回当前程序版本名
     */
    public static String getAppVersionName() {
        String versionName = "0.0.0";
        try {
            // ---get the package info---
            PackageManager pm = AppApplication.getInstance().getPackageManager();
            // 这里的context.getPackageName()可以换成你要查看的程序的包名
            PackageInfo pi = pm.getPackageInfo(AppApplication.getInstance().getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            //Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }

    /**
     * 获取屏幕宽
     *
     * @return
     */
    public static int getScreenWidth() {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) AppApplication.getInstance().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    /**
     * 屏幕高度
     *
     * @return
     */
    public static int getScreenHeight() {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) AppApplication.getInstance().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    /**
     * app是否被安装
     *
     * @param packageName
     * @return
     */
    public static boolean isAppInstalled(String packageName) {
        final PackageManager packageManager = AppApplication.getInstance().getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        List<String> pName = new ArrayList<String>();
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                pName.add(pn);
            }
        }
        return pName.contains(packageName);
    }

    private static String udid = "";

    /**
     * 获取设备号
     *
     * @param con
     * @return
     */
    public final static String KEY_DEVICE_ID = "UserDeviceId";

    public static String getUdid() {
        if (!checkUdidValid()) {
            SharedPreferences sharepre = PreferenceManager.getDefaultSharedPreferences(AppApplication.getInstance());
            udid = sharepre.getString(KEY_DEVICE_ID, "");
            if (!checkUdidValid()) {
                try {
                    TelephonyManager tm = (TelephonyManager) AppApplication.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
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
                            udid = "" + Settings.Secure.getString(AppApplication.getInstance().getContentResolver(), Settings.Secure.ANDROID_ID);
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

    /**
     * check whether be empty/null or not
     *
     * @param string
     * @return
     */
    public static boolean isEffective(String string) {
        return !((string == null) || ("".equals(string)) || (" ".equals(string))
                || ("null".equals(string)) || ("\n".equals(string)));
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

    private synchronized static void getRandomUdidFromFile() {
        File installation = new File(AppApplication.getInstance().getFilesDir(), "INSTALLATION");
        try {
            if (!installation.exists()) {
                writeInstallationFile(installation);
            }
            udid = readInstallationFile(installation);
        } catch (Exception e) {
            udid = "";
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

    /**
     * 获取市场渠道名
     *
     * @return channel
     */
    public static String getMarketId() {
        String channel = "";
        try {
            channel = ChannelReaderUtil.getChannel(AppApplication.getInstance());
        } catch (Exception e) {
        }
        return !TextUtils.isEmpty(channel) ? channel : "huawei";
    }

    /**
     * 设置edittext 禁止输入表情
     */
    public static InputFilter getEmojiFilter() {
        InputFilter emojiFilter = new InputFilter() {
            Pattern pattern = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|" +
                    "[\u2600-\u27ff]", Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                Matcher matcher = pattern.matcher(source);
                if (matcher.find()) {
                    ToastUtils.showShort("暂不支持表情输入!");
                    return "";
                }
                return null;
            }
        };
        return emojiFilter;
    }


    static ImplPreferencesHelper implPreferencesHelper = new ImplPreferencesHelper();

    public static String getToken() {
        return implPreferencesHelper.getToken();
    }

    public static String getCustomerId() {
        return implPreferencesHelper.getCustomerId();
    }


    public static String getPhoneNum() {
        return implPreferencesHelper.getPhoneNum();
    }

    /**
     * 获取H5后面拼接的xn_data字符串
     *
     * @return
     */
    public static String getXnData() {

        long timeMillis = System.currentTimeMillis();
        Map<String, String> map = new HashMap<>();
        map.put("request-id", UUID.randomUUID().toString());
        //1：android、2：iOS、3：PC、4、H5、5：wechat
        map.put("request-agent", "1");
        map.put("device-id", AndroidUtil.getUdid());
        //0：android、1：iOS
        map.put("os-version", "0");
        map.put("sdk-version", AndroidUtil.getAndroidSDKVersion() + "");
        map.put("phone-model", AndroidUtil.getSystemModel());
        map.put("market", AndroidUtil.getMarketId());
        map.put("app-version", AndroidUtil.getAppVersionName());
        map.put("app-name", AndroidUtil.getAppNum());
        map.put("app-id", BuildConfig.API_APPID);
        map.put("timestamp", timeMillis + "");
        map.put("sign", hashByHmacSHA256(BuildConfig.API_APPID + timeMillis, BuildConfig.API_APPSECRET));
        map.put("customer-id", AndroidUtil.getCustomerId());
        map.put("access-token", AndroidUtil.getToken());
        map.put("phone-number", AndroidUtil.getPhoneNum());
        return Base64.encodeToString(new Gson().toJson(map).getBytes(), Base64.NO_WRAP);
    }

    /**
     * 通过HmacSHA256进行哈希
     *
     * @param stringToSign
     * @param appSecret
     * @return
     */
    public static String hashByHmacSHA256(String stringToSign, String appSecret) {
        String signature;
        try {
            Mac hmacSha256 = Mac.getInstance("HmacSHA256");
            byte[] keyBytes = appSecret.getBytes(Charset.forName("UTF-8"));
            hmacSha256.init(new SecretKeySpec(keyBytes, 0, keyBytes.length, "HmacSHA256"));
            byte[] data = hmacSha256.doFinal(stringToSign.getBytes(Charset.forName("UTF-8")));
            StringBuffer buffer = new StringBuffer();
            for (byte item : data) {
                buffer.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
            }
            signature = buffer.toString().toUpperCase();
        } catch (Exception e) {
            throw new RuntimeException("通过HmacSHA256进行哈希出现异常：" + e.getMessage());
        }
        return signature;
    }

    public static Map<String, String> getHeader() {
        Map<String, String> maps = new HashMap<>();

        maps.put("trace-id", UUID.randomUUID().toString());
        maps.put("request-time", System.currentTimeMillis() + "");
        maps.put("device-type", "android");
        maps.put("os-version", getAndroidSDKVersion() + "");
        maps.put("device-id", getUdid());
        maps.put("app-version", getAppVersionName());
        maps.put("market", getMarketId());
        maps.put("phone-models", getModel());
        maps.put("access-token", getToken());
        return maps;
    }

    /**
     * 获取设备型号
     *
     * @return 设备型号
     */
    public static String getModel() {
        String model = Build.MODEL;
        if (model != null) {
            model = model.trim().replaceAll("\\s*", "");
        } else {
            model = "";
        }
        return model;
    }

    /**
     * 获取手机型号
     *
     * @return 手机型号
     */
    public static String getSystemModel() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取appNum
     */
    public static String getAppNum() {
        return AppApplication.getInstance().getString(R.string.app_num);
    }

    /**
     * 将请求参数转换成RequestBody
     *
     * @param map
     * @return
     */
    public static RequestBody getRequestBody(Map<String, Object> map) {
        Gson gson = new Gson();
        String json = gson.toJson(map);
        return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
    }

    public static String getClientId() {
        return implPreferencesHelper.getClientId();
    }
}
