package com.jess.arms.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;

import com.xiaoniu.cleaning.arms.R;

public class RotationLoadingView extends View {

    private Context mContext;
    private boolean mIsAnimation;
    private int rotate;
    private final int ROTATE_STEP = 10;
    private int mWidth;
    private int mHeight;
    /** 菊花 **/
    private Bitmap mForeBitmap;
    /** 旋转矩阵 **/
    private Matrix mMatrix = new Matrix();
    /** 是否逆时针转动，默认是 **/
    private boolean mClockwise = true;
    private PaintFlagsDrawFilter mPaintFlagsDrawFilter;

    /**
     * 构造函数，初始化前景图片和背景图片
     */
    public RotationLoadingView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public RotationLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    /**
     * 设置控件width、height
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = mForeBitmap.getWidth();
        mHeight = mForeBitmap.getHeight();
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 如果位图资源已经被回收，则再重新创建一次，然后再绘图
        if (mForeBitmap.isRecycled() && mIsAnimation) {
            init();
        }

        // 如果位图资源存在，则直接绘图
        if (!(mForeBitmap.isRecycled())) {
            // 设置旋转中心及旋转角度
            mMatrix.setRotate(rotate, mForeBitmap.getWidth() / 2, mForeBitmap.getHeight() / 2);
            // 消除canvas因图片旋转所产生的锯齿
            canvas.setDrawFilter(mPaintFlagsDrawFilter);
            canvas.drawBitmap(mForeBitmap, mMatrix, null); // 绘制前景图
            if (mIsAnimation) {
                rotate = rotate + ROTATE_STEP > 360 ? 0 : rotate + ROTATE_STEP;
                rotate = mClockwise ? rotate : -rotate;
                postInvalidate();
            }
        }
    }

    /** 开始动画 **/
    public void startRotationAnimation() {
        mIsAnimation = true;
        invalidate();
    }

    /** 停止动画 **/
    public void stopRotationAnimation() {
        mIsAnimation = false;
    }

    /**
     * 覆盖父类方法 以免外部忘记调用stopRotationAnimation(), 导致内存泄漏
     */
    protected void onDeAttachedToWindow() {
        stopRotationAnimation();
        super.onDetachedFromWindow();
    }

    private void init() {
        mPaintFlagsDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        mForeBitmap = ((BitmapDrawable) mContext.getResources().getDrawable(getResID())).getBitmap();
        invalidate();
    }

    private int getResID() {
        return R.mipmap.icon_dengdai;
    }
}

