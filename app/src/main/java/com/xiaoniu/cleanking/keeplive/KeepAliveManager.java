package com.xiaoniu.cleanking.keeplive;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.xiaoniu.cleanking.keeplive.config.ForegroundNotification;
import com.xiaoniu.cleanking.keeplive.config.KeepAliveConfig;
import com.xiaoniu.cleanking.keeplive.config.NotificationUtils;
import com.xiaoniu.cleanking.keeplive.config.RunMode;
import com.xiaoniu.cleanking.keeplive.service.JobHandlerService;
import com.xiaoniu.cleanking.keeplive.service.LocalService;
import com.xiaoniu.cleanking.keeplive.service.RemoteService;
import com.xiaoniu.cleanking.keeplive.utils.KeepAliveUtils;
import com.xiaoniu.cleanking.keeplive.utils.SPUtils;

import static com.xiaoniu.cleanking.keeplive.config.KeepAliveConfig.SP_NAME;

/**
 * 进程保活管理
 */
public class KeepAliveManager {
    private static final String TAG = "KeepAliveManager";

    /**
     * 启动保活
     *
     * @param application            your application
     * @param runMode
     * @param foregroundNotification 前台服务
     */
    public static void toKeepAlive(@NonNull Application application, @NonNull int runMode, String title, String content, int res_icon, ForegroundNotification foregroundNotification) {
        if (KeepAliveUtils.isRunning(application)) {
            KeepAliveConfig.foregroundNotification = foregroundNotification;
            SPUtils.getInstance(application, SP_NAME).put(KeepAliveConfig.TITLE, title);
            SPUtils.getInstance(application, SP_NAME).put(KeepAliveConfig.CONTENT, content);
            SPUtils.getInstance(application, SP_NAME).put(KeepAliveConfig.RES_ICON, res_icon);
            SPUtils.getInstance(application, SP_NAME).put(KeepAliveConfig.RUN_MODE, runMode);
            //优化后的枚举
            RunMode.setShape(runMode);
            KeepAliveConfig.runMode = RunMode.getShape();
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {  //>=5.0
                    //启动定时器，在定时器中启动本地服务和守护进程
                    JobHandlerService.startJob(application);
                } else {

                    Intent localIntent = new Intent(application, LocalService.class);
                    //启动守护进程
                    Intent guardIntent = new Intent(application, RemoteService.class);
                    if (Build.VERSION.SDK_INT >= 26) {
                        application.startForegroundService(localIntent);
                        application.startForegroundService(guardIntent);
                    } else {
                        application.startService(localIntent);
                        application.startService(guardIntent);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void stopWork(Application application) {
        try {
            KeepAliveConfig.foregroundNotification = null;
            KeepAliveConfig.keepLiveService = null;
            KeepAliveConfig.runMode = RunMode.getShape();
            JobHandlerService.stopJob();
            //启动本地服务
            Intent localIntent = new Intent(application, LocalService.class);
            //启动守护进程
            Intent guardIntent = new Intent(application, RemoteService.class);
            application.stopService(localIntent);
            application.stopService(guardIntent);
            application.stopService(new Intent(application, JobHandlerService.class));
        } catch (Exception e) {
            Log.e(TAG, "stopWork-->" + e.getMessage());
        }
    }


    public static void sendNotification(Context context, String title, String content, int icon, Intent intent2) {
        NotificationUtils.sendNotification(context, title, content, icon, intent2);
    }

}
