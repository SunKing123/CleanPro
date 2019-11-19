package com.comm.jksdk.http.utils;

import android.util.Log;

/**
 * 使用此类 必须先init
 *
 * Created by zhaobenbing@innotechx.com on 18/1/26.
 */
public class AppEnvironment {

  /**
   * 修改此值没有意义，因为使用者通常会在 application init 的时候从硬盘初始化此值。
   */
  private static ServerEnvironment sServerEnvironment = ServerEnvironment.Dev;
  private static boolean sIsInTestMode = false;
  private static boolean sInitFlag = false;
  private static ServerEnvironmentStub sEnvironmentStub;
  private static TestModeStub sTestModeStub;

  public enum ServerEnvironment {
    Dev,Test, Uat ,Product
  }

  public static synchronized void init(ServerEnvironmentStub environmentStub,
      TestModeStub testModeStub) {
    AppEnvironment.sEnvironmentStub = environmentStub;
    AppEnvironment.sTestModeStub = testModeStub;
    if (environmentStub != null) {
      int environment = sEnvironmentStub.getServerEnvironment();
      if (environment == ServerEnvironment.Dev.ordinal()) {
        sServerEnvironment = ServerEnvironment.Dev;
      } else if (environment == ServerEnvironment.Test.ordinal()) {
        sServerEnvironment = ServerEnvironment.Test;
      } else if (environment == ServerEnvironment.Uat.ordinal()) {
        sServerEnvironment = ServerEnvironment.Uat;
      } else if (environment == ServerEnvironment.Product.ordinal()) {
        sServerEnvironment = ServerEnvironment.Product;
      }
    } else {
      throw new RuntimeException("ServerEnvironmentStub should not be null ");
    }

    if (sTestModeStub != null) {
      sIsInTestMode = sTestModeStub.isTestMode();
    } else {
      throw new RuntimeException("TestModeStub should not be null ");
    }
    sInitFlag = true;
  }

  public static synchronized ServerEnvironment getServerApiEnvironment() {
    checkInit();
    return sServerEnvironment;
  }

  public static synchronized boolean isInTestMode() {
    checkInit();
    return sIsInTestMode;
  }

  public static synchronized void setServerEnvironment(ServerEnvironment environment) {
    if (environment != null) {
      sServerEnvironment = environment;
      if (sEnvironmentStub != null) {
        sEnvironmentStub.setServerEnvironmentOrdinal(environment.ordinal());
      }
    }
  }

  public static synchronized void setIsInTestMode(boolean isInTestMode) {
    sIsInTestMode = isInTestMode;
    if (sTestModeStub != null) {
      sTestModeStub.setIsTestMode(isInTestMode);
    }
  }

  public interface ServerEnvironmentStub {
    int getServerEnvironment();

    void setServerEnvironmentOrdinal(int ordinal);
  }

  public interface TestModeStub {
    void setIsTestMode(boolean isTestMode);

    boolean isTestMode();
  }

  private static void checkInit() {
    if (!sInitFlag) {
      Log.e("AppEnvironment--", "AppEnvironment should  be init");
      throw new RuntimeException("AppEnvironment should  be init");
    }
  }

}
