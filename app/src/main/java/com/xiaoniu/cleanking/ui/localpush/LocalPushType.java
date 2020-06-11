package com.xiaoniu.cleanking.ui.localpush;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({LocalPushType.TYPE_NOW_CLEAR,
        LocalPushType.TYPE_SPEED_UP,
        LocalPushType.TYPE_PHONE_CLEAR,
        LocalPushType.TYPE_FILE_CLEAR,
        LocalPushType.TYPE_WEIXIN_CLEAR,
        LocalPushType.TYPE_PHONE_COOL,
        LocalPushType.TYPE_QQ_CLEAR,
        LocalPushType.TYPE_NOTIFICATION,
        LocalPushType.TYPE_SUPER_POWER})
@Retention(RetentionPolicy.SOURCE)
public @interface LocalPushType {
    //1.立即清理 2.一键加速 3.手机清理 4.文件清理 5.微信专清 6.手机温降温 7.QQ专清 8.通知栏 9.超强省电
    public static final int TYPE_NOW_CLEAR = 1;
    public static final int TYPE_SPEED_UP = 2;
    public static final int TYPE_PHONE_CLEAR = 3;
    public static final int TYPE_FILE_CLEAR = 4;
    public static final int TYPE_WEIXIN_CLEAR = 5;
    public static final int TYPE_PHONE_COOL = 6;
    public static final int TYPE_QQ_CLEAR = 7;
    public static final int TYPE_NOTIFICATION = 8;
    public static final int TYPE_SUPER_POWER = 9;
}
