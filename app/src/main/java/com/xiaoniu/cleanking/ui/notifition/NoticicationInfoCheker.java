package com.xiaoniu.cleanking.ui.notifition;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.os.Build;

import com.geek.push.entity.PushMsg;
import com.google.gson.Gson;
import com.xiaoniu.clean.deviceinfo.EasyBatteryMod;
import com.xiaoniu.clean.deviceinfo.EasyMemoryMod;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.jpush.JPushReceiver;
import com.xiaoniu.cleanking.keeplive.KeepAliveManager;
import com.xiaoniu.cleanking.scheme.Constant.SchemeConstant;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.bean.LockScreenBtnInfo;
import com.xiaoniu.cleanking.ui.main.bean.PushSettingList;
import com.xiaoniu.cleanking.ui.main.event.NotificationEvent;
import com.xiaoniu.cleanking.ui.tool.notify.manager.NotifyCleanManager;
import com.xiaoniu.cleanking.ui.tool.notify.utils.NotifyUtils;
import com.xiaoniu.cleanking.utils.FileQueryUtils;
import com.xiaoniu.cleanking.utils.NumberUtils;
import com.xiaoniu.cleanking.utils.PermissionUtils;
import com.xiaoniu.cleanking.utils.rxjava.BackGroundIPulseObserver;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.common.utils.DateUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;


/**
 * @author zzh
 * @date 2020/7/28 19
 * @mail：zhengzhihao@xiaoniuhy.com
 */
public class NoticicationInfoCheker  implements BackGroundIPulseObserver {

    private Context mContext;
    @Override
    public void onCreate() {
        mContext = AppApplication.getInstance();
    }

    @Override
    public void onPulse(long progress) {
        Map<String, PushSettingList.DataBean> map = PreferenceUtil.getCleanLog();
        for (Map.Entry<String, PushSettingList.DataBean> entry : map.entrySet()) {
            PushSettingList.DataBean dataBean = entry.getValue();
            if (isStartScan(dataBean)) { //检测是否达到扫描时间
                startScan(dataBean, mContext);
                //更新本地保存的操作时间
                dataBean.setLastTime(System.currentTimeMillis());
                map.put(entry.getKey(), dataBean);
                PreferenceUtil.saveCleanLogMap(map);
            }
        }
    }

    @Override
    public void onDestroy() {

    }

    //检测是否达到扫描时间
    public boolean isStartScan(PushSettingList.DataBean dataBean) {
        long lastTime = dataBean.getLastTime();
        long currentTime = System.currentTimeMillis();
        if (lastTime == 0) {
            Map<String, PushSettingList.DataBean> map = PreferenceUtil.getCleanLog();
            dataBean.setLastTime(currentTime);
            map.put(dataBean.getCodeX(), dataBean);
            PreferenceUtil.saveCleanLogMap(map);
            return false;
        }
        if (lastTime > 0 && (currentTime - lastTime) >= dataBean.getInterValTime() * 60 * 1000) {
            return true;
        }
        return false;

    }

    //检测是否达到扫描时间
    public boolean isStartScan() {
        long lastTime = PreferenceUtil.getLastScanRubbishTime();
        long currentTime = System.currentTimeMillis();
        if (lastTime == 0) {
            return true;
        } else {
            long elapseTime = DateUtils.getMinuteBetweenTimestamp(currentTime, lastTime);
            //每隔一小时扫描一次
            return elapseTime > 60;
        }
    }




    /**
     * 超过时间阈值开启功能
     *
     * @param dataBean
     */
    public void startScan(PushSettingList.DataBean dataBean, Context mContext) {
        String codeX = dataBean.getCodeX();
        switch (codeX) {
            case "push_1"://垃圾清理
                startScanAll(dataBean, mContext);
                break;
            case "push_2"://一键加速
                getAccessListBelow(dataBean, mContext);
                break;
            case "push_3"://超强省电
                getBatteryInfo(dataBean, mContext);
                break;
            case "push_4"://手机降温
                phoneCooling(dataBean, mContext);
                break;
            case "push10"://状态栏更新
                refNotify(dataBean, mContext);
                break;

        }
    }


    /**
     * 状态栏更新操作（只做状态栏更新）
     *
     * @param dataBean
     * @param cxt
     */
    public void refNotify(PushSettingList.DataBean dataBean, Context cxt) {
        int mNotifySize = 0;
        NotificationEvent event = new NotificationEvent();
        event.setType("notification");
        if (NotifyUtils.isNotificationListenerEnabled()) {//已获得通知栏权限
            if (null != NotifyCleanManager.getInstance().getAllNotifications()) {
                mNotifySize = NotifyCleanManager.getInstance().getAllNotifications().size();
            }
            if (mNotifySize >= 5) {
                event.setFlag(2);
            } else {
                event.setFlag(0);
            }
        } else {
            event.setFlag(0);
        }
        EventBus.getDefault().post(event);
    }
    /**
     * 手机降温提示
     *
     * @param dataBean
     * @param cxt
     */
    public void phoneCooling(PushSettingList.DataBean dataBean, Context cxt) {
        NotificationEvent event = new NotificationEvent();
        event.setType("cooling");
        EasyBatteryMod temper = new EasyBatteryMod(cxt);
        if (temper.getBatteryPercentage() > dataBean.getThresholdNum()) {
            event.setFlag(2);
        } else {
            event.setFlag(0);
        }
        EventBus.getDefault().post(event);

    }
    /**
     * 电量提示
     *
     * @param dataBean
     * @param cxt
     */
    public void getBatteryInfo(PushSettingList.DataBean dataBean, Context cxt) {
        NotificationEvent event = new NotificationEvent();
        event.setType("power");
        EasyBatteryMod easyBatteryMod = new EasyBatteryMod(cxt);
        if (easyBatteryMod.getBatteryPercentage() < dataBean.getThresholdNum()) {  //阀值以下且没有充电
            event.setFlag(2);
        } else {
            event.setFlag(0);
        }
        EventBus.getDefault().post(event);

    }

    /**
     * 一键加速
     */
    @SuppressLint("CheckResult")
    public void getAccessListBelow(PushSettingList.DataBean dataBean, Context cxt) {

            EasyMemoryMod memoryMod = new EasyMemoryMod(mContext);
            long percent = memoryMod.getAvailableRAM()*100/memoryMod.getTotalRAM();
            LockScreenBtnInfo btnInfo = new LockScreenBtnInfo(1);
            if ((int)percent > dataBean.getThresholdNum()) {
                btnInfo.setNormal(false);
            } else {
                btnInfo.setNormal(true);
            }
            btnInfo.setCheckResult(String.valueOf(percent));
            PreferenceUtil.getInstants().save("lock_pos02", new Gson().toJson(btnInfo));

            NotificationEvent event = new NotificationEvent();
            event.setType("speed");
            event.setAppendValue((int)percent);
            EventBus.getDefault().post(event);

    }
    /**
     * 获取清理文件大小
     */
    public void startScanAll(PushSettingList.DataBean dataBean, Context mContext) {
        long mbNum = NumberUtils.mathRandomInt(500, 1850);
        //保存上一次扫秒出来的垃圾大小
        //为了保证比扫描页面的数小，强制性的/2
//                long temp = mbNum / 2;
        PreferenceUtil.saveLastScanRubbishSize(mbNum);
        NotificationEvent event = new NotificationEvent();
        event.setType("clean");
        LockScreenBtnInfo btnInfo = new LockScreenBtnInfo(0);
        if (dataBean != null && mbNum > dataBean.getThresholdNum()) {//超过阀值，发送push
            event.setFlag(2);
            btnInfo.setNormal(false);
        } else {
            event.setFlag(0);
            btnInfo.setNormal(true);
        }
        btnInfo.setCheckResult(String.valueOf(mbNum));
        PreferenceUtil.getInstants().save("lock_pos01", new Gson().toJson(btnInfo));
        EventBus.getDefault().post(event);
//            }
//        });

    }



}
