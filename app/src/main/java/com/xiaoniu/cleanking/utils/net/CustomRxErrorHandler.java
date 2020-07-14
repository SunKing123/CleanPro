package com.xiaoniu.cleanking.utils.net;

import android.content.Context;

import com.xiaoniu.cleanking.app.ResponseErrorListenerImpl;

import me.jessyan.rxerrorhandler.core.RxErrorHandler;

/**
 * Created by zhaoyingtao
 * Date: 2020/7/14
 * Describe:
 */
public class CustomRxErrorHandler {
    RxErrorHandler errorHandler;

    public CustomRxErrorHandler(Context context) {
        errorHandler = RxErrorHandler.builder().with(context).responseErrorListener(new ResponseErrorListenerImpl()).build();
    }

    public RxErrorHandler build() {
        return errorHandler;
    }
}
