package com.xiaoniu.cleanking.ui.notifition;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class AlarmTimer {

    /**
     * 设置周期性闹钟
     * @param context
     * @param firstTime
     * @param cycTime
     * @param action
     * @param AlarmManagerType
     */
    public static void setRepeatingAlarmTimer(Context context, long firstTime, long cycTime, String action, int AlarmManagerType) {
        Intent myIntent = new Intent();
        myIntent.setAction(action);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, myIntent, 0);
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManagerType, firstTime, cycTime, sender);
    }

    /**
     * 设置定时闹钟
     * @param context
     * @param cycTime
     * @param action
     * @param AlarmManagerType
     */
    public static void setAlarmTimer(Context context, long cycTime, String action, int AlarmManagerType) {
        Intent myIntent = new Intent();
        myIntent.setAction(action);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, myIntent, 0);
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.set(AlarmManagerType, cycTime, sender);
    }

    /**
     * 取消闹钟
     * @param context
     * @param action
     */
    public static void cancelAlarmTimer(Context context, String action) {
        Intent myIntent = new Intent();
        myIntent.setAction(action);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, myIntent, 0);
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(sender);
    }
}
