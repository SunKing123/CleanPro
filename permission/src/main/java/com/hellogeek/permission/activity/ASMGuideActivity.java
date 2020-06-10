package com.hellogeek.permission.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hellogeek.permission.R;
import com.hellogeek.permission.base.BaseActivity;
import com.hellogeek.permission.util.PhoneRomUtils;
import com.hellogeek.permission.util.SystemHelper;


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

public class ASMGuideActivity extends BaseActivity {
    private static final int FRAME_ANIM_SIZE = 27;
    private ImageView mImgFrameAnima;
    private TextView tvContent;
    private TextView tvOpen;
    private TextView tvStartContent;
    private LinearLayout lltitle2;
    private LinearLayout lltitle;


    @Override
    protected void initParams(Bundle savedInstanceState) {

    }

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_permission_asm_guide;
    }

    private Handler mHandler;

    @Override
    protected void initContentView() {
        mImgFrameAnima = findViewById(R.id.img_res);
        lltitle2 = findViewById(R.id.lltitle2);
        lltitle = findViewById(R.id.lltitle);
        tvOpen = findViewById(R.id.tvOpen);
        tvStartContent = findViewById(R.id.tvStartContent);

        mHandler = new FrameAnimHandler(mImgFrameAnima);
        mHandler.sendEmptyMessageDelayed(1, 40);
        tvContent = findViewById(R.id.tvContent);
        try {
            if (PhoneRomUtils.getPhoneType() == PhoneRomUtils.XIAOMI && PhoneRomUtils.isXiaoV11()) {
                lltitle2.setVisibility(View.VISIBLE);
                lltitle.setVisibility(View.GONE);
            } else {
                lltitle2.setVisibility(View.GONE);
                lltitle.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {

        }
        if (PhoneRomUtils.getPhoneType() == PhoneRomUtils.MEIZU) {
            tvContent.setText("【无障碍】\n" + getString(R.string.app_name_brackets));
        } else {
            tvContent.setText(getString(R.string.app_name_brackets));
        }
        findViewById(R.id.clRoot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ASMGuideActivity.this.setResult(1);
                finish();
            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    protected boolean onGoBack() {
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        SystemHelper.setTopApp(this);
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
