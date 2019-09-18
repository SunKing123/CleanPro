package com.xiaoniu.cleanking.jpush;

import android.content.Context;
import android.text.TextUtils;
import com.geek.push.GeekPush;
import com.geek.push.entity.PushCommand;
import com.geek.push.entity.PushMsg;
import com.geek.push.log.LogUtils;
import com.geek.push.receiver.BasePushReceiver;
import com.xiaoniu.cleanking.scheme.SchemeProxy;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.statistic.NiuDataAPI;

import org.greenrobot.eventbus.EventBus;

public class JPushReceiver extends BasePushReceiver {
    public static final String LOG_LINE = "-------%s-------";
    private final static String TAG = "pushLog " + JPushReceiver.class.getSimpleName();

    //    通知点击事件
    @Override
    public void onReceiveNotificationClick(Context context, PushMsg msg) {
        LogUtils.i(TAG, "onReceiveNotificationClick: " + msg.toString());
        if (msg.getKeyValue() != null && !msg.getKeyValue().isEmpty()) {
            for (String key : msg.getKeyValue().keySet()) {
                String url = msg.getKeyValue().get("url");
                if (!TextUtils.isEmpty(url)) {
                    StatisticsUtils.trackClickJPush("push_info_click", "\"推送消息点击\"时", "", "",url,msg.getNotifyId(),msg.getTitle());
                    SchemeProxy.openScheme(context, url);
                }
            }
        }
    }

    //    接收到通知事件
    @Override
    public void onReceiveMessage(Context context, PushMsg msg) {
        LogUtils.i(TAG, "onReceiveMessage: " + msg.toString());
        EventBus.getDefault().post(new PushEvent("NotificationClick", msg));
        if (msg.getKeyValue() != null && !msg.getKeyValue().isEmpty()) {
            for (String key : msg.getKeyValue().keySet()) {
                String url = msg.getKeyValue().get("url");
                if (!TextUtils.isEmpty(url)) {

                    StatisticsUtils.trackClickJShow("push_info_show", "推送消息曝光", "", "",url,msg.getNotifyId(),msg.getTitle());
                }
            }
        }
    }

    //    接收到操作返回事件（添加tag，alias，检查tag等）
    @Override
    public void onCommandResult(Context context, PushCommand command) {
        LogUtils.i(TAG, "onCommandResult: " + command.toString());
        //注册消息推送失败，再次注册
        if (command.getType() == GeekPush.TYPE_REGISTER) {
            if (command.getResultCode() == GeekPush.RESULT_ERROR) {
                GeekPush.register();
            }
        }
        EventBus.getDefault().post(new PushEvent("CommandResult", command));
    }

    public String generateLogByOnePushMsg(String type, PushMsg pushMsg) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format(LOG_LINE, type)).append("\n");
        if (pushMsg.getMsg() != null) {
            builder.append("消息内容：" + pushMsg.getMsg()).append("\n");
        } else {
            builder.append("通知标题：" + pushMsg.getTitle()).append("\n");
            builder.append("通知内容：" + pushMsg.getContent()).append("\n");
        }
        if (!TextUtils.isEmpty(pushMsg.getExtraMsg())) {
            builder.append("额外信息：" + pushMsg.getExtraMsg()).append("\n");
        }

        if (pushMsg.getKeyValue() != null && !pushMsg.getKeyValue().isEmpty()) {
            builder.append("键值对：").append(pushMsg.getKeyValue().toString()).append("\n");
        }
        return builder.toString();
    }

    public String generateLogByOnePushCommand(PushCommand pushCommand) {
        StringBuilder builder = new StringBuilder();
        String type = null;
        switch (pushCommand.getType()) {
            case PushCommand.TYPE_ADD_TAG:
                type = "添加标签";
                break;
            case PushCommand.TYPE_DEL_TAG:
                type = "删除标签";
                break;
            case PushCommand.TYPE_BIND_ALIAS:
                type = "绑定别名";
                break;
            case PushCommand.TYPE_UNBIND_ALIAS:
                type = "解绑别名";
                break;
            case PushCommand.TYPE_REGISTER:
                type = "注册推送";
                break;
            case PushCommand.TYPE_UNREGISTER:
                type = "取消注册推送";
                break;
            case PushCommand.TYPE_AND_OR_DEL_TAG:
                type = "添加或删除标签";
                break;
            default:
                type = "未定义类型";
                break;
        }
        builder.append(String.format(LOG_LINE, type)).append("\n");
        if (!TextUtils.isEmpty(pushCommand.getRegisterId())) {
            builder.append("推送token：").append(pushCommand.getRegisterId()).append("\n");
        }
        if (!TextUtils.isEmpty(pushCommand.getExtraMsg())) {
            builder.append("额外信息(tag/alias)：").append(pushCommand.getExtraMsg()).append("\n");
        }
        builder.append("操作结果：").append(pushCommand.getResultCode() == PushCommand.RESULT_OK ? "成功" : "code: " + pushCommand.getResultCode() + " msg:失败");
        return builder.toString();
    }
}
