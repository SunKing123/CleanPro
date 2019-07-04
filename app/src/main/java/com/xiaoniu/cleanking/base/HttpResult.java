package com.xiaoniu.cleanking.base;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import com.xiaoniu.cleanking.app.Constant;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * 请求结果封装
 * Created by tie on 2017/2/20.
 */

public abstract class HttpResult<T extends BaseEntity> implements Callback {


    private Class<? extends BaseEntity> T;
    private boolean tag;

    private Fragment mFragment;
    private AppCompatActivity mAppCompatActivity;

    public HttpResult(Class<? extends BaseEntity> entity) {
        T = entity;
    }

    @Override
    public void onResponse(String response) {
        Gson gson = new Gson();
        T t = null;
        try {
            t = (T) gson.fromJson(response, T);
        } catch (JsonSyntaxException e) {
            t = null;
        }

        if (t == null) {
            netConnectError();
            return;
        }
        if (Constant.SUCCESS.equals(t.code)) {
            //成功
            getData(t);
        } else if (Constant.TokenFailure.equals(t.code)) {
//            Intent intent = new Intent(App.getAppContext(), RegisterActivity.class);
//            intent.putExtra("Token_Failure", true);
//            intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
//            AndroidUtil.clearUserInfo(App.getAppContext());
//            App.getAppContext().startActivity(intent);
//            JPushInterface.setAlias(App.getAppContext(), "", null);
        } else {
            //失败
            showExtraOp(t.msg);
        }
    }

    @Override
    public void onErrorResponse() {
        netConnectError();
    }

    public abstract void getData(T t);

    //解析Code不为200 失败后的DataBean字段
    public abstract void showExtraOp(String message);

    //网络访问失败
    public abstract void netConnectError();
}
