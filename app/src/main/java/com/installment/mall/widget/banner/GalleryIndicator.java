package com.installment.mall.widget.banner;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;

import com.installment.mall.R;


public class GalleryIndicator extends View {

//    private static int DOT_NORMAL_ID = R.mipmap.point_nomal;
//    private static int DOT_PRESS_ID = R.mipmap.point_selected;
    private static int DOT_NORMAL_ID = R.mipmap.banner_dot_off;
    private static int DOT_PRESS_ID = R.mipmap.banner_dot_on;

    private int mCount;

    private float mSpace, mRadius;

    private Bitmap mNormalBm, mSeletedBm;

    private int mStyle = 0;

    private int mSeleted = 0;

    private Paint mPaint;

    private int mSeletedColor;

    private int mNormalColor;

    public  void setDrawable(int normalId, int pressId) {
        DOT_NORMAL_ID = normalId;
        DOT_PRESS_ID = pressId;
    }



    public GalleryIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GalleryIndicator);
        mStyle = a.getInt(R.styleable.GalleryIndicator_point_style, 0);
        if (mStyle == 0) {
            mNormalBm = BitmapFactory.decodeResource(context.getResources(), DOT_NORMAL_ID);
            mSeletedBm = BitmapFactory.decodeResource(context.getResources(), DOT_PRESS_ID);
            mRadius = mNormalBm.getHeight() / 8;
        } else {
            mRadius = a.getDimension(R.styleable.GalleryIndicator_point_radius, 3);
            mSeletedColor = a.getColor(R.styleable.GalleryIndicator_point_seleted_color, Color.BLACK);
            mNormalColor = a.getColor(R.styleable.GalleryIndicator_point_normal_color, Color.WHITE);
        }
        mCount = a.getInteger(R.styleable.GalleryIndicator_count, 0);
        mSpace = a.getDimension(R.styleable.GalleryIndicator_space, 8);
        a.recycle();
    }

    public void setCount(int count) {
        this.mCount = count;
        mSeleted = 0;
        invalidate();
    }

    public int getCount() {
        return mCount;
    }

    public void next() {
        if (mSeleted < mCount - 1)
            mSeleted++;
        else
            mSeleted = 0;
        invalidate();
    }

    public void previous() {
        if (mSeleted > 0)
            mSeleted--;
        else
            mSeleted = mCount - 1;
        invalidate();
    }

    public int getSeletion() {
        return mSeleted;
    }

    public void setSeletion(int seleted) {
        seleted = Math.min(seleted, mCount - 1);
        seleted = Math.max(seleted, 0);
        this.mSeleted = seleted;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setAntiAlias(true);

        float width = (getWidth() - ((mRadius * 2 * mCount) + (mSpace * (mCount - 1)))) / 2.f;

        for (int i = 0; i < mCount; i++) {
            float left = width + getPaddingLeft() + i * (mSpace + mRadius + mRadius);
            float top = getPaddingTop();
            if (mStyle == 0) {
                if (i == mSeleted) {
                    canvas.drawBitmap(mSeletedBm, left, top, mPaint);
                } else {
                    canvas.drawBitmap(mNormalBm, left, top, mPaint);
                }
            } else {
                if (i == mSeleted) {
                    mPaint.setColor(mSeletedColor);
                    mPaint.setStyle(Style.FILL);
                } else {
                    mPaint.setColor(mNormalColor);
                    if (mStyle == 1) {
                        mPaint.setStyle(Style.STROKE);
                    } else {
                        mPaint.setStyle(Style.FILL);
                    }
                }
                canvas.drawCircle(width + getPaddingLeft() + mRadius + i * (mSpace + mRadius + mRadius), getHeight() / 2, mRadius, mPaint);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    /**
     * Determines the width of this view
     *
     * @param measureSpec A measureSpec packed into an int
     * @return The width of the view, honoring constraints from measureSpec
     */
    private int measureWidth(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        // We were told how big to be
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        }
        // Calculate the width according the com.honglu.weipan.com.honglu.weipan.views count
        else {
            result = (int) (getPaddingLeft() + getPaddingRight() + (mCount * 2 * mRadius) + (mCount - 1) * mRadius + 1);
            // Respect AT_MOST value if that was what is called for by
            // measureSpec
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    /**
     * Determines the height of this view
     *
     * @param measureSpec A measureSpec packed into an int
     * @return The height of the view, honoring constraints from measureSpec
     */
    private int measureHeight(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        // We were told how big to be
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        }
        // Measure the height
        else {
            result = (int) (8 * mRadius + getPaddingTop() + getPaddingBottom() + 10);
            // Respect AT_MOST value if that was what is called for by
            // measureSpec
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

}
