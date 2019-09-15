package com.xiaoniu.common.http.model;

import android.util.Log;

import retrofit2.HttpException;


/**
 * 统一处理了API异常错误EHttp
 */
public class ApiException extends Exception {
    private int code = -1;
    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApiException(int code, String message) {
        super(message);
        this.code = code;
    }
    public int getCode() {
        return code;
    }

    public static Throwable handleException(Throwable e) {
        e.printStackTrace();//打印错误信息
        Log.i("123", e.toString());
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            //统一处理，或者转化错误等

        }
        return e;
    }

}