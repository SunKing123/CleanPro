package com.xiaoniu.cleanking.keeplive.service;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.xiaoniu.cleanking.keeplive.config.KeepAliveConfig;
import com.xiaoniu.cleanking.keeplive.utils.KeepAliveUtils;

/**
 * 定时器
 * 安卓5.0及以上
 */
@SuppressWarnings(value = {"unchecked", "deprecation"})
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public final class JobHandlerService extends JobService {
    private String TAG = this.getClass().getSimpleName();
    private static JobScheduler mJobScheduler;

    private static int EXECUTE_COUNT = 0;

    public static void startJob(Context context) {
        try {
            mJobScheduler = (JobScheduler) context.getSystemService(
                    Context.JOB_SCHEDULER_SERVICE);
            JobInfo.Builder builder = new JobInfo.Builder(10,
                    new ComponentName(context.getPackageName(),
                            JobHandlerService.class.getName())).setPersisted(true);
            /**
             * I was having this problem and after review some blogs and the official documentation,
             * I realised that JobScheduler is having difference behavior on Android N(24 and 25).
             * JobScheduler works with a minimum periodic of 15 mins.
             *
             */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //7.0以上延迟1s执行
                builder.setMinimumLatency(KeepAliveConfig.JOB_TIME);
            } else {
                //每隔1s执行一次job
                builder.setPeriodic(KeepAliveConfig.JOB_TIME);
            }
            mJobScheduler.schedule(builder.build());


        } catch (Exception e) {
            Log.e("startJob->", e.getMessage());
        }
    }

    public static void stopJob() {
        if (mJobScheduler != null)
            mJobScheduler.cancelAll();


    }

    private void startService(Context context) {
        try {
            Log.i(TAG, "---》启动双进程保活服务");
            //启动本地服务
            Intent localIntent = new Intent(context, LocalService.class);
            //启动守护进程
            Intent guardIntent = new Intent(context, RemoteService.class);
            if (Build.VERSION.SDK_INT >= 26) {
                startForegroundService(localIntent);
                startForegroundService(guardIntent);
            } else {
                startService(localIntent);
                startService(guardIntent);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        try {
            ++EXECUTE_COUNT;
            Log.d("JOB-->", " Job 执行 " + EXECUTE_COUNT);
            //7.0以上轮询
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                startJob(this);
            }
            if (!KeepAliveUtils.isServiceRunning(getApplicationContext(), getPackageName() + ":local") || !KeepAliveUtils.isRunningTaskExist(getApplicationContext(), getPackageName() + ":remote")) {
                Log.d("JOB-->", " 重新开启了 服务 ");
                startService(this);
            }
            if(EXECUTE_COUNT /60 ==0){
                testLib();
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.d("JOB-->", " Job 停止");
        if (!KeepAliveUtils.isServiceRunning(getApplicationContext(), getPackageName() + ":local") || !KeepAliveUtils.isRunningTaskExist(getApplicationContext(), getPackageName() + ":remote")) {
            startService(this);
        }
        return false;
    }


    public void testLib(){
        new Thread(new Runnable() {
            @Override
            public void run() {
//                List<PackageInfo> list=  SystemCommon.getInstalledList();
//                Log.i(TAG, "zzhrun: "+list.size()+"----"+list.get(0).packageName);
            }
        }).start();
    }
}
