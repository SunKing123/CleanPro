package com.installment.mall.ui.main.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.installment.mall.R;
import com.installment.mall.utils.DeviceUtils;
import com.installment.mall.utils.NumberUtils;

/**
 * 提额额度view
 * @author fengpeihao
 */
public class TextUpQuotaView extends View {

    private String textUpQuota = getResources().getString(R.string.text_up_quota);
    private Paint mTextPaint;
    private Paint mMoneyPaint;
    private Paint mLinePaint;
    private String mMoney = "9999";

    private int mOffset;

    public TextUpQuotaView(Context context) {
        super(context);
        init();
    }

    public TextUpQuotaView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextUpQuotaView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(ContextCompat.getColor(getContext(), R.color.color_CC8C1D));
        mTextPaint.setTextSize(DeviceUtils.sp2px(14));
        mMoneyPaint = new Paint();
        mMoneyPaint.setAntiAlias(true);
        mMoneyPaint.setColor(ContextCompat.getColor(getContext(), R.color.color_CC8C1D));
        mMoneyPaint.setTextSize(DeviceUtils.sp2px(60));
        mMoneyPaint.setFakeBoldText(true);
        mMoneyPaint.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/DINPro-Regular.ttf"));
        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setColor(ContextCompat.getColor(getContext(), R.color.color_FFE495));
        mLinePaint.setStrokeWidth(DeviceUtils.dip2px(0.5f));
        mLinePaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension((int) (getTextWidth(mMoneyPaint, mMoney) + DeviceUtils.dip2px(10)), DeviceUtils.dip2px(80));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawText(textUpQuota, DeviceUtils.dip2px(5), getFontHeight(DeviceUtils.sp2px(14)), mTextPaint);
        canvas.drawText(String.valueOf(mOffset), DeviceUtils.dip2px(3), getFontHeight(DeviceUtils.sp2px(60)), mMoneyPaint);
        canvas.drawLine(0, DeviceUtils.dip2px(79), getTextWidth(mMoneyPaint, String.valueOf(mOffset)) + DeviceUtils.dip2px(10), DeviceUtils.dip2px(79), mLinePaint);
    }

    private void startAnim() {
        ValueAnimator animator = ValueAnimator.ofInt(0, NumberUtils.getInteger(mMoney));
        animator.setDuration(1000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mOffset = (int) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        animator.start();
    }

    public void setMoney(String money) {
        mMoney = money;
        startAnim();
    }

    /**
     * 获取该画笔写出文字的宽度
     *
     * @param paint
     * @return
     */
    public float getTextWidth(Paint paint, String str) {
        return paint.measureText(str);
    }

    /**
     * 获取字体高度
     *
     * @param fontSize
     * @return
     */
    public float getFontHeight(float fontSize) {
        Paint paint = new Paint();
        paint.setTextSize(fontSize);
        Paint.FontMetrics fm = paint.getFontMetrics();
        return (float) Math.ceil(fm.descent - fm.ascent);
    }
}
