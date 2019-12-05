package com.xiaoniu.cleanking.keeplive.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;

import com.orhanobut.logger.Logger;
import com.xiaoniu.cleanking.scheme.utils.ActivityCollector;
import com.xiaoniu.cleanking.ui.lockscreen.FullPopLayerActivity;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;

import androidx.annotation.NonNull;


/**
 * @author zhengzhihao
 * @date 2019/12/5 13
 * @mail：zhengzhihao@hellogeek.com
 *
 *  网络状态监听
 */
public class NetWorkStateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //检测API是不是小于21，因为到了API21之后getNetworkInfo(int networkType)方法被弃用
        int wifiContected = 0;//wifi连接状态
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
            //获得ConnectivityManager对象
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            //获取ConnectivityManager对象对应的NetworkInfo对象
            //获取WIFI连接的信息
            NetworkInfo wifiNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            //获取移动数据连接的信息
            NetworkInfo dataNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (wifiNetworkInfo.isConnected()) {
                Logger.i("zz---WIFI已连接");
                wifiContected = 1;
            }

        /*    if (wifiNetworkInfo.isConnected() && dataNetworkInfo.isConnected()) {
                Logger.i("zz---WIFI已连接,移动数据已连接");
            } else if (wifiNetworkInfo.isConnected() && !dataNetworkInfo.isConnected()) {
                Logger.i("zz---WIFI已连接,移动数据已断开");
            } else if (!wifiNetworkInfo.isConnected() && dataNetworkInfo.isConnected()) {
                Logger.i("zz---WIFI已断开,移动数据已连接");
            } else {
                Logger.i("zz---WIFI已断开,移动数据已断开");
            }*/
        }else {
            System.out.println("API level 大于21");
            //获得ConnectivityManager对象
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            //获取所有网络连接的信息
            Network[] networks = connMgr.getAllNetworks();
            if(null!=networks && networks.length>0){
                for (int i=0; i < networks.length; i++){
                    NetworkInfo networkInfo = connMgr.getNetworkInfo(networks[i]);
                    if(networkInfo.getTypeName().toUpperCase().contains("WIFI")){
                        if(networkInfo.isConnected()){
                            Logger.i("zz---WIFI已连接");
                            wifiContected = 1;
                        }
                    }
                }
            }
        /*    //用于存放网络连接信息
            StringBuilder sb = new StringBuilder();
            for (int i=0; i < networks.length; i++){
                //获取ConnectivityManager对象对应的NetworkInfo对象
                NetworkInfo networkInfo = connMgr.getNetworkInfo(networks[i]);
                sb.append(networkInfo.getTypeName() + " connect is " + networkInfo.isConnected());
            }
            Logger.i("zz---"+sb.toString());*/
        }


        //网络状态变更为wifi
        if (PreferenceUtil.getInstants().getInt(SpCacheConfig.WIFI_STATE) == 0 && wifiContected == 1&& !ActivityCollector.isActivityExist(FullPopLayerActivity.class)) {
            Intent screenIntent = getIntent(context);
            context.startActivity(screenIntent);
        }

        //更新sp当前wifi状态
        PreferenceUtil.getInstants().saveInt(SpCacheConfig.WIFI_STATE, wifiContected);

    }


    //全局跳转锁屏页面
    @NonNull
    private Intent getIntent(Context context) {
        Intent screenIntent = new Intent();
        screenIntent.setClassName(context.getPackageName(), "com.xiaoniu.cleanking.ui.lockscreen.FullPopLayerActivity");
        screenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        screenIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        screenIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        screenIntent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
        return screenIntent;
    }
}
