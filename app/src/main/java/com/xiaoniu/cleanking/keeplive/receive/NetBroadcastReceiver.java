package com.xiaoniu.cleanking.keeplive.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.gson.Gson;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.ui.main.bean.ExternalPopNumEntity;
import com.xiaoniu.cleanking.ui.main.bean.InsertAdSwitchInfoList;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.newclean.model.PopEventModel;
import com.xiaoniu.cleanking.utils.AppLifecycleUtil;
import com.xiaoniu.cleanking.utils.LogUtils;
import com.xiaoniu.cleanking.utils.update.MmkvUtil;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.common.utils.DateUtils;

import org.greenrobot.eventbus.EventBus;

public class NetBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

     /*   if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
            //WIFI和移动网络均未连接
            netContentListener.netContent(false);

        } else {
            //WIFI连接或者移动网络连接
            netContentListener.netContent(true);
        }*/
        if (wifiNetInfo.isConnected()) {
            LogUtils.e("===================WIFI Connect=====");
            startExternalSceneActivity(context);
        } else {
            LogUtils.e("===================WIFI Disconnect=====");
        }
    }


    private void startExternalSceneActivity(Context mContext) {
        //应用在前台不弹出
        if (AppLifecycleUtil.isAppOnForeground(mContext)) {
            LogUtils.e("======APP在前台");
            return;
        }
        //判断WIFI插屏是否打开
        InsertAdSwitchInfoList.DataBean configBean = AppHolder.getInstance().getInsertAdInfo(PositionId.KEY_WIFI_EXTERNAL_SCREEN);
        LogUtils.e("=========wifi config:" + new Gson().toJson(configBean));
        //1.先判断是否打开了WIFI弹窗的开关
        if (configBean != null && configBean.isOpen()) {
            long currentTime = System.currentTimeMillis();
            ExternalPopNumEntity wifiEntity = PreferenceUtil.getPopupWifi();
            LogUtils.e("=========已经弹出的 wifi entity:" + new Gson().toJson(wifiEntity));
            //2.判断是否同是一天
            if (DateUtils.isSameDay(wifiEntity.getPopupTime(), currentTime)) {
                //判断当前时间是否满足上次一次的弹窗间隔
                long elapseUsedTime = DateUtils.getMinuteBetweenTimestamp(currentTime, wifiEntity.getPopupTime());
                if (elapseUsedTime < configBean.getDisplayTime()) {
                    LogUtils.e("==========不满足wifi展示的间隔时间");
                    return;
                }
                //判断已经展示的交数是否超过最大次数
                if (wifiEntity.getPopupCount() >= configBean.getShowRate()) {
                    LogUtils.e("==========不满足wifi展示的总次数");
                    return;
                }
                MmkvUtil.saveBool("isResetWiFi", false);
                EventBus.getDefault().post(new PopEventModel("wifi"));
            } else {
                //不是同一天，重置数据
                MmkvUtil.saveBool("isResetWiFi", true);
                EventBus.getDefault().post(new PopEventModel("wifi"));
            }


        }
    }


}
