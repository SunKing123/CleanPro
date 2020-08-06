package com.xiaoniu.cleanking.ui.main.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jess.arms.utils.DeviceUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.SimpleFragment;
import com.xiaoniu.cleanking.constant.RouteConstants;
import com.xiaoniu.cleanking.midas.AdRequestParams;
import com.xiaoniu.cleanking.midas.MidasConstants;
import com.xiaoniu.cleanking.midas.MidasRequesCenter;
import com.xiaoniu.cleanking.ui.main.activity.PhoneAccessActivity;
import com.xiaoniu.cleanking.ui.main.activity.PhoneThinActivity;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.newclean.util.StartFinishActivityUtil;
import com.xiaoniu.cleanking.ui.tool.notify.event.FinishCleanFinishActivityEvent;
import com.xiaoniu.cleanking.ui.tool.notify.manager.NotifyCleanManager;
import com.xiaoniu.cleanking.ui.tool.qq.activity.QQCleanHomeActivity;
import com.xiaoniu.cleanking.ui.tool.qq.util.QQUtil;
import com.xiaoniu.cleanking.ui.tool.wechat.activity.WechatCleanHomeActivity;
import com.xiaoniu.cleanking.utils.AndroidUtil;
import com.xiaoniu.cleanking.utils.CleanAllFileScanUtil;
import com.xiaoniu.cleanking.utils.FileQueryUtils;
import com.xiaoniu.cleanking.utils.NumberUtils;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.cleanking.widget.CircleProgressView;
import com.xiaoniu.common.utils.FileUtils;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.utils.ToastUtils;
import com.xiaoniu.statistic.NiuDataAPI;
import com.xnad.sdk.ad.entity.AdInfo;
import com.xnad.sdk.ad.listener.AbsAdCallBack;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
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
    @BindView(R.id.flayout_bottom_ad)
    FrameLayout frameBottomLayout;
    @BindView(R.id.ll_top_layout)
    LinearLayout llTopLayout;

    private int mNotifySize; //通知条数
    private int mPowerSize; //耗电应用数
    private int mRamScale = 20; //使用内存占总RAM的比例
    // private AdManager mAdManager;

    private static final String TAG = "GeekSdk";

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
//        mToolCircleProgress.startAnimProgress(34, 13700);
        //监听进度条进度
        mToolCircleProgress.setOnAnimProgressListener(progress -> {
            if (mTvToolPercentNum != null)
                mTvToolPercentNum.setText("" + progress + "%");
        });
        llTopLayout.setPadding(0, DeviceUtils.getStatusBarHeight(mContext), 0, 0);
        getAccessListBelow();
    }

    @SuppressLint({"CheckResult", "DefaultLocale", "SetTextI18n"})
    private void setData() {
        Observable.create((ObservableOnSubscribe<String[]>) e -> e.onNext(new String[]{FileUtils.getFreeSpace(), FileUtils.getTotalSpace()})).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(strings -> {
                    if (mTvPhoneSpaceState == null) return;
                    //String数组第一个是剩余存储量，第二个是总存储量
                    mTvPhoneSpaceState.setText("已用：" + String.format("%.1f", (Double.valueOf(strings[1]) - Double.valueOf(strings[0]))) + "GB/" + String.format("%.1f", Double.valueOf(strings[1])) + "GB");
                    int spaceProgress = (int) ((NumberUtils.getFloat(strings[1]) - NumberUtils.getFloat(strings[0])) * 100 / NumberUtils.getFloat(strings[1]));
                    mToolCircleProgress.startAnimProgress(spaceProgress, 700);
                    if ((Double.valueOf(strings[1]) - Double.valueOf(strings[0])) / Double.valueOf(strings[1]) > 0.75) {
                        //手机内存不足
                        mTvPhoneSpace.setText(R.string.tool_phone_memory_full);
                    } else {
                        //手机内存充足
                        mTvPhoneSpace.setText(R.string.tool_phone_memory_empty);
                    }
                });
        SharedPreferences sp = mContext.getSharedPreferences(SpCacheConfig.CACHES_NAME_WXQQ_CACHE, Context.MODE_PRIVATE);
        long qqCatheSize = sp.getLong(SpCacheConfig.QQ_CACHE_SIZE, 0L);
        long wxCatheSize = sp.getLong(SpCacheConfig.WX_CACHE_SIZE, 0L);
        if (wxCatheSize > 0) {
            mTvChatTitle.setVisibility(View.GONE);
            mTvDefChatTitle.setVisibility(View.GONE);

            mTvChatGbTitle.setVisibility(View.VISIBLE);
            mTvChatSubTitle.setVisibility(View.VISIBLE);

            mTvChatSubTitle.setText(CleanAllFileScanUtil.byte2FitSizeTwo(wxCatheSize, mTvDefChatSubTitleGb));
            mTvDefChatSubTitleGb.setVisibility(View.VISIBLE);
        } else {
            mTvChatTitle.setVisibility(View.VISIBLE);
            mTvDefChatTitle.setVisibility(View.VISIBLE);

            mTvChatGbTitle.setVisibility(View.GONE);
            mTvChatSubTitle.setVisibility(View.GONE);
            mTvDefChatSubTitleGb.setVisibility(View.GONE);
        }
        if (qqCatheSize > 0) {
            mTvQqTitle.setVisibility(View.GONE);
            mTvDefQqTitle.setVisibility(View.GONE);

            mTvQqGbTitle.setVisibility(View.VISIBLE);
            mTvQqSubTitle.setVisibility(View.VISIBLE);
            mTvQqSubTitle.setText(CleanAllFileScanUtil.byte2FitSizeTwo(qqCatheSize, mTvDefQqSubTitleGb));
            mTvDefQqSubTitleGb.setVisibility(View.VISIBLE);
        } else {
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
        mNotifySize = NotifyCleanManager.getInstance().getAllNotifications().size();
        mPowerSize = new FileQueryUtils().getRunningProcess().size();
    }

    @OnClick({R.id.rl_chat, R.id.rl_qq, R.id.ll_phone_speed, R.id.text_cooling, R.id.text_phone_thin, R.id.ll_notification_clear})
    public void onCoolingViewClicked(View view) {
        int ids = view.getId();
        if (ids == R.id.rl_chat) {
            if (!AndroidUtil.isInstallWeiXin(mActivity)) {
                ToastUtils.showShort(R.string.tool_no_install_chat);
                return;
            }
//            ADUtilsKt.preloadingSplashAd(getActivity(), PositionId.AD_FINISH_BEFOR);
            StatisticsUtils.trackClick("wechat_cleaning_click", "微信专清点击", AppHolder.getInstance().getSourcePageId(), "clean_up_toolbox_page");
            AppHolder.getInstance().setOtherSourcePageId(SpCacheConfig.WETCHAT_CLEAN);

            if (PreferenceUtil.getWeChatCleanTime()) {
                // 每次清理间隔 至少3秒
                startActivity(WechatCleanHomeActivity.class);
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.tool_chat_clear));
                bundle.putString("num", "");
                bundle.putString("unit", "");
                StartFinishActivityUtil.Companion.gotoFinish(getActivity(), bundle);
            }
        } else if (ids == R.id.rl_qq) {


            if (!AndroidUtil.isInstallQQ(mActivity)) {
                ToastUtils.showShort(R.string.tool_no_install_qq);
                return;
            }
//            ADUtilsKt.preloadingSplashAd(getActivity(), PositionId.AD_FINISH_BEFOR);
            if (QQUtil.audioList != null)
                QQUtil.audioList.clear();
            if (QQUtil.fileList != null)
                QQUtil.fileList.clear();
            startActivity(QQCleanHomeActivity.class);
            StatisticsUtils.trackClick("qq_cleaning_click", "QQ专清点击", AppHolder.getInstance().getSourcePageId(), "clean_up_toolbox_page");
        } else if (ids == R.id.ll_phone_speed) {
            AppHolder.getInstance().setOtherSourcePageId(SpCacheConfig.ONKEY);
            StatisticsUtils.trackClick("Mobile_phone_acceleration_click", "手机加速点击", AppHolder.getInstance().getSourcePageId(), "clean_up_toolbox_page");
            //保存本次清理完成时间 保证每次清理时间间隔为3分钟
            if (!PreferenceUtil.getCleanTime()) {
                EventBus.getDefault().post(new FinishCleanFinishActivityEvent());
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.tool_one_key_speed));
                bundle.putString("num", "");
                bundle.putString("unit", "");
                StartFinishActivityUtil.Companion.gotoFinish(getActivity(), bundle);
            } else {
                Bundle bundle = new Bundle();
                bundle.putString(SpCacheConfig.ITEM_TITLE_NAME, getString(R.string.tool_one_key_speed));
                startActivity(PhoneAccessActivity.class, bundle);
            }
        } else if (ids == R.id.text_cooling) {
            //手机降温
//            ADUtilsKt.preloadingSplashAd(getActivity(), PositionId.AD_FINISH_BEFOR);
            AppHolder.getInstance().setOtherSourcePageId(SpCacheConfig.PHONE_COOLING);
            StatisticsUtils.trackClick("detecting_mobile_temperature_click", "手机降温点击", AppHolder.getInstance().getSourcePageId(), "clean_up_toolbox_page");
            // 添加每次降温时间间隔至少3分钟
            if (PreferenceUtil.getCoolingCleanTime()) {
                startActivity(RouteConstants.PHONE_COOLING_ACTIVITY);
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.tool_phone_temperature_low));
                bundle.putString("num", "");
                bundle.putString("unit", "");
                StartFinishActivityUtil.Companion.gotoFinish(getActivity(), bundle);
            }
        } else if (ids == R.id.text_phone_thin) {
            Intent intent = new Intent(getActivity(), PhoneThinActivity.class);
            intent.putExtra(SpCacheConfig.ITEM_TITLE_NAME, getString(R.string.tool_phone_thin));
            startActivity(intent);
            StatisticsUtils.trackClick("slim_scan_page_on_phone_click", "视频专清点击", AppHolder.getInstance().getSourcePageId(), "clean_up_toolbox_page");
        } else if (ids == R.id.ll_notification_clear) {
//            ADUtilsKt.preloadingSplashAd(getActivity(), PositionId.AD_FINISH_BEFOR);
            //手机清理
            String permissionsHint = "需要打开文件读写权限";
            String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE};
            new RxPermissions(getActivity()).request(permissions).subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(Boolean aBoolean) throws Exception {
                    if (aBoolean) {//开始更新
                        //手机清理
                        AppHolder.getInstance().setOtherSourcePageId(SpCacheConfig.PHONE_CLEAN);
                        StatisticsUtils.trackClick("cell_phone_clean_click", "\"手机清理\"点击", AppHolder.getInstance().getSourcePageId(), "acceleration_page");
                        startActivity(RouteConstants.CLEAN_BIG_FILE_ACTIVITY);
                    } else {
                        ToastUtils.showShort(permissionsHint);
                    }
                }
            });
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                StatusBarCompat.setStatusBarColor(getActivity(), getResources().getColor(R.color.color_27D599), true);
//            } else {
//                StatusBarCompat.setStatusBarColor(getActivity(), getResources().getColor(R.color.color_27D599), false);
//            }
            setData();
            addBottomAdView();
        }
        if (hidden) {
            NiuDataAPI.onPageEnd("clean-up_toolbox_view_page", "工具页面浏览");
        } else {
            NiuDataAPI.onPageStart("clean-up_toolbox_view_page", "工具页面浏览");
        }
    }

    /**
     * 获取到可以加速的应用名单Android O以下的获取最近使用情况
     */
    @SuppressLint("CheckResult")
    public void getAccessListBelow() {
//        mView.showLoadingDialog();
        Observable.create((ObservableOnSubscribe<ArrayList<FirstJunkInfo>>) e -> {
            //获取到可以加速的应用名单
            FileQueryUtils mFileQueryUtils = new FileQueryUtils();
            //文件加载进度回调
            mFileQueryUtils.setScanFileListener(new FileQueryUtils.ScanFileListener() {
                @Override
                public void currentNumber() {

                }

                @Override
                public void increaseSize(long p0) {

                }

                @Override
                public void scanFile(String p0) {

                }


            });
            ArrayList<FirstJunkInfo> listInfo = mFileQueryUtils.getRunningProcess();
            if (listInfo == null) {
                listInfo = new ArrayList<>();
            }
            e.onNext(listInfo);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(strings -> {
                    if (mView == null) return;
                    getAccessListBelowSize(strings);
                });
    }

    //低于Android O
    public void getAccessListBelowSize(ArrayList<FirstJunkInfo> listInfo) {
        if (listInfo == null || listInfo.size() <= 0) return;
        mRamScale = new FileQueryUtils().computeTotalSize(listInfo);
    }


    /**
     * 底部广告样式--
     */
    private void addBottomAdView() {
        if (null == getActivity() || !AppHolder.getInstance().checkAdSwitch(PositionId.KEY_PAGE_ACCELERATE))
            return;
        StatisticsUtils.customADRequest("ad_request", "广告请求", "1", " ", " ", "all_ad_request", "acceleration_page", "acceleration_page");
        AdRequestParams params = new AdRequestParams.Builder().setAdId(MidasConstants.SPEED_BOTTOM_ID)
                .setActivity(mActivity).setViewContainer(frameBottomLayout).setViewWidthOffset(24).build();
        MidasRequesCenter.requestAd(params, new AbsAdCallBack() {
            @Override
            public void onReadyToShow(AdInfo adInfo) {
                super.onReadyToShow(adInfo);
                adInfo.getAdParameter().getViewContainer().setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdError(AdInfo adInfo, int i, String s) {
                super.onAdError(adInfo, i, s);
            }

            @Override
            public void onShowError(int i, String s) {
                super.onShowError(i, s);
            }
        });

    }

}
