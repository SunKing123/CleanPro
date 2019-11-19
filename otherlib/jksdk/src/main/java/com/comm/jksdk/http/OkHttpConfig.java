package com.comm.jksdk.http;


import com.comm.jksdk.http.interceptor.ApiEventInterceptor;
import com.comm.jksdk.http.utils.ApiManage;

import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

import okhttp3.Cache;
import okhttp3.Interceptor;
import retrofit2.Converter;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * 代码描述<p>
 *
 * @author anhuiqing
 * @since 2019/4/16 0:43
 */
public class OkHttpConfig implements IOkHttpConfig {
  private List<Interceptor> mInterceptors;
  private List<Converter.Factory> mConverterFactories;

  public OkHttpConfig() {
    initInterceptors();
    initConverters();
  }

  /**
   * 添加拦截器
   */
  private void initInterceptors() {
    mInterceptors = new ArrayList<>();
    mInterceptors.add(new ApiEventInterceptor());// 统计api 拦截器
   // mInterceptors.add(new SignInterceptor()); // 此interceptor需要放最后！！！
  }


  /**
   * 添加转换器
   */
  private void initConverters() {
    mConverterFactories = new ArrayList<>();
    mConverterFactories.add(ScalarsConverterFactory.create());// 请求结果转换为基本类型
    mConverterFactories.add(GsonConverterFactory.create());// 请求的结果转为实体类
  }

  @Override
  public List<Interceptor> getInterceptors() {
    return mInterceptors == null ? new ArrayList<Interceptor>(): mInterceptors;
  }

  @Override
  public Cache getCache() {
    return null;
  }

  @Override
  public String getBaseUrl() {
    return ApiManage.getWeatherURL();
  }

  @Override
  public List<Converter.Factory> getConverter() {
    return mConverterFactories;
  }

  @Override
  public SSLSocketFactory getSSlSocketFactory() {
    return null; // TODO:
  }

  @Override
  public HostnameVerifier getHostnameVerifier() {
    return null; // TODO:
  }

}
