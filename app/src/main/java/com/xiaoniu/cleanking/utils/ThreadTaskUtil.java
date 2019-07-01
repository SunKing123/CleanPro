package com.xiaoniu.cleanking.utils;

import android.support.annotation.NonNull;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadTaskUtil
{
  private static final int NUMBER_OF_CPU_CORES = Runtime.getRuntime().availableProcessors();
  public static int corePoolSize = NUMBER_OF_CPU_CORES * 2;
  public static ExecutorService normalTask;
  public static BlockingQueue<Runnable> workQueue;
  
  public static void executeNormalTask(String paramString, Runnable paramRunnable)
  {
    if (workQueue == null) {
      workQueue = new ArrayBlockingQueue(1000);
    }
    if (normalTask == null)
    {
      if (corePoolSize <= 0) {
        corePoolSize = 10;
      }
      normalTask = new ThreadPoolExecutor(corePoolSize, NUMBER_OF_CPU_CORES * 10, 5L, TimeUnit.SECONDS, workQueue, new ThreadFactory()
      {
        public Thread newThread(@NonNull Runnable paramAnonymousRunnable)
        {
          return new Thread(paramAnonymousRunnable, "cleanMaster_pool__");
        }
      });
    }
    normalTask.execute(paramRunnable);
  }



}
