package com.xiaoniu.cleanking.ui.main.activity;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.RouteConstants;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.utils.SystemHelper;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;

import java.lang.ref.WeakReference;

/**
 * Desc:
 * <p>
 *
 * @author: ZhouTao
 * Date: 2019/7/4
 * Copyright: Copyright (c) 2016-2020
 * Company: @小牛科技
 * Update
 */
@Route(path = RouteConstants.ACTIVITY_ASM_GUIDE)
public class ASMGuideActivity extends BaseActivity {
    private static final int FRAME_ANIM_SIZE = 27;
    private ImageView mImgFrameAnima;
    private TextView tvContent;
    private TextView tvOpen;

    LottieAnimationView mAnimationView;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_asm_guide;
    }

    @Override
    protected void onPause() {
        super.onPause();
        SystemHelper.setTopApp(this);
    }

    @Override
    protected void initView() {
        mAnimationView = findViewById(R.id.view_lottie);
//        showLottieView();
        StatusBarCompat.translucentStatusBarForImage(this, true, false);
        mImgFrameAnima = findViewById(R.id.img_res);
        tvOpen = findViewById(R.id.tvOpen);
        tvOpen.setVisibility(View.VISIBLE);

        mHandler = new FrameAnimHandler(mImgFrameAnima);
        mHandler.sendEmptyMessageDelayed(1, 40);
        tvContent = findViewById(R.id.tvContent);
//        if (PhoneRomUtils.getPhoneType() == PhoneRomUtils.MEIZU) {
//            tvContent.setText("【无障碍】\n"+getString(R.string.app_name_brackets));
//        }else{
            tvContent.setText(getString(R.string.app_name));
//        }
        findViewById(R.id.clRoot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    /**
     * 显示吸收动画
     */
    private void showLottieView() {
        mAnimationView.useHardwareAcceleration();
        mAnimationView.setAnimation("data_premis.json");
        mAnimationView.setImageAssetsFolder("images");
        mAnimationView.playAnimation();
    }

    @Override
    public void inject(ActivityComponent activityComponent) {

    }

    @Override
    public void netError() {

    }

    private class FrameAnimHandler extends Handler {

        private int index = 0;
        private WeakReference<ImageView> mImgView;

        public FrameAnimHandler(ImageView imageView) {
            mImgView = new WeakReference<>(imageView);
        }

        @Override
        public void handleMessage(Message msg) {
            ImageView img = mImgView.get();
            if (index >= FRAME_ANIM_SIZE) {

                img.setImageDrawable(getResources().getDrawable(R.mipmap.img_asm_0001));

                index = 0;
                mHandler.removeMessages(1);
                sendEmptyMessageDelayed(1, 40);
                return;
            }

            Drawable d = img.getDrawable();
            if (d != null) {
                d.setCallback(null);
                img.setBackgroundDrawable(null);
            }

            img.setImageDrawable(getResources().getDrawable(R.mipmap.img_asm_0001 + index));

            index++;
            sendEmptyMessageDelayed(1, 40);
        }
    }
}
