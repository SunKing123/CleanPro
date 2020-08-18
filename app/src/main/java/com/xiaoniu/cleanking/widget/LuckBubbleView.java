package com.xiaoniu.cleanking.widget;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.comm.jksdk.utils.DisplayUtil;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.main.bean.BubbleConfig;
import com.xiaoniu.cleanking.ui.newclean.listener.IBullClickListener;
import com.xiaoniu.cleanking.utils.GlideUtils;
import com.xiaoniu.cleanking.utils.anim.AnimationsContainer;
import com.xiaoniu.common.utils.StatisticsUtils;

import java.util.Random;

/**
 * 金币运营位动画View
 */
public class LuckBubbleView extends LinearLayout {
    private Activity activity;
    private TextView content;
    private ObjectAnimator objar;
    private AnimationsContainer.FrameseAnim ballAnim;
    private int loact;
    private boolean isAniming = false;
    private IBullClickListener iBullClickListener;
    private BubbleConfig.DataBean listBean;
    private Typeface typ_RE;
    private ImageView imgbg;

    public LuckBubbleView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initView(context, attributeSet);
    }

    private void initView(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.rubble);
        loact = typedArray.getInt(R.styleable.rubble_location, 1);
        isAniming = typedArray.getBoolean(R.styleable.rubble_isAniming, false);
        typ_RE = Typeface.createFromAsset(context.getAssets(), "fonts/DIN-Bold.otf");

        activity = (Activity) context;
        inflate(activity, R.layout.luck_bubble, this);
        content = (TextView) findViewById(R.id.tvcon);
        content.setTypeface(typ_RE);
        imgbg = (ImageView) findViewById(R.id.imgbg);
        setVisibility(VISIBLE);

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(imgbg.getLayoutParams());
        if (loact == 1) {//left-top
            content.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            lp.width = DisplayUtil.dip2px(context, 31.5f);
            lp.height = DisplayUtil.dip2px(context, 33f);
            imgbg.setLayoutParams(lp);

        } else if (loact == 2) {//left-bom
            content.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            lp.width = DisplayUtil.dip2px(context, 38.5f);
            lp.height = DisplayUtil.dip2px(context, 40.5f);
            imgbg.setLayoutParams(lp);

        } else if (loact == 3) {//right-top
            content.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            lp.width = DisplayUtil.dip2px(context, 38.5f);
            lp.height = DisplayUtil.dip2px(context, 40.5f);
            imgbg.setLayoutParams(lp);

        } else if (loact == 4) {//right-bom
            content.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            lp.width = DisplayUtil.dip2px(context, 45.5f);
            lp.height = DisplayUtil.dip2px(context, 48.5f);
            imgbg.setLayoutParams(lp);

        } else {
            content.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            lp.width = DisplayUtil.dip2px(context, 50f);
            lp.height = DisplayUtil.dip2px(context, 50f);
            imgbg.setLayoutParams(lp);
        }
        setVisibility(GONE);
    }

    /* */

    /**
     * 星星动画
     *//*
    private void setAnim(ImageView ivBallAnim) {
        if (loact == 1) {//left-top
            ballAnim = AnimationsContainer.getInstance(R.array.ball_star_anim, 150).createAnim(ivBallAnim);
        } else if (loact == 2) {//left-bom
            ballAnim = AnimationsContainer.getInstance(R.array.ball_star_anim, 120).createAnim(ivBallAnim);
        } else if (loact == 3) {//right-top
            ballAnim = AnimationsContainer.getInstance(R.array.ball_star_anim, 110).createAnim(ivBallAnim);
        } else if (loact == 4) {//right-bom
            ballAnim = AnimationsContainer.getInstance(R.array.ball_star_anim, 100).createAnim(ivBallAnim);
        }
        ballAnim.start();
    }*/
    public void setIBullListener(IBullClickListener clickListener) {
        this.iBullClickListener = clickListener;
    }

    public BubbleConfig.DataBean getBullBean() {
        return listBean;
    }

    public void setDataCheckToShow(final BubbleConfig.DataBean listBean) {
        this.listBean = listBean;
        if (listBean == null) {
            setVisibility(GONE);
            return;
        }
        setVisibility(VISIBLE);
        if (listBean.getIsShowNum() == 0) {
            content.setVisibility(GONE);
        } else if (listBean.getIsShowNum() == 1) {
            content.setVisibility(VISIBLE);
            content.setText(String.valueOf(listBean.getGoldCount()));
            imgbg.setImageResource(R.drawable.icon_kw00);
        }
        try {
            GlideUtils.loadImage(activity, listBean.getIconUrl(), imgbg);
        } catch (Exception e) {
            e.printStackTrace();
        }

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != iBullClickListener) {
                    iBullClickListener.clickBull(listBean, loact);
                }
                if (loact == 1) {
                    StatisticsUtils.trackClick("upper_left_gold_coin_click", "左上金币点击", "home_page", "home_page");
                } else if (loact == 2) {
                    StatisticsUtils.trackClick("lower_left_gold_coin_click", "左下金币点击", "home_page", "home_page");
                } else if (loact == 3) {
                    StatisticsUtils.trackClick("upper_right_gold_coin_click", "右上金币点击", "home_page", "home_page");
                } else if (loact == 4) {
                    StatisticsUtils.trackClick("lower_right_gold_coin_click", "右下金币点击", "home_page", "home_page");
                }
            }
        });
        setVisibility(VISIBLE);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (ballAnim != null) {
            ballAnim.stop();
            ballAnim = null;
        }
    }

    /* Access modifiers changed, original: protected */
    @Override
    public void onVisibilityChanged(View view, int i) {
        super.onVisibilityChanged(view, i);
        if (isAniming) {
            if (i == VISIBLE) {
                showAnim();
            } else {
                hideAnim();
            }
        }

    }

    @SuppressLint("WrongConstant")
    private void showAnim() {
        ObjectAnimator objectAnimator = objar;
        if (objectAnimator == null) {
            float a = (float) radom(activity, 5);
            float[] r2 = new float[3];
            float f = -a;
            r2[0] = f;
            r2[1] = a;
            r2[2] = f;
            objar = ObjectAnimator.ofFloat(this, "translationY", r2);
        } else {
            objectAnimator.cancel();
        }
        objar.setDuration((long) (new Random().nextInt(500) + 2000));
        objar.setRepeatCount(-1);
        objar.setRepeatMode(Animation.REVERSE);
        objar.start();
    }

    private void hideAnim() {
        ObjectAnimator objectAnimator = objar;
        if (objectAnimator != null) {
            objectAnimator.cancel();
        }
    }

    public static int radom(Context context, int i) {
        double d = (double) (((float) i) * context.getResources().getDisplayMetrics().density);
        Double.isNaN(d);
        return (int) (d + 0.5d);
    }

}