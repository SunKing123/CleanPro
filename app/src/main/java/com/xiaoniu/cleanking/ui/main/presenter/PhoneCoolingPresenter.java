package com.xiaoniu.cleanking.ui.main.presenter;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Process;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.ui.main.activity.PhoneCoolingActivity;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.bean.HardwareInfo;
import com.xiaoniu.cleanking.ui.main.model.PhoneCoolingModel;
import com.xiaoniu.cleanking.utils.FileQueryUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static android.content.Context.USAGE_STATS_SERVICE;

public class PhoneCoolingPresenter extends RxPresenter<PhoneCoolingActivity, PhoneCoolingModel> {

    private final FileQueryUtils mFileQueryUtils;
    private final HardwareInfo mHardwareInfo;
    private final RxAppCompatActivity mRxActivity;
    private Intent batteryStatus;

    @Inject
    public PhoneCoolingPresenter(RxAppCompatActivity rxActivity) {
        mRxActivity = rxActivity;
        mFileQueryUtils = new FileQueryUtils();
        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        batteryStatus = AppApplication.getInstance().registerReceiver(null, iFilter);
        mHardwareInfo = new HardwareInfo();
    }

    /**
     * 获取手机温度
     */
    public int getPhoneTemperature() {
        int tem = batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);
        int i = tem / 10;
        return i > 0 ? i : 30;
    }

    /**
     * 获取当前的进程
     */
    @SuppressLint("CheckResult")
    public void getRunningProcess() {
        Observable.create(e -> {
            ArrayList<FirstJunkInfo> runningProcess = mFileQueryUtils.getRunningProcess();
            e.onNext(runningProcess);
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(o -> {
            ArrayList<FirstJunkInfo> runningProcess = (ArrayList<FirstJunkInfo>) o;
            mView.showProcess(runningProcess);
        });
    }

    /**
     * 获取硬件信息
     */
    public void getHardwareInfo(boolean isRefresh) {
        //电池信息
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        if (level != -1 && scale != -1) {
            float batteryPct = (float) level / scale * 100.0f;
            int round = Math.round(batteryPct);
            mHardwareInfo.setBatteryLevel(String.valueOf(round) + "%");
        }
        // Are we charging / is charged?
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
        mHardwareInfo.setCharge(isCharging);
        //蓝牙
        BluetoothAdapter blueadapter = BluetoothAdapter.getDefaultAdapter();
        if (blueadapter != null) {
            mHardwareInfo.setBluetoothOpen(blueadapter.isEnabled());
        }
        //GPS
        mHardwareInfo.setGPSOpen(isGPSOPen());
        //wifi
        mHardwareInfo.setWiFiOpen(isWifiOpen());

        //CPU信息
        getCpuInfo();

        mView.showHardwareInfo(mHardwareInfo, isRefresh);
    }

    /**
     * gps是否开启
     *
     * @return
     */
    public boolean isGPSOPen() {
        LocationManager locationManager = (LocationManager) AppApplication.getInstance().getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * 是否wifi开启
     *
     * @return
     */
    private boolean isWifiOpen() {
        WifiManager wifiManager = (WifiManager) AppApplication.getInstance().getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        return wifiManager.isWifiEnabled();
    }

    private void getCpuInfo() {
        //拿到CPU型号
        File cpuInfoFile = new File("/proc/cpuinfo");
        if (cpuInfoFile.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(cpuInfoFile));
                String aLine;

                aLine = br.readLine();
                while (aLine != null) {
                    String[] stringParts = aLine.split(":");
                    if (stringParts.length == 2) {
                        if (stringParts[0].contains("Hardware")) {
                            mHardwareInfo.setCPUType(stringParts[1]);
                        }
                    }
                    aLine = br.readLine();
                }

                br.close();
            } catch (Exception e) {
            }
        }
        //CPU核心数
        mHardwareInfo.setCPUCore(Runtime.getRuntime().availableProcessors() + "个");
        //CPU负载
        File file = new File("/proc/loadavg");
        if (file.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String aLine;

                aLine = br.readLine();
                while (aLine != null) {
                    String[] s = aLine.split(" ");
                    if (s.length > 0) {
                        String s1 = s[1];
                        mHardwareInfo.setCPULoad(s1 + "%");
                        break;
                    }
                }

            } catch (Exception e) {
            }
        }
    }

    @TargetApi(22)
    public List<ActivityManager.RunningAppProcessInfo> getProcess() {
        UsageStatsManager usageStatsManager = (UsageStatsManager) mRxActivity.getSystemService(USAGE_STATS_SERVICE);
        if (usageStatsManager == null) {
            return null;
        }
        List<UsageStats> lists = usageStatsManager.queryUsageStats(4, System.currentTimeMillis() - 86400000, System.currentTimeMillis());
        ArrayList arrayList = new ArrayList();
        if (!(lists == null || lists.size() == 0)) {
            for (UsageStats usageStats : lists) {
                if (!(usageStats == null || usageStats.getPackageName() == null || usageStats.getPackageName().contains("com.cleanmaster.mguard_cn"))) {
                    ActivityManager.RunningAppProcessInfo runningAppProcessInfo = new ActivityManager.RunningAppProcessInfo();
                    runningAppProcessInfo.processName = usageStats.getPackageName();
                    runningAppProcessInfo.pkgList = new String[]{usageStats.getPackageName()};
                    int uidForName = Process.getUidForName(usageStats.getPackageName());
                    arrayList.add(runningAppProcessInfo);
                }
            }
        }

        return arrayList;
    }
}
