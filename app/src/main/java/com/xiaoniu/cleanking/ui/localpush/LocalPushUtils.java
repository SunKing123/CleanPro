package com.xiaoniu.cleanking.ui.localpush;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.utils.LogUtils;
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
}
