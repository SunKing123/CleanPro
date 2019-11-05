package com.xiaoniu.cleanking.keeplive.receive;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Keep;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.geek.push.entity.PushMsg;
import com.xiaoniu.cleanking.BuildConfig;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.jpush.JPushReceiver;
import com.xiaoniu.cleanking.keeplive.KeepAliveManager;
import com.xiaoniu.cleanking.keeplive.config.KeepAliveConfig;
import com.xiaoniu.cleanking.keeplive.config.NotificationUtils;
import com.xiaoniu.cleanking.keeplive.service.LocalService;
import com.xiaoniu.cleanking.keeplive.utils.SPUtils;
import com.xiaoniu.cleanking.scheme.Constant.SchemeConstant;
import com.xiaoniu.cleanking.ui.main.activity.MainActivity;
import com.xiaoniu.cleanking.ui.main.bean.CleanLogInfo;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.bean.JunkGroup;
import com.xiaoniu.cleanking.ui.main.bean.PushSettingList;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.event.NotificationEvent;
import com.xiaoniu.cleanking.ui.main.widget.SPUtil;
import com.xiaoniu.cleanking.ui.tool.notify.activity.NotifyCleanDetailActivity;
import com.xiaoniu.cleanking.ui.tool.notify.activity.NotifyCleanGuideActivity;
import com.xiaoniu.cleanking.ui.tool.notify.manager.NotifyCleanManager;
import com.xiaoniu.cleanking.ui.tool.notify.utils.NotifyUtils;
import com.xiaoniu.cleanking.utils.CleanUtil;
import com.xiaoniu.cleanking.utils.FileQueryUtils;
import com.xiaoniu.cleanking.utils.LogUtils;
import com.xiaoniu.cleanking.utils.PermissionUtils;
import com.xiaoniu.cleanking.utils.net.RxUtil;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.common.utils.ContextUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static com.xiaoniu.cleanking.keeplive.config.KeepAliveConfig.DEF_ICONS;
import static com.xiaoniu.cleanking.keeplive.config.KeepAliveConfig.SP_NAME;

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

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        mBatteryPower = intent.getIntExtra("battery", 50);
        temp = intent.getIntExtra("temp", 30);
        Map<String, PushSettingList.DataBean> map = PreferenceUtil.getCleanLog();
        for (Map.Entry<String, PushSettingList.DataBean> entry : map.entrySet()) {
            PushSettingList.DataBean dataBean = entry.getValue();
            if (isStartScan(dataBean)) { //检测是否达到扫描时间
                startScan(dataBean, context);
                //更新本地保存的操作时间
                dataBean.setLastTime(System.currentTimeMillis());
                map.put(entry.getKey(), dataBean);
                PreferenceUtil.saveCleanLogMap(map);
            }
        }
        //重新打开保活service
        Intent i = new Intent(context, LocalService.class);
        context.startService(i);

    }

    //检测是否达到扫描时间
    public boolean isStartScan(PushSettingList.DataBean dataBean) {
        long lastTime = dataBean.getLastTime();
        long currentTime = System.currentTimeMillis();
        if ((currentTime - lastTime) >= dataBean.getInterValTime() * 60 * 1000) {
            return true;
        }
        return false;

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
     * @param dataBean
     * @param cxt
     */
    public void refNotify(PushSettingList.DataBean dataBean, Context cxt){
        int mNotifySize = 0;
        NotificationEvent event = new NotificationEvent();
        event.setType("notification");
        if (NotifyUtils.isNotificationListenerEnabled()) {//已获得通知栏权限
            if (null != NotifyCleanManager.getInstance().getAllNotifications()) {
                mNotifySize = NotifyCleanManager.getInstance().getAllNotifications().size();
            }
            if(mNotifySize>=5){
                event.setFlag(2);
            }else{
                event.setFlag(0);
            }
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
            String push_content = cxt.getString(R.string.push_content_phoneCooling, temp);
            //cheme跳转路径
            Map<String, String> actionMap = new HashMap<>();
            actionMap.put("url", SchemeConstant.LocalPushScheme.SCHEME_PHONECOOLINGACTIVITY);
            createNotify(cxt, push_content, actionMap,cxt.getString(R.string.push_cool_btn));
        }else{
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
        event.setType("cooling");
        if (mBatteryPower < dataBean.getThresholdNum()) {
            event.setFlag(2);
            String push_content = cxt.getString(R.string.push_content_power, 30);
            //cheme跳转路径
            Map<String, String> actionMap = new HashMap<>();
            actionMap.put("url", SchemeConstant.LocalPushScheme.SCHEME_PHONESUPERPOWERACTIVITY);
            createNotify(cxt, push_content, actionMap,cxt.getString(R.string.push_power_btn));
        }else{
            event.setFlag(0);
        }
        EventBus.getDefault().post(event);

    }


    /**
     * 一键加速
     */
    public void getAccessListBelow(PushSettingList.DataBean dataBean, Context cxt) {
        //8.0以下 && 已经开启权限
        if (Build.VERSION.SDK_INT < 26 && PermissionUtils.isUsageAccessAllowed(cxt)) {
            FileQueryUtils mFileQueryUtils = new FileQueryUtils();
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
                    public void reduceSize(long p0) {

                    }

                    @Override
                    public void scanFile(String p0) {

                    }

                    @Override
                    public void totalSize(int p0) {

                    }
                });
                ArrayList<FirstJunkInfo> listInfo = mFileQueryUtils.getRunningProcess();
                if (listInfo == null) {
                    listInfo = new ArrayList<>();
                }
                e.onNext(listInfo);
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(list -> {
                        int computeTotalSize = mFileQueryUtils.computeTotalSize(list);
                        if (computeTotalSize > dataBean.getThresholdNum()) {
                            String push_content = mContext.getString(R.string.push_content_access, computeTotalSize);
                            //cheme跳转路径
                            Map<String, String> actionMap = new HashMap<>();
                            actionMap.put("url", SchemeConstant.LocalPushScheme.SCHEME_PHONEACCESSACTIVITY);
                            createNotify(mContext, push_content, actionMap,mContext.getString(R.string.push_btn_access));
                        }

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
    public void startScanAll(PushSettingList.DataBean dataBean, Context mContext) {
        HashMap<Integer, JunkGroup> mJunkGroups = new HashMap<>();
        FileQueryUtils mFileQueryUtils = new FileQueryUtils();
        Observable.create(e -> {
            //扫描进程占用内存情况
            ArrayList<FirstJunkInfo> runningProcess = mFileQueryUtils.getRunningProcess();
            e.onNext(runningProcess);
            //扫描apk安装包
            List<FirstJunkInfo> apkJunkInfos = mFileQueryUtils.queryAPkFile();
            e.onNext(apkJunkInfos);

            boolean isScanFile = apkJunkInfos.size() > 0;
            //扫描私有路径下缓存文件
            ArrayList<FirstJunkInfo> androidDataInfo = mFileQueryUtils.getAndroidDataInfo(isScanFile);
            //根据私有路径扫描公用路径
            ArrayList<FirstJunkInfo> publicDataInfo = mFileQueryUtils.getExternalStorageCache(androidDataInfo);
            e.onNext(publicDataInfo);

//            //公用路径残留文件
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
                NotificationEvent event = new NotificationEvent();
                event.setType("clean");
                if (mbNum > dataBean.getThresholdNum()) {//超过阀值，发送push
                    event.setFlag(2);

                    String push_content = mContext.getString(R.string.push_content_scan_all, mbNum);
                    //cheme跳转路径
                    Map<String, String> actionMap = new HashMap<>();
                    actionMap.put("url", SchemeConstant.LocalPushScheme.SCHEME_NOWCLEANACTIVITY);
                    createNotify(mContext, push_content, actionMap,mContext.getString(R.string.tool_now_clean));

                }else{
                    event.setFlag(0);
                }
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
    public void createNotify(Context conx, String push_content, Map<String, String> actionMap,String btn) {
        Intent intent = new Intent(conx, JPushReceiver.class);
        intent.setAction("com.geek.push.ACTION_RECEIVE_NOTIFICATION_CLICK");
        //notifyId不关注_跟产品已经确认(100001)
        intent.putExtra("push_data", new PushMsg(100001, "", push_content, null, null, actionMap));
        KeepAliveManager.sendNotification(conx, "", push_content, R.drawable.ic_launcher, intent,btn);
    }


}
