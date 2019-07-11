package com.xiaoniu.cleanking.ui.main.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.utils.DeviceUtils;


/**
 * @author zhang
 */
public class AccessAnimView extends RelativeLayout {
    private Context mContext;
    RelativeLayout viewt;
    View viewa2;
    LinearLayout line_hj;

    //倒计时图片
    public AccessAnimView(Context context) {
        super(context);
        initView(context);
    }

    public AccessAnimView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
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
        viewa2 = v.findViewById(R.id.viewa2);
        line_hj = v.findViewById(R.id.line_hj);

    }

    public void startTopAnim() {
        int startHeight = DeviceUtils.dip2px(150);
        int endHeight = DeviceUtils.getScreenHeight() - DeviceUtils.dip2px(56);
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
        startMiddleAnim();
    }

    //中间的控件高度变化
    public void startMiddleAnim() {
        int startHeight = DeviceUtils.dip2px(1);
        int endHeight = DeviceUtils.dip2px(50);
        ValueAnimator anim = ValueAnimator.ofInt(startHeight, endHeight);
        anim.setDuration(500);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        LinearLayout.LayoutParams rlp = (LinearLayout.LayoutParams) viewa2.getLayoutParams();
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int currentValue = (int) animation.getAnimatedValue();
                rlp.height = currentValue;
                viewa2.setLayoutParams(rlp);
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

    public void setHjAnim() {
        line_hj.setVisibility(VISIBLE);
        PropertyValuesHolder anim4 = PropertyValuesHolder.ofFloat("scaleX",
                0f, 1.0f);
        PropertyValuesHolder anim5 = PropertyValuesHolder.ofFloat("scaleY",
                0f, 1.0f);
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(line_hj, anim4, anim5).setDuration(320);
        animator.start();
    }
}

