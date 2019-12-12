package com.xiaoniu.cleanking.ui.main.activity;

import android.os.Build;
import android.util.Log;

import com.orhanobut.logger.Logger;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.SimpleActivity;
import com.xiaoniu.cleanking.keeplive.KeepAliveManager;
import com.xiaoniu.cleanking.keeplive.config.ForegroundNotification;
import com.xiaoniu.cleanking.keeplive.service.LocalService;
import com.xiaoniu.common.utils.SystemUtils;

import static com.xiaoniu.cleanking.keeplive.config.RunMode.HIGH_POWER_CONSUMPTION;

/**
 * @author zhengzhihao
 * @date 2019/12/10 12
 * @mail：zhengzhihao@hellogeek.com
 */
public class LauncherActivity extends SimpleActivity {

    private boolean isStarted = false;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_hot_redpacket;
    }

    @Override
    protected void initView() {
        //启动微信
        isStarted = false;

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!isStarted) {
            SystemUtils.openApp(getApplicationContext(), "com.tencent.mm");
            isStarted = true;
            //唤起本地service
            if (!SystemUtils.isServiceRunning(this, LocalService.class)) {
                start();
                Logger.i("zz--22");
            } else {
                Logger.i("zz--11");
            }
        }else{
            finish();
        }
    }

    public void start() {
        try {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                //启动保活服务
                KeepAliveManager.toKeepAlive(
                        getApplication()
                        , HIGH_POWER_CONSUMPTION,
                        mContext.getString(R.string.push_content_default_title),
                        mContext.getString(R.string.push_content_default_content),
                        R.mipmap.applogo,
                        new ForegroundNotification(
                                //定义前台服务的通知点击事件
                                (context, intent) -> Log.d("JOB-->", " foregroundNotificationClick"))
                );
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}