package com.xiaoniu.cleanking.ui.main.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.RouteConstants;
import com.xiaoniu.cleanking.base.SimpleFragment;
import com.xiaoniu.cleanking.ui.main.activity.PhoneAccessActivity;
import com.xiaoniu.cleanking.ui.main.activity.PhoneThinActivity;
import com.xiaoniu.cleanking.ui.tool.qq.activity.QQCleanHomeActivity;
import com.xiaoniu.cleanking.ui.tool.qq.util.QQUtil;
import com.xiaoniu.cleanking.ui.tool.wechat.activity.WechatCleanHomeActivity;
import com.xiaoniu.cleanking.utils.StatisticsUtils;
import com.xiaoniu.cleanking.widget.CircleProgressView;
import com.xiaoniu.statistic.NiuDataAPI;

import butterknife.BindView;
import butterknife.OnClick;

public class ToolFragment extends SimpleFragment {

    @BindView(R.id.tv_chat_subtitle)
    TextView mTvChatSubtitle;//微信清理
    @BindView(R.id.tv_qq_subtitle)
    TextView mTvQqSubtitle;  //qq清理
    @BindView(R.id.tool_circle_progress)
    CircleProgressView mToolCircleProgress;
    @BindView(R.id.tv_tool_percent_num)
    TextView mTvToolPercentNum;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tool;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            NiuDataAPI.onPageEnd("clean-up_toolbox_view_page", "工具页面浏览");
        } else {
            NiuDataAPI.onPageStart("clean-up_toolbox_view_page", "工具页面浏览");
        }
    }

    @Override
    protected void initView() {
        mToolCircleProgress.startAnimProgress(34, 700);
        //监听进度条进度
        mToolCircleProgress.setOnAnimProgressListener(progress -> mTvToolPercentNum.setText("" + progress + "%"));
    }


    @OnClick({R.id.rl_chat, R.id.rl_qq, R.id.ll_phone_speed, R.id.text_cooling, R.id.text_phone_thin})
    public void onCoolingViewClicked(View view) {
        int ids = view.getId();
        if (ids == R.id.rl_chat) {
            startActivity(WechatCleanHomeActivity.class);
            StatisticsUtils.trackClick("wechat_cleaning_click", "微信专清点击", "home_page", "clean_up_toolbox_page");
        } else if (ids == R.id.rl_qq) {
            if (QQUtil.audioList != null)
                QQUtil.audioList.clear();
            if (QQUtil.fileList != null)
                QQUtil.fileList.clear();
            startActivity(QQCleanHomeActivity.class);
            StatisticsUtils.trackClick("qq_cleaning_click", "QQ专清点击", "home_page", "clean_up_toolbox_page");
        } else if (ids == R.id.ll_phone_speed) {
            startActivity(PhoneAccessActivity.class);
            StatisticsUtils.trackClick("Mobile_phone_acceleration_click", "手机加速点击", "home_page", "clean_up_toolbox_page");
        } else if (ids == R.id.text_cooling) {
            //手机降温
            startActivity(RouteConstants.PHONE_COOLING_ACTIVITY);
            StatisticsUtils.trackClick("detecting_mobile_temperature_click", "手机降温点击", "home_page", "clean_up_toolbox_page");
        } else if (ids == R.id.text_phone_thin) {
            startActivity(new Intent(getContext(), PhoneThinActivity.class));
            StatisticsUtils.trackClick("slim_scan_page_on_phone_click", "手机瘦身点击", "home_page", "clean_up_toolbox_page");
        }
    }

}
