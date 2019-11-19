package com.comm.jksdk.http;

import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

import okhttp3.Cache;
import okhttp3.Interceptor;
import retrofit2.Converter;

/**
 * 代码描述<p>
 *
 * @author anhuiqing
 * @since 2019/4/16 0:48
 */

public interface IOkHttpConfig {
  /**
   * @return interceptor for doing something on the request or response
   */
  List<Interceptor> getInterceptors();

  /**
   * @return cache for OkHttp lib
   */
  Cache getCache();

  /**
   * @return protocol + domain,like "http://xxx.xx.com"
   */
  String getBaseUrl();

  /**
   * @return response data converter , json,xml, etc.
   */
  List<Converter.Factory> getConverter();

  /**
   * @return ssl for https
   */
  SSLSocketFactory getSSlSocketFactory();

  /**
   * @return verifier for request
   */
  HostnameVerifier getHostnameVerifier();
}
