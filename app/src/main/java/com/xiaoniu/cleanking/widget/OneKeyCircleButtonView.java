package com.xiaoniu.cleanking.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.bean.LottiePathdata;
import com.xiaoniu.cleanking.ui.main.widget.ScreenUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhengzhihao
 * @date 2020/6/11 09
 * @mail：zhengzhihao@hellogeek.com
 * 首页头部一键清理按钮
 */
public class OneKeyCircleButtonView extends RelativeLayout {

    private Context mContext;
    private LottieAnimationView viewLottieLower;
    private LottieAnimationView viewLottieTop;
    private ImageView ivCenter;
    private Map<Integer, LottiePathdata> lottiePathdataMap;
    private int lottieIndex =1;
    public OneKeyCircleButtonView(Context context) {
        super(context);
        initView(context);
    }

    public OneKeyCircleButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public OneKeyCircleButtonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    /**
     * 初始化布局
     * @return
     */
    public void initView(Context context) {
        mContext = context;
        View v = LayoutInflater.from(mContext).inflate(R.layout.layout_home_top_circle_anim, this, true);
        viewLottieLower = (LottieAnimationView) v.findViewById(R.id.view_lottie_lower);
        viewLottieTop = (LottieAnimationView) v.findViewById(R.id.view_lottie_top_tag01);
        ivCenter = (ImageView) v.findViewById(R.id.iv_center);
        setViewLayoutParms();
        setlottieData();
    }

    public void setViewLayoutParms(){
        int screenWidth = ScreenUtils.getScreenWidth(mContext);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)viewLottieTop.getLayoutParams();
        layoutParams.height = screenWidth;
        layoutParams.width = screenWidth;
        viewLottieTop.setLayoutParams(layoutParams);

        RelativeLayout.LayoutParams imglayoutParams = (RelativeLayout.LayoutParams)ivCenter.getLayoutParams();
        imglayoutParams.height = Float.valueOf(screenWidth*0.447f).intValue();
        imglayoutParams.width =  Float.valueOf(screenWidth*0.447f).intValue();
        ivCenter.setLayoutParams(imglayoutParams);
    }

    public void startLottie(){
        lottieIndex = 1;
        playLottie(viewLottieTop,lottiePathdataMap.get(lottieIndex),lottieIndex);
        viewLottieTop.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (lottieIndex <= 5) {
                    lottieIndex++;
                    playLottie(viewLottieTop, lottiePathdataMap.get(lottieIndex),lottieIndex);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public void playLottie(LottieAnimationView lottieview,LottiePathdata pathdata,int lottieIndex){
        if (lottieIndex != 1 && lottieIndex % 2 == 1) {
            lottieview.setAlpha(0f);
        }
        lottieview.setVisibility(VISIBLE);
        lottieview.animate()
                .alpha(1f)
                .setDuration(500)
                .setListener(null);
        if(lottieIndex ==6){
            lottieview.setRepeatCount(LottieDrawable.INFINITE);
        }
        lottieview.setAnimation(pathdata.getJsonPath());
        lottieview.setImageAssetsFolder(pathdata.getImgPath());
        lottieview.playAnimation();

        setCenterImg(lottieIndex);
    }

    public void cancleLottie(LottieAnimationView lottieview){
        lottieview.cancelAnimation();
        lottieview.clearAnimation();
    }

    public void setlottieData(){
        lottiePathdataMap = new HashMap<>();
        lottiePathdataMap.put(1,new LottiePathdata("home_top_scan/anim01a/data.json","home_top_scan/anim01a/images"));
        lottiePathdataMap.put(2,new LottiePathdata("home_top_scan/anim01b/data.json","home_top_scan/anim01b/images"));
        lottiePathdataMap.put(3,new LottiePathdata("home_top_scan/anim02a/data.json","home_top_scan/anim02a/images"));
        lottiePathdataMap.put(4,new LottiePathdata("home_top_scan/anim02b/data.json","home_top_scan/anim02b/images"));
        lottiePathdataMap.put(5,new LottiePathdata("home_top_scan/anim03a/data.json","home_top_scan/anim03a/images"));
        lottiePathdataMap.put(6,new LottiePathdata("home_top_scan/anim03b/data.json","home_top_scan/anim03b/images"));
    }

    public void setCenterImg(int index) {
        ivCenter.setVisibility(VISIBLE);
        if (index <= 2) {
            ivCenter.setImageResource(R.drawable.icon_circle_btn_green);
        } else if (index > 2 && index <= 4) {
            ivCenter.setImageResource(R.drawable.icon_circle_btn_yellow);
        } else if (index > 4) {
            ivCenter.setImageResource(R.drawable.icon_circle_btn_red);
        }

    }
}
