package com.xiaoniu.cleanking.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.xiaoniu.cleanking.app.AppManager;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.scheme.Constant.SchemeConstant;
import com.xiaoniu.cleanking.scheme.utils.ActivityCollector;
import com.xiaoniu.cleanking.ui.lockscreen.LockActivity;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.widget.SPUtil;
import com.xiaoniu.cleanking.utils.CollectionUtils;

/**
 * @author zzh
 * @date 2020/7/29 09
 * @mail：zhengzhihao@xiaoniuhy.com
 */
public class LockActivityStarReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!(ActivityCollector.currentActivity() instanceof LockActivity)) {       //判断是否在锁屏界面 && 没有LockActivity
            //避免两次都是屏幕on
            //判断广告开关是否打开，以及频次判断
            String action = intent.getAction();
            boolean isScreenOff = Intent.ACTION_SCREEN_OFF.equals(action);
            String auditSwitch = SPUtil.getString(context, SpCacheConfig.AuditSwitch, "1");
            boolean lock_sw = AppHolder.getInstance().checkAdSwitch(PositionId.KEY_LOCK_SCREEN, PositionId.KEY_ADVERT_LOCK_SCREEN);//锁屏开关
            if (TextUtils.equals(auditSwitch, "1") && lock_sw && isScreenOff) { //过审开关打开状态
                Intent screenIntent = new Intent();
                screenIntent.setClassName(context.getPackageName(), SchemeConstant.StartFromClassName.CLASS_LOCKACTIVITY);
                screenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                screenIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                screenIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                screenIntent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
                context.startActivity(screenIntent);
            }
        }
    }
}
