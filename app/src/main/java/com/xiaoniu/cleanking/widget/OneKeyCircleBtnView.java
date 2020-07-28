package com.xiaoniu.cleanking.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.main.bean.BubbleConfig;
import com.xiaoniu.cleanking.ui.main.bean.CountEntity;
import com.xiaoniu.cleanking.ui.main.widget.ScreenUtils;
import com.xiaoniu.cleanking.utils.BitmapUtil;
import com.xiaoniu.cleanking.utils.CleanUtil;
import com.xiaoniu.cleanking.utils.LogUtils;
import com.xiaoniu.cleanking.utils.lottie.AnimHelper;

/**
 * @author zhengzhihao
 * @date 2020/6/11 09
 * @mail：zhengzhihao@hellogeek.com 首页头部一键清理按钮
 */
public class OneKeyCircleBtnView extends RelativeLayout {

    private Context mContext;
    private LottieAnimationView viewLottie;
    private RelativeLayout rel_bubble;

    private LuckBubbleView lftop, lfbotm, rttop, rtbotm;
    private TextView tv_file_total_size, tv_file_total_tag;
    private LinearLayout linear_text_tag;
    private ImageView iv_top_perview;
    private AnimHelper mAnimHelper = new AnimHelper();
    private static final int ANIML_COLOR_STATE_GREEN = 1;
    private static final int ANIML_COLOR_STATE_YELLOW = 2;
    private static final int ANIML_COLOR_STATE_RED = 3;

    private int currentState = ANIML_COLOR_STATE_GREEN;   //当前动画状态
    private boolean currentIsFinish = false;            //当前动画状态

    public OneKeyCircleBtnView(Context context) {
        super(context);
        initView(context);
    }

    public OneKeyCircleBtnView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public OneKeyCircleBtnView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);

    }


    /**
     * 初始化布局
     *
     * @return
     */
    public void initView(Context context) {
        mContext = context;
        View v = LayoutInflater.from(mContext).inflate(R.layout.layout_home_top_circle_anim, this, true);
        linear_text_tag = (LinearLayout) v.findViewById(R.id.linear_text_tag);
        viewLottie = (LottieAnimationView) v.findViewById(R.id.view_lottie_top_center);
        rel_bubble = (RelativeLayout) v.findViewById(R.id.rel_bubble);
        tv_file_total_size = (TextView) v.findViewById(R.id.tv_file_total_size);
        tv_file_total_tag = (TextView) v.findViewById(R.id.tv_file_total_tag);
        iv_top_perview = (ImageView) v.findViewById(R.id.tv_top_perview);
        lftop = (LuckBubbleView) v.findViewById(R.id.lftop);
        lfbotm = (LuckBubbleView) v.findViewById(R.id.lfbotm);
        rttop = (LuckBubbleView) v.findViewById(R.id.rttop);
        rtbotm = (LuckBubbleView) v.findViewById(R.id.rtbotm);
        setViewLayoutParms();

    }

    public void setViewLayoutParms() {
        int screenWidth = ScreenUtils.getScreenWidth(mContext);
        LayoutParams layoutParams = (LayoutParams) viewLottie.getLayoutParams();
        layoutParams.height = screenWidth;
        layoutParams.width = screenWidth;
        rel_bubble.setLayoutParams(layoutParams);    //金币布局
        rel_bubble.setVisibility(VISIBLE);


//        layoutParams.setMargins(0, -Float.valueOf(screenWidth *0.9f* 0.1f).intValue(), 0, 0);
        layoutParams.height = Float.valueOf((screenWidth / 1.43f)).intValue();  //根据lottie素材宽高比设置
        viewLottie.setLayoutParams(layoutParams);    //波纹动画
        iv_top_perview.setLayoutParams(layoutParams);//bitmap遮罩层；


        RelativeLayout.LayoutParams textLayout = (RelativeLayout.LayoutParams) linear_text_tag.getLayoutParams();
        textLayout.topMargin = Float.valueOf((screenWidth / 1.43f / 1.81f)).intValue();
        linear_text_tag.setLayoutParams(textLayout);
        linear_text_tag.setVisibility(VISIBLE);
        startAnimation();

    }



    public void startAnimation(){
        viewLottie.setMinAndMaxFrame(0, 74);
        viewLottie.playAnimation();
        viewLottie.addAnimatorUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = animation.getAnimatedFraction();
                if (fraction >= 1f && null != viewLottie) {
                    viewLottie.setMinAndMaxFrame(75, 143);
                    viewLottie.setRepeatCount(LottieDrawable.INFINITE);
                    viewLottie.playAnimation();
                }
            }
        });
    }

    //根据扫描垃圾总数
    public void setTotalSize(long totalSize) {
        final CountEntity countEntity = CleanUtil.formatShortFileSize(totalSize);
        if (null != countEntity&&null!= tv_file_total_size) {
            tv_file_total_size.setText(countEntity.getTotalSize() + countEntity.getUnit());
            tv_file_total_size.setVisibility(VISIBLE);
            tv_file_total_tag.setVisibility(VISIBLE);
            tv_file_total_tag.setText(getContext().getResources().getString(R.string.home_top_text_tag));
        }

    }

    //未授权情况
    public void setNoSize() {
        tv_file_total_size.setText(getContext().getResources().getString(R.string.home_top_pop01_tag));
        tv_file_total_size.setVisibility(VISIBLE);
        tv_file_total_tag.setVisibility(GONE);
        greenState();
    }

    //清理完成狀態
    public void setClendedState(CountEntity countEntity) {
        if (null != countEntity && !TextUtils.isEmpty(countEntity.getResultSize())) {
            tv_file_total_size.setVisibility(GONE);
            tv_file_total_tag.setText(getContext().getResources().getString(R.string.home_top_pop02_tag, countEntity.getResultSize()));
            tv_file_total_tag.setVisibility(VISIBLE);
            greenState();
        }

    }

    //扫描完毕
    public void scanFinish(long totalSize) {
        redState();
    }


    public void greenState() {
        viewLottie.removeAllUpdateListeners();
        viewLottie.setRepeatCount(LottieDrawable.INFINITE);
        viewLottie.setAnimation("home_top_scan/state03/data.json");
        viewLottie.setImageAssetsFolder("home_top_scan/state03/images");
        viewLottie.playAnimation();
    }

    public void redState() {
        if (null != viewLottie) {
            if (!viewLottie.getImageAssetsFolder().contains("state02")) {
                viewLottie.cancelAnimation();
                viewLottie.removeAllUpdateListeners();
                viewLottie.setMinAndMaxFrame(144, 192);
                viewLottie.setRepeatCount(0);
                viewLottie.playAnimation();
                viewLottie.addAnimatorUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        if (animation.getAnimatedFraction() >= 1f && null != viewLottie) {
                            Bitmap bitmap = BitmapUtil.convertViewToBitmap(viewLottie);
                            if (bitmap != null && null != iv_top_perview) {
                                iv_top_perview.setImageBitmap(bitmap);
                                iv_top_perview.setVisibility(VISIBLE);
                            }
                            viewLottie.removeAllUpdateListeners();
                            viewLottie.setRepeatCount(LottieDrawable.INFINITE);
                            viewLottie.setAnimation("home_top_scan/state02/data.json");
                            viewLottie.setImageAssetsFolder("home_top_scan/state02/images");
                            viewLottie.playAnimation();

                            mAnimHelper.changeAnim(iv_top_perview, viewLottie);
                        }
                    }
                });
            }

        }

    }


    /**
     * 头部金币刷新
     *
     * @param dataBean
     */
    public void refBubbleView(BubbleConfig dataBean) {
        lfbotm.setDataCheckToShow(null);
        lftop.setDataCheckToShow(null);
        rtbotm.setDataCheckToShow(null);
        rttop.setDataCheckToShow(null);
        for (BubbleConfig.DataBean dataBean1 : dataBean.getData()) {
            if (dataBean1 == null) {
                return;
            }
            switch (dataBean1.getLocationNum()) {
                case 1://左上
                    lftop.setDataCheckToShow(dataBean1);
                    break;
                case 2://右上
                    rttop.setDataCheckToShow(dataBean1);
                    break;
                case 3://左下
                    lfbotm.setDataCheckToShow(dataBean1);
                    break;
                case 4://右下
                    rtbotm.setDataCheckToShow(dataBean1);
                    break;

            }
        }
    }


}
