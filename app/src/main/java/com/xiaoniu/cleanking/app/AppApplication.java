package com.xiaoniu.cleanking.app;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bun.miitmdid.core.JLibrary;
import com.xiaoniu.cleanking.AppConstants;
import com.xiaoniu.cleanking.BuildConfig;
import com.xiaoniu.common.AppProfile;
import com.xiaoniu.common.base.BaseApplication;
import com.xiaoniu.common.utils.ChannelUtil;
import com.xiaoniu.common.utils.ContextUtils;
import com.xiaoniu.common.utils.MiitHelper;
import com.xiaoniu.statistic.Configuration;
import com.xiaoniu.statistic.HeartbeatCallBack;
import com.xiaoniu.statistic.NiuDataAPI;
import com.xiaoniu.statistic.NiuDataTrackEventCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;


/**
 * Created by admin on 2017/7/13.
 */

public class AppApplication extends BaseApplication {
    private static AppApplication sInstance;
    public static boolean isFromHome;
    public static String mapCoordinate;//坐标
    public static String provinceName;//省名
    public static String cityName;//市名
    public static String cityAreaName;//区（县）名
    public static Set<Integer> popSet = new HashSet<>();//运营弹窗是否一弹过位置统计
    public static String officialAccountContent;
    public static String officialAccountName;
    public static String AuditSwitch = "AuditSwitch";
    public static boolean isAudit;//是否市场审核中
    private String oaId ="";
    private boolean isInited =false;

    public AppApplication() {
        super();
        AppProfile.API_APPID = BuildConfig.API_APPID;
        AppProfile.API_APPSECRET = BuildConfig.API_APPSECRET;
        AppProfile.APPLICATION_ID = BuildConfig.APPLICATION_ID;
        AppProfile.BUILD_TYPE = BuildConfig.BUILD_TYPE;
        AppProfile.DEBUG = BuildConfig.DEBUG;
        AppProfile.MESSAGE = BuildConfig.MESSAGE;
        AppProfile.PLATFORM = BuildConfig.PLATFORM;
        AppProfile.TINKER_ID = BuildConfig.TINKER_ID;
        AppProfile.VERSION_CODE = BuildConfig.VERSION_CODE;
        AppProfile.VERSION_NAME = BuildConfig.VERSION_NAME;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //设置oaid到埋点公共参数
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) { //4.4以上版本oaid
            try {
                JLibrary.InitEntry(base);
                //获取oaid
                new MiitHelper(new MiitHelper.AppIdsUpdater() {
                    @Override
                    public void OnIdsAvalid(@NonNull String oaid) {
                        if (!isInited) {
                            initNiuData(sInstance);
                        }
                        NiuDataAPI.setOaid(oaid);
                        NiuDataAPI.setTrackEventCallback(new NiuDataTrackEventCallBack() {
                            //添加到默认事件
                            @Override
                            public void onTrackAutoCollectEvent(String eventCode, JSONObject eventProperties) {
                                try {
                                    eventProperties.put("oaid", oaid);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            //添加到其他事件
                            @Override
                            public void onTrackEvent(String eventCode, JSONObject eventProperties) {
                                try {
                                    eventProperties.put("oaid", oaid);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }).getDeviceIds(sInstance);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCreate() {
        sInstance = this;
        super.onCreate();
        ContextUtils.initApplication(this);
        initNiuData(this);
    }

    //埋点初始化
    public void initNiuData(Application application) {
        //测试环境
        NiuDataAPI.init(application, new Configuration()
                //切换到sdk默认的测试环境地址
                .setHeartbeatMode(Configuration.HEARTBEAT_MODE_FOREGROUND)
                .serverUrl(AppConstants.BIGDATA_MD)
                .setHeartbeatUrl(AppConstants.BIGDATA_MD)
                //打开sdk日志信息
                .logOpen()
                .setHeartbeatInterval(5000)
                .channel(ChannelUtil.getChannel())
        );

        NiuDataAPI.setHeartbeatCallback(new HeartbeatCallBack() {
            @Override
            public void onHeartbeatStart(JSONObject eventProperties) {
                //这里可以给心跳事件 追加额外字段  在每次心跳启动的时候，会带上额外字段
                Log.d("onHeartbeatStart", "onHeartbeatStart: " + "这里可以给心跳事件 追加额外字段  在每次心跳启动的时候，会带上额外字段");
            }
        });
        isInited = true;
    }

    /**
     * @return App 全局上下文
     */
    public static AppApplication getInstance() {
        return sInstance;
    }
}
