package com.xiaoniu.cleanking.app.injector.module;

import android.app.Application;
import android.content.Context;

import com.orhanobut.logger.Logger;
import com.xiaoniu.cleanking.BuildConfig;
import com.xiaoniu.cleanking.api.UserApiService;
import com.xiaoniu.cleanking.api.WeatherDataApiService;
import com.xiaoniu.cleanking.app.injector.interceptor.RequestParamInterceptor;
import com.xiaoniu.common.utils.JSONUtils;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by tie on 2017/3/20.
 */
@Module
public class ApiModule {

    private static Retrofit mRetrofit;
    private Retrofit mRetrofit2;

    //网络地址
    public final static String Base_Host = BuildConfig.BASE_HOST;
    public final static String Base_H5_Host = BuildConfig.Base_H5_Host;//H5路径
    public final static String base_weather_api = BuildConfig.WEATHER_BASE_URL;//大数据接口路径


    public final static String SHOPPING_MALL = Base_H5_Host + "/home_new.html";//商城

    public ApiModule(Application application) {
        //原生Log日志拦截
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLogger());
        if (BuildConfig.DEBUG) {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        }

        //自定义日志拦截
        //LoggingInterceptor interceptor = new LoggingInterceptor();
        //请求参数拦截
        RequestParamInterceptor requestParamInterceptor = new RequestParamInterceptor();

        //设置cache
//        File cacheFile = new File(application.getCacheDir(), "Cache");
//        Cache cache = new Cache(cacheFile, 1024 * 1024 * 10);

        //请求参数拦截
        //RequestParamInterceptor requestParamInterceptor = new RequestParamInterceptor();

        OkHttpClient okHttpClient = null;
        try {
            okHttpClient = new OkHttpClient.Builder()
                    //                .addInterceptor(new LoggerInterceptor("TAG"))
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .sslSocketFactory(ignoreSSLSocketFactory(application.getApplicationContext()))
                    .hostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER)
                    .addInterceptor(requestParamInterceptor)
                    .addInterceptor(loggingInterceptor)
                    //其他配置
                    .build();


        } catch (
                Exception e) {
            e.printStackTrace();
        }


        OkHttpClient okHttpClient3 = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .sslSocketFactory(ignoreSSLSocketFactory(application.getApplicationContext()))
                .hostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER)
//                .addInterceptor(requestParamInterceptor)
                .addInterceptor(loggingInterceptor)
                //其他配置
                .build();

        //Retrofit初始化
        mRetrofit = new Retrofit.Builder()
                .baseUrl(Base_Host)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        mRetrofit2 = new Retrofit.Builder()
                .baseUrl(base_weather_api)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();


    }

    //忽略https证书验证
    protected static SSLSocketFactory ignoreSSLSocketFactory(Context context) {
        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, getTrustManager(), new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //获取TrustManager
    private static TrustManager[] getTrustManager() {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[]{};
            }
        }};
        return trustAllCerts;
    }

    @Provides
    @Singleton
    public UserApiService provideHomeService() {
        return mRetrofit.create(UserApiService.class);
    }

/*    @Provides
    @Singleton
    public BigDataApiService provideBigDataApiService() {
        return mRetrofit3.create(BigDataApiService.class);
    }*/

    @Provides
    @Singleton
    public WeatherDataApiService provideWeatherDataApiService() {
        return mRetrofit2.create(WeatherDataApiService.class);
    }

    public static Retrofit getRetrofit() {
        return mRetrofit;
    }

    //日志拼接
    private class HttpLogger implements HttpLoggingInterceptor.Logger {
        private StringBuilder mMessage = new StringBuilder();

        @Override
        public void log(String message) {
            // 请求或者响应开始
            if (message.startsWith("--> POST")) {
                mMessage.setLength(0);
            }
            // 以{}或者[]形式的说明是响应结果的json数据，需要进行格式化
            if ((message.startsWith("{") && message.endsWith("}"))
                    || (message.startsWith("[") && message.endsWith("]"))) {
                message = JSONUtils.formatJson(JSONUtils.decodeUnicode(message));
            }
            mMessage.append(message.concat("\n"));
            // 响应结束，打印整条日志
            if (message.startsWith("<-- END HTTP")) {
                Logger.d(mMessage.toString());
            }
        }


    }

}
