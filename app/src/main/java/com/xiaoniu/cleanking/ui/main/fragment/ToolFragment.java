package com.xiaoniu.cleanking.ui.main.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.RouteConstants;
import com.xiaoniu.cleanking.base.SimpleFragment;
import com.xiaoniu.cleanking.ui.main.activity.PhoneAccessActivity;
import com.xiaoniu.cleanking.ui.main.activity.PhoneThinActivity;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.tool.qq.activity.QQCleanHomeActivity;
import com.xiaoniu.cleanking.ui.tool.qq.util.QQUtil;
import com.xiaoniu.cleanking.ui.tool.wechat.activity.WechatCleanHomeActivity;
import com.xiaoniu.cleanking.utils.AndroidUtil;
import com.xiaoniu.cleanking.utils.CleanAllFileScanUtil;
import com.xiaoniu.cleanking.utils.NumberUtils;
import com.xiaoniu.cleanking.widget.CircleProgressView;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;
import com.xiaoniu.common.utils.FileUtils;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.utils.ToastUtils;
import com.xiaoniu.statistic.NiuDataAPI;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ToolFragment extends SimpleFragment {

    @BindView(R.id.tool_circle_progress)
    CircleProgressView mToolCircleProgress;
    @BindView(R.id.tv_tool_percent_num)
    TextView mTvToolPercentNum;
    @BindView(R.id.tv_phone_space_state)
    TextView mTvPhoneSpaceState;

    @BindView(R.id.tv_chat_title)
    TextView mTvChatTitle;
    @BindView(R.id.tv_qq_title)
    TextView mTvQqTitle;
    @BindView(R.id.tv_chat_gb_title)
    TextView mTvChatGbTitle;
    @BindView(R.id.tv_qq_gb_title)
    TextView mTvQqGbTitle;
    @BindView(R.id.tv_def_chat_title)
    TextView mTvDefChatTitle;
    @BindView(R.id.tv_def_qq_title)
    TextView mTvDefQqTitle;
    @BindView(R.id.tv_chat_subtitle)
    TextView mTvChatSubTitle;
    @BindView(R.id.tv_qq_subtitle)
    TextView mTvQqSubTitle;
    @BindView(R.id.tv_chat_subtitle_gb)
    TextView mTvDefChatSubTitleGb;
    @BindView(R.id.tv_qq_subtitle_gb)
    TextView mTvDefQqSubTitleGb;
    @BindView(R.id.tv_phone_space)
    TextView mTvPhoneSpace;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tool;
    }

    @Override
    protected void initView() {
        mToolCircleProgress.startAnimProgress(34, 13700);
        setData();
        //监听进度条进度
        mToolCircleProgress.setOnAnimProgressListener(progress -> {
            if (mTvToolPercentNum != null)
                mTvToolPercentNum.setText("" + progress + "%");
        });
    }

    @SuppressLint({"CheckResult", "DefaultLocale", "SetTextI18n"})
    private void setData() {
        Observable.create((ObservableOnSubscribe<String[]>) e -> e.onNext(new String[]{FileUtils.getFreeSpace(), FileUtils.getTotalSpace()})).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(strings -> {
                    //String数组第一个是剩余存储量，第二个是总存储量
                    mTvPhoneSpaceState.setText("已用：" + String.format("%.1f", (Double.valueOf(strings[1]) - Double.valueOf(strings[0])))+ "GB/" +  String.format("%.1f",  Double.valueOf(strings[1]))+ "GB");
                    int spaceProgress = (int) ((NumberUtils.getFloat(strings[1]) - NumberUtils.getFloat(strings[0])) * 100 / NumberUtils.getFloat(strings[1]));
                    mToolCircleProgress.startAnimProgress(spaceProgress, 700);
                    if ((Double.valueOf(strings[1]) - Double.valueOf(strings[0])) / Double.valueOf(strings[1]) > 0.75){
                        //手机内存不足
                        mTvPhoneSpace.setText(R.string.tool_phone_memory_full);
                    }else {
                        //手机内存充足
                        mTvPhoneSpace.setText(R.string.tool_phone_memory_empty);
                    }
                });
        SharedPreferences sp = mContext.getSharedPreferences(SpCacheConfig.CACHES_NAME_WXQQ_CACHE, Context.MODE_PRIVATE);
        long qqCatheSize = sp.getLong(SpCacheConfig.QQ_CACHE_SIZE,0L);
        long wxCatheSize = sp.getLong(SpCacheConfig.WX_CACHE_SIZE,0L);
        if (wxCatheSize > 0){
            mTvChatTitle.setVisibility(View.GONE);
            mTvDefChatTitle.setVisibility(View.GONE);

            mTvChatGbTitle.setVisibility(View.VISIBLE);
            mTvChatSubTitle.setVisibility(View.VISIBLE);

            mTvChatSubTitle.setText(CleanAllFileScanUtil.byte2FitSizeTwo(wxCatheSize,mTvDefChatSubTitleGb));
            mTvDefChatSubTitleGb.setVisibility(View.VISIBLE);
        }else {
            mTvChatTitle.setVisibility(View.VISIBLE);
            mTvDefChatTitle.setVisibility(View.VISIBLE);

            mTvChatGbTitle.setVisibility(View.GONE);
            mTvChatSubTitle.setVisibility(View.GONE);
            mTvDefChatSubTitleGb.setVisibility(View.GONE);
        }
        if (qqCatheSize >0){
            mTvQqTitle.setVisibility(View.GONE);
            mTvDefQqTitle.setVisibility(View.GONE);

            mTvQqGbTitle.setVisibility(View.VISIBLE);
            mTvQqSubTitle.setVisibility(View.VISIBLE);
            mTvQqSubTitle.setText(CleanAllFileScanUtil.byte2FitSizeTwo(qqCatheSize,mTvDefQqSubTitleGb));
            mTvDefQqSubTitleGb.setVisibility(View.VISIBLE);
        }else {
            mTvQqTitle.setVisibility(View.VISIBLE);
            mTvDefQqTitle.setVisibility(View.VISIBLE);

            mTvQqGbTitle.setVisibility(View.GONE);
            mTvQqSubTitle.setVisibility(View.GONE);
            mTvDefQqSubTitleGb.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        setData();
        super.onResume();
    }

    @OnClick({R.id.rl_chat, R.id.rl_qq, R.id.ll_phone_speed, R.id.text_cooling, R.id.text_phone_thin})
    public void onCoolingViewClicked(View view) {
        int ids = view.getId();
        if (ids == R.id.rl_chat) {
            if (!AndroidUtil.isAppInstalled(SpCacheConfig.CHAT_PACKAGE)) {
                ToastUtils.showShort(R.string.tool_no_install_chat);
                return;
            }
            startActivity(WechatCleanHomeActivity.class);
            StatisticsUtils.trackClick("wechat_cleaning_click", "微信专清点击", "tool_page", "clean_up_toolbox_page");
        } else if (ids == R.id.rl_qq) {
            if (!AndroidUtil.isAppInstalled(SpCacheConfig.QQ_PACKAGE)) {
                ToastUtils.showShort(R.string.tool_no_install_qq);
                return;
            }
            if (QQUtil.audioList != null)
                QQUtil.audioList.clear();
            if (QQUtil.fileList != null)
                QQUtil.fileList.clear();
            startActivity(QQCleanHomeActivity.class);
            StatisticsUtils.trackClick("qq_cleaning_click", "QQ专清点击", "tool_page", "clean_up_toolbox_page");
        } else if (ids == R.id.ll_phone_speed) {
            Bundle bundle = new Bundle();
            bundle.putString(SpCacheConfig.ITEM_TITLE_NAME, getString(R.string.tool_phone_speed));
            startActivity(PhoneAccessActivity.class,bundle);
            StatisticsUtils.trackClick("Mobile_phone_acceleration_click", "手机加速点击", "tool_page", "clean_up_toolbox_page");
        } else if (ids == R.id.text_cooling) {
            //手机降温
            startActivity(RouteConstants.PHONE_COOLING_ACTIVITY);
            StatisticsUtils.trackClick("detecting_mobile_temperature_click", "手机降温点击", "tool_page", "clean_up_toolbox_page");
        } else if (ids == R.id.text_phone_thin) {
            Intent intent = new Intent(getActivity(), PhoneThinActivity.class);
            intent.putExtra(SpCacheConfig.ITEM_TITLE_NAME,getString(R.string.tool_phone_thin));
            startActivity(intent);
            StatisticsUtils.trackClick("slim_scan_page_on_phone_click", "视频专清点击", "tool_page", "clean_up_toolbox_page");
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                StatusBarCompat.setStatusBarColor(getActivity(), getResources().getColor(R.color.color_4690FD), true);
            } else {
                StatusBarCompat.setStatusBarColor(getActivity(), getResources().getColor(R.color.color_4690FD), false);
            }
            setData();
        }
        if (hidden) {
            NiuDataAPI.onPageEnd("clean-up_toolbox_view_page", "工具页面浏览");
        } else {
            NiuDataAPI.onPageStart("clean-up_toolbox_view_page", "工具页面浏览");
        }
    }
}
