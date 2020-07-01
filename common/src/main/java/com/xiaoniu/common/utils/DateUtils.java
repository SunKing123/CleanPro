package com.xiaoniu.common.utils;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.Time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DateUtils {


    /**
     * 秒
     */
    private static String seconds = "秒前";
    /**
     * 分
     */
    private static String minutes = "分钟前";
    /**
     * 时
     */
    private static String hours = "小时前";

    /**
     * 天
     */
    private static String days = "天前";
    /**
     * 月
     */
    private static String months = "个月前";
    /**
     * 刚刚
     */
    private static String before = "刚刚";

    /**
     * The format used is 2013-07-13 05:07
     */
    public static final String PATTERN_YMDHM = "yyyy-MM-dd HH:mm";

    public static final String PATTERN_YMDHM2 = "yyyy/MM/dd HH:mm";

    public static final String PATTERN_YMDHMS = "yyyy-MM-dd HH:mm:ss";

    /**
     * The format used is 2013-07-13
     */
    public static final String PATTERN_YMD = "yyyy-MM-dd";

    public static final SimpleDateFormat YYYYMMDDHHMMSS_FORMAT_SIMPLE = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());

    /**
     * The format used is 05:07
     */
    public static final String PATTERN_TIME = "HH:mm:ss";

    public static final String PATTERN_FEEDBACK_TIME = "HH:mm";

    public static String format(Date date, String format) {

        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }

    public static String format(String lo, String format) {
        return format(lo, format, TimeUnit.SECONDS);
    }

    public static String format(String lo, String format, TimeUnit timeU) {
        long time = 0L;
        try {
            time = Long.parseLong(lo);
        } catch (Exception e) {
        }
        if (timeU == TimeUnit.SECONDS) {
            time *= 1000;
        }
        return format(new Date(time), format);
    }

    public static String getYearMonthDay() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date curDate = new Date(System.currentTimeMillis());
        String str = formatter.format(curDate);
        return str;
    }

    public static String getDate(String dateStr, String format) {
        if (TextUtils.isEmpty(dateStr)) return "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = dateFormat.parse(dateStr);
            return format(date, format);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static long getTimeLong(String str) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = dateFormat.parse(str);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static String getStringDate(String lo, String format) {
        long time = 0L;
        try {
            time = Long.parseLong(lo);
        } catch (Exception e) {
        }
        Date date = new Date(time);
        SimpleDateFormat sd = new SimpleDateFormat(format);
        return sd.format(date);
    }


    // 获取当前月份
    public static int currentMon() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.MONTH) + 1;
    }

    /**
     * 时间戳转特定格式时间
     *
     * @param dataFormat
     * @param timeStamp
     * @return
     */
    public static String formatData(String dataFormat, long timeStamp) {
        if (timeStamp == 0) {
            return "";
        }
        timeStamp = timeStamp * 1000;
        SimpleDateFormat format = new SimpleDateFormat(dataFormat);
        return format.format(new Date(timeStamp));
    }

    /**
     * 描述：获取milliseconds表示的日期时间的字符串.
     *
     * @return String 日期时间字符串
     */
    public static String getStringByFormat(long milliseconds) {
        String thisDateTime = null;
        try {
            SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            thisDateTime = mSimpleDateFormat.format(milliseconds);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return thisDateTime;
    }

    /**
     * 判断当前系统时间是否在指定时间的范围内
     *
     * @param beginHour 开始小时，例如22
     * @param beginMin  开始小时的分钟数，例如30
     * @param endHour   结束小时，例如 8
     * @param endMin    结束小时的分钟数，例如0
     * @return true表示在范围内，否则false
     */
    public static boolean isCurrentInTimeScope(int beginHour, int beginMin, int endHour, int endMin) {
        boolean result = false;
        final long aDayInMillis = 1000 * 60 * 60 * 24;
        final long currentTimeMillis = System.currentTimeMillis();

        Time now = new Time();
        now.set(currentTimeMillis);

        Time startTime = new Time();
        startTime.set(currentTimeMillis);
        startTime.hour = beginHour;
        startTime.minute = beginMin;

        Time endTime = new Time();
        endTime.set(currentTimeMillis);
        endTime.hour = endHour;
        endTime.minute = endMin;

        if (!startTime.before(endTime)) {
            // 跨天的特殊情况（比如22:00-8:00）
            startTime.set(startTime.toMillis(true) - aDayInMillis);
            result = !now.before(startTime) && !now.after(endTime); // startTime <= now <= endTime
            Time startTimeInThisDay = new Time();
            startTimeInThisDay.set(startTime.toMillis(true) + aDayInMillis);
            if (!now.before(startTimeInThisDay)) {
                result = true;
            }
        } else {
            // 普通情况(比如 8:00 - 14:00)
            result = !now.before(startTime) && !now.after(endTime); // startTime <= now <= endTime
        }
        return result;

    }


    /**
     * 将时间戳转换为指定时间(按指定格式).
     *
     * @param timestamp 时间戳.
     * @param pattern   时间格式.
     * @return 指定时间(按指定格式).
     * @version 1.0
     * @createTime 2014年5月14日, 下午3:58:22
     * @updateTime 2014年5月14日, 下午3:58:22
     * @createAuthor paladin
     * @updateAuthor paladin
     * @updateInfo
     */
    public static String timestampToPatternTime(long timestamp, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(new Date(timestamp));
    }


    /**
     * 返回当前时间的格式为 yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(System.currentTimeMillis());
    }


    /**
     * 获取当前时间(按指定格式).
     *
     * @param pattern 时间格式.
     * @return 当前时间(按指定格式).
     * @version 1.0
     * @createTime 2014年5月14日, 下午3:54:59
     * @updateTime 2014年5月14日, 下午3:54:59
     * @createAuthor paladin
     * @updateAuthor paladin
     * @updateInfo
     */
    public static String getCurrentTime(long systemTime, String pattern) {
        return timestampToPatternTime(systemTime, pattern);
    }

    /*
     * 判断两个时间戳之间间隔多少分钟
     */
    public static long getMinuteBetweenTimestamp(Long firstTimestamp, Long twoTimestamp) {
        return Math.abs(firstTimestamp - twoTimestamp) / (1000 * 60);
    }


    /**
     * 判断是否为同一天
     *
     * @param time1 时间丑
     */
    public static boolean isSameDay(long time1, long time2) {
        Date date1 = new Date();
        date1.setTime(time1);

        Date date2 = new Date();
        date2.setTime(time2);

        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(date1);
        //
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(date2);
        if (calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
                calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH) &&
                calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH)) {
            return true;

        }
        return false;
    }


    /**
     * 判断是否为同一个月
     *
     * @param time1 时间丑
     */
    public static boolean isSameMonth(long time1, long time2) {
        Date date1 = new Date();
        date1.setTime(time1);

        Date date2 = new Date();
        date2.setTime(time2);

        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(date1);
        //
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(date2);
        if (calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH)) {
            return true;

        }
        return false;
    }


    /**
     * 判断是否为昨天(效率比较高)
     *
     * @param day 传入的 时间  "2016-06-28 10:10:30" "2016-06-28" 都可以
     * @return true昨天 false不是
     * @throws ParseException
     */
    public static boolean isYesterday(long day) {

        Calendar pre = Calendar.getInstance();
        Date predate = new Date(System.currentTimeMillis());
        pre.setTime(predate);

        Calendar cal = Calendar.getInstance();
        Date date = new Date(day);
        cal.setTime(date);

        if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
            int diffDay = cal.get(Calendar.DAY_OF_YEAR)
                    - pre.get(Calendar.DAY_OF_YEAR);

            if (diffDay == -1) {
                return true;
            }
        }
        return false;
    }


    /**
     * 判断是否间隔N天以上
     *
     * @param day
     * @throws ParseException
     */
    public static boolean isOverThreeDay(long day, int count) {
        Calendar pre = Calendar.getInstance();
        Date predate = new Date(System.currentTimeMillis());
        pre.setTime(predate);

        Calendar cal = Calendar.getInstance();
        Date date = new Date(day);
        cal.setTime(date);

        if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
            int diffDay = cal.get(Calendar.DAY_OF_YEAR) - pre.get(Calendar.DAY_OF_YEAR);

            if (diffDay < (0 - count)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 计算距离当前时间.
     *
     * @param timestamp  当时时间戳.
     * @param systemTime 服务器时间戳.
     * @return 若距离当前时间小于1分钟则返回xx秒之前，若距离当前时间小于1小时则返回xx分钟之前，若距离当前时间小于1天则返回xx小时前，
     * 若时间为今年则返回MM-dd，否则返回yyyy-MM-dd.
     */
    public static String fromTheCurrentTime(long systemTime, long timestamp) {
        if (timestamp < 100000000000L) {
            timestamp = timestamp * 1000;
        }
        if (systemTime < 1000000000000L) {
            systemTime = systemTime * 1000;
        }
        //
        long timeDifferenceMillisecond = (timestamp - systemTime) / 1000;

        if (timeDifferenceMillisecond > 0 && timeDifferenceMillisecond < 60) {
            return before;
        } else if (timeDifferenceMillisecond / 60 >= 1 && timeDifferenceMillisecond / 60 <= 60) {
            return String.valueOf(timeDifferenceMillisecond / 60) + minutes;
        } else if (timeDifferenceMillisecond / 3600 >= 1 && timeDifferenceMillisecond / 3600 < 24) {
            return String.valueOf(timeDifferenceMillisecond / 3600) + hours;
        } else if (timeDifferenceMillisecond / 86400 >= 1 && timeDifferenceMillisecond / 86400 < 30) {
            //return timestampToPatternTime(systemTime, "MM-dd HH:mm");
            return String.valueOf(timeDifferenceMillisecond / 86400) + days;
        } else if (timeDifferenceMillisecond / 2592000 >= 1 && timeDifferenceMillisecond / 2592000 < 12) {
            //return timestampToPatternTime(systemTime, "yyyy-MM-dd");
            return String.valueOf(timeDifferenceMillisecond / 2592000) + months;
        } else if (timestampToPatternTime(timestamp, "yyyy").equals(getCurrentTime(systemTime, "yyyy"))) {
            return timestampToPatternTime(systemTime, "yyyy-MM-dd");
        } else {
            return timestampToPatternTime(systemTime, "yyyy-MM-dd");
        }
    }


    public static String formatCurrentTime(long systemTime, long timestamp) {

        if (timestamp < 1000000000000L) {
            timestamp = timestamp * 1000;
        }
        if (systemTime < 1000000000000L) {
            systemTime = systemTime * 1000;
        }
        long timeDifferenceMillisecond = (timestamp - systemTime) / 1000;
        if (timeDifferenceMillisecond >= 0 && timeDifferenceMillisecond < 60) {
            return before;
        } else if (timeDifferenceMillisecond / 60 >= 1 && timeDifferenceMillisecond / 60 <= 60) {
            return String.valueOf(timeDifferenceMillisecond / 60) + minutes;
        } else if (timeDifferenceMillisecond / 3600 >= 1 && timeDifferenceMillisecond / 3600 < 24) {
            return String.valueOf(timeDifferenceMillisecond / 3600) + hours;
        } else if (timeDifferenceMillisecond / 3600 >= 24 && timeDifferenceMillisecond / 3600 < 48) {
            if (!isSameDay(systemTime / 1000, timestamp / 1000 - 3600 * 24)) {
                return "前天 " + timestampToPatternTime(systemTime, "HH:mm");
            }
            return "昨天 " + timestampToPatternTime(systemTime, "HH:mm");

        } else if (timeDifferenceMillisecond / 86400 >= 2 && timeDifferenceMillisecond / 86400 < 3) {
            if (!isSameDay(systemTime / 1000, timestamp / 1000 - 3600 * 24 * 2)) {
                return timestampToPatternTime(systemTime, "MM-dd HH:mm");
            }
            return "前天 " + timestampToPatternTime(systemTime, "HH:mm");
        } else {
            String Now = timestampToPatternTime(timestamp, "yyyy");
            String Sys = timestampToPatternTime(systemTime, "yyyy");
            if (Now.equals(Sys)) {
                return timestampToPatternTime(systemTime, "MM-dd HH:mm");
            }
            return timestampToPatternTime(systemTime, "yy-MM-dd");
        }

    }


    /**
     * 时间格式化（刚刚、几分钟前、几小时前、昨天、前天、年-月-日）
     */
    public static String getShortTime(long time) {
        String shortString = "";
        if (time == 0) {
            return shortString;
        }

        long now = Calendar.getInstance().getTimeInMillis();
        long datetime = (now - time) / 1000;
        if (datetime > 365 * 24 * 60 * 60) {
            shortString = formatDate(PATTERN_YMD, time);
            //shortString = FORMAT_DATE.format(new Date(time));
        } else if (datetime > 24 * 60 * 60 && ((int) (datetime / (24 * 60 * 60))) == 2) {
            shortString = "前天";
        } else if (datetime > 24 * 60 * 60 && ((int) (datetime / (24 * 60 * 60))) == 1) {
            shortString = "昨天";
        } else if (datetime > 60 * 60) {
            shortString = (int) (datetime / (60 * 60)) + "小时前";
        } else if (datetime > 60) {
            shortString = (int) (datetime / (60)) + "分钟前";
        } else {
            shortString = "刚刚";
        }
        return shortString;
    }

    /**
     * 时间格式化
     */
    public static String formatDate(String format, Long time) {
        return formatDate(new SimpleDateFormat(format, Locale.CHINA), time);
    }


    /**
     * 时间格式化
     */
    public static String formatDate(SimpleDateFormat format, Long time) {
        if (null == time || time <= 0) {
            return "";
        }
        return format.format(new Date(String.valueOf(time).length() == 13 ? time : time * 1000));
    }


    public static String formatSimplifyCurrentTime(long systemTime, long timestamp) {

        if (timestamp < 1000000000000l) {
            timestamp = timestamp * 1000;
        }
        if (systemTime < 1000000000000L) {
            systemTime = systemTime * 1000;
        }
        long timeDifferenceMillisecond = (timestamp - systemTime) / 1000;
        if (timeDifferenceMillisecond < 0) {
            timeDifferenceMillisecond *= -1;
        }
        if (timeDifferenceMillisecond >= 0 && timeDifferenceMillisecond < 60) {
            return before;
        } else if (timeDifferenceMillisecond / 60 >= 1 && timeDifferenceMillisecond / 60 <= 60) {
            return String.valueOf(timeDifferenceMillisecond / 60) + minutes;
        } else if (timeDifferenceMillisecond / 3600 >= 1 && timeDifferenceMillisecond / 3600 < 24) {
            return String.valueOf(timeDifferenceMillisecond / 3600) + hours;
        } else if (timeDifferenceMillisecond / 3600 >= 24 && timeDifferenceMillisecond / 3600 < 48) {
            if (!isSameDay(systemTime / 1000, timestamp / 1000 - 3600 * 24)) {
                return "前天";
            }
            return "昨天";

        } else if (timeDifferenceMillisecond / 86400 >= 2 && timeDifferenceMillisecond / 86400 < 3) {
            if (!isSameDay(systemTime / 1000, timestamp / 1000 - 3600 * 24 * 2)) {
                return timestampToPatternTime(systemTime, "MM-dd");
            }
            return "前天";
        } else {
            String Now = timestampToPatternTime(timestamp, "yyyy");
            String Sys = timestampToPatternTime(systemTime, "yyyy");
            if (Now.equals(Sys)) {
                return timestampToPatternTime(systemTime, "MM-dd");
            }
            return timestampToPatternTime(systemTime, "yy-MM-dd");
        }

    }


    private static SimpleDateFormat sHourFormat24 = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private static SimpleDateFormat sHourFormat12 = new SimpleDateFormat("hh:mm", Locale.getDefault());

    public static String getHourString(Context context, long time) {
        String strTimeFormat = android.provider.Settings.System.getString(context.getContentResolver(),
                android.provider.Settings.System.TIME_12_24);
        if (("12").equals(strTimeFormat)) {
            try {
                return sHourFormat12.format(time);
            } catch (Exception e) {
            }
        }
        return sHourFormat24.format(time);
    }


    /**
     * 获取"yyyyMMddHHmmss"格式的14位时间字符串
     *
     * @return
     */
    public static String getCurrentSimpleYYYYMMDDHHMM() {
        Date date = new Date();
        return YYYYMMDDHHMMSS_FORMAT_SIMPLE.format(date);
    }

}
