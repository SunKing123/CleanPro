package com.xiaoniu.cleanking.ui.newclean.dialog;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.common.utils.DisplayUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhaoyingtao
 * Date: 2020/7/24
 * Describe:首页引导弹窗
 */
public class GuideHomeDialog extends Dialog {
    Context mContext;
    @BindView(R.id.iv_guide_11)
    ImageView ivGuide11;
    @BindView(R.id.iv_guide_12)
    ImageView ivGuide12;
    @BindView(R.id.guide_ll01)
    LinearLayout guideLl01;
    @BindView(R.id.iv_guide_21)
    ImageView ivGuide21;
    @BindView(R.id.iv_guide_22)
    ImageView ivGuide22;
    @BindView(R.id.guide_ll02)
    LinearLayout guideLl02;
    @BindView(R.id.iv_guide_31)
    ImageView ivGuide31;
    @BindView(R.id.iv_guide_32)
    ImageView ivGuide32;
    @BindView(R.id.guide_ll03)
    LinearLayout guideLl03;
    @BindView(R.id.iv_guide_41)
    ImageView ivGuide41;
    @BindView(R.id.iv_guide_42)
    ImageView ivGuide42;
    @BindView(R.id.iv_guide_43)
    ImageView ivGuide43;
    @BindView(R.id.guide_ll04)
    LinearLayout guideLl04;
    @BindView(R.id.iv_guide_next)
    ImageView ivGuideNext;
    private int clickPosition = 0;
    private Handler handler = new Handler();
    private final int duration = 1000;

    public GuideHomeDialog(@NonNull Context context) {
        super(context, R.style.dialogDefaultFullScreen);
        this.mContext = context;
        initDialog();
        //设置alterdialog全屏
        if (getWindow() != null) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
//            lp.height = (int) (DisplayUtils.getScreenHeight()); //设置宽度
            lp.width = (int) (DisplayUtils.getScreenWidth()); //设置宽度
            getWindow().setAttributes(lp);
        }
    }

    private void initDialog() {
        setContentView(R.layout.dialog_guide_home);
        ButterKnife.bind(this, this);
        setCanceledOnTouchOutside(false);
        animationFirst();
    }

    @OnClick({R.id.iv_guide_next, R.id.iv_close})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_guide_next:
                switch (clickPosition) {
                    case 0:
                        guideLl01.setVisibility(View.GONE);
                        animationSecond();
                        break;
                    case 1:
                        guideLl02.setVisibility(View.GONE);
                        animationThird();
                        break;
                    case 2:
                        guideLl03.setVisibility(View.GONE);
                        animationFour();
                        break;
                    default:
                        dismiss();
                        break;
                }
                clickPosition++;
                break;
            case R.id.iv_close:
                dismiss();
                break;
        }

    }

    private void animationFirst() {
        ivGuide11.setVisibility(View.GONE);
        ivGuide12.setVisibility(View.GONE);
        ivGuideNext.setVisibility(View.GONE);
        guideLl01.setVisibility(View.VISIBLE);
        ivGuide11.setVisibility(View.VISIBLE);
        AnimatorSet animatorSet = playScaleAnimation(ivGuide11, duration);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ivGuide12.setVisibility(View.VISIBLE);
                handler.postDelayed(() -> {
                    ivGuideNext.setVisibility(View.VISIBLE);
                }, 100);
            }
        });
        animatorSet.start();
    }

    private void animationSecond() {
        ivGuide21.setVisibility(View.GONE);
        ivGuide22.setVisibility(View.GONE);
        ivGuideNext.setVisibility(View.GONE);
        guideLl02.setVisibility(View.VISIBLE);
        AnimatorSet animatorSet = playScaleAnimation(ivGuide21, duration);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                ivGuide21.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ivGuide22.setVisibility(View.VISIBLE);
                handler.postDelayed(() -> {
                    ivGuideNext.setVisibility(View.VISIBLE);
                }, 500);
            }
        });
        animatorSet.start();
    }

    private void animationThird() {
        ivGuide31.setVisibility(View.GONE);
        ivGuide32.setVisibility(View.GONE);
        ivGuideNext.setVisibility(View.GONE);
        guideLl03.setVisibility(View.VISIBLE);
        AnimatorSet animatorSet = playScaleAnimation(ivGuide31, duration);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                ivGuide31.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ivGuide32.setVisibility(View.VISIBLE);
                handler.postDelayed(() -> {
                    ivGuideNext.setVisibility(View.VISIBLE);
                }, 500);
            }
        });
        animatorSet.start();
    }

    private void animationFour() {
        ivGuide41.setVisibility(View.GONE);
        ivGuide42.setVisibility(View.GONE);
        ivGuide43.setVisibility(View.GONE);
        ivGuideNext.setVisibility(View.GONE);
        guideLl04.setVisibility(View.VISIBLE);
        AnimatorSet animatorShou = fourAnimation();
        animatorShou.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                ivGuide42.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ivGuide43.setVisibility(View.VISIBLE);
                handler.postDelayed(() -> {
                    ivGuideNext.setBackgroundResource(R.mipmap.icon_guide_4_4);
                    ivGuideNext.setVisibility(View.VISIBLE);
                }, 500);
            }
        });
        animatorShou.start();

        AnimatorSet animatorJianTou = from0To1Animation(ivGuide41, 100);
        animatorJianTou.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                ivGuide41.setVisibility(View.VISIBLE);
            }
        });
        animatorJianTou.start();
    }

    private AnimatorSet fourAnimation() {
        AnimatorSet animatorSet = new AnimatorSet();//组合动画
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(ivGuide42, "scaleX", 0, 1);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(ivGuide42, "scaleY", 0, 1);
        ObjectAnimator translationY = ObjectAnimator.ofFloat(ivGuide42, "translationY", -100);
        translationY.setDuration(2000);
        animatorSet.setDuration(duration);
        scaleX.setInterpolator(new BounceInterpolator());
        scaleY.setInterpolator(new BounceInterpolator());
        animatorSet.play(scaleX).with(scaleY).before(translationY);//两个动画同时开始
        return animatorSet;
    }

    public AnimatorSet playScaleAnimation(View view, int during) {
        AnimatorSet animatorSet = new AnimatorSet();//组合动画
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 0, 1);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 0, 1);
//        scaleX.setRepeatCount(1);
//        scaleY.setRepeatCount(1);
        animatorSet.setDuration(during);
        animatorSet.setInterpolator(new BounceInterpolator());
        animatorSet.play(scaleX).with(scaleY);//两个动画同时开始
        return animatorSet;
    }

    public AnimatorSet from0To1Animation(View view, int during) {
        AnimatorSet animatorSet = new AnimatorSet();//组合动画
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 0, 1);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 0, 1);
        animatorSet.setDuration(during);
        animatorSet.play(scaleX).with(scaleY);//两个动画同时开始
        return animatorSet;
    }
}
