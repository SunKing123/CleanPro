package com.xiaoniu.cleanking.widget;

import android.animation.Animator;
import android.content.Context;
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
import com.xiaoniu.cleanking.bean.LottiePathdata;
import com.xiaoniu.cleanking.ui.main.widget.ScreenUtils;
import com.xiaoniu.cleanking.utils.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhengzhihao
 * @date 2020/6/11 09
 * @mail：zhengzhihao@hellogeek.com
 * 首页头部一键清理按钮
 */
public class OneKeyCircleButtonView extends RelativeLayout {

    private Context mContext;
    private LottieAnimationView viewLottieRed;
    private LottieAnimationView viewLottieYellow;
    private LottieAnimationView viewLottieGreen;
    private List<LottieAnimationView> lottieList;
    private TouchImageView ivCenter;
    private Map<Integer, LottiePathdata> lottiePathdataMap;
    private int lottieIndex =0;
    private TextView tv_file_total_size;
    private LinearLayout linear_text_tag;
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
        linear_text_tag = (LinearLayout)v.findViewById(R.id.linear_text_tag);
        viewLottieRed = (LottieAnimationView) v.findViewById(R.id.view_lottie_lower_red);
        viewLottieYellow = (LottieAnimationView) v.findViewById(R.id.view_lottie_top_yellow);
        viewLottieGreen = (LottieAnimationView) v.findViewById(R.id.view_lottie_top_green);
        tv_file_total_size = (TextView)v.findViewById(R.id.tv_file_total_size);
        lottieList = new ArrayList<>();
        lottieList.add(viewLottieGreen);
        lottieList.add(viewLottieYellow);
        lottieList.add(viewLottieRed);
        setlottieData();
        ivCenter = (TouchImageView) v.findViewById(R.id.iv_center);
        ivCenter.setAlpha(0f);
        ivCenter.animate().alpha(1f).setDuration(100);
        setViewLayoutParms();

    }

    public void setViewLayoutParms(){
        int screenWidth = ScreenUtils.getScreenWidth(mContext);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewLottieYellow.getLayoutParams();
        layoutParams.height = Float.valueOf(screenWidth*1.2f).intValue();
        layoutParams.width = Float.valueOf(screenWidth*1.2f).intValue();
        viewLottieYellow.setLayoutParams(layoutParams);
        viewLottieRed.setLayoutParams(layoutParams);
        viewLottieGreen.setLayoutParams(layoutParams);

        RelativeLayout.LayoutParams imglayoutParams = (RelativeLayout.LayoutParams)ivCenter.getLayoutParams();
        imglayoutParams.height = Float.valueOf(screenWidth*0.447f*1.2f).intValue();
        imglayoutParams.width =  Float.valueOf(screenWidth*0.447f*1.2f).intValue();
        ivCenter.setLayoutParams(imglayoutParams);

//
//        RelativeLayout.LayoutParams imglayoutParams = (RelativeLayout.LayoutParams)lin.getLayoutParams();
//        imglayoutParams.height = Float.valueOf(screenWidth*0.447f*1.2f).intValue();
//        imglayoutParams.width =  Float.valueOf(screenWidth*0.447f*1.2f).intValue();
//        ivCenter.setLayoutParams(imglayoutParams);
    }

    public void startLottie(){
        lottieIndex = 0;
        playLottie(lottieIndex);

    }

    //清理完成绿色状态;
    public void setGreenState(){
        //"home_top_scan/anim01a/data.json","home_top_scan/anim01a/images"
        viewLottieGreen.setAnimation("home_top_scan/anim01b/data.json");
        viewLottieGreen.setImageAssetsFolder("home_top_scan/anim01b/images");
        viewLottieGreen.playAnimation();
        setCenterImg(0);
    }


    public void clancleAnim(LottieAnimationView lottieAnimationView){
        lottieAnimationView.cancelAnimation();
        lottieAnimationView.clearAnimation();
        lottieAnimationView.setVisibility(GONE);
    }
    public void playLottie(int index){
        LottieAnimationView lottieview = lottieList.get(index);
        LottiePathdata pathdata = lottiePathdataMap.get(index);
        lottieview.setAlpha(0f);
        lottieview.setVisibility(VISIBLE);
        lottieview.animate()
                .alpha(1f)
                .setDuration(500)
                .setListener(null);
        lottieview.setAnimation(pathdata.getJsonPath());
        lottieview.setImageAssetsFolder(pathdata.getImgPath());
        lottieview.playAnimation();
        setCenterImg(index);
        lottieview.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                //当前动画隐藏
                if (null != lottieview.getTag() && lottieview.getTag().toString().trim().equals("stoped"))
                    return;
                if(lottieIndex != 2){  //最后一个不隐藏
                    lottieview.setTag("stoped");
                    lottieview.setAlpha(1f);
                    lottieview.setVisibility(VISIBLE);
                    lottieview.animate()
                            .alpha(0f)
                            .setDuration(500)
                            .setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    clancleAnim(lottieview);
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            });
                }

                //下个动画
                if (lottieIndex <= 1){
                    lottieIndex++;
                    playLottie(lottieIndex);
                }

            }
        });
    }


    public void setTotalSize(String size){
        tv_file_total_size.setText(size);
    }
    public void scanFinish(){
        LottiePathdata pathdata = lottiePathdataMap.get(3);
        LottieAnimationView lottieAnimationView = lottieList.get(2);
        lottieAnimationView.setAnimation(pathdata.getJsonPath());
        lottieAnimationView.setImageAssetsFolder(pathdata.getImgPath());
        lottieAnimationView.playAnimation();
    }

    //正常扫描流程
    public void setlottieData(){
        lottiePathdataMap = new HashMap<>();
        lottiePathdataMap.put(0,new LottiePathdata("home_top_scan/anim01a/data.json","home_top_scan/anim01a/images"));
        lottiePathdataMap.put(1,new LottiePathdata("home_top_scan/anim02a/data.json","home_top_scan/anim02a/images"));
        lottiePathdataMap.put(2,new LottiePathdata("home_top_scan/anim03a/data.json","home_top_scan/anim03a/images"));
        lottiePathdataMap.put(3,new LottiePathdata("home_top_scan/anim03b/data.json","home_top_scan/anim03b/images"));
    }

    public void setCenterImg(int index) {
        ivCenter.setVisibility(VISIBLE);
        if (index == 0) {
            ivCenter.setImageResource(R.drawable.icon_circle_btn_white);
        } else if (index == 1) {
            ivCenter.setImageResource(R.drawable.icon_circle_btn_white);
        } else if (index == 2) {
            ivCenter.setImageResource(R.drawable.icon_circle_btn_white);
        }

    }
}
