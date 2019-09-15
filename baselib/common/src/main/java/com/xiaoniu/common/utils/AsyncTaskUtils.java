package com.xiaoniu.common.utils;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class AsyncTaskUtils {

    private static Handler sHandler = new Handler(Looper.getMainLooper());
    public static Executor THREAD_POOL_EXECUTOR = null;
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = Math.max(2, CPU_COUNT * 2);
    private static final int MAXIMUM_POOL_SIZE = CORE_POOL_SIZE;
    private static final int KEEP_ALIVE_SECONDS = 30;

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "AsyncTaskUtils #" + mCount.getAndIncrement());
        }
    };

    private static final BlockingQueue<Runnable> sPoolWorkQueue =
            new LinkedBlockingQueue<Runnable>(128);

    static {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
                sPoolWorkQueue, sThreadFactory);
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        THREAD_POOL_EXECUTOR = threadPoolExecutor;
    }

    public static void execute(AsyncTask asyncTask) {
        if (asyncTask != null) {
            asyncTask.executeOnExecutor(THREAD_POOL_EXECUTOR);
        }
    }

    public static void execute(final ThreadTask threadTask) {
        if (threadTask != null) {
            THREAD_POOL_EXECUTOR.execute(new Runnable() {
                @Override
                public void run() {
                    final Object result = threadTask.doInBackground();
                    sHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            threadTask.onResultMainThread(result);
                        }
                    });
                }
            });
        }
    }

    public static void background(final Runnable task) {
        if (task != null) {
            THREAD_POOL_EXECUTOR.execute(task);
        }
    }

    public static void backgroundDelay(final Runnable task, long delay) {
        sHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                background(task);
            }
        }, delay);
    }

    public static void uiThread(Runnable task) {
        sHandler.post(task);
    }

    public static void uiThreadDelay(Runnable task, long delay) {
        sHandler.postDelayed(task, delay);
    }

    public static interface ThreadTask<T> {
        T doInBackground();

        void onResultMainThread(T result);
    }
}
