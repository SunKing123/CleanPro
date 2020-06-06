package com.xiaoniu.cleanking.keeplive.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;
import com.xiaoniu.cleanking.BuildConfig;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.scheme.Constant.SchemeConstant;
import com.xiaoniu.cleanking.scheme.utils.ActivityCollector;
import com.xiaoniu.cleanking.ui.lockscreen.FullPopLayerActivity;
import com.xiaoniu.cleanking.ui.lockscreen.LockActivity;
import com.xiaoniu.cleanking.ui.lockscreen.PopLayerActivity;
import com.xiaoniu.cleanking.ui.main.bean.InsertAdSwitchInfoList;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.widget.SPUtil;
import com.xiaoniu.cleanking.utils.LogUtils;
import com.xiaoniu.cleanking.utils.update.MmkvUtil;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.common.utils.ToastUtils;


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
                            wifiContected = 1;
                        }
                    }
                }
            }
        }
//        Logger.i("zz---总开关："+AppHolder.getInstance().getSwitchInfoList().getData()+"--插屏开关："+AppHolder.getInstance().getInsertAdSwitchmap());
        //网络状态变更为wifi
        if (PreferenceUtil.getInstants().getInt(SpCacheConfig.WIFI_STATE) == 0 && wifiContected == 1) {
            startFullInsertAd(context);
        }
        if(!BuildConfig.SYSTEM_EN.contains("prod"))
        ToastUtils.showShort("WIFI---"+(wifiContected == 0 ? "未连接" : "已连接"));
        Logger.i("zz---WIFI" + (wifiContected == 0 ? "未连接" : "已连接"));
        //更新sp当前wifi状态
        PreferenceUtil.getInstants().saveInt(SpCacheConfig.WIFI_STATE, wifiContected);

    }


    //全局跳转全屏插屏页面
    public void startFullInsertAd(Context context) {
        try {
            String auditSwitch = SPUtil.getString(context, AppApplication.AuditSwitch, "1");
            //过审开关打开状态
            //!PreferenceUtil.isShowAD()广告展示状态
            Logger.i("zz---"+(TextUtils.equals(auditSwitch, "1")?"1":"0") + (!ActivityCollector.isActivityExist(PopLayerActivity.class)?"1":"0") + (!ActivityCollector.isActivityExist(LockActivity.class)?"1":0) + (!PreferenceUtil.isShowAD()?"1":"0"));
            if (TextUtils.equals(auditSwitch, "1") && MmkvUtil.isShowFullInsert()) {

                String adSwitch = MmkvUtil.getInsertSwitchInfo();
                //内外部插屏
                InsertAdSwitchInfoList.DataBean dataBean= AppHolder.getInstance().getInsertAdInfo(PositionId.KEY_PAGE_INTERNAL_EXTERNAL_FULL,adSwitch);
                //外部插屏
                InsertAdSwitchInfoList.DataBean dataBean01= AppHolder.getInstance().getInsertAdInfo(PositionId.KEY_PAGE_EXTERNAL_FULL,adSwitch);

                if (null != context && null != dataBean) {//内外部插屏
                    int showTimes = 2;
                    if (dataBean.isOpen()) {
                        showTimes = dataBean.getShowRate();
                        LogUtils.i("zz--times-"+showTimes);
                        if (MmkvUtil.fullInsertPageIsShow(showTimes)) {
                            startFullInsertPage(context,PositionId.AD_EXTERNAL_ADVERTISING_03);
                        }
                    } else {
                        if (null != dataBean01) {       //外部插屏
                            if (dataBean01.isOpen()) {
                                showTimes = dataBean01.getShowRate();
                                //判断应用是否进入后台
                                int isBack = MmkvUtil.getInt("isback",-1);
                                if (isBack != 1)
                                    return;

                                if (MmkvUtil.fullInsertPageIsShow(showTimes)) {
                                    startFullInsertPage(context,PositionId.AD_EXTERNAL_ADVERTISING_02);
                                }

                            }
                        }
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
//            Log.e("LockerService", "start lock activity error:" + e.getMessage());
        }
    }


    //全局跳转全屏插屏页面
    private void startFullInsertPage(Context context,String adStyle) {
        if(ActivityCollector.isActivityExist(FullPopLayerActivity.class))
            return;
        Intent screenIntent = new Intent();
        screenIntent.setClassName(context.getPackageName(), SchemeConstant.StartFromClassName.CLASS_FULLPOPLAYERACTIVITY);
        screenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        screenIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        screenIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        screenIntent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
        screenIntent.putExtra("ad_style",adStyle);
        context.startActivity(screenIntent);
    }







}