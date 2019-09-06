package com.xiaoniu.common.http.intercepter;


import java.io.IOException;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 配置请求头部信息

 */
public class HeadersInterceptor implements Interceptor {
    private Map<String, String> commonHeaders;
    public HeadersInterceptor(Map<String, String> commonHeaders) {
        this.commonHeaders = commonHeaders;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        if (commonHeaders != null && !commonHeaders.isEmpty()) {
            Request.Builder builder = original.newBuilder();
            for (String key : commonHeaders.keySet()) {
                builder.addHeader(key, commonHeaders.get(key));
            }
            original = builder.build();
        }
        return chain.proceed(original);
    }
}
