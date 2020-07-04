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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.comm.jksdk.utils.DisplayUtil;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.newclean.bean.BallRewardBean;
import com.xiaoniu.cleanking.ui.newclean.listener.IBullClickListener;
import com.xiaoniu.cleanking.utils.anim.AnimationRotateUtils;
import com.xiaoniu.cleanking.utils.anim.AnimationsContainer;
import java.util.Random;

public class LuckBubbleView extends LinearLayout {
    private Activity activity;
    private TextView content;
    private ObjectAnimator objar;
    private AnimationsContainer.FrameseAnim ballAnim;
    private int loact;
    private IBullClickListener iBullClickListener;
    private BallRewardBean.DataBean.BallBean listBean;
    private Typeface typ_RE;

    public LuckBubbleView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initView(context, attributeSet);
    }

    private void initView(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.rubble);
        loact = typedArray.getInt(R.styleable.rubble_location, 1);
        typ_RE = Typeface.createFromAsset(context.getAssets(), "DIN-Medium.otf");

        activity = (Activity) context;
        inflate(activity, R.layout.luck_bubble, this);
        content = (TextView) findViewById(R.id.tvcon);
        content.setTypeface(typ_RE);
        ImageView imgbg = (ImageView) findViewById(R.id.imgbg);
        setVisibility(VISIBLE);

        ImageView ivBallAnim = (ImageView) findViewById(R.id.iv_ball_anim);
        setAnim(ivBallAnim);

        LayoutParams lp = new LayoutParams(ivBallAnim.getLayoutParams());
        lp.setMargins(50, 100, 0, 0);
        if (loact == 1) {//left-top
            content.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
            lp.setMargins(DisplayUtil.dip2px(context,-15), 0, 0, DisplayUtil.dip2px(context,-12));
            imgbg.setImageResource(R.drawable.kw1);
        } else if (loact == 2) {//left-bom
            content.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
            AnimationRotateUtils.setRotationAnimator(ivBallAnim, 30f);
            lp.setMargins(DisplayUtil.dip2px(context,30), 0, 0,DisplayUtil.dip2px(context,-15));
            imgbg.setImageResource(R.drawable.kw2);
        } else if (loact == 3) {//right-top
            content.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            lp.setMargins(DisplayUtil.dip2px(context,-10), 0, 0, DisplayUtil.dip2px(context,-10));
            imgbg.setImageResource(R.drawable.kw3);
        } else if (loact == 4) {//right-bom
            content.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            AnimationRotateUtils.setRotationAnimator(ivBallAnim, 60f);
            lp.setMargins(DisplayUtil.dip2px(context,40), 0, 0, DisplayUtil.dip2px(context,-15));
            imgbg.setImageResource(R.drawable.kw4);
        }
        ivBallAnim.setLayoutParams(lp);
//        setVisibility(GONE);
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

    public BallRewardBean.DataBean.BallBean getBullBean() {
        return listBean;
    }

    public void setDataCheckToShow(final BallRewardBean.DataBean.BallBean listBean) {
        this.listBean = listBean;
        if (listBean == null) {
            setVisibility(GONE);
            return;
        }
        setVisibility(VISIBLE);
        if (listBean.getCode().contains("randomShowNum")) {//随机可见金币 randomShowNum1 randomShowNum2 randomShowNum3
            content.setText(String.valueOf(listBean.getGoldAmount()));
        } else if (TextUtils.equals("randomHideNum", listBean.getCode())) {
            content.setText(String.valueOf(listBean.getGoldAmount()));
        } else if (TextUtils.equals("exchangeStep", listBean.getCode())) {
            content.setText(String.valueOf(listBean.getGoldAmount()));

        }
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                iBullClickListener.clickBull(listBean, loact);
                if (loact == 1) {
//                    NiuDataUtils.trickCoinBull(listBean.getGoldAmount(), "1");
                    listBean.setPosition("1");
                } else if (loact == 2) {
//                    NiuDataUtils.trickCoinBull(listBean.getGoldAmount(), "3");
                    listBean.setPosition("3");
                } else if (loact == 3) {
//                    NiuDataUtils.trickCoinBull(listBean.getGoldAmount(), "2");
                    listBean.setPosition("2");
                } else if (loact == 4) {
//                    NiuDataUtils.trickCoinBull(listBean.getGoldAmount(), "4");
                    listBean.setPosition("4");
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