package com.xiaoniu.cleanking.app.injector.interceptor;

import android.os.Build;

import com.google.gson.Gson;
import com.ishumei.smantifraud.SmAntiFraud;
import com.xiaoniu.cleanking.BuildConfig;
import com.xiaoniu.cleanking.utils.AndroidUtil;
import com.xiaoniu.cleanking.utils.net.RequestApiInfoLog;
import com.xiaoniu.cleanking.utils.user.UserHelper;
import com.xiaoniu.common.utils.AppUtils;
import com.xiaoniu.common.utils.ChannelUtil;
import com.xiaoniu.common.utils.ContextUtils;
import com.xiaoniu.common.utils.DeviceUtils;
import com.xiaoniu.common.utils.SystemUtils;
import com.xiaoniu.statistic.NiuDataAPI;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by tie on 2017/5/17.
 * 网络请求拦截器；
 */
public class RequestParamInterceptor implements Interceptor {
    private static final String TAG = "RequestParamInterceptor";

    @Override
    public Response intercept(Chain chain) throws IOException {

        long timeMillis = System.currentTimeMillis();
        Request original = chain.request();
        Request.Builder requestBuilder = original.newBuilder();
        Map<String, Object> mapHeader = new HashMap<>();
        mapHeader.put("Ip", "172.16.88.23");
        mapHeader.put("appVersion", AppUtils.getVersionName(ContextUtils.getContext(), ContextUtils.getContext().getPackageName()));
        mapHeader.put("channel", ChannelUtil.getChannel());
        mapHeader.put("deviceId", DeviceUtils.getUdid());
        mapHeader.put("imei", "357755073075671");
        mapHeader.put("os", "android");
        mapHeader.put("phoneType", DeviceUtils.getModel());
        mapHeader.put("phoneVersion", "Android" + DeviceUtils.getSDKVersion() + "");
        mapHeader.put("sign", "7cdd4afd6a0bd76080a973ada0566598");
        mapHeader.put("timestamp", System.currentTimeMillis());
        mapHeader.put("versionCode", AppUtils.getVersionCode(ContextUtils.getContext(), ContextUtils.getContext().getPackageName()));
        mapHeader.put("appName", "gj_clean");
        mapHeader.put("bid", (int) (99 * Math.random()));
        mapHeader.put("activate-timestamp", String.valueOf(SystemUtils.getAppLastUpdateTime(ContextUtils.getContext())));//当前版本激活时间戳
        requestBuilder.addHeader("UserAgent", new Gson().toJson(mapHeader));

        //爱步行header参数；
        requestBuilder.addHeader("request-id", UUID.randomUUID().toString());
        requestBuilder.addHeader("language", "cn");
        requestBuilder.addHeader("request-agent", "1");//1：android、2：iOS、3：PC、4、H5、5：wechat
        requestBuilder.addHeader("device-id", AndroidUtil.getDeviceID());
        requestBuilder.addHeader("os-version", "0");//0：android、1：iOS
        requestBuilder.addHeader("sdk-version", AndroidUtil.getAndroidSDKVersion() + "");
        requestBuilder.addHeader("phone-model", Build.MODEL);
        requestBuilder.addHeader("market", ChannelUtil.getChannel());
        requestBuilder.addHeader("app-version",AppUtils.getVersionName(ContextUtils.getContext(), ContextUtils.getContext().getPackageName()));
        requestBuilder.addHeader("app-name", "gj_clean");
        requestBuilder.addHeader("app-id", BuildConfig.API_APPID);
//        requestBuilder.addHeader("gt-id", TextUtils.isEmpty(pushId)?"":pushId)//todo:zzh  暂时未接入个推
        requestBuilder.addHeader("timestamp", timeMillis + "");
        requestBuilder.addHeader("sign", hashByHmacSHA256(BuildConfig.API_APPID + timeMillis, BuildConfig.API_APPSECRET));
        requestBuilder.addHeader("customer-id", UserHelper.init().getCustomerId()); //登录的信息
        requestBuilder.addHeader("access-token", UserHelper.init().getToken());
        requestBuilder.addHeader("sm-deviceid", SmAntiFraud.getDeviceId());  //数美参数DeviceId（）
        requestBuilder.addHeader("gps-lng", "");
        requestBuilder.addHeader("gps-lat", "");
        requestBuilder.addHeader("sdk-uid", NiuDataAPI.getUUID());
        requestBuilder.addHeader("activate-timestamp", String.valueOf(SystemUtils.getAppLastUpdateTime(ContextUtils.getContext())));//当前版本激活时间戳


        if (original.body() instanceof FormBody) {

            FormBody oidFormBody = (FormBody) original.body();
            Map<String, Object> map = new HashMap<>();
            for (int i = 0; i < oidFormBody.size(); i++) {
                map.put(oidFormBody.encodedName(i), oidFormBody.value(i));
            }
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), new Gson().toJson(map));

            requestBuilder.method(original.method(), requestBody);
        }

//        //请求体定制：统一添加参数
//        if (original.body() instanceof FormBody) {
//            FormBody.Builder newFormBody = new FormBody.Builder();
//            FormBody oidFormBody = (FormBody) original.body();
//            for (int i = 0; i < oidFormBody.size(); i++) {
//                newFormBody.addEncoded(oidFormBody.encodedName(i), oidFormBody.encodedValue(i));
//            }
//
//            newFormBody.addEncoded("xn_data", json);
//
//            requestBuilder.method(original.method(), newFormBody.build());
//        } else if (original.body() instanceof MultipartBody) {
//
//        } else if (original.body() != null && original.body().contentType() != null &&
//                MediaType.parse("application/json").toString().equals(original.body().contentType().toString())) {
//
//        } else if ("GET".equals(original.method())) {
//        } else {
//            FormBody.Builder newFormBody = new FormBody.Builder();
//            newFormBody.addEncoded("xn_data", json);
//            requestBuilder.method(original.method(), newFormBody.build());
//        }

        Request request = requestBuilder.build();
        Response response = chain.proceed(request);
        if (BuildConfig.DEBUG){
            //打印请求相关信息
            RequestApiInfoLog.collectionLog(request,response);
        }
        return response;
    }

    /**
     * 通过HmacSHA256进行哈希
     *
     * @param stringToSign
     * @param appSecret
     * @return
     */
    public String hashByHmacSHA256(String stringToSign, String appSecret) {
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
}

