package com.installment.mall.ui.main.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.installment.mall.R;


/**
 * Created on 16/6/3.
 */
public class BottomBarTab extends FrameLayout {
    private ImageView mIcon;
    private TextView mTvTitle;
    private Context mContext;
    private int mTabPosition = -1;

    private int[] animations = {R.drawable.tab_loan, R.drawable.tab_shopping,R.drawable.tab_quota, R.drawable.tab_me};
    private int[] iconsSelect = {R.mipmap.loan_select, R.mipmap.mall_select, R.mipmap.up_quota_select,R.mipmap.me_select};
    private int[] icons = {R.mipmap.loan_normal, R.mipmap.mall_normal,R.mipmap.up_quota_normal, R.mipmap.me_normal};


    private TextView mTvUnreadCount;
    private ObjectAnimator mObjectAnimator;

    public BottomBarTab(Context context, @DrawableRes int icon, CharSequence title) {
        this(context, null, icon, title);
    }

    public BottomBarTab(Context context, AttributeSet attrs, int icon, CharSequence title) {
        this(context, attrs, 0, icon, title);
    }

    public BottomBarTab(Context context, AttributeSet attrs, int defStyleAttr, int icon, CharSequence title) {
        super(context, attrs, defStyleAttr);
        init(context, icon, title);
    }

    private void init(Context context, int icon, CharSequence title) {
        mContext = context;
        TypedArray typedArray = context.obtainStyledAttributes(new int[]{R.attr.selectableItemBackgroundBorderless});
        Drawable drawable = typedArray.getDrawable(0);
        setBackgroundDrawable(drawable);
        typedArray.recycle();

        LinearLayout lLContainer = new LinearLayout(context);
        lLContainer.setOrientation(LinearLayout.VERTICAL);
        lLContainer.setGravity(Gravity.CENTER);
        LayoutParams paramsContainer = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsContainer.gravity = Gravity.CENTER;
        lLContainer.setLayoutParams(paramsContainer);

        mIcon = new ImageView(context);
        //int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 27, getResources().getDisplayMetrics());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mIcon.setImageResource(icon);
        mIcon.setLayoutParams(params);
        //mIcon.setColorFilter(ContextCompat.getColor(context, R.color.tab_unselect));
        lLContainer.addView(mIcon);

        mTvTitle = new TextView(context);
        mTvTitle.setText(title);
        LinearLayout.LayoutParams paramsTv = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsTv.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
        mTvTitle.setTextSize(10);
        mTvTitle.setTextColor(ContextCompat.getColor(context, R.color.color_7c7a7a));
        mTvTitle.setLayoutParams(paramsTv);
        lLContainer.addView(mTvTitle);

        addView(lLContainer);

//        int min = dip2px(context, 20);
//        int padding = dip2px(context, 5);
//        mTvUnreadCount = new TextView(context);
//        mTvUnreadCount.setBackgroundResource(R.drawable.bg_msg_bubble);
//        mTvUnreadCount.setMinWidth(min);
//        mTvUnreadCount.setTextColor(Color.WHITE);
//        mTvUnreadCount.setPadding(padding, 0, padding, 0);
//        mTvUnreadCount.setGravity(Gravity.CENTER);
//        LayoutParams tvUnReadParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, min);
//        tvUnReadParams.gravity = Gravity.CENTER;
//        tvUnReadParams.leftMargin = dip2px(context, 17);
//        tvUnReadParams.bottomMargin = dip2px(context, 14);
//        mTvUnreadCount.setLayoutParams(tvUnReadParams);
//        mTvUnreadCount.setVisibility(GONE);
//
//        addView(mTvUnreadCount);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (selected) {
//            if (mTabPosition == MAIN_ACTIVITY.ACTION) {
//                mTvTitle.setVisibility(VISIBLE);
//                if (mObjectAnimator != null) {
//                    mObjectAnimator.cancel();
//                }
//                mIcon.setImageResource(R.mipmap.make_money_select);
//                mTvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.color_252222));
//            } else {
//                showAnimation(mIcon,animations[mTabPosition]);
                mIcon.setImageResource(iconsSelect[mTabPosition]);
                //mIcon.setColorFilter(ContextCompat.getColor(mContext, R.color.color_FB296B));
                mTvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.color_5A6572));
//            }
        } else {
            //mIcon.setColorFilter(ContextCompat.getColor(mContext, R.color.color_A47282));
            mIcon.setImageResource(icons[mTabPosition]);
            mTvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.color_5A6572_50));
//            if (mTabPosition == MAIN_ACTIVITY.ACTION) {
//                mIcon.setImageResource(R.mipmap.icon_action);
//                mTvTitle.setVisibility(GONE);
//                if (mObjectAnimator != null) {
//                    mObjectAnimator.cancel();
//                    mObjectAnimator.start();
//                } else {
//                    startActionAnim();
//                }
//            }
        }
    }

    public void setRejectIcon(int iconRes) {
        mIcon.setImageResource(iconRes);
//        iconsSelect[MAIN_ACTIVITY.LIFE] = R.mipmap.activity_select;
//        icons[MAIN_ACTIVITY.LIFE] = R.mipmap.activity_normal;
//        mTvTitle.setText("活动");
    }

    private void startActionAnim() {
        if (mObjectAnimator == null) {
            mObjectAnimator = ObjectAnimator.ofFloat(mIcon, "rotation", 0, -10, 10, -10, 0);
            mObjectAnimator.setDuration(450);
            mObjectAnimator.setStartDelay(1000);
            mObjectAnimator.setRepeatMode(ValueAnimator.RESTART);
            mObjectAnimator.setRepeatCount(ValueAnimator.INFINITE);
            mObjectAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {

                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    mIcon.setRotation(0);
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                    animation.setStartDelay(1000);
                    animation.start();
                }
            });
        }
        mObjectAnimator.start();
    }

    public void setTabPosition(int position, int currentPosition) {
        mTabPosition = position;
        if (position == currentPosition) {
            setSelected(true);
        }
//        if (mTabPosition == MAIN_ACTIVITY.ACTION) {
//            mIcon.setImageResource(R.mipmap.icon_action);
//            mTvTitle.setVisibility(GONE);
//            if (mObjectAnimator != null) {
//                mObjectAnimator.cancel();
//                mObjectAnimator.start();
//            } else {
//                startActionAnim();
//            }
//        }
    }

    public int getTabPosition() {
        return mTabPosition;
    }

    /**
     * 加载帧动画
     *
     * @param imageView
     * @param resourceId 帧动画
     */
    private void showAnimation(ImageView imageView, int resourceId) {
        imageView.setImageResource(resourceId);
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getDrawable();
        //执行一次
        animationDrawable.setOneShot(true);
        animationDrawable.start();
    }
}
