package com.xiaoniu.cleanking.ui.localpush;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import androidx.collection.SparseArrayCompat;

import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.scheme.Constant.SchemeConstant;
import com.xiaoniu.cleanking.scheme.utils.ActivityCollector;
import com.xiaoniu.cleanking.ui.main.bean.InsertAdSwitchInfoList;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.utils.AppLifecycleUtil;
import com.xiaoniu.cleanking.utils.LogUtils;
import com.xiaoniu.cleanking.utils.update.MmkvUtil;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.common.utils.NetworkUtils;
import com.xiaoniu.common.utils.StatisticsUtils;

import java.util.Map;

public class LocalPushDispatcher {

    private Context mContext;

    public LocalPushDispatcher(Context context) {
        mContext = context;
    }

    public void showLocalPushDialog() {
        //app在前台的时候不弹出
        if (AppLifecycleUtil.isAppOnForeground(mContext.getApplicationContext())) {
            return;
        }

        //1.读取本地缓存的推送配置Config列表
        SparseArrayCompat<LocalPushConfigModel.Item> map = PreferenceUtil.getLocalPushConfig();

        //2.判断【一键加速】功能是否满足推送条件
        LocalPushConfigModel.Item speedItem = map.get(LocalPushType.TYPE_SPEED_UP);

        if (speedItem != null) {
            if (LocalPushUtils.getInstance().allowPopSpeedUp(speedItem)) {
                LogUtils.e("===允许弹出speed的window");
                startPopActivity(speedItem);
                return;
            } else {
                LogUtils.e("===不允许弹出speed的window");
            }
        }

        //3.判断【垃圾清理】功能是否满足推送条件
        LocalPushConfigModel.Item clearItem = map.get(LocalPushType.TYPE_NOW_CLEAR);
        if (clearItem != null && LocalPushUtils.getInstance().allowPopClear(clearItem)) {
            LogUtils.e("===允许弹出clear的window");
            startPopActivity(clearItem);
            return;
        }

        //4.判断【手机降温】功能是否满足推送条件
        LocalPushConfigModel.Item coolItem = map.get(LocalPushType.TYPE_PHONE_COOL);
        if (coolItem != null) {
            if (LocalPushUtils.getInstance().allowPopCool(coolItem)) {
                LogUtils.e("===允许弹出cool的window");
                coolItem.setLocalTemp(LocalPushUtils.getInstance().getTemp());
                startPopActivity(coolItem);
                return;
            }
        }

        //5.判断【超强省电】功能是否满足推送条件
        LocalPushConfigModel.Item powerItem = map.get(LocalPushType.TYPE_SUPER_POWER);
        if (powerItem != null) {
            boolean isCharged = LocalPushUtils.getInstance().getCharged();
            int mBatteryPower = LocalPushUtils.getInstance().getBatteryPower();
            if (LocalPushUtils.getInstance().allowPopPowerSaving(powerItem, isCharged, mBatteryPower)) {
                LogUtils.e("===允许弹出power的window");
                powerItem.setLocalPower(mBatteryPower);
                startPopActivity(powerItem);
            }
        }


    }


    private void startPopActivity(LocalPushConfigModel.Item item) {

        if (ActivityCollector.hasExternalActivity()) {
            return;
        }

        StatisticsUtils.customTrackEvent("local_push_window_custom", "推送弹窗满足展示时机", "", "local_push_window");
        Intent screenIntent = new Intent();
        screenIntent.setClassName(mContext.getPackageName(), SchemeConstant.StartFromClassName.CLASS_LOCAL_PUSH_ACTIVITY);
        screenIntent.putExtra("config", item);
        screenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        screenIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        screenIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        screenIntent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
        mContext.startActivity(screenIntent);
    }


    //悬浮广告页面
    public void startDialogActivityOnLauncher() {

        try {
            //app在前台的时候不弹出
            if (AppLifecycleUtil.isAppOnForeground(mContext.getApplicationContext())) {
                return;
            }
            //判断是否进入后台
            // int isBack = MmkvUtil.getInt("isback", -1);
            // if (isBack != 1 || ActivityCollector.isActivityExistMkv(FullPopLayerActivity.class))
            //  return;
            //判断距离安装APP是否过了一小时
            long currentTime = System.currentTimeMillis();
            long installTime = MmkvUtil.getLong(SpCacheConfig.KEY_FIRST_INSTALL_APP_TIME, currentTime);


            if (currentTime - installTime < 1000 * 60 * 60) {
                LogUtils.e("==========新安装APP在1小时内，不弹出");
                return;
            }

            //判断广告开关
            boolean isOpen = false;
            int showTimes = 3;
            int displayTime = 0;
            if (null != AppHolder.getInstance().getInsertAdSwitchMap()) {
                Map<String, InsertAdSwitchInfoList.DataBean> map = AppHolder.getInstance().getInsertAdSwitchMap();
                InsertAdSwitchInfoList.DataBean data = map.get("page_outside_screen");
                if (data != null) {
                    isOpen = data.isOpen();
                    showTimes = data.getShowRate();
                    displayTime = data.getDisplayTime();
                }
            }
            if (!isOpen)
                return;

            String mPopLayerTime = PreferenceUtil.getInstants().get(SpCacheConfig.POP_LAYER_TIME);
            long preTime = TextUtils.isEmpty(mPopLayerTime) ? 0 : Long.parseLong(mPopLayerTime);
            int number = PreferenceUtil.getInstants().getInt(SpCacheConfig.POP_LAYER_NUMBERS);
            //第一次|| 间隔时间大于一个小时||一小时内N次（N<showRate）(每次间隔时间<displayTime)
            if (preTime == 0 || (System.currentTimeMillis() - preTime) > (60 * 60 * 1000) || ((System.currentTimeMillis() - preTime) > (displayTime * 60 * 1000) && (System.currentTimeMillis() - preTime) <= (60 * 60 * 1000) && number < showTimes)) {
                if ((System.currentTimeMillis() - preTime) > (60 * 60 * 1000)) {//超过一小时重置次数
                    PreferenceUtil.getInstants().saveInt(SpCacheConfig.POP_LAYER_NUMBERS, 0);
                }
                if (NetworkUtils.isNetConnected()) {
                    if (ActivityCollector.hasExternalActivity()) {
                        return;
                    }
                    Intent screenIntent = getIntent(mContext);
                    mContext.startActivity(screenIntent);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private Intent getIntent(Context context) {
        Intent screenIntent = new Intent();
        screenIntent.setClassName(context.getPackageName(), SchemeConstant.StartFromClassName.CLASS_POPLAYERACTIVITY);
        screenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        screenIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        screenIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        screenIntent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
        return screenIntent;
    }

}
