package com.xiaoniu.cleanking.ui.main.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.SimpleActivity;

/**
 * @author zhengzhihao
 * @date 2019/12/10 12
 * @mail：zhengzhihao@hellogeek.com
 */
public class LauncherTwoActivity extends Activity {

    private boolean isStarted = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent shortcutInfoIntent = new Intent(this, SplashADActivity.class);
        shortcutInfoIntent.setAction(Intent.ACTION_VIEW);
        startActivity(shortcutInfoIntent);
        finish();
    }





    @Override
    protected void onResume() {
        super.onResume();
//        if (!isStarted) {
//            SystemUtils.openApp(getApplicationContext(), "com.tencent.mm");
//            isStarted = true;
//            //唤起本地service
//            if (!SystemUtils.isServiceRunning(this, LocalService.class)) {
//                start();
//                Logger.i("zz--22");
//            } else {
//                Logger.i("zz--11");
//            }
//        }else{
//            finish();
//        }
    }

//    public void start() {
//        try {
//            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
//                //启动保活服务
//                KeepAliveManager.toKeepAlive(
//                        getApplication()
//                        , HIGH_POWER_CONSUMPTION,
//                        mContext.getString(R.string.push_content_default_title),
//                        mContext.getString(R.string.push_content_default_content),
//                        R.mipmap.applogo,
//                        new ForegroundNotification(
//                                //定义前台服务的通知点击事件
//                                (context, intent) -> Log.d("JOB-->", " foregroundNotificationClick"))
//                );
//            }
//        } catch (RuntimeException e) {
//            e.printStackTrace();
//        }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
