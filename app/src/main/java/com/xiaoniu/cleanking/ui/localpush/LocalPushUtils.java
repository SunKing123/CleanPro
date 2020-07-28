package com.xiaoniu.cleanking.ui.localpush;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.xiaoniu.cleanking.BuildConfig;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.scheme.utils.ActivityCollector;
import com.xiaoniu.cleanking.ui.lockscreen.FullPopLayerActivity;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.utils.LogUtils;
import com.xiaoniu.cleanking.utils.NumberUtils;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.common.utils.DateUtils;

public class LocalPushUtils {
    private LocalPushUtils() {
    }


    private static class Holder {
        static LocalPushUtils pushHandle = new LocalPushUtils();
    }

    public static LocalPushUtils getInstance() {
        return Holder.pushHandle;
    }


    private boolean commonParamsVerify(LocalPushConfigModel.Item config, String functionType) {
        //1.先判断今天允许弹出的次数是否小于最大次数
        if (isTodayLimitAllowed(config, functionType)) {
            LogUtils.e("===当日总次数验证通过:" + functionType);
            Long currentTimeStamp = System.currentTimeMillis();
            //2.判断距上次使用该功能是否已经大于配置指定的时间
            Long lastUsedTime = PreferenceUtil.getLastUseFunctionTime(functionType);

            LogUtils.e("=====上次使用" + functionType + "的时间为:" + lastUsedTime);

            //计算上次使用该功能距离现在过于多少分钟
            long elapseUsedTime = DateUtils.getMinuteBetweenTimestamp(currentTimeStamp, lastUsedTime);
            if (elapseUsedTime >= config.getFunctionUsedInterval()) {
                LogUtils.e("===上次使用功能时间验证通过:" + functionType);
                //判断上次弹框的时间是否已经大于配置指定的时间
                Long lastPopTime = PreferenceUtil.getLastPopupTime();
                //计算上次弹框距离现在过于多少分钟
                long elapsePopTime = DateUtils.getMinuteBetweenTimestamp(currentTimeStamp, lastPopTime);
                if (elapsePopTime >= config.getPopWindowInterval()) {
                    LogUtils.e("===上次弹框时间验证通过:" + functionType);
                } else {
                    LogUtils.e("===上次弹框时间验证不通过:" + functionType);
                }
                return elapsePopTime >= config.getPopWindowInterval();
            } else {
                LogUtils.e("===上次使用功能时间验证不通过:" + functionType);
                return false;
            }
        } else {
            LogUtils.e("===当日总次数验证不通过:" + functionType);
            return false;
        }
    }

    public boolean allowPopClear(LocalPushConfigModel.Item config) {
        if (commonParamsVerify(config, SpCacheConfig.KEY_FUNCTION_CLEAR_RUBBISH)) {
            //上一次垃圾扫描的结果为0的时候也不弹出
            long size = PreferenceUtil.getLastScanRubbishSize();
            LogUtils.e("====上次扫描垃圾的大小为：" + size);
            return size != 0L;
        } else {
            return false;
        }
    }

    public boolean allowPopSpeedUp(LocalPushConfigModel.Item config) {
        return commonParamsVerify(config, SpCacheConfig.KEY_FUNCTION_SPEED_UP);
    }


    public boolean allowPopCool(LocalPushConfigModel.Item config) {
        return commonParamsVerify(config, SpCacheConfig.KEY_FUNCTION_COOL);
    }


    public boolean allowPopPowerSaving(LocalPushConfigModel.Item config, boolean isCharged, int mBatteryPower) {
        if (isCharged) {
            LogUtils.e("===当前设备在充电");
            return false;
        } else {
            if (commonParamsVerify(config, SpCacheConfig.KEY_FUNCTION_POWER_SAVING)) {
                //省电除了判断服务器返回的参数要符合条件外，还要判断电量是否小于阈值
                LogUtils.e("=====:当前设备电量：" + mBatteryPower + "  阈值电量：" + config.getThresholdNum());
                return mBatteryPower < config.getThresholdNum();
            } else {
                return false;
            }
        }

    }


    private boolean isTodayLimitAllowed(LocalPushConfigModel.Item config, String functionType) {
        //1.先判断今天的次数是否小于最大次数
        String dayLimitJson = PreferenceUtil.getPopupCount(functionType);
        if (TextUtils.isEmpty(dayLimitJson)) {
            LogUtils.e("===第一次弹框次数为0：" + " 当前最大次数是:" + config.getDailyLimit() + " 类型：" + functionType);
            return true;
        } else {
            DayLimit dayLimit = new Gson().fromJson(dayLimitJson, DayLimit.class);
            LogUtils.e("===弹框次数为:" + new Gson().toJson(dayLimit) + "  当前最大次数是:" + config.getDailyLimit() + " 类型：" + functionType);
            //如果是同一天判断次数是否小于配置的最大次数
            if (DateUtils.isSameDay(System.currentTimeMillis(), dayLimit.getUpdateTime())) {
                return dayLimit.getAlreadyPopCount() < config.getDailyLimit();
            } else {
                //不是同一天直接返回True
                return true;
            }
        }
    }

    //更新上一次弹框的时间
    public void updateLastPopTime(int onlyCode) {
        String type = getFuncTypeByOnlyCode(onlyCode);
        if (!TextUtils.isEmpty(type)) {
            PreferenceUtil.saveLastPopupTime(System.currentTimeMillis());
        }
    }

    //更新当天已经弹出的次数，每次+1
    public void autoIncrementDayLimit(int onlyCode) {
        String functionType = getFuncTypeByOnlyCode(onlyCode);
        if (!TextUtils.isEmpty(functionType)) {
            String dayLimitJson = PreferenceUtil.getPopupCount(functionType);
            DayLimit dayLimit;
            if (TextUtils.isEmpty(dayLimitJson)) {
                dayLimit = new DayLimit();
                dayLimit.setAlreadyPopCount(1);
            } else {
                dayLimit = new Gson().fromJson(dayLimitJson, DayLimit.class);
                dayLimit.setAlreadyPopCount(dayLimit.getAlreadyPopCount() + 1);
            }
            dayLimit.setUpdateTime(System.currentTimeMillis());
            PreferenceUtil.updatePopupCount(functionType, new Gson().toJson(dayLimit));
        }
    }

    //更新上一次使用功能的时间
    public void updateLastUsedFunctionTime(String funcType) {
        PreferenceUtil.saveLastUseFunctionTime(funcType, System.currentTimeMillis());
    }


    private String getFuncTypeByOnlyCode(int onlyCode) {
        switch (onlyCode) {
            case LocalPushType.TYPE_NOW_CLEAR:
                return SpCacheConfig.KEY_FUNCTION_CLEAR_RUBBISH;
            case LocalPushType.TYPE_SPEED_UP:
                return SpCacheConfig.KEY_FUNCTION_SPEED_UP;
            case LocalPushType.TYPE_PHONE_COOL:
                return SpCacheConfig.KEY_FUNCTION_COOL;
            case LocalPushType.TYPE_SUPER_POWER:
                return SpCacheConfig.KEY_FUNCTION_POWER_SAVING;
            default:
                return "";
        }
    }


    private BroadcastReceiver batteryReceiver;

    private boolean isCharged = false;  //是否为充电状态
    private int mBatteryPower = 50;  //当前电量监控
    private int temp = 30;           //点前电池温度


    public boolean getCharged() {
        return isCharged;
    }

    public int getTemp() {
        return temp;
    }

    public int getBatteryPower() {
        return mBatteryPower;
    }

    //判断是否充电
    public void checkCharge() {
        try {
            boolean usb = false;//usb充电
            boolean ac = false;//交流电
            boolean wireless = false; //无线充电
            int chargePlug = -1;

            IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            if (batteryReceiver == null) {
                batteryReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        //获取当前电量，如未获取具体数值，则默认为0
                        int batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                        //获取最大电量，如未获取到具体数值，则默认为100
                        int batteryScale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
                        mBatteryPower = (batteryLevel * 100 / batteryScale);
                        //获取当前电池温度
                        temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);
                        int i = temp / 10;
                        temp = i > 0 ? i : 30 + NumberUtils.mathRandomInt(1, 3);
                    }
                };
            }
            //注册接收器以获取电量信息
            Intent powerIntent = AppApplication.getInstance().registerReceiver(batteryReceiver, iFilter);
            //----判断是否为充电状态-------------------------------
            chargePlug = powerIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            usb = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
            ac = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
            //无线充电---API>=17
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                wireless = chargePlug == BatteryManager.BATTERY_PLUGGED_WIRELESS;
            }

//            Logger.i(SystemUtils.getProcessName(this) + "zz--" + (usb ? "usb" : ac ? "ac" : wireless ? "wireless" : ""));
            isCharged = usb || ac || wireless;
        } catch (Exception e) {
            e.printStackTrace();
            isCharged = false;
        }

        //充电状态变更
        if (PreferenceUtil.getInstants().getInt(SpCacheConfig.CHARGE_STATE) == 0 && isCharged && !ActivityCollector.isActivityExist(FullPopLayerActivity.class)) {

        } else if (PreferenceUtil.getInstants().getInt(SpCacheConfig.CHARGE_STATE) == 1 && !isCharged && !ActivityCollector.isActivityExist(FullPopLayerActivity.class)) {//拔电状态变更

        }
        if (!BuildConfig.SYSTEM_EN.contains("prod"))
//            ToastUtils.showShort("charge--" + (isCharged ? "充电中" : "未充电"));
//        Logger.i("zz---charge--" + (isCharged ? "充电中" : "未充电"));
            //更新sp当前充电状态
            PreferenceUtil.getInstants().saveInt(SpCacheConfig.CHARGE_STATE, isCharged ? 1 : 0);

    }

}
