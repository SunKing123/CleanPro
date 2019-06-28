package com.installment.mall.base;

/**
 * 网络访问回调
 * Created by tie on 2017/2/16.
 */

public interface Callback {

    void onResponse(String response);

    void onErrorResponse();
}
