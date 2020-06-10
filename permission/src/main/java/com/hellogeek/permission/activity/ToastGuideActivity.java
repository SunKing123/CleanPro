package com.hellogeek.permission.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hellogeek.permission.R;
import com.hellogeek.permission.base.BaseActivity;

/**
 * Desc:
 * <p>
 *
 * @author: ZhouTao
 * Date: 2019/7/26
 * Copyright: Copyright (c) 2016-2020
 * Company: @小牛科技
 * Update
 */
public class ToastGuideActivity extends BaseActivity {
    /* renamed from: a */
    public static final String item_top = "item_top";

    /* renamed from: b */
    public static final String item_bottom = "item_bottom";

    /* renamed from: c */
    public static final String extra_action_type = "extra_action_type";
    int mTopMargin;
    int mBottomMargin;
    int action_type;
    private TextView text_guide;
    private RelativeLayout guide_content;
    private RelativeLayout guide_bottom_re;

    @Override
    protected void initParams(Bundle savedInstanceState) {

    }

    @Override
    protected int getLayoutResID() {
        return R.layout.accessibility_super_vivo_guide_view;
    }

    @Override
    protected void initContentView() {
        getIntentExtra();
        initView();
        m13326a();
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
//        m13098a();
    }

    private void m13098a() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    ((ActivityManager) getSystemService("activity")).moveTaskToFront(ToastGuideActivity.this.getTaskId(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 900);
    }

    private void m13326a() {
        int c = getResources().getDisplayMetrics().heightPixels - this.mBottomMargin;
        StringBuilder sb = new StringBuilder();
        sb.append("permission test ActionExecutor setView -----the height ");
        sb.append(c);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.guide_bottom_re.getLayoutParams();
        if (layoutParams != null) {
            layoutParams.height = c;
            layoutParams.topMargin = this.mBottomMargin - this.mTopMargin;
            this.guide_bottom_re.setLayoutParams(layoutParams);
        }
    }

    private void getIntentExtra() {
        Intent intent = getIntent();
        this.mTopMargin = intent.getIntExtra(item_top, 0);
        this.mBottomMargin = intent.getIntExtra(item_bottom, 0);
        this.action_type = intent.getIntExtra(extra_action_type, 1);
        if (this.action_type == 0) {
            finish();
        }
    }

    private void initView() {
        this.text_guide = (TextView) findViewById(R.id.text_guide);
        this.guide_content = (RelativeLayout) findViewById(R.id.guide_content);
        this.guide_bottom_re = (RelativeLayout) findViewById(R.id.guide_bottom_re);
    }

    public static void start(Context context, int i, int i2) {
        if (context != null) {
            Intent intent = new Intent(context, ToastGuideActivity.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            intent.putExtra(item_top, i);
            intent.putExtra(item_bottom, i2);
            intent.putExtra(extra_action_type, 1);
            context.startActivity(intent);
            if (context instanceof Activity) {
                ((Activity) context).overridePendingTransition(0, 0);
            }

        }
    }

    public static void start(Context context) {
        if (context != null) {
            Intent intent = new Intent(context, ToastGuideActivity.class);
            intent.setFlags(268468224);
            intent.putExtra(extra_action_type, 0);
            context.startActivity(intent);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0) {
            finish();
        }
        return true;
    }

    @Override
    public void onDestroy() {
        Log.d("TAGTAG", "onDestroy: ");
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


}
