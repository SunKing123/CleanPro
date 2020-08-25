package com.xiaoniu.cleanking.utils.wifi;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.text.format.Formatter;
import android.util.Log;

import java.net.InetAddress;

/**
 * Desc:
 * <p>  扫描局域网内的设备数量
 * Date: 2020/8/17
 * Copyright: Copyright (c) 2016-2022
 * Company: @小牛科技
 * Email:zengbo@xiaoniuhy.com
 * Update Comments:
 *
 * @author ZengBo
 */
public class ScanDeviceManager {

    private Context mContext;
    private final String TAG = "===============";
    private int mDeviceTotal = 0;
    private ConnectivityManager mConnectivityManager;
    private NetworkInfo mNetworkInfo;
    private WifiManager mWifiManager;
    private WifiInfo mWifiInfo;
    private ScanThread scanThread = new ScanThread();
    private boolean isScanFinish;

    public ScanDeviceManager(Context context) {
        mContext = context;
        mConnectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        mWifiManager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mWifiInfo = mWifiManager.getConnectionInfo();
    }


    class ScanThread extends Thread {
        @Override
        public void run() {
            super.run();
            int ipAddress = mWifiInfo.getIpAddress();
            Log.e(TAG, "ipAddress:" + ipAddress);
            String ipString = Formatter.formatIpAddress(ipAddress);


            Log.e(TAG, "activeNetwork: " + String.valueOf(mNetworkInfo));
            Log.e(TAG, "ipString: " + String.valueOf(ipString));

            String prefix = ipString.substring(0, ipString.lastIndexOf(".") + 1);
            Log.e(TAG, "prefix: " + prefix);

            try {

                for (int i = 0; i < 255; i++) {
                    if (isScanFinish) {
                        return;
                    }
                    String testIp = prefix + i;
                    InetAddress address = InetAddress.getByName(testIp);
                    boolean reachable = address.isReachable(1000);
                    String hostName = address.getCanonicalHostName();
                    if (reachable) {
                        Log.e(TAG, "Host: " + hostName + "(" + testIp + ") is reachable!");
                        mDeviceTotal += 1;
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }

    public void startScanDevice(int scanDuration, ScanResultListener listener) {
        scanThread.start();
        new Handler().postDelayed(() -> {
            scanThread.interrupt();
            isScanFinish = true;
            if (listener != null) {
                listener.scanResult(mDeviceTotal);
            }
        }, scanDuration);

    }


    public interface ScanResultListener {
        void scanResult(int deviceNum);
    }

}
