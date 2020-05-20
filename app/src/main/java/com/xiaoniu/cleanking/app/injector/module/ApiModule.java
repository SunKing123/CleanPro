package com.xiaoniu.cleanking.app.injector.module;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.xiaoniu.cleanking.BuildConfig;
import com.xiaoniu.cleanking.api.UserApiService;

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

    private Retrofit mRetrofit;

    public static String Base_H5_Host = BuildConfig.H5_BASE_URL;//H5路径

    public static String SHOPPING_MALL = Base_H5_Host + "/home_new.html";//商城

    public ApiModule(Application application) {
        //原生Log日志拦截
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message ->
                Log.i("print", "okhttp=>" + message));
        if (BuildConfig.DEBUG) {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        }
        RequestParamInterceptor requestParamInterceptor = new RequestParamInterceptor();
        OkHttpClient okHttpClient = null;
        try {
            okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .sslSocketFactory(ignoreSSLSocketFactory(application.getApplicationContext()))
                    .hostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER)
                    .addInterceptor(requestParamInterceptor)
                    .addInterceptor(loggingInterceptor)
                    //其他配置
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Retrofit初始化
        //网络地址
        String base_Host = BuildConfig.BASE_URL;
        mRetrofit = new Retrofit.Builder()
                .baseUrl(base_Host)
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
}
