package com.xiaoniu.cleanking.ui.newclean.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.widget.NestedScrollView;

/**
 * author : zyt
 * e-mail : 632105276@qq.com
 * date   : 2018/12/29
 * desc   :自定义ScrollView，实现滑动监听和滑动到底部监听
 */

public class ObservableScrollView extends NestedScrollView {
    private ScrollViewListener scrollViewListener;
    private int lastScrollY = -1;
    //停止状态
    public static final int STATE_DEFAULT = 1;
    //正在滑动
    public static final int STATE_SLIDING = 2;
    //判断是否是拖动状态
    boolean isDragState = false;

    public ObservableScrollView(Context context) {
        super(context);
    }

    public ObservableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 10001:
                    int scrollY = getScrollY();
                    if (scrollY != lastScrollY) {
                        mHandler.removeMessages(10001);
                        mHandler.sendEmptyMessageDelayed(10001, 50);
                    } else {
                        if (scrollViewListener != null) {
                            scrollViewListener.onScrollState(STATE_DEFAULT);
                        }
                    }
                    lastScrollY = scrollY;
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        View view = getChildAt(getChildCount() - 1);
        int d = view.getBottom();
        d -= (getHeight() + getScrollY());
        boolean isBottom = false;
        if (d == 0) {
            isBottom = true;
        }
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChanged(x, y, oldx, oldy, isBottom);
            //正在拖拽
            if (Math.abs(y - oldy) > 3) {
                scrollViewListener.onScrollState(STATE_SLIDING);
            }
        }
    }

    /**
     * 在activity的ondestroy调用下。以防handler造成泄漏
     */
    public void onDestroyView() {
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                isDragState = true;
                if (mHandler != null) {
                    mHandler.removeMessages(10001);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isDragState = false;
                if (mHandler != null) {
                    mHandler.sendEmptyMessageDelayed(10001, 50);
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    public interface ScrollViewListener {
        void onScrollChanged(int x, int y, int oldx, int oldy, boolean isBottom);

        void onScrollState(int scrollState);
    }
}
