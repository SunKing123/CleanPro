package com.xiaoniu.cleanking.keeplive.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;

import com.orhanobut.logger.Logger;


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
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {

            //获得ConnectivityManager对象
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            //获取ConnectivityManager对象对应的NetworkInfo对象
            //获取WIFI连接的信息
            NetworkInfo wifiNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            //获取移动数据连接的信息
            NetworkInfo dataNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (wifiNetworkInfo.isConnected()){
                Logger.i("zz---WIFI已连接");
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
            //用于存放网络连接信息
            StringBuilder sb = new StringBuilder();
            for (int i=0; i < networks.length; i++){
                //获取ConnectivityManager对象对应的NetworkInfo对象
                NetworkInfo networkInfo = connMgr.getNetworkInfo(networks[i]);
                sb.append(networkInfo.getTypeName() + " connect is " + networkInfo.isConnected());
            }
//            Logger.i("zz---"+sb.toString());
        }

    }
}
