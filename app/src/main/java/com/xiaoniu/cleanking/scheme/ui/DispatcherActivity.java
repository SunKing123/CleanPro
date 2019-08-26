package com.xiaoniu.cleanking.scheme.ui;

import android.content.Intent;

import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.scheme.SchemeProxy;
import com.xiaoniu.cleanking.scheme.utils.ActivityCollector;
import com.xiaoniu.cleanking.ui.main.activity.MainActivity;
import com.xiaoniu.cleanking.utils.AndroidUtil;

/**
 * 描述:协议分发Activity
 * 作者:安亚波
 * 创建时间:2017/8/3
 */
public class DispatcherActivity extends BaseActivity {
    @Override
    public void inject(ActivityComponent activityComponent) {

    }

    @Override
    public void netError() {

    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected void initView() {
        finish();
        if (!ActivityCollector.isActivityExist(MainActivity.class)
                && !AndroidUtil.isFastDoubleClick()) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        SchemeProxy.dispatcher(this, getIntent());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        super.onActivityResult(requestCode, resultCode, resultIntent);
        setResult(resultCode, resultIntent);
        finish();
    }

}

