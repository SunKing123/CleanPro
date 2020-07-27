package com.xiaoniu.cleanking.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.xiaoniu.cleanking.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

/**
 * @author zzh
 * @date 2020/7/25 10
 * @mail：zhengzhihao@xiaoniuhy.com
 */
public class CircleRoundingAnim extends FrameLayout {
    private Context mContext;
    private LinearLayout rootLayout;
    private FrameLayout flayout_circle_bg;
    private ImageView iconCircle;
    private TextView tvDesContent;

    public static final int CHARGE_STATE_YELLOW = 1;
    public static final int CHARGE_STATE_GREEN = 2;
    public static final int CHARGE_STATE_BLUE = 3;
    int itemIndex = 1;
    public CircleRoundingAnim(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.circleRound);
        itemIndex = array.getInt(R.styleable.circleRound_index, 1);
        initView(itemIndex);
    }


    public void initView(int index) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_circle_round_anim, this, true);
        rootLayout = (LinearLayout) view.findViewById(R.id.root_layout);
        iconCircle = (ImageView) view.findViewById(R.id.icon_circle);
        tvDesContent = (TextView) view.findViewById(R.id.tv_des_content);
        flayout_circle_bg = (FrameLayout) view.findViewById(R.id.flayout_circle_bg);
        initState(index);
    }

    //设置充电状态
    public void initState(int chargeState) {
        switch (chargeState) {
            case CHARGE_STATE_YELLOW:
                tvDesContent.setText(mContext.getString(R.string.fast_charging));
                flayout_circle_bg.setBackgroundResource(R.drawable.icon_circle_state01_normal_bg);
                break;
            case CHARGE_STATE_GREEN:
                tvDesContent.setText(mContext.getString(R.string.cycle_charging));
                flayout_circle_bg.setBackgroundResource(R.drawable.icon_circle_state02_normal_bg);
                break;
            case CHARGE_STATE_BLUE:
                tvDesContent.setText(mContext.getString(R.string.vortex_charging));
                flayout_circle_bg.setBackgroundResource(R.drawable.icon_circle_state03_normal_bg);
                break;
        }

    }

    public void setSelected() {
//      view_power_lottie = (LottieAnimationView) view.findViewById(R.id.view_power_lottie);
        switch (itemIndex) {
            case CHARGE_STATE_YELLOW:
                tvDesContent.setTextColor(mContext.getResources().getColor(R.color.color_ffad00));
                flayout_circle_bg.setBackgroundResource(R.drawable.icon_circle_state01_selected_bg);
                iconCircle.setImageResource(R.drawable.icon_circle_state01_yellow);

                break;
            case CHARGE_STATE_GREEN:
                tvDesContent.setTextColor(mContext.getResources().getColor(R.color.green_02D086));
                flayout_circle_bg.setBackgroundResource(R.drawable.icon_circle_state02_selected_bg);
                iconCircle.setImageResource(R.drawable.icon_circle_state02_green);

                break;
            case CHARGE_STATE_BLUE:
                tvDesContent.setTextColor(mContext.getResources().getColor(R.color.color_00cfec));
                flayout_circle_bg.setBackgroundResource(R.drawable.icon_circle_state03_selected_bg);
                iconCircle.setImageResource(R.drawable.icon_circle_state03_blue);

                break;
        }
        Animation anim = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(1000); // 设置动画时间
        anim.setRepeatMode(Animation.RESTART);
        anim.setRepeatCount(Animation.INFINITE);
        anim.setInterpolator(new LinearInterpolator()); // 设置插入器
        if (null != iconCircle)
            iconCircle.startAnimation(anim);
    }





}
