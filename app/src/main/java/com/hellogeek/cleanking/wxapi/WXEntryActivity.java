package com.hellogeek.cleanking.wxapi;


import android.os.Build;
import android.os.Bundle;

import com.umeng.socialize.weixin.view.WXCallbackActivity;
import com.xiaoniu.common.utils.AppActivityUtils;

public class WXEntryActivity extends WXCallbackActivity {
    @Override
    protected void onCreate(Bundle bundle) {
        //透明activity在Android8.0上崩溃 解决方案
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O && AppActivityUtils.isTranslucentOrFloating(this)) {
            AppActivityUtils.fixOrientation(this);
        }
        super.onCreate(bundle);

    }
}
