package com.xiaoniu.cleanking.keeplive.receive;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Keep;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.geek.push.entity.PushMsg;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.keeplive.KeepAliveManager;
import com.xiaoniu.cleanking.keeplive.config.KeepAliveConfig;
import com.xiaoniu.cleanking.keeplive.config.NotificationUtils;
import com.xiaoniu.cleanking.keeplive.service.LocalService;
import com.xiaoniu.cleanking.keeplive.utils.SPUtils;
import com.xiaoniu.cleanking.ui.main.activity.MainActivity;
import com.xiaoniu.cleanking.ui.main.bean.CleanLogInfo;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.bean.JunkGroup;
import com.xiaoniu.cleanking.ui.main.bean.PushSettingList;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.utils.CleanUtil;
import com.xiaoniu.cleanking.utils.FileQueryUtils;
import com.xiaoniu.cleanking.utils.LogUtils;
import com.xiaoniu.cleanking.utils.net.RxUtil;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.common.utils.ContextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;

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

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        Map<String, PushSettingList.DataBean> map = PreferenceUtil.getCleanLog();
        for (Map.Entry<String, PushSettingList.DataBean> entry : map.entrySet()) {
            PushSettingList.DataBean dataBean = entry.getValue();
            if (isStartScan(dataBean)) { //检测是否达到扫描时间
                startScan(dataBean, context);
                //更新本地保存的操作时间
                dataBean.setLastTime(System.currentTimeMillis());
                map.put(entry.getKey(),dataBean);
                PreferenceUtil.saveCleanLogMap(map);
            }
        }
        //重新打开保活service
        Intent i = new Intent(context, LocalService.class);
        context.startService(i);
        createNotify();
    }


    public boolean isStartScan(PushSettingList.DataBean dataBean) {
        long lastTime = dataBean.getLastTime();
        long currentTime = System.currentTimeMillis();
        if ((currentTime - lastTime) >= (dataBean.getInterValTime() * 60 * 1000)) {
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
                startScan(mContext);
                break;
            case "push_2"://一键加速
                break;
            case "push_3"://超强省电
                break;
            case "push_4"://手机降温
                break;


        }

    }


    /**
     * 获取清理文件大小
     */
    public void startScan(Context mContext) {
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

            //其他垃圾
            JunkGroup otherGroup = mFileQueryUtils.getOtherCache();
            e.onNext(otherGroup);
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
       /*         long totalSize = CleanUtil.getTotalSize(mJunkGroups);
                if (total > totalSize) {//设置为其他垃圾
                    JunkGroup adGroup = mJunkGroups.get(JunkGroup.GROUP_OTHER);
                    if (adGroup != null) {
                        adGroup.mSize += (total - totalSize);
//                        mJunkGroups.put(JunkGroup.GROUP_OTHER, adGroup);
                    } else {
                        JunkGroup otherGroup = new JunkGroup();
                        otherGroup.mName = ContextUtils.getContext().getString(R.string.other_clean);
                        otherGroup.isChecked = true;
                        otherGroup.isExpand = true;
                        adGroup.needExpand = false;//不能展开显示
                        otherGroup.mChildren = new ArrayList<>();
                        otherGroup.mSize += (total - totalSize);
                        mJunkGroups.put(JunkGroup.GROUP_OTHER, otherGroup);
                    }
                }*/
                long totalSize = CleanUtil.getTotalSize(mJunkGroups);
                LogUtils.i("----" + totalSize);



            }
        });

    }

    public void createNotify(){
        KeepAliveConfig.CONTENT = "sssss";
        KeepAliveConfig.DEF_ICONS = SPUtils.getInstance(mContext, SP_NAME).getInt(KeepAliveConfig.RES_ICON, R.drawable.ic_launcher);
        KeepAliveConfig.TITLE = "ffffffff";
        String title = SPUtils.getInstance(mContext, SP_NAME).getString(KeepAliveConfig.TITLE);

        if (!TextUtils.isEmpty(KeepAliveConfig.TITLE) && !TextUtils.isEmpty(KeepAliveConfig.CONTENT)) {
            //启用前台服务，提升优先级
            Intent intent2 = new Intent(mContext, NotificationClickReceiver.class);
            intent2.setAction(NotificationClickReceiver.CLICK_NOTIFICATION);
            Map<String,String> actionMap = new HashMap<>();
            actionMap.put("url","cleanking://com.xiaoniu.cleanking/native_no_params?a_name=main.activity.PhoneAccessActivity");
            intent2.putExtra("push_data", new PushMsg(102102, "test", "this is a test", null, null, actionMap));
//                    Notification notification = NotificationUtils.createNotification(mContext, KeepAliveConfig.TITLE, KeepAliveConfig.CONTENT, KeepAliveConfig.DEF_ICONS, intent2);
            KeepAliveManager.sendNotification(mContext,"aa","ssssssss",DEF_ICONS,intent2);
            Log.d("JOB-->", TAG + "显示通知栏");
        }
    }




}
