package com.xiaoniu.cleanking.widget;

import android.content.Context;
import android.text.TextPaint;

import com.xiaoniu.cleanking.widget.magicIndicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;


/**
 * @Description:
 * @Author: Xiaodong.He
 * @Date: 2020/5/12
 */
public class ScaleTransitionPagerTitleView  extends ColorTransitionPagerTitleView {
    private float mMinScale = 0.8f;
    private boolean isMax = true;

    public ScaleTransitionPagerTitleView(Context context) {
        super(context);
    }

    @Override
    public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
        super.onEnter(index, totalCount, enterPercent, leftToRight);    // 实现颜色渐变
        setScaleX(mMinScale + (1.0f - mMinScale) * enterPercent);
        setScaleY(mMinScale + (1.0f - mMinScale) * enterPercent);
    }

    @Override
    public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
        super.onLeave(index, totalCount, leavePercent, leftToRight);    // 实现颜色渐变
        setScaleX(1.0f + (mMinScale - 1.0f) * leavePercent);
        setScaleY(1.0f + (mMinScale - 1.0f) * leavePercent);
    }

    private boolean isFirst;
    private float textSize;

    @Override
    public void onSelected(int index, int totalCount) {
        super.onSelected(index, totalCount);
        TextPaint paint = getPaint();
//        if (!isMax) {
//            if (!isFirst) {
//                textSize = getPaint().getTextSize() - 3;
//            }
//            isFirst = true;
//            paint.setTextSize(textSize);
//        }
        paint.setFakeBoldText(true);
    }

    @Override
    public void onDeselected(int index, int totalCount) {
        super.onDeselected(index, totalCount);
        TextPaint paint = getPaint();
        paint.setFakeBoldText(false);
    }


    public float getMinScale() {
        return mMinScale;
    }

    public void setMinScale(float minScale) {
        mMinScale = minScale;
    }

    public void setIsMax(boolean isMax) {
        this.isMax = isMax;
    }
}