package com.installment.mall.utils;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.CalendarContract;

import com.installment.mall.app.AppApplication;
import com.installment.mall.ui.main.widget.SPUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by zy on 2017/6/14.
 */

public class CalendarEventUtil {

    private static String CALANDER_URL = "content://com.android.calendar/calendars";
    private static String CALANDER_EVENT_URL = "content://com.android.calendar/events";
    private static String CALANDER_REMIDER_URL = "content://com.android.calendar/reminders";

    private static String CALENDARS_NAME = "shandai";
    private static String CALENDARS_ACCOUNT_NAME = "shandai@gmail.com";
    private static String CALENDARS_ACCOUNT_TYPE = "com.feilu.flashloan";
    private static String CALENDARS_DISPLAY_NAME = "shandai";

    /**
     * 日历权限申请
     */
    public static final int REQUEST_DELETE_CALENDAR = 9000;

    /**
     * 续期 删除之前的事件
     */
    public static final int CALENDAR_DELETE_EXTENSION = 1;
    /**
     * 还款删除最后还款日 日历提醒
     */
    public static final int CALENDAR_DELETE_PAY = 2;


    /**
     * 检查是否已经添加了日历账户，如果没有添加先添加一个日历账户再查询
     */
    public static int checkAndAddCalendarAccount() {
        int oldId = checkCalendarAccount();
        if (oldId >= 0) {
            return oldId;
        } else {
            long addId = addCalendarAccount();
            if (addId >= 0) {
                return checkCalendarAccount();
            } else {
                return -1;
            }
        }
    }


    private static int checkCalendarAccount() {
        Cursor userCursor = null;
        try {
            userCursor = AppApplication.getInstance().getContentResolver().query(Uri.parse(CALANDER_URL), null, null, null, null);
            //查询返回空值
            if (userCursor == null) {
                return -1;
            }
            int count = userCursor.getCount();
            //存在现有账户，取第一个账户的id返回
            if (count > 0) {
                userCursor.moveToFirst();
                return userCursor.getInt(userCursor.getColumnIndex(CalendarContract.Calendars._ID));
            } else {
                return -1;
            }
        } catch (Exception e) {
//            System.out.println(e);
            if (userCursor != null) {
                userCursor.close();
            }
            return -1;
        } finally {
            if (userCursor != null) {
                userCursor.close();
            }
        }
    }

    //添加账户
    private static long addCalendarAccount() {
        TimeZone timeZone = TimeZone.getDefault();
        ContentValues value = new ContentValues();
        value.put(CalendarContract.Calendars.NAME, CALENDARS_NAME);

        value.put(CalendarContract.Calendars.ACCOUNT_NAME, CALENDARS_ACCOUNT_NAME);
        value.put(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE);
        value.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, CALENDARS_DISPLAY_NAME);
        value.put(CalendarContract.Calendars.VISIBLE, 1);
        value.put(CalendarContract.Calendars.CALENDAR_COLOR, Color.BLUE);
        value.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
        value.put(CalendarContract.Calendars.SYNC_EVENTS, 1);
        value.put(CalendarContract.Calendars.CALENDAR_TIME_ZONE, timeZone.getID());
        value.put(CalendarContract.Calendars.OWNER_ACCOUNT, CALENDARS_ACCOUNT_NAME);
        value.put(CalendarContract.Calendars.CAN_ORGANIZER_RESPOND, 0);

        long id = -1;
        try {
            Uri calendarUri = Uri.parse(CALANDER_URL);
            calendarUri = calendarUri.buildUpon()
                    .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                    .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, CALENDARS_ACCOUNT_NAME)
                    .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE)
                    .build();

            Uri result = AppApplication.getInstance().getContentResolver().insert(calendarUri, value);
            id = result == null ? -1 : ContentUris.parseId(result);

        } catch (Exception e) {
            e.printStackTrace();
            return id;
        }
        return id;
    }

    public static void addCalendarEvents(String title, String description, String date) {
        //写入提前一天的提醒
        String previouseDay = TimeUtil.getNextDayOfCurrent(date, -1);
        //写入提前两天的提醒
        String previouseTwoDay = TimeUtil.getNextDayOfCurrent(date, -2);
        //日历写入一天4个时间段
        CalendarEventUtil.addCalendarEvent(title, description, date + " 08:00:00", date + " 09:00:00");
        CalendarEventUtil.addCalendarEvent(title, description, date + " 12:00:00", date + " 13:00:00");
        CalendarEventUtil.addCalendarEvent(title, description, date + " 16:00:00", date + " 17:00:00");
        CalendarEventUtil.addCalendarEvent(title, description, date + " 23:00:00", date + " 24:00:00");
        //提前一天
        CalendarEventUtil.addCalendarEvent(title, description, previouseDay + " 12:00:00", previouseDay + " 13:00:00");
        //提前两天
        CalendarEventUtil.addCalendarEvent(title, description, previouseTwoDay + " 12:00:00", previouseTwoDay + " 13:00:00");
        SPUtil.setString(AppApplication.getInstance(), SPUtil.TITLE_CALENDAR, title);

    }

    /**
     * @param title       标题
     * @param description 事件
     * @param dataTimeEnd 截止时间
     */
    public static void addCalendarEvent(String title, String description, String dataTimeStart, String dataTimeEnd) {
        // 获取日历账户的id
        int calId = checkAndAddCalendarAccount();
        if (calId < 0) {
            // 获取账户id失败直接返回，添加日历事件失败
            return;
        }
        ContentValues event = new ContentValues();
        event.put("title", title);
        event.put("description", description);
        // 插入账户的id
        event.put("calendar_id", calId);
        String dateFormat = "yyyy-MM-dd HH:mm:ss";
        Calendar mCalendar = Calendar.getInstance();
//        String eventStartTime1 = data + " " +"08:00:00";
//        String eventEndTime1 = data + " " +"09:00:00";
        long start1 = getDatelongMills(dataTimeStart, dateFormat);
        long end1 = getDatelongMills(dataTimeEnd, dateFormat);
        //设置开始时间
        mCalendar.setTimeInMillis(start1);
        //设置终止时间
        mCalendar.setTimeInMillis(end1);

        event.put(CalendarContract.Events.DTSTART, start1);
        event.put(CalendarContract.Events.DTEND, end1);
        //设置有闹钟提醒
        event.put(CalendarContract.Events.HAS_ALARM, 1);
        //这个是时区，必须有，
        event.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Shanghai");
        //添加事件
        Uri newEvent = AppApplication.getInstance().getContentResolver().insert(Uri.parse(CALANDER_EVENT_URL), event);
        if (newEvent == null) {
            // 添加日历事件失败直接返回
            return;
        }
        //事件提醒的设定
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Reminders.EVENT_ID, ContentUris.parseId(newEvent));
        // 提前0分钟有提醒
        values.put(CalendarContract.Reminders.MINUTES, 0);
        values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        Uri uri = AppApplication.getInstance().getContentResolver().insert(Uri.parse(CALANDER_REMIDER_URL), values);
        if (uri == null) {
            // 添加闹钟提醒失败直接返回
            return;
        }
    }

    /**
     * 获取指定时间的毫秒值
     */
    public static long getDatelongMills(String dateStr, String fomat) {
        SimpleDateFormat sdf = new SimpleDateFormat(fomat);
        Date date = null;
        try {
            date = sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long longDate = date.getTime();
        return longDate;
    }

}
