package com.xiaoniu.cleanking.app.injector.module;

import com.xiaoniu.cleanking.BuildConfig;
import com.xiaoniu.cleanking.utils.AndroidUtil;
import com.google.gson.Gson;

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
 */
public class RequestParamInterceptor implements Interceptor {
    private static final String TAG = "RequestParamInterceptor";

    @Override
    public Response intercept(Chain chain) throws IOException {

        long timeMillis = System.currentTimeMillis();
        Request original = chain.request();
        Request.Builder requestBuilder = original.newBuilder();
//        requestBuilder.addHeader("request-id", UUID.randomUUID().toString());
//        requestBuilder.addHeader("request-agent", "1");//1：android、2：iOS、3：PC、4、H5、5：wechat
//        requestBuilder.addHeader("device-id", AndroidUtil.getUdid());
//        requestBuilder.addHeader("os-version", "0");//0：android、1：iOS
//        requestBuilder.addHeader("sdk-version", AndroidUtil.getAndroidSDKVersion()+"");
//        requestBuilder.addHeader("phone-model", AndroidUtil.getSystemModel());
//        requestBuilder.addHeader("market", AndroidUtil.getMarketId());
//        requestBuilder.addHeader("app-version", AndroidUtil.getAppVersionName());
//        requestBuilder.addHeader("app-name", AndroidUtil.getAppNum());
//        requestBuilder.addHeader("app-id", BuildConfig.API_APPID);
//        requestBuilder.addHeader("timestamp", timeMillis +"");
//        requestBuilder.addHeader("sign", hashByHmacSHA256(BuildConfig.API_APPID + timeMillis, BuildConfig.API_APPSECRET));
//        requestBuilder.addHeader("customer-id", AndroidUtil.getCustomerId());
//        requestBuilder.addHeader("access-token", AndroidUtil.getToken());



//        //请求定制：添加请求头
//        Request.Builder requestBuilder = original.newBuilder();
//        CommonParam commonParam = new CommonParam();
//        //Token
//        commonParam.setToken(AndroidUtil.getToken());
//        //phone
//        commonParam.setPhone(AndroidUtil.getPhoneNum());
//        //requestId
//        commonParam.setRequestId(UUID.randomUUID().toString());
//        //cid
//        commonParam.setCid(AndroidUtil.getCustomerId());
//        //osversion
//        commonParam.setOsversion("android");
//        commonParam.setAppName("4");
//        //appversion
//        commonParam.setAppversion(AndroidUtil.getAppVersionName(AppApplication.getInstance()));
//        //deviceId
//        commonParam.setDeviceId(AndroidUtil.getUdid(AppApplication.getInstance()));
//        //sdkversion
//        commonParam.setSdkversion(AndroidUtil.getAndroidSDKVersion() + "");
//        //市场
//        commonParam.setMarket(AndroidUtil.getMarketId(AppApplication.getInstance()));

//        String json = new Gson().toJson(commonParam);
        Map<String, Object> mapHeader = new HashMap<>();
        mapHeader.put("Ip", "172.16.88.23");
        mapHeader.put("appVersion", AndroidUtil.getAppVersionName());
        mapHeader.put("channel", AndroidUtil.getMarketId());
        mapHeader.put("deviceId", AndroidUtil.getUdid());
        mapHeader.put("gtId", "419441fd0260490413e73830c3326298");

        mapHeader.put("imei", "357755073075671");
        mapHeader.put("os", "android");
        mapHeader.put("phoneType", AndroidUtil.getSystemModel());
        mapHeader.put("phoneVersion", "Android"+AndroidUtil.getAndroidSDKVersion()+"");
        mapHeader.put("sessionId", "");
        mapHeader.put("sign", "7cdd4afd6a0bd76080a973ada0566598");
        mapHeader.put("talkVersion", "");
        mapHeader.put("timestamp", "1562234756542");
        mapHeader.put("uid", "");
        requestBuilder.addHeader("UserAgent", new Gson().toJson(mapHeader));
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
        return chain.proceed(request);
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

