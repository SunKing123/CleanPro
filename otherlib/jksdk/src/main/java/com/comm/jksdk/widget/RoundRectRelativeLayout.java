package com.comm.jksdk.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.comm.jksdk.R;

public class RoundRectRelativeLayout extends RelativeLayout {

    private Paint mRectPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);

    private ValueAnimator valueAnimator;
    private float updateValue;

    private RectF mBorderRectF = new RectF();
    private float mBorderRadius;
    private float mBorderRectPadding;
    private Path mRectPath = new Path();
    private PathMeasure mPathMeasure = new PathMeasure();
    private Path mBorderSegmentPath = new Path();
    private Path mBorderOtherPath = new Path();

    public RoundRectRelativeLayout(Context context) {
        this(context, null);
    }

    public RoundRectRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundRectRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RoundRectRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init();
    }

    private void init() {
        setWillNotDraw(false);
        mBorderRadius = dip2Pixel(16);
        mBorderRectPadding = dip2Pixel(8);
        mRectPaint.setColor(Color.parseColor("#f14400"));
        mRectPaint.setStrokeWidth(20);
        mRectPaint.setStrokeCap(Paint.Cap.ROUND);
        mRectPaint.setStyle(Paint.Style.STROKE);
    }

    private int dip2Pixel(int defValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, defValue, getResources().getDisplayMetrics());
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startLooperAnimation();
    }

    private void startLooperAnimation() {
        if (valueAnimator != null) {
            valueAnimator.start();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopLooperAnimation();
    }

    private void stopLooperAnimation() {
        if (valueAnimator != null) {
            valueAnimator.end();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        final int viewWidth = getWidth();
        final int viewHeight = getHeight();

        mBorderRectF.left = mBorderRectPadding;
        mBorderRectF.top = mBorderRectPadding;
        mBorderRectF.right = viewWidth - mBorderRectPadding;
        mBorderRectF.bottom = viewHeight - mBorderRectPadding;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mRectPath.reset();
        mRectPath.addRoundRect(mBorderRectF, mBorderRadius, mBorderRadius, Path.Direction.CW);
        mPathMeasure.setPath(mRectPath, false);


        mRectPaint.setShader(new LinearGradient(updateValue, 0, mPathMeasure.getLength(), 0, new int[]{
                ContextCompat.getColor(getContext(), R.color.color_FECE22),
                ContextCompat.getColor(getContext(), R.color.colorPrimary),
                ContextCompat.getColor(getContext(), R.color.colorAccent),
                ContextCompat.getColor(getContext(), R.color.color_803fc5f0),
        }, new float[]{0.2F, 0.4F, 0.6F, 0.8F}, Shader.TileMode.MIRROR));

        float mProgressWidth = mPathMeasure.getLength() / 3;

        if (mProgressWidth + updateValue >= mPathMeasure.getLength()) {
            float nextStop = mProgressWidth + updateValue - mPathMeasure.getLength();
            mPathMeasure.getSegment(0, nextStop, mBorderSegmentPath, true);
            canvas.drawPath(mBorderSegmentPath, mRectPaint);
            mBorderSegmentPath.reset();

            mBorderOtherPath.reset();
            mPathMeasure.getSegment(mPathMeasure.getLength() - (mProgressWidth - nextStop), mPathMeasure.getLength(), mBorderSegmentPath, true);
            canvas.drawPath(mBorderOtherPath, mRectPaint);
        } else {
            mBorderSegmentPath.reset();
            mPathMeasure.getSegment(updateValue, mProgressWidth + updateValue, mBorderSegmentPath, true);
            canvas.drawPath(mBorderSegmentPath, mRectPaint);
        }

        if (valueAnimator == null) {
            valueAnimator = ValueAnimator.ofFloat(0, mPathMeasure.getLength());
            valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
            valueAnimator.setDuration(10000);
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(valueAnimator -> {
                updateValue = (float) valueAnimator.getAnimatedValue();
                invalidate();
            });
        }

        if (!valueAnimator.isRunning()) {
            valueAnimator.start();
        }

    }

}
