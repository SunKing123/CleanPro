package com.xiaoniu.cleanking.app;

import android.content.Context;
import android.content.Intent;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.ui.main.activity.RedPacketHotActivity;
import com.xiaoniu.cleanking.ui.main.activity.SplashADHotActivity;
import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.event.LifecycEvent;
import com.xiaoniu.cleanking.utils.AppLifecycleUtil;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.common.utils.NetworkUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * @author XiLei
 * @date 2019/10/15.
 * description：监听Application生命周期
 */
public class AppLifecycleObserver implements LifecycleObserver {
    private Context mContext;
    private boolean mIsBack; //mIsBack = true 记录当前已经进入后台

    public AppLifecycleObserver(Context context) {
        this.mContext = context;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    void onEnterForeground() {
        if (null == mContext || !mIsBack) return;
        PreferenceUtil.saveRedPacketShowCount(PreferenceUtil.getRedPacketShowCount() + 1);
        if (null != AppHolder.getInstance().getSwitchInfoList() && null != AppHolder.getInstance().getSwitchInfoList().getData()
                && AppHolder.getInstance().getSwitchInfoList().getData().size() > 0) {
            for (SwitchInfoList.DataBean switchInfoList : AppHolder.getInstance().getSwitchInfoList().getData()) {
                if (PreferenceUtil.getHomeBackTime() && PositionId.HOT_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    Intent intent = new Intent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setClass(mContext, SplashADHotActivity.class);
                    mContext.startActivity(intent);
                    mIsBack = false;
                    return;  //热启动有广告时不展示红包
                }

                if (NetworkUtils.getNetworkType() == NetworkUtils.NetworkType.NETWORK_3G
                        || NetworkUtils.getNetworkType() == NetworkUtils.NetworkType.NETWORK_2G
                        || NetworkUtils.getNetworkType() == NetworkUtils.NetworkType.NETWORK_NO)
                    return;
                if (PositionId.KEY_RED_JILI.equals(switchInfoList.getConfigKey()) && switchInfoList.isOpen()) {  //展示红包
                    if (null == AppHolder.getInstance() || null == AppHolder.getInstance().getRedPacketEntityList()
                            || null == AppHolder.getInstance().getRedPacketEntityList().getData()
                            || AppHolder.getInstance().getRedPacketEntityList().getData().size() <= 0
                            || null == AppHolder.getInstance().getRedPacketEntityList().getData().get(0).getImgUrls()
                            || AppHolder.getInstance().getRedPacketEntityList().getData().get(0).getImgUrls().size() <= 0)
                        return;
                    //暂时注释
//        if (PreferenceUtil.getRedPacketShowCount() % AppHolder.getInstance().getRedPacketEntityList().getData().get(0).getTrigger() == 0) {
                    switch (AppHolder.getInstance().getRedPacketEntityList().getData().get(0).getLocation()) {
                        case 5: //所有页面展示红包
                            mContext.startActivity(new Intent(mContext, RedPacketHotActivity.class));
                            mIsBack = false;
                            break;
                    }
//        }
                }
            }
        }
        EventBus.getDefault().post(new LifecycEvent(true));
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    void onEnterBackground() {
        if (!AppLifecycleUtil.isAppOnForeground(mContext)) {
            //app 进入后台
            mIsBack = true;
            PreferenceUtil.saveHomeBackTime();
        }
    }

}
