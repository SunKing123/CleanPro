package com.xiaoniu.cleanking.utils.net;

import android.util.Log;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * Created by zhaoyingtao
 * Date: 2020/7/7
 * Describe: 直接打印app请求的所有信息
 */
public class RequestApiInfoLog {
    /**
     * 日志收集
     *
     * @param request
     * @param response
     * @throws IOException
     */
    public static void collectionLog(Request request, Response response) throws IOException {
        /**获取请求接口前的数据 */
        //the request url
        String url = request.url().toString();
        //the request method
        String method = request.method();
        //the request body
        RequestBody requestBody = request.body();
        String postRequstBody = "不是post请求";
        if (requestBody != null) {//post请求会有请求体
            StringBuilder sb = new StringBuilder("Request Body [");
            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);
            Charset charset = Charset.forName("UTF-8");
            MediaType contentType = requestBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(charset);
            }
            if (isPlaintext(buffer)) {
                sb.append(buffer.readString(charset));
                sb.append(" (Content-Type = ").append(String.valueOf(contentType)).append(",")
                        .append(requestBody.contentLength()).append("-byte body)");
            } else {
                sb.append(" (Content-Type = ").append(String.valueOf(contentType))
                        .append(",binary ").append(requestBody.contentLength()).append("-byte body omitted)");
            }
            sb.append("]");
            postRequstBody = sb.toString();
        }

        /**获取请求接口后的数据 ===剔除掉下载的大数据，否则会报oom的*/
        String bodyString = url;
        if (!url.endsWith("mp4") && !url.endsWith("mp3") && !url.endsWith("apk") && !url.endsWith("m3u8")) {
            ResponseBody body = response.body();
            BufferedSource source = body.source();
            source.request(Long.MAX_VALUE); // Buffer the entire body.
            Buffer buffer = source.buffer();
            Charset charset = Charset.defaultCharset();
            MediaType contentType = body.contentType();
            if (contentType != null) {
                charset = contentType.charset(charset);
                bodyString = buffer.clone().readString(charset);
            }
        }
        log("url---------", request.url() + "");
        log("header---------", request.headers().toString());
        log("post参数---------", postRequstBody);
        log("请求方式---------", method);
        log("请求后的数据==", bodyString + "\n\n");
    }

    private static boolean isPlaintext(Buffer buffer) {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false; // Truncated UTF-8 sequence.
        }
    }

    /**
     * 打印请求链接、请求头、post请求参数、返回结果
     *
     * @param remind
     * @param logData
     */
    private static void log(String remind, String logData) {
        Log.e("_api", remind + logData);
    }
}
