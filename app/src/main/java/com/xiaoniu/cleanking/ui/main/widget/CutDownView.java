package com.xiaoniu.cleanking.ui.main.widget;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.common.utils.DisplayUtils;


public class CutDownView extends RelativeLayout {

    private ImageView backgroundIv;
    private TextView dayTv;
    private TextView hourNumOneTv;
    private TextView hourNumTwoTv;
    private TextView minuteNumOneTv;
    private TextView minuteNumTwoTv;
    private TextView secondNumOneTv;
    private TextView secondNumTwoTv;

    private long endTime;

    public void setEndTime(long endTime) {
        if (mHandler != null && mRunnable != null) {
            mHandler.removeCallbacks(mRunnable);
        }
        this.endTime = endTime;
    }

    public CutDownView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    private void initViews() {
        Context context = getContext();
        int width = getScreenWidth() - DisplayUtils.dip2px(50);
        //添加背景图
        backgroundIv = new ImageView(context);
        LayoutParams bgIvParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        bgIvParams.addRule(CENTER_VERTICAL);
        backgroundIv.setAdjustViewBounds(true);
        backgroundIv.setImageResource(R.mipmap.other_empty);
        addView(backgroundIv, bgIvParams);
        //天
        dayTv = new TextView(context);
        configTextView(dayTv);
        LayoutParams dayParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dayParams.addRule(CENTER_VERTICAL);
        dayParams.addRule(ALIGN_PARENT_LEFT);


        float tvWidth = getTextWidth(context, "9", 10) / 2;
        dayParams.setMargins((int) (((float) 290 * (float) width / (float) 688) - tvWidth), 0, 0, 0);
        dayTv.setText("0");
        addView(dayTv, dayParams);
        //时
        hourNumOneTv = new TextView(context);
        configTextView(hourNumOneTv);
        LayoutParams hourNumOneParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        hourNumOneParams.addRule(CENTER_VERTICAL);
        hourNumOneParams.addRule(ALIGN_PARENT_LEFT);
        hourNumOneParams.setMargins((int) (((float) 365 * (float) width / (float) 688) - tvWidth), 0, 0, 0);
        hourNumOneTv.setText("0");
        addView(hourNumOneTv, hourNumOneParams);

        hourNumTwoTv = new TextView(context);
        configTextView(hourNumTwoTv);
        LayoutParams hourNumTwoParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        hourNumTwoParams.addRule(CENTER_VERTICAL);
        hourNumTwoParams.addRule(ALIGN_PARENT_LEFT);
        hourNumTwoParams.setMargins((int) (((float) (392.5) * (float) width / (float) 688) - tvWidth), 0, 0, 0);
        hourNumTwoTv.setText("0");
        addView(hourNumTwoTv, hourNumTwoParams);

        //分
        minuteNumOneTv = new TextView(context);
        configTextView(minuteNumOneTv);
        LayoutParams minuteNumOneParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        minuteNumOneParams.addRule(CENTER_VERTICAL);
        minuteNumOneParams.addRule(ALIGN_PARENT_LEFT);
        minuteNumOneParams.setMargins((int) (((float) 467 * (float) width / (float) 688) - tvWidth), 0, 0, 0);
        minuteNumOneTv.setText("0");
        addView(minuteNumOneTv, minuteNumOneParams);

        minuteNumTwoTv = new TextView(context);
        configTextView(minuteNumTwoTv);
        LayoutParams minuteNumTwoParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        minuteNumTwoParams.addRule(CENTER_VERTICAL);
        minuteNumTwoParams.addRule(ALIGN_PARENT_LEFT);
        minuteNumTwoParams.setMargins((int) (((float) 494 * (float) width / (float) 688) - tvWidth), 0, 0, 0);
        minuteNumTwoTv.setText("0");
        addView(minuteNumTwoTv, minuteNumTwoParams);

        //秒
        secondNumOneTv = new TextView(context);
        configTextView(secondNumOneTv);
        LayoutParams secondNumOneParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        secondNumOneParams.addRule(CENTER_VERTICAL);
        secondNumOneParams.addRule(ALIGN_PARENT_LEFT);
        secondNumOneParams.setMargins((int) (((float) 564 * (float) width / (float) 688) - tvWidth), 0, 0, 0);
        secondNumOneTv.setText("0");
        addView(secondNumOneTv, secondNumOneParams);

        secondNumTwoTv = new TextView(context);
        configTextView(secondNumTwoTv);
        LayoutParams secondNumTwoParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        secondNumTwoParams.addRule(CENTER_VERTICAL);
        secondNumTwoParams.addRule(ALIGN_PARENT_LEFT);
        secondNumTwoParams.setMargins((int) (((float) 592 * (float) width / (float) 688) - tvWidth), 0, 0, 0);
        secondNumTwoTv.setText("0");
        addView(secondNumTwoTv, secondNumTwoParams);

    }

    private void configTextView(TextView textView) {
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(10);
    }

    private static final int DELAY = 1000;
    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            endTime = endTime - 1;
            if (endTime > 0) {
                updateViews();
            }
        }
    };

    public void updateViews() {
        try {
            //天
            long totalSecond = endTime;
            long totalMinute = totalSecond / 60;
            long totalHour = totalMinute / 60;
            long totalDay = totalHour / 24;
            dayTv.setText("" + totalDay);
            //时
            long surplusHour = totalHour - totalDay * 24;
            if (surplusHour > 0) {
                String surplusH = String.valueOf(surplusHour);
                if (surplusH.length() > 1) {
                    hourNumOneTv.setText(surplusH.substring(0, 1));
                    hourNumTwoTv.setText(surplusH.substring(1, 2));
                } else {
                    hourNumOneTv.setText("0");
                    hourNumTwoTv.setText(surplusH);
                }
            } else {
                hourNumOneTv.setText("0");
                hourNumTwoTv.setText("0");
            }
            //分
            long surplusMinute = totalMinute - totalDay * 24 * 60 - surplusHour * 60;
            if (surplusMinute > 0) {
                String surplusM = String.valueOf(surplusMinute);
                if (surplusM.length() > 1) {
                    minuteNumOneTv.setText(surplusM.substring(0, 1));
                    minuteNumTwoTv.setText(surplusM.substring(1, 2));
                } else {
                    minuteNumOneTv.setText("0");
                    minuteNumTwoTv.setText(surplusM);
                }
            } else {
                minuteNumOneTv.setText("0");
                minuteNumTwoTv.setText("0");
            }
            //秒
            long surplusSecond = totalSecond - totalDay * 24 * 60 * 60 - surplusHour * 60 * 60 - surplusMinute * 60;
            if (surplusSecond > 0) {
                String surplusS = String.valueOf(surplusSecond);
                if (surplusS.length() > 1) {
                    secondNumOneTv.setText(surplusS.substring(0, 1));
                    secondNumTwoTv.setText(surplusS.substring(1, 2));
                } else {
                    secondNumOneTv.setText("0");
                    secondNumTwoTv.setText(surplusS);
                }
            } else {
                secondNumOneTv.setText("0");
                secondNumTwoTv.setText("0");
            }
            mHandler.postDelayed(mRunnable, DELAY);
        } catch (Exception e) {
            Log.e("ayb", "" + e.getMessage());
        }
    }

    private float getTextWidth(Context context, String text, int textSize) {
        TextPaint paint = new TextPaint();
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        paint.setTextSize(scaledDensity * textSize);
        return paint.measureText(text);
    }

    private int getScreenWidth() {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }
}
