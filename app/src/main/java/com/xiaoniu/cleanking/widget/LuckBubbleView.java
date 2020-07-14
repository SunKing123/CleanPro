package com.xiaoniu.cleanking.widget;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.TextUtils;
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
import com.xiaoniu.cleanking.ui.newclean.bean.BallRewardBean;
import com.xiaoniu.cleanking.ui.newclean.listener.IBullClickListener;
import com.xiaoniu.cleanking.utils.anim.AnimationRotateUtils;
import com.xiaoniu.cleanking.utils.anim.AnimationsContainer;
import com.xiaoniu.common.utils.StatisticsUtils;

import java.util.Random;

public class LuckBubbleView extends LinearLayout {
    private Activity activity;
    private TextView content;
    private LottieAnimationView lottie_gold;
    private ObjectAnimator objar;
    private AnimationsContainer.FrameseAnim ballAnim;
    private int loact;
    private IBullClickListener iBullClickListener;
    private BubbleConfig.DataBean listBean;
    private Typeface typ_RE;

    public LuckBubbleView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initView(context, attributeSet);
    }

    private void initView(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.rubble);
        loact = typedArray.getInt(R.styleable.rubble_location, 1);
        typ_RE = Typeface.createFromAsset(context.getAssets(), "fonts/DIN-Bold.otf");


        activity = (Activity) context;
        inflate(activity, R.layout.luck_bubble, this);
        lottie_gold = (LottieAnimationView)findViewById(R.id.lottie_gold);
        content = (TextView) findViewById(R.id.tvcon);
        content.setTypeface(typ_RE);
        ImageView imgbg = (ImageView) findViewById(R.id.imgbg);
        setVisibility(VISIBLE);

        if (!lottie_gold.isAnimating()) {
            lottie_gold.playAnimation();
        }

//        ImageView ivBallAnim = (ImageView) findViewById(R.id.iv_ball_anim);
//        setAnim(ivBallAnim);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(imgbg.getLayoutParams());
        imgbg.setImageResource(R.drawable.icon_kw00);
//        lp.setMargins(50, 100, 0, 0);
        if (loact == 1) {//left-top
            content.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            lp.width = DisplayUtil.dip2px(context, 31.5f);
            lp.height = DisplayUtil.dip2px(context, 33f);
            imgbg.setLayoutParams(lp);
            lottie_gold.setLayoutParams(lp);
        } else if (loact == 2) {//left-bom
            content.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
//            AnimationRotateUtils.setRotationAnimator(ivBallAnim, 30f);
//            lp.setMargins(DisplayUtil.dip2px(context,30), 0, 0,DisplayUtil.dip2px(context,-15));
            lp.width = DisplayUtil.dip2px(context, 38.5f);
            lp.height = DisplayUtil.dip2px(context, 40.5f);
            imgbg.setLayoutParams(lp);
            lottie_gold.setLayoutParams(lp);

        } else if (loact == 3) {//right-top
            content.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
//            lp.setMargins(DisplayUtil.dip2px(context,-10), 0, 0, DisplayUtil.dip2px(context,-10));
            lp.width = DisplayUtil.dip2px(context, 38.5f);
            lp.height = DisplayUtil.dip2px(context, 40.5f);
            imgbg.setLayoutParams(lp);
            lottie_gold.setLayoutParams(lp);
        } else if (loact == 4) {//right-bom
            content.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
//            AnimationRotateUtils.setRotationAnimator(ivBallAnim, 60f);
//            lp.setMargins(DisplayUtil.dip2px(context,40), 0, 0, DisplayUtil.dip2px(context,-15));
            lp.width = DisplayUtil.dip2px(context, 45.5f);
            lp.height = DisplayUtil.dip2px(context, 48.5f);
            imgbg.setLayoutParams(lp);
            lottie_gold.setLayoutParams(lp);
        }
//        ivBallAnim.setLayoutParams(lp);
        setVisibility(GONE);
    }

    /**
     * 星星动画
     *
     * @param ivBallAnim
     */
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
    }

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
        content.setText(String.valueOf(listBean.getGoldCount()));
        /*if (listBean.getLocationNum() == 1.contains("randomShowNum")) {//随机可见金币 randomShowNum1 randomShowNum2 randomShowNum3
            content.setText(String.valueOf(listBean.getGoldAmount()));
        } else if (TextUtils.equals("randomHideNum", listBean.getCode())) {
            content.setText(String.valueOf(listBean.getGoldAmount()));
        } else if (TextUtils.equals("exchangeStep", listBean.getCode())) {
            content.setText(String.valueOf(listBean.getGoldAmount()));

        }*/
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                iBullClickListener.clickBull(listBean, loact);
                if (loact == 1) {
//                    listBean.setPosition("1");
                    StatisticsUtils.trackClick("upper_left_gold_coin_click", "左上金币点击", "home_page", "home_page");
                } else if (loact == 2) {
//                    listBean.setPosition("3");
                    StatisticsUtils.trackClick("lower_left_gold_coin_click", "左下金币点击", "home_page", "home_page");
                } else if (loact == 3) {
//                    listBean.setPosition("2");
                    StatisticsUtils.trackClick("upper_right_gold_coin_click", "右上金币点击", "home_page", "home_page");
                } else if (loact == 4) {
                    StatisticsUtils.trackClick("lower_right_gold_coin_click", "右下金币点击", "home_page", "home_page");
//                    listBean.setPosition("4");
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
        if (i == VISIBLE) {
            showAnim();
        } else {
            hideAnim();
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