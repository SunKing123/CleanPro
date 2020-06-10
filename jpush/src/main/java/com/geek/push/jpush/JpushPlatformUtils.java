package com.geek.push.jpush;

import android.app.Application;

import com.geek.push.utils.RomUtils;
import com.xiaomi.mipush.sdk.MiPushClient;

import cn.jpush.android.api.JPushInterface;

/**
 * 厂商平台注册集成成功id<p>
 *
 * @author zixuefei
 * @since 2019/5/17 11:56
 */
public class JpushPlatformUtils {

    public static String getFormatPlatformRegisterId(Application context) {
        String registerId = null;
        if (RomUtils.isHuaweiRom()) {
            registerId = "HUAWEI:" + JPushInterface.getRegistrationID(context);
        }
        if (RomUtils.isMiuiRom()) {
            registerId = "XIAOMI:" + MiPushClient.getRegId(context);
        }

        if (RomUtils.isFlymeRom()) {
            registerId = "MEIZU:" + JPushInterface.getRegistrationID(context);
        }

        if (RomUtils.isOppoRom()) {
            registerId = "OPPO:" + JPushInterface.getRegistrationID(context);
        }

        if (RomUtils.isVivoRom()) {
            registerId = "VIVO:" + JPushInterface.getRegistrationID(context);
        }
        return registerId;
    }
}
