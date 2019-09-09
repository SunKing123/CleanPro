package com.xiaoniu.cleanking.ui.main.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umeng.socialize.UMShareAPI;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.main.fragment.CleanFinishWebFragment;
import com.xiaoniu.cleanking.ui.main.interfac.AppBarStateChangeListener;
import com.xiaoniu.cleanking.ui.news.fragment.NewsFragment;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.common.base.BaseActivity;

import java.util.Random;

import cn.jzvd.Jzvd;

import static android.view.View.GONE;

/**
 * 清理完成 显示咨询
 */
public class CleanFinish2Activity extends BaseActivity {

    private NewsFragment mNewsFragment;
    private LinearLayout mLlTopTitle;
    private AppBarLayout mAppBarLayout;
    private ImageView mIvBack;
    private boolean isAnimShow = false;
    private String mTitle;
    private TextView mTvSize;
    private TextView mTvGb;
    private TextView mTvQl;
    private TextView mTopSubTitle;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_finish_layout;
    }

    @Override
    protected void initVariable(Intent intent) {

    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mIvBack = findViewById(R.id.iv_back_clear_finish);
        mTvSize = findViewById(R.id.tv_size);
        mTvGb = findViewById(R.id.tv_clear_finish_gb_title);
        mLlTopTitle = findViewById(R.id.ll_top_title);
        mAppBarLayout = findViewById(R.id.appbar_layout);
        mTopSubTitle = findViewById(R.id.tv_top_sub_title);
        mTvQl = findViewById(R.id.tv_ql);

        Intent intent = getIntent();
        //保存清理完成次数
        PreferenceUtil.saveCleanNum();
        if (intent != null) {
            mTitle = intent.getStringExtra("title");
            String num = intent.getStringExtra("num");
            String unit = intent.getStringExtra("unit");
            mTvSize.setText(num);
            mTvGb.setText(unit);
            if (TextUtils.isEmpty(mTitle))
                mTitle = getString(R.string.app_name);
            if (getString(R.string.app_name).contains(mTitle)) {
                //悟空清理
                if (TextUtils.isEmpty(num) || num.equals("0.0")|| num.equals("0")) {
                    mTvSize.setText("");
                    mTvGb.setText("已达到最佳状态");
                    mTvGb.setTextSize(20);
                    mTvQl.setText("快去体验其他功能");
                }
            }else if (getString(R.string.tool_one_key_speed).contains(mTitle)) {
                //一键加速
                if (TextUtils.isEmpty(num) || num.equals("0.0")|| num.equals("0")) {
                    mTvSize.setText("");
                    mTvGb.setText("已达到最佳状态");
                    mTvGb.setTextSize(20);
                    mTvQl.setText("快去体验其他功能");
                }
            }else if (getString(R.string.tool_phone_clean).contains(mTitle)) {
                //一键加速
                if (TextUtils.isEmpty(num) || num.equals("0.0")|| num.equals("0")) {
                    mTvSize.setText("");
                    mTvGb.setText("已达到最佳状态");
                    mTvGb.setTextSize(20);
                    mTvQl.setText("快去体验其他功能");
                }
            } else if (getString(R.string.tool_super_power_saving).contains(mTitle)) {
                //超强省电
                if (TextUtils.isEmpty(num) || num.equals("0.0")|| num.equals("0")) {
                    mTvSize.setText("");
                    mTvGb.setText("已达到最佳状态");
                    mTvGb.setTextSize(20);
                    mTvQl.setText("快去体验其他功能");
                }
            } else if (getString(R.string.tool_chat_clear).contains(mTitle)||getString(R.string.tool_chat_clear_n).contains(mTitle)) {
                //微信专情
                if (TextUtils.isEmpty(num) || num.equals("0.0")|| num.equals("0")) {
                    mTvSize.setText("");
                    mTvGb.setText("手机很干净");
                    mTvGb.setTextSize(20);
                    mTvQl.setText("快去体验其他功能");
                }
            } else if (getString(R.string.tool_qq_clear).contains(mTitle)) {
                //QQ专清
                if (TextUtils.isEmpty(num) || num.equals("0.0")|| num.equals("0")) {
                    mTvSize.setText("");
                    mTvGb.setText("手机很干净");
                    mTvGb.setTextSize(20);
                    mTvQl.setText("快去体验其他功能");
                }
            } else if (getString(R.string.tool_notification_clean).contains(mTitle)) {
                //通知栏清理
                if (TextUtils.isEmpty(num) || num.equals("0.0")|| num.equals("0")) {
                    mTvSize.setText("");
                    mTvGb.setText("通知栏很干净");
                    mTvGb.setTextSize(20);
                    mTvQl.setText("快去体验其他炫酷功能");
                }
            } else if (getString(R.string.tool_phone_temperature_low).contains(mTitle)) {
                //手机降温
                mTvSize.setText("");
                int tem = new Random().nextInt(3) + 1;
                mTvGb.setText("成功降温" + tem + "°C");
                mTvGb.setTextSize(20);
                mTvQl.setText("60s后达到最佳降温效果");
            }
            mTopSubTitle.setText(mTitle);
        }
        if (!PreferenceUtil.getClearNum() || !PreferenceUtil.getShareNum())
            showNews();
        else
            showWeb();
    }

    @Override
    protected void setListener() {
        mIvBack.setOnClickListener(v -> finish());
        mAppBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                if (state == State.EXPANDED) {
                    if (!isAnimShow)
                        return;
                    if (mNewsFragment == null) return;
                    //展开状态
                    mNewsFragment.moveNavigation(false);
                    hideBackImg(true);
                    mNewsFragment.getIvBack().setVisibility(View.VISIBLE);
                    isAnimShow = false;
                } else if (state == State.COLLAPSED) {
                    if (isAnimShow)
                        return;
                    //折叠状态
                    if (mNewsFragment == null) return;
                    mNewsFragment.getIvBack().setVisibility(View.VISIBLE);
                    mNewsFragment.moveNavigation(true);
                    hideBackImg(false);
                    mLlTopTitle.setVisibility(View.INVISIBLE);
                    isAnimShow = true;
                }
            }
        });
    }

    private void hideBackImg(boolean hide) {
        ObjectAnimator animator;
        if (hide) {
            animator = ObjectAnimator.ofFloat(mNewsFragment.getIvBack(), "alpha", 1, 0);
            animator.setDuration(200);
        } else {
            animator = ObjectAnimator.ofFloat(mNewsFragment.getIvBack(), "alpha", 0, 1);
            animator.setDuration(500);
        }

        animator.start();
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animator.cancel();
                if (hide) {
                    mLlTopTitle.setVisibility(View.VISIBLE);
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



    @Override
    protected void loadData() {

    }

    private void showNews() {
        hideToolBar();
        mNewsFragment = NewsFragment.getNewsFragment("white");
        replaceFragment(R.id.fragment_container, mNewsFragment, false);
    }

    private void showWeb() {
        setLeftTitle(mTitle);
        mLlTopTitle.setVisibility(GONE);
        replaceFragment(R.id.fragment_container, CleanFinishWebFragment.getInstance(), false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (Jzvd.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        Jzvd.releaseAllVideos();
        super.onPause();
    }
}
