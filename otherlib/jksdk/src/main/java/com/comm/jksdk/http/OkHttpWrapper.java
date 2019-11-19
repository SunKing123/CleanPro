package com.comm.jksdk.http;

import android.text.TextUtils;

import com.comm.jksdk.utils.CollectionUtils;

import java.io.IOException;

import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;


/**
 * 代码描述<p>
 *
 *     OkHttpWrapper
 *
 * @author anhuiqing
 * @since 2019/4/16 0:38
 */


public class OkHttpWrapper {

  private OkHttpConfig mConfig;
  private OkHttpClient mOkHttpClient;
  private Retrofit mRetrofit;
  private Retrofit.Builder mBuilder;

  private OkHttpWrapper() {
    init();
  }

  private void init() {
    initConfig();
    initOkHttpClient();
    initRetrofit();
  }

  private void initConfig() {
    mConfig = new OkHttpConfig();
  }

  private void initOkHttpClient() {
    OkHttpClient.Builder builder = new OkHttpClient.Builder();
    if (mConfig.getCache() != null) {
      builder.cache(mConfig.getCache());
    }
    if (!CollectionUtils.isEmpty(mConfig.getInterceptors())) {
      for (Interceptor i : mConfig.getInterceptors()) {
        builder.addInterceptor(i);
      }
    }
    mOkHttpClient =  RetrofitUrlManager.getInstance().with(builder)
            .build();
  }

  /**
   * retrofit默认连接配置，连接超时15秒,读取超时20秒,没有写入超时
   */
  private void initRetrofit() {
    mBuilder = new Retrofit.Builder();
    mBuilder.callFactory(mOkHttpClient)
        .baseUrl(mConfig.getBaseUrl());
    if (!CollectionUtils.isEmpty(mConfig.getConverter())) {
      for (Converter.Factory c : mConfig.getConverter()) {
        mBuilder.addConverterFactory(c);
      }
    }
    mBuilder.addCallAdapterFactory(RxJava2CallAdapterFactory.create());
    mRetrofit = mBuilder.build();
  }

  public static OkHttpWrapper getInstance() {
    return Holder.instance;
  }

  private static class Holder {
    public static OkHttpWrapper instance = new OkHttpWrapper();
  }

  public OkHttpClient getOkHttpClient() {
    return mOkHttpClient;
  }

  public Retrofit getRetrofit() {
    return mRetrofit;
  }

  public boolean clearCache() {
    try {
      mConfig.getCache().delete();
      return true;
    } catch (IOException e) {
      return false;
    }
  }

  public void updateBaseUrl(String url) {
    if (!TextUtils.isEmpty(url)) {
      mBuilder = mBuilder.baseUrl(url);
      mRetrofit = mBuilder.build();
    }
  }
}
