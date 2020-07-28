package com.xiaoniu.cleanking.widget;

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
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.bean.LottiePathdata;
import com.xiaoniu.cleanking.ui.main.bean.BubbleConfig;
import com.xiaoniu.cleanking.ui.main.bean.CountEntity;
import com.xiaoniu.cleanking.ui.main.widget.ScreenUtils;
import com.xiaoniu.cleanking.utils.BitmapUtil;
import com.xiaoniu.cleanking.utils.CleanUtil;
import com.xiaoniu.cleanking.utils.lottie.AnimHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhengzhihao
 * @date 2020/6/11 09
 * @mail：zhengzhihao@hellogeek.com 首页头部一键清理按钮
 */
public class OneKeyCircleBtnView extends RelativeLayout {

    private Context mContext;
    private LottieAnimationView viewLottie;
    private RelativeLayout rel_bubble,rlayout_onekey;
    private TouchImageView ivCenter;

    private Map<Integer, LottiePathdata> lottiePathdataMap;
    private LuckBubbleView lftop,lfbotm,rttop,rtbotm;
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
        viewLottie = (LottieAnimationView) v.findViewById(R.id.view_lottie_top_green);
        rel_bubble = (RelativeLayout)v.findViewById(R.id.rel_bubble);
        tv_file_total_size = (TextView) v.findViewById(R.id.tv_file_total_size);
        tv_file_total_tag = (TextView) v.findViewById(R.id.tv_file_total_tag);
        iv_top_perview = (ImageView)v.findViewById(R.id.tv_top_perview);
        ivCenter = (TouchImageView) v.findViewById(R.id.iv_center);
        rlayout_onekey = (RelativeLayout)v.findViewById(R.id.rlayout_onekey);
        lftop = (LuckBubbleView)v.findViewById(R.id.lftop);
        lfbotm = (LuckBubbleView)v.findViewById(R.id.lfbotm);
        rttop = (LuckBubbleView)v.findViewById(R.id.rttop);
        rtbotm = (LuckBubbleView)v.findViewById(R.id.rtbotm);
        setlottieData();
        setViewLayoutParms();
        greenState(false);

    }

    public void setViewLayoutParms() {
        int screenWidth = ScreenUtils.getScreenWidth(mContext);
        LayoutParams layoutParams = (LayoutParams) viewLottie.getLayoutParams();
        layoutParams.height =screenWidth;
        layoutParams.width = screenWidth;
        rel_bubble.setLayoutParams(layoutParams);    //金币布局
        rel_bubble.setVisibility(VISIBLE);
        iv_top_perview.setLayoutParams(layoutParams);//bitmap遮罩层；

//      layoutParams.setMargins(0, -Float.valueOf(screenWidth *0.9f* 0.1f).intValue(), 0, 0);
        viewLottie.setLayoutParams(layoutParams);    //波纹动画

        LayoutParams imglayoutParams = (LayoutParams) rlayout_onekey.getLayoutParams();
        imglayoutParams.height = Float.valueOf(screenWidth * 0.9f * 0.497f).intValue();
        imglayoutParams.width = Float.valueOf(screenWidth * 0.9f * 0.497f).intValue();
        rlayout_onekey.setLayoutParams(imglayoutParams);//按钮区域

//        RelativeLayout.LayoutParams textLayout = (RelativeLayout.LayoutParams) linear_text_tag.getLayoutParams();
//        textLayout.height = Float.valueOf(screenWidth * 0.1f).intValue();
//        linear_text_tag.setLayoutParams(textLayout);
        linear_text_tag.setVisibility(VISIBLE);
        viewLottie.playAnimation();

    }

    //根据扫描垃圾总数
    public void setTotalSize(long totalSize) {
        final CountEntity countEntity = CleanUtil.formatShortFileSize(totalSize);
        if(null!=countEntity){
            tv_file_total_size.setText(countEntity.getTotalSize() + countEntity.getUnit());
            tv_file_total_size.setVisibility(VISIBLE);
            tv_file_total_tag.setVisibility(VISIBLE);
            tv_file_total_tag.setText(getContext().getResources().getString(R.string.home_top_text_tag));
            changeScanAnim(totalSize);
        }

    }

    //未授权情况
    public void setNoSize() {
        tv_file_total_size.setText(getContext().getResources().getString(R.string.home_top_pop01_tag));
        tv_file_total_size.setVisibility(VISIBLE);
        tv_file_total_tag.setVisibility(GONE);
        greenState(true);
    }

    //清理完成狀態
    public void setClendedState(CountEntity countEntity) {
        if (null != countEntity&& !TextUtils.isEmpty(countEntity.getResultSize())) {
            tv_file_total_size.setVisibility(GONE);
            tv_file_total_tag.setText(getContext().getResources().getString(R.string.home_top_pop02_tag, countEntity.getResultSize()));
            tv_file_total_tag.setVisibility(VISIBLE);
            greenState(true);
        }

    }

    //扫描完毕
    public void scanFinish(long totalSize) {
        if (totalSize < 50 * 1024 * 1024) {//50mb以内
            greenState(true);
        } else if (totalSize > 50 * 1024 * 1024 && totalSize < 100 * 1024 * 1024) {//50mb~100mb
            yellowState(true);
        } else {
            redState(true);
        }
    }



    //根据扫描数值展示状态
    public void changeScanAnim(long totalSize) {
        if (totalSize < 50 * 1024 * 1024) {//50mb以内
            greenState(false);
        } else if (totalSize > 50 * 1024 * 1024 && totalSize < 100 * 1024 * 1024) {
            yellowState(false);
        } else {
            redState(false);
        }
    }

    public void greenState(boolean isFinish) {
        changeState(isFinish,ANIML_COLOR_STATE_GREEN);
    }

    public void yellowState(boolean isFinish) {
        changeState(isFinish,ANIML_COLOR_STATE_YELLOW);
    }

    public void redState(boolean isFinish) {
        changeState(isFinish,ANIML_COLOR_STATE_RED);
    }

    String newjsonPath = "";
    String newPath = "";
    //改变扫描动画
    public void changeState(boolean isFinish,int state){
        switch (state) {
            case ANIML_COLOR_STATE_GREEN:
                newjsonPath = lottiePathdataMap.get(isFinish ? 10 : 0).getJsonPath().toString();
                newPath = lottiePathdataMap.get(isFinish ? 10 : 0).getImgPath().toString();
                break;
            case ANIML_COLOR_STATE_YELLOW:
                newjsonPath = lottiePathdataMap.get(isFinish ? 11 : 1).getJsonPath().toString();
                newPath = lottiePathdataMap.get(isFinish ? 11 : 1).getImgPath().toString();
                break;
            case ANIML_COLOR_STATE_RED:
                newPath = lottiePathdataMap.get(isFinish ? 12 : 2).getImgPath().toString();
                newjsonPath = lottiePathdataMap.get(isFinish ? 12 : 2).getJsonPath().toString();
                break;
        }

        if (TextUtils.isEmpty(newjsonPath) || TextUtils.isEmpty(newPath))
            return;

        if (currentState != state) {//红黄绿状态切换;
          /*  long delay = 500;
            if (!AndroidUtil.isFastDoubleClick()) {
                delay = 200;
            }
            AppLifecyclesImpl.postDelay(new Runnable() {
                @Override
                public void run() {*/
                    // 设置预览图
                    // 保留上一次图片动画-位图
                    Bitmap bitmap = BitmapUtil.convertViewToBitmap(viewLottie);
                    if (bitmap != null) {
                        iv_top_perview.setImageBitmap(bitmap);
                        iv_top_perview.setVisibility(VISIBLE);
                    }

                    if (!viewLottie.isAnimating() || !viewLottie.getImageAssetsFolder().equals(newPath)) {
                        viewLottie.setAnimation(newjsonPath);
                        viewLottie.setImageAssetsFolder(newPath);
                        viewLottie.playAnimation();
                    }
                    mAnimHelper.changeAnim(iv_top_perview, viewLottie);
          /*      }
            }, delay);*/
        }


        if (!(currentIsFinish && isFinish)) { //是否完成状态切换
            if (!viewLottie.isAnimating() || !viewLottie.getImageAssetsFolder().equals(newPath)) {
                viewLottie.setAnimation(newjsonPath);
                viewLottie.setImageAssetsFolder(newPath);
                viewLottie.playAnimation();
            }
        }
        currentState = state;
        currentIsFinish = isFinish;



    }

    /**
     * 头部金币刷新
     * @param dataBean
     */
    public void refBubbleView(BubbleConfig dataBean){
        lfbotm.setDataCheckToShow(null);
        lftop.setDataCheckToShow(null);
        rtbotm.setDataCheckToShow(null);
        rttop.setDataCheckToShow(null);
        for (BubbleConfig.DataBean dataBean1 : dataBean.getData()){
            if(dataBean1 ==null){
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


    //动画素材初始化
    public void setlottieData() {
        lottiePathdataMap = new HashMap<>();
        lottiePathdataMap.put(0, new LottiePathdata("home_top_scan/anim01a/data.json", "home_top_scan/anim01a/images"));
        lottiePathdataMap.put(1, new LottiePathdata("home_top_scan/anim02a/data.json", "home_top_scan/anim02a/images"));
        lottiePathdataMap.put(2, new LottiePathdata("home_top_scan/anim03a/data.json", "home_top_scan/anim03a/images"));
        lottiePathdataMap.put(10, new LottiePathdata("home_top_scan/anim01b/data.json", "home_top_scan/anim01b/images"));
        lottiePathdataMap.put(11, new LottiePathdata("home_top_scan/anim02b/data.json", "home_top_scan/anim02b/images"));
        lottiePathdataMap.put(12, new LottiePathdata("home_top_scan/anim03b/data.json", "home_top_scan/anim03b/images"));
    }



}
