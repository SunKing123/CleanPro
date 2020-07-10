package com.xiaoniu.cleanking.keeplive.receive;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.collection.SparseArrayCompat;

import com.geek.push.entity.PushMsg;
import com.google.gson.Gson;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.jpush.JPushReceiver;
import com.xiaoniu.cleanking.keeplive.KeepAliveManager;
import com.xiaoniu.cleanking.keeplive.service.LocalService;
import com.xiaoniu.cleanking.scheme.Constant.SchemeConstant;
import com.xiaoniu.cleanking.scheme.utils.ActivityCollector;
import com.xiaoniu.cleanking.ui.localpush.LocalPushConfigModel;
import com.xiaoniu.cleanking.ui.localpush.LocalPushType;
import com.xiaoniu.cleanking.ui.localpush.LocalPushUtils;
import com.xiaoniu.cleanking.ui.lockscreen.FullPopLayerActivity;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.bean.InsertAdSwitchInfoList;
import com.xiaoniu.cleanking.ui.main.bean.JunkGroup;
import com.xiaoniu.cleanking.ui.main.bean.LockScreenBtnInfo;
import com.xiaoniu.cleanking.ui.main.bean.PushSettingList;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.event.NotificationEvent;
import com.xiaoniu.cleanking.ui.tool.notify.manager.NotifyCleanManager;
import com.xiaoniu.cleanking.ui.tool.notify.utils.NotifyUtils;
import com.xiaoniu.cleanking.utils.AppLifecycleUtil;
import com.xiaoniu.cleanking.utils.CleanUtil;
import com.xiaoniu.cleanking.utils.CollectionUtils;
import com.xiaoniu.cleanking.utils.FileQueryUtils;
import com.xiaoniu.cleanking.utils.LogUtils;
import com.xiaoniu.cleanking.utils.NumberUtils;
import com.xiaoniu.cleanking.utils.PermissionUtils;
import com.xiaoniu.cleanking.utils.net.RxUtil;
import com.xiaoniu.cleanking.utils.update.MmkvUtil;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.common.utils.ContextUtils;
import com.xiaoniu.common.utils.DateUtils;
import com.xiaoniu.common.utils.NetworkUtils;
import com.xiaoniu.common.utils.StatisticsUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author zhengzhihao
 * @date 2019/10/30 14
 * @mail：zhengzhihao@hellogeek.com
 */
public class TimingReceiver extends BroadcastReceiver {

    private static final String TAG = "TimingReceiver";
    private Context mContext;
    private int mBatteryPower = 50;  //当前电量监控
    private int temp = 30;          //当前电池温度
    private boolean isCharged = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;

        String action = intent.getStringExtra("action");
        if (!TextUtils.isEmpty(action)) {


            mBatteryPower = intent.getIntExtra("battery", 50);
            temp = intent.getIntExtra("temp", 30);
            isCharged = intent.getBooleanExtra("isCharged", false);

            switch (action) {
                case "scan_heart"://本地push心跳
                   /* Map<String, PushSettingList.DataBean> map = PreferenceUtil.getCleanLog();
                    for (Map.Entry<String, PushSettingList.DataBean> entry : map.entrySet()) {
                        PushSettingList.DataBean dataBean = entry.getValue();
                        if (isStartScan(dataBean)) { //检测是否达到扫描时间
                            startScan(dataBean, context);
                            //更新本地保存的操作时间
                            dataBean.setLastTime(System.currentTimeMillis());
                            map.put(entry.getKey(), dataBean);
                            PreferenceUtil.saveCleanLogMap(map);
                        }
                    }*/
                    if (isStartScan()) { //检测是否达到扫描时间
                        startScanAll(null, mContext);
                    }

                    Intent i = new Intent(context, LocalService.class);
                    i.putExtra("action", "heartbeat");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(i);
                    } else {
                        context.startService(i);
                    }
                    break;
                case "home"://home按键触发
                    if (null == context) {
                        return;
                    }
                    long lastAppPressHome = MmkvUtil.getLong(SpCacheConfig.KEY_LAST_CLEAR_APP_PRESSED_HOME, 0L);
                    if (lastAppPressHome > 0) {
                        long current = System.currentTimeMillis();
                        long period = current / 1000 - lastAppPressHome / 1000;
                        if (period < 10 * 60) {
                            //if (period < 5) {
                            LogUtils.e("====距离上次清理APP触发Home键过了" + period + "秒小于限制时间，直接返回");
                            return;
                        }
                    }
                    startDialogActivityOnLauncher(context, 10 * 1000);
                  /*   if (!RomUtils.checkFloatWindowPermission(context)) {
                        LogUtils.e("====TimingReceiver中 没有PopWindow权限===");
                        return;
                    }
                   if (WindowUtil.getInstance().isShowing()) {
                        LogUtils.e("====TimingReceiver中 PopWindow正在弹出===");
                        return;
                    }*/
                    long homePressTime = intent.getLongExtra("homePressed", 0L);
                    showLocalPushAlertWindow(context, homePressTime);
                    break;
                case "app_add_full"://锁屏打开页面||home按键触发  //应用植入插屏全屏广告
                    if (null != context) {
                        startFullActivity(context.getApplicationContext());
                    }
                    break;
                case "unlock_screen"://锁屏打开页面
                    LogUtils.e("================监听到锁屏打开页面");
                    startDialogActivityOnLauncher(context, 3000);
                    break;
                default:
                    break;
            }
        }


    }


   /* private void showToastPopWindow(Context context, Long homePressTime, LocalPushConfigModel.Item item) {
        StatisticsUtils.customTrackEvent("local_push_window_custom", "推送弹窗满足推送时机弹窗创建时", "", "local_push_window");
        long current = System.currentTimeMillis();
        long period = current / 1000 - homePressTime / 1000;
        LocalPushWindow toast = new LocalPushWindow(context, item);
        if (period >= 3) {
            toast.show(1000 * 20);
        } else {
            new Handler().postDelayed(() -> {
                toast.show(1000 * 20);
            }, (3 - period) * 1000);
        }
    }*/

    private void startPopActivity(Context context, Long homePressTime, LocalPushConfigModel.Item item) {
        StatisticsUtils.customTrackEvent("local_push_window_custom", "推送弹窗满足推送时机弹窗创建时", "", "local_push_window");
        Intent screenIntent = new Intent();
        screenIntent.setClassName(context.getPackageName(), SchemeConstant.StartFromClassName.CLASS_LOCAL_PUSH_ACTIVITY);
        screenIntent.putExtra("config", item);
        screenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        screenIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        screenIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        screenIntent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
        long current = System.currentTimeMillis();
        long period = current / 1000 - homePressTime / 1000;
        //本来是延迟3秒后弹出，但是启动Activity时间较长，所以这里改成2秒(popup window里面延迟了一秒，所以这里先不延迟)
        context.startActivity(screenIntent);
    }


    private void showLocalPushAlertWindow(Context context, Long homePressTime) {

        //1.读取本地缓存的推送配置Config列表
        SparseArrayCompat<LocalPushConfigModel.Item> map = PreferenceUtil.getLocalPushConfig();

        //2.判断【一键加速】功能是否满足推送条件
        LocalPushConfigModel.Item speedItem = map.get(LocalPushType.TYPE_SPEED_UP);

        if (speedItem != null) {
            if (LocalPushUtils.getInstance().allowPopSpeedUp(speedItem)) {
                LogUtils.e("===允许弹出speed的window");
                // WindowUtil.getInstance().showWindowWhenDelayTwoSecond(context, homePressTime, speedItem);
                startPopActivity(context, homePressTime, speedItem);
                return;
            } else {
                LogUtils.e("===不允许弹出speed的window");
            }
        } else {
            LogUtils.e("=====speedItem为空");
        }


        //3.判断【垃圾清理】功能是否满足推送条件
        LocalPushConfigModel.Item clearItem = map.get(LocalPushType.TYPE_NOW_CLEAR);
        if (clearItem != null && LocalPushUtils.getInstance().allowPopClear(clearItem)) {
            LogUtils.e("===允许弹出clear的window");
            // WindowUtil.getInstance().showWindowWhenDelayTwoSecond(context, homePressTime, clearItem);
            startPopActivity(context, homePressTime, clearItem);
            return;
        }

        //4.判断【手机降温】功能是否满足推送条件
        LocalPushConfigModel.Item coolItem = map.get(LocalPushType.TYPE_PHONE_COOL);
        if (coolItem != null) {
            if (LocalPushUtils.getInstance().allowPopCool(coolItem)) {
                LogUtils.e("===允许弹出cool的window");
                //  WindowUtil.getInstance().showWindowWhenDelayTwoSecond(context, homePressTime, coolItem);
                coolItem.setLocalTemp(temp);
                startPopActivity(context, homePressTime, coolItem);
                return;
            }
        }

        //5.判断【超强省电】功能是否满足推送条件
        LocalPushConfigModel.Item powerItem = map.get(LocalPushType.TYPE_SUPER_POWER);
        if (powerItem != null) {
            if (LocalPushUtils.getInstance().allowPopPowerSaving(powerItem, isCharged, mBatteryPower)) {
                LogUtils.e("===允许弹出power的window");
                // WindowUtil.getInstance().showWindowWhenDelayTwoSecond(context, homePressTime, powerItem);
                powerItem.setLocalPower(mBatteryPower);
                startPopActivity(context, homePressTime, powerItem);
            }
        }


    }


    //悬浮广告页面
    public void startDialogActivityOnLauncher(Context context, int delay) {


        try {
            //判断是否进入后台
            int isBack = MmkvUtil.getInt("isback", -1);
            if (isBack != 1 || ActivityCollector.isActivityExistMkv(FullPopLayerActivity.class))
                return;
            //判断距离安装APP是否过了一小时
            long currentTime = System.currentTimeMillis();
            long installTime = MmkvUtil.getLong(SpCacheConfig.KEY_FIRST_INSTALL_APP_TIME, currentTime);
            if (currentTime - installTime < 1000 * 60 * 60) {
                return;
            }

            //判断广告开关
            boolean isOpen = false;
            //  int showTimes = 3;
            //  int displayTime = 0;
            if (null != AppHolder.getInstance().getInsertAdSwitchMap()) {
                Map<String, InsertAdSwitchInfoList.DataBean> map = AppHolder.getInstance().getInsertAdSwitchMap();
                isOpen = null != map.get("page_outside_screen") && map.get("page_outside_screen").isOpen();
                // showTimes = null == map.get("page_outside_screen") ? 3 : map.get("page_outside_screen").getShowRate();
                // displayTime = null == map.get("page_outside_screen") ? 0 : map.get("page_outside_screen").getDisplayTime();
            }
            if (!isOpen)
                return;

/*
            String mPopLayerTime = PreferenceUtil.getInstants().get(SpCacheConfig.POP_LAYER_TIME);
            long preTime = TextUtils.isEmpty(mPopLayerTime) ? 0 : Long.parseLong(mPopLayerTime);
            int number = PreferenceUtil.getInstants().getInt(SpCacheConfig.POP_LAYER_NUMBERS);
            //第一次|| 间隔时间大于一个小时||一小时内N次（N<showRate）(每次间隔时间<displayTime)
            if (preTime == 0 || (System.currentTimeMillis() - preTime) > (60 * 60 * 1000) || ((System.currentTimeMillis() - preTime) > (displayTime * 60 * 1000) && (System.currentTimeMillis() - preTime) <= (60 * 60 * 1000) && number < showTimes)) {
                if ((System.currentTimeMillis() - preTime) > (60 * 60 * 1000)) {//超过一小时重置次数
                    PreferenceUtil.getInstants().saveInt(SpCacheConfig.POP_LAYER_NUMBERS, 0);
                }
                if (NetworkUtils.isNetConnected()) {
                    Intent screenIntent = getIntent(context);
                    context.startActivity(screenIntent);
                }
            }*/

            if (NetworkUtils.isNetConnected()) {
                new Handler().postDelayed(() -> {
                    Intent screenIntent = getIntent(context);
                    context.startActivity(screenIntent);
                }, delay);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startFullActivity(Context context) {
        //判断是否进入后台
        boolean isBack = AppLifecycleUtil.isRunningForeground(context);
        Log.e("dong", "应用内插屏展示isBack ==" + isBack);

//        if (isBack || ActivityCollector.isActivityExistMkv(FullPopLayerActivity.class)) {
//            return;
//        }
        if (isBack) {
            return;
        }
        if (NetworkUtils.isNetConnected()) {
//            Toast.makeText(context,"应用内插屏!",Toast.LENGTH_LONG).show();
            Log.e("dong", "应用内插屏展示");
            Intent screenIntent = new Intent();
            screenIntent.setClassName(context.getPackageName(), SchemeConstant.StartFromClassName.CLASS_FULLPOPLAYERACTIVITY);
            screenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            screenIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            screenIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            screenIntent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
            screenIntent.putExtra("ad_style", PositionId.AD_EXTERNAL_ADVERTISING_04);
            context.startActivity(screenIntent);
        } else {

        }
    }


    //全局跳转插屏页面
    @NonNull
    private Intent getIntent(Context context) {
        Intent screenIntent = new Intent();
        screenIntent.setClassName(context.getPackageName(), SchemeConstant.StartFromClassName.CLASS_POPLAYERACTIVITY);
        screenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        screenIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        screenIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        screenIntent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
        return screenIntent;
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
        if (temp > dataBean.getThresholdNum()) {
            event.setFlag(2);
            String push_content = cxt.getString(R.string.push_content_phoneCooling, temp + "°C");
            //cheme跳转路径
            Map<String, String> actionMap = new HashMap<>();
            actionMap.put("url", SchemeConstant.LocalPushScheme.SCHEME_PHONECOOLINGACTIVITY);
            createNotify(cxt, push_content, actionMap, cxt.getString(R.string.push_cool_btn));
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
        if (mBatteryPower < dataBean.getThresholdNum() && !isCharged) {  //阀值以下且没有充电
            event.setFlag(2);
            String push_content = cxt.getString(R.string.push_content_power, dataBean.getThresholdNum());
            //cheme跳转路径
            Map<String, String> actionMap = new HashMap<>();
            actionMap.put("url", SchemeConstant.LocalPushScheme.SCHEME_PHONESUPERPOWERACTIVITY);
            createNotify(cxt, push_content, actionMap, cxt.getString(R.string.push_power_btn));
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
        //8.0以下 && 已经开启权限
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O && PermissionUtils.isUsageAccessAllowed(cxt)) {
            FileQueryUtils mFileQueryUtils = new FileQueryUtils();
            mFileQueryUtils.setIsService(false);
            Observable.create((ObservableOnSubscribe<ArrayList<FirstJunkInfo>>) e -> {
                //获取到可以加速的应用名单
                //文件加载进度回调
                mFileQueryUtils.setScanFileListener(new FileQueryUtils.ScanFileListener() {
                    @Override
                    public void currentNumber() {

                    }

                    @Override
                    public void increaseSize(long p0) {

                    }

                    @Override
                    public void scanFile(String p0) {

                    }
                });
                mFileQueryUtils.setRandRamNum(true);
                ArrayList<FirstJunkInfo> listInfo = mFileQueryUtils.getRunningProcess();
                if (listInfo == null) {
                    listInfo = new ArrayList<>();
                }
                e.onNext(listInfo);
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(list -> {
                        int computeTotalSize = mFileQueryUtils.computeTotalSize(list);
                        LockScreenBtnInfo btnInfo = new LockScreenBtnInfo(1);
                        if (computeTotalSize > dataBean.getThresholdNum()) {
//                            String value = String.format("%d%% %n", computeTotalSize);
                            String push_content = mContext.getString(R.string.push_content_access, computeTotalSize);
                            //cheme跳转路径
                            Map<String, String> actionMap = new HashMap<>();
                            actionMap.put("url", SchemeConstant.LocalPushScheme.SCHEME_PHONEACCESSACTIVITY);
                            createNotify(mContext, push_content, actionMap, mContext.getString(R.string.push_btn_access));
                            btnInfo.setNormal(false);
                        } else {
                            btnInfo.setNormal(true);
                        }
                        btnInfo.setCheckResult(String.valueOf(computeTotalSize));
                        PreferenceUtil.getInstants().save("lock_pos02", new Gson().toJson(btnInfo));

                        NotificationEvent event = new NotificationEvent();
                        event.setType("speed");
                        event.setAppendValue(computeTotalSize);
                        EventBus.getDefault().post(event);

                    });
        }
    }


    /**
     * 获取清理文件大小
     */
    @SuppressLint("CheckResult")
    public void startScanAll(PushSettingList.DataBean dataBean, Context mContext) {
        HashMap<Integer, JunkGroup> mJunkGroups = new HashMap<>();
        FileQueryUtils mFileQueryUtils = new FileQueryUtils();
        mFileQueryUtils.setIsService(false);
        Observable.create(e -> {
            //扫描进程占用内存情况
            mFileQueryUtils.setRandRamNum(true);
            ArrayList<FirstJunkInfo> runningProcess = mFileQueryUtils.getRunningProcess();
            e.onNext(runningProcess);
            //扫描apk安装包
            List<FirstJunkInfo> apkJunkInfos = mFileQueryUtils.queryAPkFile();
            if (CollectionUtils.isEmpty(apkJunkInfos)) {
                apkJunkInfos.addAll(mFileQueryUtils.queryAPkFile());
            }
            e.onNext(apkJunkInfos);
            //扫描卸载残余垃圾
            ArrayList<FirstJunkInfo> leaveDataInfo = mFileQueryUtils.getOmiteCache();
            e.onNext(leaveDataInfo);

            //扫描完成表示
            e.onNext("FINISH");
        }).compose(RxUtil.rxObservableSchedulerHelper()).subscribe(o -> {
            if (o instanceof ArrayList) {
                ArrayList<FirstJunkInfo> a = (ArrayList<FirstJunkInfo>) o;
                //缓存垃圾
                JunkGroup cacheGroup = mJunkGroups.get(JunkGroup.GROUP_CACHE);
                if (cacheGroup == null) {
                    cacheGroup = new JunkGroup();
                    cacheGroup.mName = ContextUtils.getContext().getString(R.string.cache_clean);
                    cacheGroup.isChecked = true;
                    cacheGroup.isExpand = true;
                    cacheGroup.mChildren = new ArrayList<>();
                    mJunkGroups.put(JunkGroup.GROUP_CACHE, cacheGroup);
                    cacheGroup.mSize += 0;
                }
                //卸载残留
                JunkGroup uninstallGroup = mJunkGroups.get(JunkGroup.GROUP_UNINSTALL);
                if (uninstallGroup == null) {
                    uninstallGroup = new JunkGroup();
                    uninstallGroup.mName = ContextUtils.getContext().getString(R.string.uninstall_clean);
                    uninstallGroup.isChecked = true;
                    uninstallGroup.isExpand = true;
                    uninstallGroup.mChildren = new ArrayList<>();
                    mJunkGroups.put(JunkGroup.GROUP_UNINSTALL, uninstallGroup);
                    uninstallGroup.mSize += 0;
                }
                //无用安装包
                JunkGroup apkGroup = mJunkGroups.get(JunkGroup.GROUP_APK);
                if (apkGroup == null) {
                    apkGroup = new JunkGroup();
                    apkGroup.mName = ContextUtils.getContext().getString(R.string.apk_clean);
                    apkGroup.isChecked = true;
                    apkGroup.isExpand = true;
                    apkGroup.mChildren = new ArrayList<>();
                    mJunkGroups.put(JunkGroup.GROUP_APK, apkGroup);
                    apkGroup.mSize += 0;
                }
                //内存清理
                JunkGroup processGroup = mJunkGroups.get(JunkGroup.GROUP_PROCESS);
                if (processGroup == null) {
                    processGroup = new JunkGroup();
                    processGroup.mName = ContextUtils.getContext().getString(R.string.process_clean);
                    processGroup.isChecked = true;
                    processGroup.isExpand = true;
                    processGroup.mChildren = new ArrayList<>();
                    mJunkGroups.put(JunkGroup.GROUP_PROCESS, processGroup);
                    processGroup.mSize += 0;
                }
                for (FirstJunkInfo info : a) {
                    if ("TYPE_CACHE".equals(info.getGarbageType())) {
                        if (!SpCacheConfig.CHAT_PACKAGE.equals(info.getAppPackageName()) && !SpCacheConfig.QQ_PACKAGE.equals(info.getAppPackageName())) {
                            cacheGroup.mChildren.add(info);
                            cacheGroup.mSize += info.getTotalSize();
                        }
                    } else if ("TYPE_PROCESS".equals(info.getGarbageType())) {
                        if (!SpCacheConfig.CHAT_PACKAGE.equals(info.getAppPackageName()) && !SpCacheConfig.QQ_PACKAGE.equals(info.getAppPackageName())) {
                            processGroup.mChildren.add(info);
                            processGroup.mSize += info.getTotalSize();
                        }
                    } else if ("TYPE_APK".equals(info.getGarbageType())) {
                        apkGroup.mChildren.add(info);
                        apkGroup.mSize += info.getTotalSize();

                    } else if ("TYPE_LEAVED".equals(info.getGarbageType())) {
                        if (!SpCacheConfig.CHAT_PACKAGE.equals(info.getAppPackageName()) && !SpCacheConfig.QQ_PACKAGE.equals(info.getAppPackageName())) {
                            uninstallGroup.mChildren.add(info);
                            uninstallGroup.mSize += info.getTotalSize();
                        }
                    }
                }
            } else {
                long totalSize = CleanUtil.getTotalSize(mJunkGroups);
                long mbNum = totalSize / (1024 * 1024);
                //保存上一次扫秒出来的垃圾大小
                //为了保证比扫描页面的数小，强制性的/2
                long temp = mbNum / 2;
                PreferenceUtil.saveLastScanRubbishSize(temp);
                NotificationEvent event = new NotificationEvent();
                event.setType("clean");
                LockScreenBtnInfo btnInfo = new LockScreenBtnInfo(0);
                if (dataBean != null && mbNum > dataBean.getThresholdNum()) {//超过阀值，发送push
                    event.setFlag(2);
                    String push_content = mContext.getString(R.string.push_content_scan_all, mbNum);
                    //cheme跳转路径
                    Map<String, String> actionMap = new HashMap<>();
                    actionMap.put("url", SchemeConstant.LocalPushScheme.SCHEME_NOWCLEANACTIVITY);
                    createNotify(mContext, push_content, actionMap, mContext.getString(R.string.tool_now_clean));
                    btnInfo.setNormal(false);
                } else {
                    event.setFlag(0);
                    btnInfo.setNormal(true);
                }
                btnInfo.setCheckResult(String.valueOf(mbNum));
                PreferenceUtil.getInstants().save("lock_pos01", new Gson().toJson(btnInfo));

                EventBus.getDefault().post(event);
            }
        });

    }

    /**
     * 发送push
     *
     * @param conx
     * @param push_content
     * @param actionMap
     */
    public void createNotify(Context conx, String push_content, final Map<String, String> actionMap, String btn) {
        try {
            if (null != mContext) {
                Intent intent = new Intent(conx, JPushReceiver.class);
                intent.setAction("com.geek.push.ACTION_RECEIVE_NOTIFICATION_CLICK");
                //notifyId不关注_跟产品已经确认()
                intent.putExtra("push_data", new PushMsg((100001 + NumberUtils.mathRandomInt(1, 100000)), "清理管家极速版", push_content, null, null, actionMap));
                intent.addCategory(mContext.getPackageName());
                intent.setPackage(mContext.getPackageName());
                KeepAliveManager.sendNotification(conx, "", push_content, R.drawable.ic_launcher, intent, btn);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
