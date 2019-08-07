package com.xiaoniu.cleanking.app.injector.module;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.xiaoniu.cleanking.AppConstants;
import com.xiaoniu.cleanking.BuildConfig;
import com.xiaoniu.cleanking.api.BigDataApiService;
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
    private Retrofit mRetrofit2;
    private Retrofit mRetrofit3;

    //网络地址
    public static String Base_Host = AppConstants.Base_Host;
    public static String Base_H5_Host = AppConstants.Base_H5_Host;//H5路径
    public static String Base_Big_Data = AppConstants.Base_Big_Data;//大数据接口路径

    public static String Base_Host2 = "https://www.juxinli.com";//聚信立路径
    public static String Base_Host3 = "https://credit.baiqishi.com";//白骑士路径
    public static String ZhiMaXinYong = Base_H5_Host + "/FlashLoanH5/html/page/my/zhima.html";//芝麻信用路径
    public static String SHOPPING_MALL = Base_H5_Host + "/home_new.html";//商城


    public ApiModule(Application application) {
        //原生Log日志拦截
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.e("print","okhttp=>"+message);
            }
        });
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
        try

        {
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
                Exception e)

        {
            e.printStackTrace();
        }

        OkHttpClient okHttpClient2 = new OkHttpClient.Builder()
                .addInterceptor(new RequestUrlInterceptor())
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
//                .sslSocketFactory(getSSLSocketFactory(application.getApplicationContext())).hostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER)
                .addInterceptor(loggingInterceptor)
                //其他配置
                .build();

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
                .baseUrl(Base_Host2)
                .client(okHttpClient2)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        mRetrofit3 = new Retrofit.Builder()
                .baseUrl(Base_Big_Data)
                .client(okHttpClient3)
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

    @Provides
    @Singleton
    public BigDataApiService provideBigDataApiService() {
        return mRetrofit3.create(BigDataApiService.class);
    }

}
