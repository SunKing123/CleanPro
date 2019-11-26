package com.xiaoniu.cleanking.app;

import android.view.View;

import com.xiaoniu.cleanking.AppConstants;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.scheme.Constant.SchemeConstant;
import com.xiaoniu.cleanking.scheme.SchemeProxy;

/**
 * deprecation:调试页面
 * author:ayb
 * time:2018/11/28
 */
public class DebugActivity extends BaseActivity {

    @Override
    public void inject(ActivityComponent activityComponent) {

    }

    @Override
    public void netError() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_debug;
    }

    @Override
    protected void initView() {

    }

    public void close(View view) {
        finish();
    }

    public void toHomeClean(View view) {
        //原生带参数 native协议
//        "cleanking://com.xiaoniu.cleanking/native?name=main&main_index=0"
        String nativeHeader = SchemeConstant.SCHEME + "://" +
                SchemeConstant.HOST + SchemeConstant.NATIVE + "?name=";
        String nativeName = SchemeConstant.NATIVE_MAIN;
        String nativeParams = "&" + SchemeConstant.EXTRA_MAIN_INDEX + "=4";
        String scheme = nativeHeader + nativeName + nativeParams;
        SchemeProxy.openScheme(this, scheme);
    }

    public void toHomeTools(View view) {
        //原生带参数 native协议
//        "cleanking://com.xiaoniu.cleanking/native?name=main&main_index=1"
        String nativeHeader = SchemeConstant.SCHEME + "://" +
                SchemeConstant.HOST + SchemeConstant.NATIVE + "?name=";
        String nativeName = SchemeConstant.NATIVE_MAIN;
        String nativeParams = "&" + SchemeConstant.EXTRA_MAIN_INDEX + "=1";
        String scheme = nativeHeader + nativeName + nativeParams;
        SchemeProxy.openScheme(this, scheme);
    }

    public void toHomeNews(View view) {
        //原生带参数 native协议
//        "cleanking://com.xiaoniu.cleanking/native?name=main&main_index=2"
        String nativeHeader = SchemeConstant.SCHEME + "://" +
                SchemeConstant.HOST + SchemeConstant.NATIVE + "?name=";
        String nativeName = SchemeConstant.NATIVE_MAIN;
        String nativeParams = "&" + SchemeConstant.EXTRA_MAIN_INDEX + "=2";
        String scheme = nativeHeader + nativeName + nativeParams;
        SchemeProxy.openScheme(this, scheme);
    }

    public void toHomeMine(View view) {
        //原生带参数 native协议
//        "cleanking://com.xiaoniu.cleanking/native?name=main&main_index=3"
        String nativeHeader = SchemeConstant.SCHEME + "://" +
                SchemeConstant.HOST + SchemeConstant.NATIVE + "?name=";
        String nativeName = SchemeConstant.NATIVE_MAIN;
        String nativeParams = "&" + SchemeConstant.EXTRA_MAIN_INDEX + "=3";
        String scheme = nativeHeader + nativeName + nativeParams;
        SchemeProxy.openScheme(this, scheme);
    }

    public void toH5(View view) {
        //jump 协议
//        "cleanking://com.xiaoniu.cleanking/jump?url=XXXX"
        String url = AppConstants.Base_H5_Host + "/userAgreement.html";
        String jump = SchemeConstant.SCHEME + "://" +
                SchemeConstant.HOST + SchemeConstant.JUMP + "?url=";
        String jumpParams = "&is_no_title=0&h5_title=协议";
        String scheme = jump + url + jumpParams;
        SchemeProxy.openScheme(this, scheme);
    }

    public void toWeChatClean(View view) {
        //原生不带参数 native_no_params协议
//        "cleanking://com.xiaoniu.cleanking/native_no_params?a_name=包名.ui.后面的路径"

//        SCHEME + "://" + HOST + NATIVE_NO_PARAMS + "?" + ANDROID_NAME + "=" + "tool.notify.activity.NotifyCleanGuideActivity";
        String packagePath = "tool.wechat.activity.WechatCleanHomeActivity";
        String schemeHeader = SchemeConstant.SCHEME + "://" +
                SchemeConstant.HOST + SchemeConstant.NATIVE_NO_PARAMS +
                "?" + SchemeConstant.ANDROID_NAME + "=";
        String scheme = schemeHeader + packagePath;
        SchemeProxy.openScheme(this, scheme);
    }


}
