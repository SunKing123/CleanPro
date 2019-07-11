package com.xiaoniu.cleanking.ui.main.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.utils.DeviceUtils;
import com.xiaoniu.cleanking.utils.NumberUtils;

import java.util.logging.Handler;


/**
 * @author zhang
 */
public class AccessAnimView extends RelativeLayout {
    private Context mContext;
    RelativeLayout viewt;
    LinearLayout line_hj;
    TextView line_access;
    LinearLayout line_size;
    LinearLayout line_title;
    ImageView iv_bot;
    TextView tv_size;
    TextView tv_gb;
    LinearLayout line_allnum;
    int sizeMb;
    String strGb;
    onAnimEndListener listener;

    public void setListener(onAnimEndListener listener) {
        this.listener = listener;
    }

    //倒计时图片
    public AccessAnimView(Context context) {
        super(context);
        initView(context);
    }

    public AccessAnimView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public interface onAnimEndListener {
        public void onAnimEnd();
    }

    /**
     * 初始化布局
     *
     * @return
     */
    public void initView(Context context) {

        mContext = context;
        View v = LayoutInflater.from(mContext).inflate(R.layout.layout_access_anim, this, true);
        viewt = v.findViewById(R.id.viewt);
        line_hj = v.findViewById(R.id.line_hj);
        iv_bot = v.findViewById(R.id.iv_bot);
        tv_size = v.findViewById(R.id.tv_size);
        tv_gb = v.findViewById(R.id.tv_gb);
        line_access = v.findViewById(R.id.line_access);
        line_size = v.findViewById(R.id.line_size);
        line_allnum = v.findViewById(R.id.line_allnum);
        line_title = v.findViewById(R.id.line_title);

    }

    public void setData(int sizeMb, String strGb) {
        this.sizeMb = sizeMb;
        this.strGb = strGb;
        tv_size.setText(sizeMb + "");
        tv_gb.setText("MB");
    }

    //Step1:上面红色布局和中间1dp的布局动画开始
    public void startTopAnim() {
        int startHeight = DeviceUtils.dip2px(150);
        int endHeight = DeviceUtils.getScreenHeight() - DeviceUtils.dip2px(56);
        ValueAnimator anim = ValueAnimator.ofInt(startHeight, endHeight);
        anim.setDuration(300);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        RelativeLayout.LayoutParams rlp = (LayoutParams) viewt.getLayoutParams();
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int currentValue = (int) animation.getAnimatedValue();
                rlp.height = currentValue;
                viewt.setLayoutParams(rlp);
            }
        });
        anim.start();
        startMiddleAnim();
    }

    //中间的控件高度变化
    public void startMiddleAnim() {
        int startHeight = DeviceUtils.dip2px(30);
        int endHeight = DeviceUtils.dip2px(300);
        ValueAnimator anim = ValueAnimator.ofInt(startHeight, endHeight);
        anim.setDuration(300);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) line_allnum.getLayoutParams();
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int currentValue = (int) animation.getAnimatedValue();
                rlp.topMargin = currentValue;
                line_allnum.setLayoutParams(rlp);
            }
        });
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                setHjAnim();
            }
        });
        anim.start();
    }

    //Step2:高度下降完毕，中间的火箭缩放动画开始，动画播放完毕后火箭底部的帧动画开始播放
    public void setHjAnim() {
        line_hj.setVisibility(VISIBLE);
        PropertyValuesHolder anim4 = PropertyValuesHolder.ofFloat("scaleX",
                0f, 1.0f);
        PropertyValuesHolder anim5 = PropertyValuesHolder.ofFloat("scaleY",
                0f, 1.0f);
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(line_hj, anim4, anim5).setDuration(400);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                iv_bot.setImageResource(R.drawable.anim_hj);
                AnimationDrawable animationDrawable = (AnimationDrawable) iv_bot.getDrawable();
                if (!animationDrawable.isRunning()) {
                    animationDrawable.start();
                }
                setNumAnim(tv_size, tv_gb, viewt, sizeMb, 0, TextUtils.equals(strGb, "GB") ? 2 : 1);
            }
        });
        animator.start();
    }

    /**
     * 数字动画 清理开始
     *
     * @param
     * @param startNum
     * @param endNum
     * @param type     1是MB，2是GB
     */
    boolean canPlaying = true;

    public void setNumAnim(TextView tv_size, TextView tv_gb, View viewt, int startNum, int endNum, int type) {
        ValueAnimator anim = ValueAnimator.ofInt(startNum, endNum);
        anim.setDuration(3000);
        anim.setInterpolator(new DecelerateInterpolator());
        canPlaying = true;
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int currentValue = (int) animation.getAnimatedValue();
                tv_size.setText(currentValue + "");
                Log.d("asdf", "cuurent time " + animation.getCurrentPlayTime());
                if (canPlaying && animation.getCurrentPlayTime() > 2800) {
                    canPlaying = false;
                    //播放的后500ms，背景色改变
                    setBgChanged(viewt, 200);
                }
                if (currentValue == endNum) {
                    tv_size.setText(type == 1 ? String.valueOf(currentValue) : String.valueOf(NumberUtils.getFloatStr2(currentValue / 1024)));
                    tv_gb.setText(type == 1 ? "MB" : "GB");
                }
            }
        });
        anim.start();
    }

    boolean isFirstChangeColor = true;

    //数字动画播放的最后200ms，背景颜色变化
    public void setBgChanged(View viewt, long time) {
        isFirstChangeColor = true;
        ValueAnimator anim = ValueAnimator.ofInt(0, 100);
        anim.setDuration(time);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int currentValue = (int) animation.getAnimatedValue();
                if (currentValue < 66) {
                    viewt.setBackgroundColor(getResources().getColor(R.color.color_FD6F46));
                    line_title.setBackgroundColor(getResources().getColor(R.color.color_FD6F46));
                } else if (currentValue < 77) {
                    viewt.setBackgroundColor(getResources().getColor(R.color.color_06C581));
                    line_title.setBackgroundColor(getResources().getColor(R.color.color_06C581));
                } else {
                    viewt.setBackgroundColor(getResources().getColor(R.color.color_06C581));
                    line_title.setBackgroundColor(getResources().getColor(R.color.color_06C581));
                    if (isFirstChangeColor) {
                        isFirstChangeColor = false;
                        if (listener != null)
                            listener.onAnimEnd();
                    }
                }

            }
        });
        anim.start();
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setViewTrans();
                    }
                }, 200);
            }
        });
    }

    //数字动画播放完后火箭上移，布局高度缩小
    public void setViewTrans() {
        line_size.setVisibility(GONE);
        line_access.setVisibility(GONE);

        int endHeight = DeviceUtils.dip2px(150);
        int startHeight = DeviceUtils.getScreenHeight() - DeviceUtils.dip2px(56);
        ValueAnimator anim = ValueAnimator.ofInt(startHeight, endHeight);
        anim.setDuration(500);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        RelativeLayout.LayoutParams rlp = (LayoutParams) viewt.getLayoutParams();
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int currentValue = (int) animation.getAnimatedValue();
                rlp.height = currentValue;
                viewt.setLayoutParams(rlp);
            }
        });
        anim.start();
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                setVisibility(GONE);
            }
        });
        createFadeAnimator();
    }

    /**
     * @return 火箭向上飞
     */
    public ObjectAnimator createFadeAnimator() {

        PropertyValuesHolder translationY = PropertyValuesHolder.ofFloat("translationY", line_hj.getTranslationY(), (-1) * DeviceUtils.dip2px(306));
//        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat("alpha", 1.0f, 0f);
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(line_hj, translationY);
        animator.setDuration(300);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.start();
        return animator;
    }

}

