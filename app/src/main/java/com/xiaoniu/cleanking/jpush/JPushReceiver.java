package com.xiaoniu.cleanking.jpush;

import android.content.Context;
import android.text.TextUtils;
import com.geek.push.GeekPush;
import com.geek.push.entity.PushCommand;
import com.geek.push.entity.PushMsg;
import com.geek.push.receiver.BasePushReceiver;
import com.xiaoniu.cleanking.scheme.SchemeProxy;
import com.xiaoniu.common.utils.StatisticsUtils;

import org.greenrobot.eventbus.EventBus;

public class JPushReceiver extends BasePushReceiver {

    //    通知点击事件
    @Override
    public void onReceiveNotificationClick(Context context, PushMsg msg) {
        if (msg.getKeyValue() != null && !msg.getKeyValue().isEmpty()) {
            for (String key : msg.getKeyValue().keySet()) {
                String url = msg.getKeyValue().get("url");
                if (!TextUtils.isEmpty(url)) {
                    StatisticsUtils.trackClickJPush("push_info_click", "推送消息点击", "", "notification_page",url,msg.getNotifyId(),msg.getTitle());
                    SchemeProxy.openScheme(context, url);
                }
            }
        }
    }

    //    接收到通知事件
    @Override
    public void onReceiveMessage(Context context, PushMsg msg) {
        EventBus.getDefault().post(new PushEvent("NotificationClick", msg));
        if (msg.getKeyValue() != null && !msg.getKeyValue().isEmpty()) {
            for (String key : msg.getKeyValue().keySet()) {
                String url = msg.getKeyValue().get("url");
                if (!TextUtils.isEmpty(url)) {
                    StatisticsUtils.trackClickJShow("push_info_show", "推送消息曝光", "", "notification_page",url,msg.getNotifyId(),msg.getTitle());
                }
            }
        }
    }

    //    接收到操作返回事件（添加tag，alias，检查tag等）
    @Override
    public void onCommandResult(Context context, PushCommand command) {
        //注册消息推送失败，再次注册
        if (command.getType() == GeekPush.TYPE_REGISTER) {
            if (command.getResultCode() == GeekPush.RESULT_ERROR) {
                GeekPush.register();
            }
        }
    }

}
