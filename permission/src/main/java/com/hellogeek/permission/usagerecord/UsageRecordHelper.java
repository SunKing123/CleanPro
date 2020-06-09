package com.hellogeek.permission.usagerecord;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.hellogeek.permission.Integrate.Permission;
import com.hellogeek.permission.bean.ASBase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsageRecordHelper {

    public static String getPermissionResultJson(List<ASBase> bases) {
        int excute = 0;
        int success = 0;
        for (ASBase base : bases) {
            if (base.isAllow) {
                success++;
                excute++;
            } else if (base.executeNumber > 0) {
                excute++;
            }
        }
        Map<String, String> tempMap = new HashMap<>();
        tempMap.put("all", String.valueOf(bases.size()));
        tempMap.put("excute", String.valueOf(excute));
        tempMap.put("success", String.valueOf(success));
        tempMap.put("complete", success == bases.size() ? "yes" : "no");
//        tempMap.put("complete", isSuccess ? "yes" : "no");
        return new Gson().toJson(tempMap);
    }

    public static void recordItemData(UsageBuider buider, int isAuto, ASBase base) {

        Map<String, String> tempMap = new HashMap<>();
        tempMap.put("authorization_result", base.isAllow ? "sucess" : "fail");
        tempMap.put("operation_mode", isAuto == 0 ? "manual" : "auto");
        if (base.permission == Permission.SUSPENDEDTOAST) {
            buider.setEventCode("show_pearl_speechless")
                    .setEventName("“展示来电视频”权限结果上报")
                    .setExtraMap(tempMap);
        } else if (base.permission == Permission.SELFSTARTING) {
            buider.setEventCode("keep_call_show_startup")
                    .setEventName("“保持来电秀正常启动”权限结果上报")
                    .setExtraMap(tempMap);
        } else if (base.permission == Permission.LOCKDISPALY) {
            buider.setEventCode("lock_screen_display")
                    .setEventName("“锁屏显示”结果上报")
                    .setExtraMap(tempMap);
        } else if (base.permission == Permission.BACKSTAGEPOPUP) {
            buider.setEventCode("backstage_pop_up")
                    .setEventName("“后台弹出”结果上报")
                    .setExtraMap(tempMap);
        } else if (base.permission == Permission.SYSTEMSETTING) {
            buider.setEventCode("modify_ring")
                    .setEventName("“修改手机来电铃声”权限结果上报")
                    .setExtraMap(tempMap);
        } else if (base.permission == Permission.REPLACEACLLPAGE) {
            buider.setEventCode("replace_caller_page")
                    .setEventName("“替换来电页面”权限结果上报")
                    .setExtraMap(tempMap);
        } else if (base.permission == Permission.NOTIFICATIONBAR) {
            buider.setEventCode("notification_management")
                    .setEventName("“通知管理”结果上报 ")
                    .setExtraMap(tempMap);
        } else if (base.permission == Permission.NOTICEOFTAKEOVER) {
            buider.setEventCode("read_caller_notification")
                    .setEventName("“读取来电时的通知”结果上报")
                    .setExtraMap(tempMap);
        } else if (base.permission == Permission.PACKAGEUSAGESTATS) {
            buider.setEventCode("packge_usage_stats")
                    .setEventName("“查看应用使用情況”结果上报")
                    .setExtraMap(tempMap);
        }
        buider.send();
    }

    public static String getPermissionClickJson(List<ASBase> bases) {
        Map<String, String> tempMap = new HashMap<>();
        tempMap.put("权限数量", String.valueOf(bases.size()));
        for (ASBase base : bases) {
            if (base.permission == Permission.SUSPENDEDTOAST) {
                tempMap.put("show_pearl_speechless", "展示来电视频");
            } else if (base.permission == Permission.SELFSTARTING) {
                tempMap.put("keep_call_show_startup", "保持来电秀正常启动");
            } else if (base.permission == Permission.LOCKDISPALY) {
                tempMap.put("lock_screen_display", "锁屏显示");
            } else if (base.permission == Permission.BACKSTAGEPOPUP) {
                tempMap.put("backstage_pop_up", "后台弹出");
            } else if (base.permission == Permission.SYSTEMSETTING) {
                tempMap.put("modify_ring", "修改手机来电铃声");
            } else if (base.permission == Permission.REPLACEACLLPAGE) {
                tempMap.put("replace_caller_page", "替换来电页面");
            } else if (base.permission == Permission.NOTIFICATIONBAR) {
                tempMap.put("notification_management", "通知管理");
            } else if (base.permission == Permission.NOTICEOFTAKEOVER) {
                tempMap.put("read_caller_notification", "读取来电时的通知");
            }
        }
        return new Gson().toJson(tempMap);
    }

}
