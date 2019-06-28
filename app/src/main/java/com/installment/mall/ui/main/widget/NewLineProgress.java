package com.installment.mall.ui.main.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.installment.mall.callback.OnButtonClickListener;

/**
 * Created by fengpeihao on 2017/9/11.
 */

public class NewLineProgress extends View {

    private int mHeight;
    private int mWidth;
    private Paint mPaint;
    private int mMax = 1;
    private int mProgress;
    private float frac;
    private int mPreProgress;
    private Handler mHandler = new Handler();

    public NewLineProgress(Context context) {
        super(context);
        mPaint = new Paint();
    }

    public NewLineProgress(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
    }

    public NewLineProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setAntiAlias(true);  //消除锯齿
        mPaint.setShader(null);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setColor(Color.parseColor("#FB7448"));
        float paintWidth = mHeight;
        mPaint.setStrokeWidth(mHeight);
        float startX = paintWidth / 2f;
        float startY = mHeight / 2f;
        float endX = mWidth - paintWidth / 2f;
        float endY = mHeight / 2f;
        canvas.drawLine(startX, startY, endX, endY, mPaint);//底层白
        mPaint.setColor(Color.parseColor("#FFFFFF"));
        paintWidth = mHeight * 3f / 4f;
        startY = mHeight / 2f;
        endY = startY;
        mPaint.setStrokeWidth(paintWidth);
        canvas.drawLine(startX + mHeight / 32f, startY, endX - mHeight / 32f, endY, mPaint);//背景
//        mPaint.setColor(Color.parseColor("#A46472"));
//        mPaint.setARGB(20, 0, 0, 0);
//        paintWidth = mHeight * 5f / 8f;
//        mPaint.setStrokeWidth(paintWidth);
//        canvas.drawLine(startX, startY, endX, endY, mPaint);//内阴影

        mPaint.reset();
        mPaint.setAntiAlias(true);  //消除锯齿
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        float progressWidth = mWidth - dip2px(2) - paintWidth;
        float preWidth = mPreProgress * 1.0f / mMax * progressWidth;//变化前的长度
        float fracWidth = (mProgress - mPreProgress) * 1.0f / mMax * progressWidth * frac;//变化的长度
//        int[] colors = {Color.parseColor("#FFF8D3"), Color.parseColor("#FFF8D3")};
//        LinearGradient linearGradient = new LinearGradient(startX, 0, Math.max(preWidth + fracWidth, startX), mHeight, colors, null, Shader.TileMode.CLAMP);
//        mPaint.setShader(linearGradient);
        mPaint.setColor(Color.parseColor("#FB7448"));
        mPaint.setStrokeWidth(mHeight / 2f);
        float endx = Math.max(startX, startX + preWidth + fracWidth);
        canvas.drawLine(startX, startY, endx, endY, mPaint);//进度
//        paintWidth = mHeight * 21f / 32f;
//        mPaint.setStrokeWidth(paintWidth);
//        mPaint.setARGB(20, 0, 0, 0);
//        mPaint.setShader(null);
//        startY = mHeight / 2f;
//        endY = startY;
//        canvas.drawLine(startX - mHeight / 16f, startY, Math.max(startX + preWidth + fracWidth + mHeight / 16f, startX - mHeight / 16f), endY, mPaint);//进度蒙影

//        mPaint.setARGB(45, 255, 255, 255);
//        mPaint.setStrokeWidth(mHeight / 8f);
//        startX = mHeight * 5f / 8f;
//        float maxWidth = startX + preWidth + fracWidth + mHeight / 16f;
//        while (startX <= maxWidth) {
//            endX = startX - mHeight / 4f;
//            startY = mHeight / 4f;
//            endY = mHeight * 11f / 16f;
//            canvas.drawLine(startX, startY, endX, endY, mPaint);
//            startX += mHeight / 3f;
//        }
    }

    public void setProgressAndMax(int progress, int max) {
        mMax = max;
        mPreProgress = mProgress;
        mProgress = progress;
        setAnim(1000);
    }

    public void levelUp(final int progress, final int max, final OnButtonClickListener listener) {
        mPreProgress = mProgress;
        mProgress = mMax;
        setAnim(500);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                listener.onClick();
                mPreProgress = 0;
                mProgress = progress;
                mMax = max;
                setAnim(500);
            }
        }, 500);
    }

    public void setProgressFromZero(int progress, int max) {
        mPreProgress = 0;
        mProgress = progress;
        mMax = max;
        setAnim(1000);
    }

    private void setAnim(long duration) {
        frac = 0;
        ValueAnimator anim = ValueAnimator.ofFloat(0, 1);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                frac = animation.getAnimatedFraction();
                postInvalidate();
            }
        });
        anim.setDuration(duration);
        anim.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (widthMode == MeasureSpec.AT_MOST) {
            mWidth = dip2px(201);
        }
        if (heightMode == MeasureSpec.AT_MOST) {
            mHeight = dip2px(8);
        }
        if (widthMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(mWidth, mHeight);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
