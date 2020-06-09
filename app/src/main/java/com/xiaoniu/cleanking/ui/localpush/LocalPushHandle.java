package com.xiaoniu.cleanking.ui.localpush;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.common.utils.DateUtils;

public class LocalPushHandle {
    private LocalPushHandle() {
    }

    private static class Holder {
        static LocalPushHandle pushHandle = new LocalPushHandle();
    }

    public static LocalPushHandle getInstance() {
        return Holder.pushHandle;
    }

    private boolean commonParamsVerify(LocalPushConfigModel.Item config, String functionType) {
        //1.先判断今天允许弹出的次数是否小于最大次数
        if (isTodayLimitAllowed(config, functionType)) {
            Long currentTimeStamp = System.currentTimeMillis();
            //2.判断距上次使用该功能是否已经大于配置指定的时间
            Long lastUsedTime = PreferenceUtil.getLastUseFunctionTime(functionType);
            //计算上次使用该功能距离现在过于多少分钟
            long elapseUsedTime = DateUtils.getMinuteBetweenTimestamp(currentTimeStamp, lastUsedTime);
            if (elapseUsedTime >= config.getFunctionUsedInterval()) {
                //判断上次弹框的时间是否已经大于配置指定的时间
                Long lastPopTime = PreferenceUtil.getLastUseFunctionTime(functionType);
                //计算上次弹框距离现在过于多少分钟
                long elapsePopTime = DateUtils.getMinuteBetweenTimestamp(currentTimeStamp, lastPopTime);
                return elapsePopTime >= config.getPopWindowInterval();
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean allowPopClear(LocalPushConfigModel.Item config) {
        return commonParamsVerify(config, SpCacheConfig.KEY_FUNCTION_CLEAR_RUBBISH);
    }

    public boolean allowPopSpeedUp(LocalPushConfigModel.Item config) {
        return commonParamsVerify(config, SpCacheConfig.KEY_FUNCTION_SPEED_UP);
    }


    public boolean allowPopCool(LocalPushConfigModel.Item config, int temperature) {
        if (commonParamsVerify(config, SpCacheConfig.KEY_FUNCTION_COOL)) {
            //降温在满足公共条件的情况下，还要判断是否大于温度的阈值
            return temperature > config.getThresholdNum();
        } else {
            return false;
        }
    }


    public boolean allowPopPowerSaving(LocalPushConfigModel.Item config, boolean isCharged, int mBatteryPower) {
        if (isCharged) {
            return false;
        } else {
            if (commonParamsVerify(config, SpCacheConfig.KEY_FUNCTION_POWER_SAVING)) {
                //省电除了判断服务器返回的参数要符合条件外，还要判断电量是否小于阈值
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
            return true;
        } else {
            DayLimit dayLimit = new Gson().fromJson(dayLimitJson, DayLimit.class);
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
            PreferenceUtil.saveLastPopupTime(type, System.currentTimeMillis());
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
            case 1:
                return SpCacheConfig.KEY_FUNCTION_CLEAR_RUBBISH;
            case 2:
                return SpCacheConfig.KEY_FUNCTION_SPEED_UP;
            case 6:
                return SpCacheConfig.KEY_FUNCTION_COOL;
            case 9:
                return SpCacheConfig.KEY_FUNCTION_POWER_SAVING;
            default:
                return "";
        }
    }
}
