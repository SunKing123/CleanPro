package com.comm.jksdk.config;

import android.content.Context;

import com.comm.jksdk.GeekAdSdk;
import com.comm.jksdk.http.Api;
import com.comm.jksdk.http.utils.ApiManage;
import com.comm.jksdk.http.utils.AppEnvironment;
import com.comm.jksdk.http.utils.LogUtils;
import com.comm.jksdk.utils.SpUtils;

import me.jessyan.retrofiturlmanager.RetrofitUrlManager;

/**
 * @author liupengbing
 * @date 2019/9/24
 */
public class InitBaseConfig {
    private final String TAG = "MainApp";
    private final String SERVER_ENVIRONMENT = "server_environment";
    private final String TEST_MODE_IS_OPEN = "test_is_open";
    private static InitBaseConfig instance;

    public static InitBaseConfig getInstance() {
        if (instance == null) {
            synchronized (InitBaseConfig.class) {
                if (instance == null) {
                    instance = new InitBaseConfig();
                }
            }
        }
        return instance;
    }

    public void init(Context context) {
        // 需要放在接口请求之前
        initNetWork();
        //初始化穿山甲
//        initChjAd(context);

    }

    public void initChjAd(Context context, String appId) {
        //强烈建议在应用对应的Application#onCreate()方法中调用，避免出现content为null的异常
        TTAdManagerHolder.init(context, appId);
    }

    private void initNetWork() {
        System.getProperty("os.arch");
        initServerEnvironmentStub();
        try {
            RetrofitUrlManager.getInstance().setDebug(true);
            //将每个 BaseUrl 进行初始化,运行时可以随时改变 DOMAIN_NAME 对应的值,从而达到切换 BaseUrl 的效果
            RetrofitUrlManager.getInstance().putDomain(Api.WEATHER_DOMAIN_NAME, ApiManage.getWeatherURL());

        } catch (Exception e) {
            LogUtils.d(TAG, "onCreate()->" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initServerEnvironmentStub() {
        AppEnvironment.init(new AppEnvironment.ServerEnvironmentStub() {
            @Override
            public int getServerEnvironment() {
//                //判断应用环境：release 且不为mt_test 渠道时候使用正式环境
//                boolean product = !Constant.CHANNEL_TEST.equals(ChannelUtil.getChannel());
                String product = GeekAdSdk.isFormal();
                int defEnvironment;
                switch (product) {
                    case "dev":
                        defEnvironment = AppEnvironment.ServerEnvironment.Dev.ordinal();
                        break;
                    case "btest":
                        defEnvironment = AppEnvironment.ServerEnvironment.Test.ordinal();
                        break;
                    case "prod":
                        defEnvironment = AppEnvironment.ServerEnvironment.Product.ordinal();
                        break;
                    default:
                        defEnvironment = AppEnvironment.ServerEnvironment.Product.ordinal();
                }
                return SpUtils.getInt(SERVER_ENVIRONMENT, defEnvironment);
            }

            @Override
            public void setServerEnvironmentOrdinal(int ordinal) {
                SpUtils.putInt(SERVER_ENVIRONMENT, ordinal);
            }
        }, new AppEnvironment.TestModeStub() {
            @Override
            public void setIsTestMode(boolean isTestMode) {
                SpUtils.putBoolean(TEST_MODE_IS_OPEN, isTestMode);
            }

            @Override
            public boolean isTestMode() {
                return SpUtils.getBoolean(TEST_MODE_IS_OPEN, false);
            }
        });
    }
}
