package com.xiaoniu.cleanking.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.xiaoniu.cleanking.BuildConfig;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.utils.encypt.Base64;
import com.xiaoniu.cleanking.utils.prefs.ImplPreferencesHelper;
import com.xiaoniu.common.utils.DeviceUtils;
import com.xiaoniu.common.utils.ToastUtils;

import java.io.File;
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

import static android.content.Context.BATTERY_SERVICE;


/**
 * android系统相关常用操作
 * 说明：提供一些android系统常用操作：如系统版本，图片操作等
 */

public class AndroidUtil {
    private static String mac = "";
    private static String sImei;

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


    //调用第三方程序uri版本兼容
    public static void fileUri(Context context, Intent intent, File file, String type) {
        //判断是否是AndroidN以及更高的版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".updatefileprovider", file);
            intent.setDataAndType(contentUri, type);
        } else {
            intent.setDataAndType(Uri.fromFile(file), type);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
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
        map.put("device-id", "");
        //0：android、1：iOS
        map.put("os-version", "0");
        map.put("sdk-version", DeviceUtils.getSDKVersion() + "");
//        map.put("phone-model", AndroidUtil.getModel());
        map.put("market", "");
        map.put("app-version", "");
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
     * 打开文件
     *
     * @param context
     * @param path
     */
    public static void openFileSafe(Context context, String path) {

        String format = path.substring(path.lastIndexOf(".") + 1);
        try {
            if (TextUtils.equals("doc", format) || TextUtils.equals("docx", format)) {
                context.startActivity(IntentDocumentUtil.getWordFileIntent(path));
            } else if (TextUtils.equals("xls", format) || TextUtils.equals("xlsx", format)) {
                context.startActivity(IntentDocumentUtil.getExcelFileIntent(path));
            } else if (TextUtils.equals("zip", format) || TextUtils.equals("rar", format)) {
                context.startActivity(IntentDocumentUtil.getZipRarFileIntent(path));
            } else if (TextUtils.equals("pdf", format) || TextUtils.equals("PDF", format)) {
                context.startActivity(IntentDocumentUtil.getPdfFileIntent(path));
            } else if (TextUtils.equals("ppt", format) || TextUtils.equals("PPT", format)) {
                context.startActivity(IntentDocumentUtil.getPptFileIntent(path));
            } else if (TextUtils.equals("txt", format) || TextUtils.equals("text", format)) {
                context.startActivity(IntentDocumentUtil.getTextFileIntent(path, false));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "没有打开相关应用的软件...", Toast.LENGTH_SHORT).show();
        }

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
        maps.put("os-version", DeviceUtils.getSDKVersion() + "");
//        maps.put("device-id", getUdid());
//        maps.put("app-version", getAppVersionName());
//        maps.put("market", getMarketId());
        maps.put("phone-models", DeviceUtils.getModel());
        maps.put("access-token", getToken());
        return maps;
    }

    /**
     * 获取appNum
     */
    public static String getAppNum() {
        return AppApplication.getInstance().getString(R.string.app_num);
    }

    public static boolean isLegalPackageName(String name) {
        if (TextUtils.isEmpty(name)) {
            return false;
        }

        String regex = "[a-zA-Z][a-zA-Z0-9_]*(\\.[a-zA-Z][a-zA-Z0-9_]*)+";
        return name.matches(regex);
    }

    //防连续点击
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

    /**
     * 获取手机剩余电量
     *
     * @param context
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static int getElectricityNum(Context context) {
        BatteryManager batteryManager = (BatteryManager) context.getSystemService(BATTERY_SERVICE);
        return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
    }
}
