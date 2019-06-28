package com.installment.mall.widget;

//import com.scwang.smartrefresh.layout.api.RefreshHeader;
//import com.scwang.smartrefresh.layout.api.RefreshKernel;
//import com.scwang.smartrefresh.layout.api.RefreshLayout;
//import com.scwang.smartrefresh.layout.constant.RefreshState;
//import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
//
///**
// * Created by fengpeihao on 2019/1/21.
// */
//
//public class AnimHeader extends AppCompatImageView implements RefreshHeader {
//
//    private boolean mIsInLoading = false;
//
//    public AnimHeader(Context context) {
//        super(context);
//        init();
//    }
//
//    public AnimHeader(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        init();
//    }
//
//    private void init() {
//        setPadding(0, DeviceUtils.getStatusBarHeight(getContext()) + 10, 0, 0);
//    }
//
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//    }
//
//    public AnimHeader(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//    }
//
//    @NonNull
//    @Override
//    public View getView() {
//        return this;
//    }
//
//    @NonNull
//    @Override
//    public SpinnerStyle getSpinnerStyle() {
//        return SpinnerStyle.Translate;
//    }
//
//    @Override
//    public void setPrimaryColors(int... colors) {
//
//    }
//
//    @Override
//    public void onInitialized(@NonNull RefreshKernel kernel, int height, int maxDragHeight) {
//
//    }
//
//    @Override
//    public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {
//        showImage(percent);
//        Log.e("onMoving", "isDragging" + isDragging + "  percent" + percent + "  height" + height + "   maxDragHeight" + maxDragHeight);
//    }
//
//    @Override
//    public void onReleased(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {
//
//    }
//
//    @Override
//    public void onStartAnimator(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {
//        mIsInLoading = true;
//        Log.e("onStartAnimator", "height" + height + "   maxDragHeight" + maxDragHeight);
//    }
//
//    @Override
//    public int onFinish(@NonNull RefreshLayout refreshLayout, boolean success) {
//        return 500;
//    }
//
//    @Override
//    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {
//
//    }
//
//    @Override
//    public boolean isSupportHorizontalDrag() {
//        return false;
//    }
//
//    @Override
//    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
//        Log.e("onStateChanged", newState + "");
//        switch (newState) {
//            case None:
//            case PullDownToRefresh://下拉开始刷新
//                break;
//            case Refreshing://正在刷新
//                getAnimationDrawable(AnimHeader.this, R.drawable.refresh_circulation).start();
//                break;
//            case ReleaseToRefresh:
//
//                break;
//            case RefreshReleased://释放立即刷新
//                getAnimationDrawable(this, R.drawable.refresh_release).start();
//                break;
//        }
//
//    }
//
//    private void showImage(float percent) {
//        percent = percent - 0.1f;
//        float divide = (float)(1.0 / 17.0);
//        System.out.print("--------------------------------------" +percent);
//        if (0 <= percent && percent < divide) {
//            setImageResource(R.mipmap.xl0001);
//        }
//        if (divide <= percent && percent < divide * 2) {
//            setImageResource(R.mipmap.xl0002);
//        }
//        if (divide * 2 <= percent && percent < divide * 3) {
//            setImageResource(R.mipmap.xl0003);
//        }
//        if (divide * 3 <= percent && percent < divide * 4) {
//            setImageResource(R.mipmap.xl0004);
//        }
//        if (divide * 4 <= percent && percent < divide * 5) {
//            setImageResource(R.mipmap.xl0005);
//        }
//        if (divide * 5 <= percent && percent < divide * 6) {
//            setImageResource(R.mipmap.xl0006);
//        }
//        if (divide * 6 <= percent && percent < divide * 7) {
//            setImageResource(R.mipmap.xl0007);
//        }
//        if (divide * 7 <= percent && percent < divide * 8) {
//            setImageResource(R.mipmap.xl0008);
//        }
//        if (divide * 8 <= percent && percent < divide * 9) {
//            setImageResource(R.mipmap.xl0009);
//        }
//        if (divide * 9 <= percent && percent < divide * 10) {
//            setImageResource(R.mipmap.xl0010);
//        }
//        if (divide * 10 <= percent && percent < divide * 11) {
//            setImageResource(R.mipmap.xl0011);
//        }
//        if (divide * 11 <= percent && percent < divide * 12) {
//            setImageResource(R.mipmap.xl0012);
//        }
//        if (divide * 12 <= percent && percent < divide * 13) {
//            setImageResource(R.mipmap.xl0013);
//        }
//        if (divide * 13 <= percent && percent < divide * 14) {
//            setImageResource(R.mipmap.xl0014);
//        }
//        if (divide * 14 <= percent && percent < divide * 15) {
//            setImageResource(R.mipmap.xl0015);
//        }
//        if (divide * 15 <= percent && percent < divide * 16) {
//            setImageResource(R.mipmap.xl0016);
//        }
//        if (divide * 16 <= percent && percent < divide * 17) {
//            setImageResource(R.mipmap.xl0017);
//        }
//    }
//
//
//    /**
//     * 加载帧动画
//     *
//     * @param imageView
//     * @param resourceId 帧动画
//     */
//    private AnimationDrawable getAnimationDrawable(ImageView imageView, int resourceId) {
//        imageView.setImageResource(resourceId);
//        AnimationDrawable drawable = (AnimationDrawable) imageView.getDrawable();
//        drawable.setOneShot(true);
//        return drawable;
//    }
//}
