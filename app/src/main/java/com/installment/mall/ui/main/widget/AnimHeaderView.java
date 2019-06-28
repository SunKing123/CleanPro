package com.installment.mall.ui.main.widget;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.installment.mall.R;
import com.installment.mall.utils.DeviceUtils;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;

/**
 * Created by fengpeihao on 2019/1/22.
 */

public class AnimHeaderView extends AppCompatImageView implements RefreshHeader {
    private boolean mIsInLoading = false;

    public AnimHeaderView(Context context) {
        super(context);
        init();
    }

    public AnimHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AnimHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setPadding(0, DeviceUtils.getStatusBarHeight() + 10, 0, 0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @NonNull
    @Override
    public View getView() {
        return this;
    }

    @NonNull
    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.Translate;
    }

    @Override
    public void setPrimaryColors(int... colors) {

    }

    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int maxDragHeight) {

    }

    @Override
    public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {
        if (mIsInLoading)
            showImage(percent);
        Log.e("onMoving", "isDragging" + isDragging + "  percent" + percent + "  height" + height + "   maxDragHeight" + maxDragHeight);
    }

    @Override
    public void onReleased(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {

    }

    @Override
    public void onStartAnimator(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {

        Log.e("onStartAnimator", "height" + height + "   maxDragHeight" + maxDragHeight);
    }

    @Override
    public int onFinish(@NonNull RefreshLayout refreshLayout, boolean success) {
        return 200;
    }

    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {

    }

    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }

    @Override
    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
        Log.e("onStateChanged", newState + "");
        switch (newState) {
            case None:
            case PullDownToRefresh:
                //下拉开始刷新
                mIsInLoading = true;
                break;
            case Refreshing:
                //正在刷新
                break;
            case ReleaseToRefresh:

                break;
            case RefreshReleased:
                //释放立即刷新
                mIsInLoading = false;
                AnimationDrawable animationDrawable = getAnimationDrawable(AnimHeaderView.this, R.drawable.refresh_release);
                animationDrawable.setOneShot(true);
                animationDrawable.start();
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getAnimationDrawable(AnimHeaderView.this, R.drawable.refresh_circulation).start();
                    }
                }, 520);
                break;
            default:
                break;
        }

    }

    private void showImage(float percent) {
        percent = percent - 0.1f;
        float divide = (float) (1.0 / 17.0);
        if (0 <= percent && percent < divide) {
            setImageResource(R.mipmap.xl0001);
        }
        if (divide <= percent && percent < divide * 2) {
            setImageResource(R.mipmap.xl0002);
        }
        if (divide * 2 <= percent && percent < divide * 3) {
            setImageResource(R.mipmap.xl0003);
        }
        if (divide * 3 <= percent && percent < divide * 4) {
            setImageResource(R.mipmap.xl0004);
        }
        if (divide * 4 <= percent && percent < divide * 5) {
            setImageResource(R.mipmap.xl0005);
        }
        if (divide * 5 <= percent && percent < divide * 6) {
            setImageResource(R.mipmap.xl0006);
        }
        if (divide * 6 <= percent && percent < divide * 7) {
            setImageResource(R.mipmap.xl0007);
        }
        if (divide * 7 <= percent && percent < divide * 8) {
            setImageResource(R.mipmap.xl0008);
        }
        if (divide * 8 <= percent && percent < divide * 9) {
            setImageResource(R.mipmap.xl0009);
        }
        if (divide * 9 <= percent && percent < divide * 10) {
            setImageResource(R.mipmap.xl0010);
        }
        if (divide * 10 <= percent && percent < divide * 11) {
            setImageResource(R.mipmap.xl0011);
        }
        if (divide * 11 <= percent && percent < divide * 12) {
            setImageResource(R.mipmap.xl0012);
        }
        if (divide * 12 <= percent && percent < divide * 13) {
            setImageResource(R.mipmap.xl0013);
        }
        if (divide * 13 <= percent && percent < divide * 14) {
            setImageResource(R.mipmap.xl0014);
        }
        if (divide * 14 <= percent && percent < divide * 15) {
            setImageResource(R.mipmap.xl0015);
        }
        if (divide * 15 <= percent && percent < divide * 16) {
            setImageResource(R.mipmap.xl0016);
        }
        if (divide * 16 <= percent && percent < divide * 17) {
            setImageResource(R.mipmap.xl0017);
        }
    }


    /**
     * 加载帧动画
     *
     * @param imageView
     * @param resourceId 帧动画
     */
    private AnimationDrawable getAnimationDrawable(ImageView imageView, int resourceId) {
        imageView.setImageResource(resourceId);
        return (AnimationDrawable) imageView.getDrawable();
    }
}
