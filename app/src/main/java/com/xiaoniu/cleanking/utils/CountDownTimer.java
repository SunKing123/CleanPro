package com.xiaoniu.cleanking.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * create by fireking on 2017/11/12
 */
public class CountDownTimer {

    private Handler mUiHandler;

    /**
     * 计时器
     */
    private long count = 0;

    /**
     * 计时间隔
     */
    private final long CELL_TIME = 1100;

    private OnTimeChangedListener mOnTimeChangedListener;

    private final int COUNT_DOWN = 1;

    private boolean isInterupt = false;
    private boolean isRunning = false;

    public CountDownTimer(OnTimeChangedListener onTimeChangedListener) {
        mOnTimeChangedListener = onTimeChangedListener;
        mUiHandler = new Handler(Looper.getMainLooper(), mUiHandlerCallback);
    }

    private Handler.Callback mUiHandlerCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case COUNT_DOWN:
                    if (isInterupt) {
                        break;
                    } else {
                        count = count - CELL_TIME;
                        if (count == 0) {
                            isRunning = false;
                            mOnTimeChangedListener.onFinish();
                            mUiHandler.removeMessages(COUNT_DOWN);
                            break;
                        }
                        if (count > 0) {
                            mOnTimeChangedListener.onTicket(count / CELL_TIME);
                        }
                        mUiHandler.removeMessages(COUNT_DOWN);
                        mUiHandler.sendEmptyMessageDelayed(COUNT_DOWN, CELL_TIME);
                    }
                    break;
            }
            return true;
        }
    };

    /**
     * 开始计时操作调用
     */
    public void start(long countTime) {
        count = countTime;
        isRunning = true;
        mUiHandler.sendEmptyMessageDelayed(COUNT_DOWN, CELL_TIME);
    }

    /**
     * 停止计时
     */
    public void stop() {
        isInterupt = true;
        isRunning = false;
        mUiHandler.removeMessages(COUNT_DOWN);
    }

    public boolean isRunning() {
        return isRunning;
    }

    /**
     * 暂停之后调用
     */
    public void onPause() {
        if (count != 0) {
            mUiHandler.removeMessages(COUNT_DOWN);
        }
    }

    /**
     * 恢复调用
     */
    public void onResume() {
        if (count != 0) {
            mUiHandler.sendEmptyMessage(COUNT_DOWN);
        }
    }

    /**
     * 时间变化之后进行逻辑处理
     */
    public interface OnTimeChangedListener {

        /**
         * 每次变化都进行触发
         */
        void onTicket(long currentTime);

        /**
         * 时间变化倒计时完成触发
         */
        void onFinish();
    }
}