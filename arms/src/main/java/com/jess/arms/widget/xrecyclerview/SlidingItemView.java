package com.jess.arms.widget.xrecyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;


public class SlidingItemView extends HorizontalScrollView {
    private View mMenuView;
    private LinearLayout mRootLayout;
    private boolean isOpen;
    private View mContentView;

    public SlidingItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public SlidingItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setHorizontalScrollBarEnabled(false);
        /*rootView*/
        mRootLayout = new LinearLayout(context);
        mRootLayout.setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams rootParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        addView(mRootLayout, rootParams);
    }

    public SlidingItemView(Context context) {
        this(context, null, 0);
    }

    public void addMenuView(View menuView) {
        this.mMenuView = menuView;
        mRootLayout.addView(menuView);
    }

    public View getMenuView() {
        return mMenuView;
    }

    public void addContentView(View contentView) {
        this.mContentView = contentView;
        mRootLayout.addView(contentView);
    }

    public View getContentView() {
        return mContentView;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = ((View) getParent()).getMeasuredWidth();
        if (mContentView != null) {
            mContentView.getLayoutParams().width = width;
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            //默认将菜单隐藏
            this.scrollTo(0, 0);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            // Up时，进行判断，如果显示区域大于菜单宽度一半则完全显示，否则隐藏
            case MotionEvent.ACTION_UP:
                int scrollX = getScrollX();
                if (scrollX > mMenuView.getWidth() / 2) {
                    this.smoothScrollTo(mMenuView.getWidth(), 0);
                    isOpen = true;
                } else {
                    this.smoothScrollTo(0, 0);
                    isOpen = false;
                }
                return true;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 打开菜单
     */
    public void openMenu() {
        if (!isOpen) {
            this.smoothScrollTo(mMenuView.getWidth(), 0);
            isOpen = true;
        }
    }

    /**
     * 关闭菜单
     */
    public void closeMenu() {
        if (isOpen) {
            this.smoothScrollTo(0, 0);
            isOpen = false;
        }
    }

    /**
     * 切换菜单状态
     */
    public void toggle() {
        if (isOpen) {
            closeMenu();
        } else {
            openMenu();
        }
    }

}
