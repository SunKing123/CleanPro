package com.xiaoniu.cleanking.ui.tool.wechat.util;

import com.xiaoniu.cleanking.ui.tool.wechat.bean.Constants;

import java.util.Calendar;

public class TimeUtil {
    public static boolean getShowTimeLimit(int i, String str) {
        int i2 = Calendar.getInstance().get(11);
        if (i > i2 || i2 > 24 || !isTimeToshow(str)) {
            return false;
        }
        PrefsCleanUtil.getInstance().putInt(Constants.NOTIFY_GARB_TIME, Calendar.getInstance().get(6));
        return true;
    }

    public static boolean getShowTimeLimitHour(String str, int i) {
        int i2 = Calendar.getInstance().get(11);
        if ((i2 - PrefsCleanUtil.getInstance().getInt(str, 0) < i || isTimeToshow(str + "day")) && !isTimeToshow(str + "day")) {
            return false;
        }
        saveTheTime(str + "day");
        PrefsCleanUtil.getInstance().putInt(str, i2);
        return true;
    }

    public static boolean getShowTimeLimitHourByCurrent(String str, int i) {
        if ((System.currentTimeMillis() - PrefsCleanUtil.getInstance().getLong(str, 0)) / 3600000 < ((long) i)) {
            return false;
        }
        PrefsCleanUtil.getInstance().putLong(str, System.currentTimeMillis());
        return true;
    }

    public static boolean getShowTimeLimitDay(String str, int i) {
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - PrefsCleanUtil.getInstance().getLong(str + "day_long", 0) < ((long) (i * 24 * 60 * 60 * 1000))) {
            return false;
        }
        PrefsCleanUtil.getInstance().putLong(str + "day_long", currentTimeMillis);
        return true;
    }

    public static boolean getShowTimeLimitDayOverZero(String str, int i) {
        long currentTimeMillis = System.currentTimeMillis();
        if (getTimeByDay() <= changeTimeToDay(PrefsCleanUtil.getInstance().getLong(str + "day_long", 0))) {
            return false;
        }
        PrefsCleanUtil.getInstance().putLong(str + "day_long", currentTimeMillis);
        return true;
    }

    public static boolean isTimeToshow(String str) {
        if (PrefsCleanUtil.getInstance().getInt(str, 0) != Calendar.getInstance().get(6)) {
            return true;
        }
        return false;
    }

    public static void saveTheTime(String str) {
        PrefsCleanUtil.getInstance().putInt(str, Calendar.getInstance().get(6));
    }

    public static int getTimeByDay() {
        return (int) ((((Long.valueOf(System.currentTimeMillis() + 28800000).longValue() / 1000) / 60) / 60) / 24);
    }

    public static int changeTimeToDay(long j) {
        return (int) (((((28800000 + j) / 1000) / 60) / 60) / 24);
    }
}