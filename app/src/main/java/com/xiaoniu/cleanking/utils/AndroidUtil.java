package com.xiaoniu.cleanking.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.TypefaceSpan;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.google.gson.Gson;
import com.ishumei.smantifraud.SmAntiFraud;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.xiaoniu.cleanking.BuildConfig;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.app.AppLifecyclesImpl;
import com.xiaoniu.cleanking.constant.Constant;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.utils.update.MmkvUtil;
import com.xiaoniu.cleanking.utils.user.UserHelper;
import com.xiaoniu.common.utils.AppUtils;
import com.xiaoniu.common.utils.ChannelUtil;
import com.xiaoniu.common.utils.ContextUtils;
import com.xiaoniu.common.utils.DeviceUtils;
import com.xiaoniu.common.utils.ToastUtils;
import com.xiaoniu.statistic.NiuDataAPI;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
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
        //修复友盟bug，添加【try catch】 参考：https://www.jianshu.com/p/2f25259a5a15
        try {
            final PackageManager packageManager = AppApplication.getInstance().getPackageManager();
            List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
            List<String> pName = new ArrayList<>();
            if (pinfo != null) {
                for (int i = 0; i < pinfo.size(); i++) {
                    String pn = pinfo.get(i).packageName;
                    pName.add(pn);
                }
            }
            return pName.contains(packageName);
        } catch (Exception e) {

        }

        return false;
    }

    /**
     * 判断手机是否安装微信的两种方法：
     * 1.通过判断手机上安装的应用的包名集合中是否包含微信的包名判断
     * 2.通过集成微信sdk后，使用sdk提供的api进行判断
     * 经测试，单独使用这两种方法中的其中一种都会出现不能适配所有机型的情况。
     *
     * @param mContext
     * @return
     */
    public static boolean isInstallWeiXin(Context mContext) {
        IWXAPI wxApi = WXAPIFactory.createWXAPI(mContext, Constant.WEICHAT_APPID);
        //先用微信API判断一次，再用包名判断一次
        if (wxApi.isWXAppInstalled()) {
            return true;
        } else {
            return isAppInstalled(SpCacheConfig.CHAT_PACKAGE);
        }
    }

    public static boolean isInstallQQ(Context mContext) {
        UMShareAPI umShareAPI = UMShareAPI.get(mContext);
        if (umShareAPI.isInstall((Activity) mContext, SHARE_MEDIA.QQ)) {
            return true;
        } else {
            return isAppInstalled(SpCacheConfig.QQ_PACKAGE);
        }
    }

    //调用第三方程序uri版本兼容
    public static void fileUri(Context context, Intent intent, File file, String type) {
        //判断是否是AndroidN以及更高的版本
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".updatefileprovider", file);
                intent.setDataAndType(contentUri, type);
            } else {
                intent.setDataAndType(Uri.fromFile(file), type);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
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


    public static boolean haveLiuhai = false;

    /**
     * 获取H5后面拼接的xn_data字符串
     *
     * @return
     */
    public static String getXnData() {
        long timeMillis = System.currentTimeMillis();
        Map<String, String> map = new HashMap<>();
        map.put("request-id", UUID.randomUUID().toString());
        map.put("haveLiuhai", haveLiuhai ? "1" : "0");
        map.put("language", "cn");
        //1：android、2：iOS、3：PC、4、H5、5：wechat
        map.put("request-agent", "1");
        map.put("device-id", AndroidUtil.getDeviceID());
        //0：android、1：iOS
        map.put("os-version", "0");
        map.put("sdk-version", AndroidUtil.getAndroidSDKVersion() + "");
        map.put("phone-model", AndroidUtil.getSystemModel());
        map.put("market", ChannelUtil.getChannel());
        map.put("app-version", AppUtils.getVersionName(ContextUtils.getContext(), ContextUtils.getContext().getPackageName()));
        map.put("app-name", "gj_clean");
//        map.put("app-id", AppConfig.API_APPID);//todo:zzh
        map.put("timestamp", timeMillis + "");
//        map.put("sign", hashByHmacSHA256(AppConfig.API_APPID + timeMillis, AppConfig.API_APPSECRET));//todo:zz
        map.put("customer-id", UserHelper.init().getCustomerId());
        map.put("access-token", UserHelper.init().getToken());
        map.put("phone-number", UserHelper.init().getPhoneNum());
        map.put("uuid", NiuDataAPI.getUUID());
        if (UserHelper.init().isLogin()) {
            if (UserHelper.init().isWxLogin()) {
                map.put("userType", "1");
                map.put("uid", UserHelper.init().getOpenID());
            } else {
                map.put("userType", "2");
            }
        } else {
            map.put("userType", "其他账号");
        }
        String deviceId = SmAntiFraud.getDeviceId();
        map.put("sm-deviceid", deviceId);
        map.put("sdk-uid", NiuDataAPI.getUUID());
        String xnData = android.util.Base64.encodeToString(new Gson().toJson(map).getBytes(), android.util.Base64.NO_WRAP);
        return xnData;
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
        maps.put("access-token", UserHelper.init().getToken());
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

    //防连续点击
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 800) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    //防连续点击
    public static boolean isFastDoubleBtnClick(long durastime) {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < durastime) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    /**
     * 获取手机剩余电量
     *
     * @param context
     * @return 获取手机电量手机系统大于5.0才有该方法，5.0之下返回小于70让首页超强省电展示危急状态
     */
    public static int getElectricityNum(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            BatteryManager batteryManager = (BatteryManager) context.getSystemService(BATTERY_SERVICE);
            return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        }
        return 65;
    }

    /**
     * 获取
     *
     * @param context
     * @return
     */
    public static ArrayList<FirstJunkInfo> getAllReadyInstallApps(Context context) {
        ArrayList<FirstJunkInfo> aboveListInfo = new ArrayList<>();
        List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            //判断是否系统应用
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) { //非系统应用
                if (packageInfo.applicationInfo.packageName.equals(BuildConfig.APPLICATION_ID)) {
                    continue;
                }
                FirstJunkInfo tmpInfo = new FirstJunkInfo();
                tmpInfo.setAppName(packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString());
                tmpInfo.setGarbageIcon(packageInfo.applicationInfo.loadIcon(context.getPackageManager()));
                tmpInfo.setAppPackageName(packageInfo.applicationInfo.packageName);
                tmpInfo.setAppProcessName(packageInfo.applicationInfo.processName);
                aboveListInfo.add(tmpInfo);
            }
        }
        return aboveListInfo;
    }

    /**
     * 获取
     *
     * @param context
     * @return
     */
    public static ArrayList<FirstJunkInfo> getSystemInstallApps(Context context, int maxCount) {
        ArrayList<FirstJunkInfo> aboveListInfo = new ArrayList<>();
        List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            //判断是否系统应用
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) { //系统应用
                if ((i + 1) > maxCount) {
                    break;
                }
                FirstJunkInfo tmpInfo = new FirstJunkInfo();
                tmpInfo.setAppName(packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString());
                tmpInfo.setGarbageIcon(packageInfo.applicationInfo.loadIcon(context.getPackageManager()));
                tmpInfo.setAppPackageName(packageInfo.applicationInfo.packageName);
                tmpInfo.setAppProcessName(packageInfo.applicationInfo.processName);
                tmpInfo.setAllchecked(true);
                tmpInfo.setTotalSize(getCounterfeitMemorySize());
                aboveListInfo.add(tmpInfo);
            }
        }
        return aboveListInfo;
    }

    public static Long getCounterfeitMemorySize() {
        int un = 80886656;
        return (long) (Math.random() * un) + un;
    }

    /**
     * 取本地已安装app信息
     *
     * @param context
     * @param maxCount 最多取几个
     * @return
     */
    public static ArrayList<FirstJunkInfo> getRandomMaxCountInstallApp(Context context, int maxCount) {
        ArrayList<FirstJunkInfo> list = getAllReadyInstallApps(context);
        Collections.shuffle(list);
        int count = Math.min(list.size(), maxCount);
        if (count < 0) {
            count = 0;
        }
        return new ArrayList<>(list.subList(0, count));
    }

    public static SpannableString inertColorText(String content, int startColorIndex, int endColorIndex, int color) {
        SpannableString spanString = new SpannableString(content);
        //add color
        ForegroundColorSpan span = new ForegroundColorSpan(color);
        spanString.setSpan(span, startColorIndex, endColorIndex, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        //add bold style
        TypefaceSpan typefaceSpan = new TypefaceSpan("default-bold");
        spanString.setSpan(typefaceSpan, startColorIndex, endColorIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //zoom size
        RelativeSizeSpan relativeSizeSpan = new RelativeSizeSpan(1.1f);
        spanString.setSpan(relativeSizeSpan, startColorIndex, endColorIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spanString;
    }

    public static SpannableString zoomText(String content, float rate, int startColorIndex, int endColorIndex) {
        SpannableString spanString = new SpannableString(content);
        TypefaceSpan typefaceSpan = new TypefaceSpan("default-bold");
        spanString.setSpan(typefaceSpan, startColorIndex, endColorIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        RelativeSizeSpan relativeSizeSpan = new RelativeSizeSpan(rate);
        spanString.setSpan(relativeSizeSpan, startColorIndex, endColorIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanString;
    }

    //数美接入
    private static String sDeviceID = "";
    //数美接入

    /**
     * 热云方法[暂时只做IMEI调用时使用]
     *
     * @return
     */
    @SuppressLint({"MissingPermission"})
    public static String getDeviceID() {
        Context context = AppApplication.getInstance();
        String mkId = MmkvUtil.getString(PositionId.HEAD_DEVICE_ID, "");
        if (!TextUtils.isEmpty(mkId) && !"unknown".equals(mkId)) {
            sDeviceID = mkId;
            return mkId;
        } else {
            try {
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                if (Build.VERSION.SDK_INT >= 26) {
                    sDeviceID = tm.getImei();
                } else if (Build.VERSION.SDK_INT >= 23) {
                    sDeviceID = tm.getDeviceId(0);
                } else {
                    sDeviceID = tm.getDeviceId();
                }
                setDeviceID();
                return sDeviceID;
            } catch (Exception var2) {
                setDeviceID();
                return sDeviceID;
            } finally {
                if (!TextUtils.isEmpty(sDeviceID) && !"unknown".equals(sDeviceID)) {
                    MmkvUtil.saveString(PositionId.HEAD_DEVICE_ID, sDeviceID);
                }
            }
        }
    }

    private static void setDeviceID() {
        if (TextUtils.isEmpty(sDeviceID)) {
            if (Build.VERSION.SDK_INT >= 29 && AppLifecyclesImpl.isSupportOaid()) {
                sDeviceID = AppLifecyclesImpl.getOaid();
                return;
            }
            sDeviceID = getAndroidId(AppApplication.getInstance());
        }
    }

    public static String getAndroidId(Context context) {
        String androidId = "unknown";
        try {
            androidId = Settings.Secure.getString(context.getContentResolver(),
                    "android_id");
        } catch (Exception e) {
        }
        return androidId != null && androidId.length() != 0 ? androidId : "unknown";
    }


    /**
     * 获取sdk版本
     *
     * @return
     */
    public static int getAndroidSDKVersion() {
        return Build.VERSION.SDK_INT;
    }


    /**
     * 获取手机型号
     *
     * @return 手机型号
     */
    public static String getSystemModel() {
        return Build.MODEL;
    }


}
