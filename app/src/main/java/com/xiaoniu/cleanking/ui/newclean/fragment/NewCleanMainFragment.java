package com.xiaoniu.cleanking.ui.newclean.fragment;

import android.animation.Animator;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.tencent.tinker.loader.shareutil.SharePatchFileUtil;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.app.Constant;
import com.xiaoniu.cleanking.app.RouteConstants;
import com.xiaoniu.cleanking.app.injector.component.FragmentComponent;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.BaseFragment;
import com.xiaoniu.cleanking.ui.main.activity.AgentWebViewActivity;
import com.xiaoniu.cleanking.ui.main.activity.FileManagerHomeActivity;
import com.xiaoniu.cleanking.ui.main.activity.MainActivity;
import com.xiaoniu.cleanking.ui.main.activity.NewsActivity;
import com.xiaoniu.cleanking.ui.main.activity.PhoneAccessActivity;
import com.xiaoniu.cleanking.ui.main.activity.PhoneSuperPowerActivity;
import com.xiaoniu.cleanking.ui.main.activity.PhoneThinActivity;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.bean.ImageAdEntity;
import com.xiaoniu.cleanking.ui.main.bean.InteractionSwitchList;
import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.event.CleanEvent;
import com.xiaoniu.cleanking.ui.main.event.LifecycEvent;
import com.xiaoniu.cleanking.ui.main.widget.SPUtil;
import com.xiaoniu.cleanking.ui.newclean.activity.CleanFinishAdvertisementActivity;
import com.xiaoniu.cleanking.ui.newclean.activity.NewCleanFinishActivity;
import com.xiaoniu.cleanking.ui.newclean.activity.NowCleanActivity;
import com.xiaoniu.cleanking.ui.newclean.presenter.NewCleanMainPresenter;
import com.xiaoniu.cleanking.ui.tool.notify.event.FinishCleanFinishActivityEvent;
import com.xiaoniu.cleanking.ui.tool.notify.event.FromHomeCleanFinishEvent;
import com.xiaoniu.cleanking.ui.tool.notify.event.InternalStoragePremEvent;
import com.xiaoniu.cleanking.ui.tool.notify.manager.NotifyCleanManager;
import com.xiaoniu.cleanking.ui.tool.notify.utils.NotifyUtils;
import com.xiaoniu.cleanking.ui.tool.qq.activity.QQCleanHomeActivity;
import com.xiaoniu.cleanking.ui.tool.qq.util.QQUtil;
import com.xiaoniu.cleanking.ui.tool.wechat.activity.WechatCleanHomeActivity;
import com.xiaoniu.cleanking.utils.AndroidUtil;
import com.xiaoniu.cleanking.utils.ExtraConstant;
import com.xiaoniu.cleanking.utils.FileQueryUtils;
import com.xiaoniu.cleanking.utils.GlideUtils;
import com.xiaoniu.cleanking.utils.ImageUtil;
import com.xiaoniu.cleanking.utils.LogUtils;
import com.xiaoniu.cleanking.utils.NumberUtils;
import com.xiaoniu.cleanking.utils.PermissionUtils;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static android.view.View.VISIBLE;

/**
 * 1.2.1 新版本清理主页
 */
public class NewCleanMainFragment extends BaseFragment<NewCleanMainPresenter> {

    private long firstTime;
    @BindView(R.id.tv_clean_type)
    TextView mTvCleanType;

    @BindView(R.id.line_shd)
    LinearLayout lineShd;
    @BindView(R.id.text_wjgl)
    LinearLayout textWjgl;
    @BindView(R.id.view_phone_thin)
    View viewPhoneThin;
    @BindView(R.id.view_qq_clean)
    View viewQqClean;
    @BindView(R.id.view_news)
    View viewNews;
    @BindView(R.id.tv_acc)
    TextView mAccTv;
    @BindView(R.id.tv_noti_clear)
    TextView mNotiClearTv;
    @BindView(R.id.tv_electricity)
    TextView mElectricityTv;
    @BindView(R.id.iv_acc)
    ImageView mAccIv;
    @BindView(R.id.iv_noti_clear)
    ImageView mNotiClearIv;
    @BindView(R.id.iv_electricity)
    ImageView mElectricityIv;
    @BindView(R.id.iv_acc_g)
    ImageView mAccFinishIv;
    @BindView(R.id.iv_noti_g)
    ImageView mNotiClearFinishIv;
    @BindView(R.id.iv_electricity_g)
    ImageView mElectricityFinishIv;
    @BindView(R.id.iv_interaction)
    ImageView mInteractionIv;
    @BindView(R.id.image_ad_bottom_first)
    ImageView mImageFirstAd;
    @BindView(R.id.image_ad_bottom_second)
    ImageView mImageSecondAd;
    @BindView(R.id.view_lottie_home)
    LottieAnimationView mLottieHomeView;
    @BindView(R.id.tv_now_clean)
    ImageView tvNowClean;

    private int mNotifySize; //通知条数
    private int mPowerSize; //耗电应用数
    private int mRamScale; //使用内存占总RAM的比例
    private int mInteractionPoistion; //互动式广告position、
    private int mShowCount;


    private List<InteractionSwitchList.DataBean.SwitchActiveLineDTOList> mInteractionList;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_new_clean_main;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void initView() {
        tvNowClean.setVisibility(View.VISIBLE);
        EventBus.getDefault().register(this);
        showHomeLottieView();
        mPresenter.requestBottomAd();
        mPresenter.getInteractionSwitch();
        if (PreferenceUtil.isFirstForHomeIcon()) {
            PreferenceUtil.saveFirstForHomeIcon(false);
        } else {
            if (!PreferenceUtil.getCleanTime()) {
                mAccFinishIv.setVisibility(View.VISIBLE);
                GlideUtils.loadDrawble(getActivity(), R.drawable.icon_yjjs, mAccIv);
                mAccTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_323232));
                mAccTv.setText(getString(R.string.internal_storage_scale, NumberUtils.mathRandom(15, 30)) + "%");
            } else {
                mShowCount++;
                if (!PermissionUtils.isUsageAccessAllowed(getActivity())) {
                    GlideUtils.loadDrawble(getActivity(), R.drawable.icon_yjjs_o, mAccIv);
                    mAccTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_FFAC01));
                    mAccTv.setText(getString(R.string.tool_one_key_speed));
                } else {
                    GlideUtils.loadDrawble(getActivity(), R.drawable.icon_quicken, mAccIv);
                    mAccTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_FF4545));
                    mAccTv.setText(getString(R.string.internal_storage_scale, NumberUtils.mathRandom(70, 85)) + "%");
                }
            }

            if (!NotifyUtils.isNotificationListenerEnabled()) {
                mShowCount++;
                GlideUtils.loadDrawble(getActivity(), R.drawable.icon_home_qq_o, mNotiClearIv);
                mNotiClearTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_FFAC01));
                mNotiClearTv.setText(R.string.find_harass_notify);
            } else {
                if (!PreferenceUtil.getNotificationCleanTime()) {
                    mNotiClearFinishIv.setVisibility(View.VISIBLE);
                    GlideUtils.loadDrawble(getActivity(), R.drawable.icon_home_qq, mNotiClearIv);
                    mNotiClearTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_323232));
                    mNotiClearTv.setText(R.string.finished_clean_notify_hint);
                } else if (NotifyCleanManager.getInstance().getAllNotifications().size() > 0) {
                    mShowCount++;
                    GlideUtils.loadDrawble(getActivity(), R.drawable.icon_notify, mNotiClearIv);
                    mNotiClearTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_FF4545));
                    mNotiClearTv.setText(getString(R.string.find_harass_notify_num, NotifyCleanManager.getInstance().getAllNotifications().size() + ""));
                }
            }

            if (mShowCount < 2 && AndroidUtil.getElectricityNum(getActivity()) <= 70) {
                if (!PreferenceUtil.getPowerCleanTime()) {
                    mElectricityFinishIv.setVisibility(View.VISIBLE);
                    GlideUtils.loadDrawble(getActivity(), R.drawable.icon_power, mElectricityIv);
                    mElectricityTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_323232));
                    if (TextUtils.isEmpty(PreferenceUtil.getLengthenAwaitTime())) {
                        mElectricityTv.setText(getString(R.string.lengthen_time, "40"));
                    } else {
                        mElectricityTv.setText(getString(R.string.lengthen_time, PreferenceUtil.getLengthenAwaitTime()));
                    }
                } else {
                    GlideUtils.loadDrawble(getActivity(), R.drawable.icon_power_gif, mElectricityIv);
                    mElectricityTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_FF4545));
                    mElectricityTv.setText(getString(R.string.power_consumption_num, NumberUtils.mathRandom(8, 15)));
                }
            }
        }
        //状态（0=隐藏，1=显示）
        String auditSwitch = SPUtil.getString(getActivity(), AppApplication.AuditSwitch, "1");
        if (TextUtils.equals(auditSwitch, "0")) {
            viewNews.setVisibility(View.GONE);
        } else {
            viewNews.setVisibility(VISIBLE);
        }
    }

    /**
     * 显示广告 position = 0 第一个 position = 1  第二个
     *
     * @param dataBean
     */
    public void showFirstAd(ImageAdEntity.DataBean dataBean, int position) {
        AppHolder.getInstance().setOtherSourcePageId(SpCacheConfig.BANNER);
        if (position == 0) {
            mImageFirstAd.setVisibility(VISIBLE);
            ImageUtil.display(dataBean.getImageUrl(), mImageFirstAd);
            clickDownload(mImageFirstAd, dataBean.getDownloadUrl(), position);
//            mTextBottomTitle.setVisibility(GONE);
        } else if (position == 1) {
            mImageSecondAd.setVisibility(VISIBLE);
            ImageUtil.display(dataBean.getImageUrl(), mImageSecondAd);
            clickDownload(mImageSecondAd, dataBean.getDownloadUrl(), position);
//            mTextBottomTitle.setVisibility(GONE);
        }
        StatisticsUtils.trackClickAD("ad_show", "\"广告展示曝光", AppHolder.getInstance().getSourcePageId(), "home_page_clean_up_page", String.valueOf(position));
    }

    /**
     * 获取互动式广告成功
     *
     * @param switchInfoList
     */
    public void getInteractionSwitchSuccess(InteractionSwitchList switchInfoList) {
        if (null == switchInfoList || null == switchInfoList.getData() || switchInfoList.getData().size() <= 0)
            return;
        if (switchInfoList.getData().get(0).isOpen()) {
            mInteractionList = switchInfoList.getData().get(0).getSwitchActiveLineDTOList();
            Glide.with(this).load(switchInfoList.getData().get(0).getSwitchActiveLineDTOList().get(0).getImgUrl()).into(mInteractionIv);
        }
    }

    /**
     * 互动式广告
     */
    @OnClick(R.id.iv_interaction)
    public void interactionClick() {
        if (mInteractionPoistion > 2) {
            mInteractionPoistion = 0;
        }
        StatisticsUtils.trackClick("Interaction_ad_click", "用户在首页点击互动式广告按钮", "clod_splash_page", "home_page");
        if (null != mInteractionList && mInteractionList.size() > 0) {

            if (mInteractionList.size() == 1) {
                startActivity(new Intent(getActivity(), AgentWebViewActivity.class)
                        .putExtra(ExtraConstant.WEB_URL, mInteractionList.get(0).getLinkUrl()));
            } else {
                startActivity(new Intent(getActivity(), AgentWebViewActivity.class)
                        .putExtra(ExtraConstant.WEB_URL, mInteractionList.get(mInteractionPoistion).getLinkUrl()));
            }
            mInteractionPoistion++;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onResume() {
        super.onResume();
        mPresenter.getSwitchInfoList();
        mPresenter.getAccessListBelow();
        mNotifySize = NotifyCleanManager.getInstance().getAllNotifications().size();
        mPowerSize = new FileQueryUtils().getRunningProcess().size();

        if (null != mInteractionList && mInteractionList.size() > 0) {
            if (mInteractionPoistion > 2) {
                mInteractionPoistion = 0;
            }
            if (mInteractionList.size() == 1) {
                GlideUtils.loadGif(getActivity(), mInteractionList.get(0).getImgUrl(), mInteractionIv, 10000);
            } else {
                GlideUtils.loadGif(getActivity(), mInteractionList.get(mInteractionPoistion).getImgUrl(), mInteractionIv, 10000);
            }
        }

        lineShd.setEnabled(true);
        textWjgl.setEnabled(true);
        viewPhoneThin.setEnabled(true);
        viewQqClean.setEnabled(true);
        viewNews.setEnabled(true);
//        EventBus.getDefault().post(new ResidentUpdateEvent(false));
    }

    /**
     * 清理完成回调
     * @param event
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Subscribe
    public void fromHomeCleanFinishEvent(FromHomeCleanFinishEvent event) {
        if (null == event || TextUtils.isEmpty(event.getTitle())) return;
        if (getString(R.string.tool_one_key_speed).contains(event.getTitle())) { //一键加速
            mShowCount--;
            mAccFinishIv.setVisibility(View.VISIBLE);
            GlideUtils.loadDrawble(getActivity(), R.drawable.icon_yjjs, mAccIv);
            mAccTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_323232));
            mAccTv.setText(getString(R.string.internal_storage_scale, NumberUtils.mathRandom(15, 30)) + "%");

            //通知栏清理
            if (!NotifyUtils.isNotificationListenerEnabled()) {
                mShowCount++;
                GlideUtils.loadDrawble(getActivity(), R.drawable.icon_home_qq_o, mNotiClearIv);
                mNotiClearTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_FFAC01));
                mNotiClearTv.setText(R.string.find_harass_notify);
            } else {
                if (!PreferenceUtil.isCleanNotifyUsed() && NotifyCleanManager.getInstance().getAllNotifications().size() > 0) {
                    mShowCount++;
                    GlideUtils.loadDrawble(getActivity(), R.drawable.icon_notify, mNotiClearIv);
                    mNotiClearTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_FF4545));
                    mNotiClearTv.setText(getString(R.string.find_harass_notify_num, NotifyCleanManager.getInstance().getAllNotifications().size() + ""));
                } else if (!PreferenceUtil.isCleanNotifyUsed() && NotifyCleanManager.getInstance().getAllNotifications().size() <= 0) {
                    GlideUtils.loadDrawble(getActivity(), R.drawable.icon_home_qq, mNotiClearIv);
                    mNotiClearTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_323232));
                    mNotiClearTv.setText(R.string.tool_notification_clean);
                } else if (NotifyCleanManager.getInstance().getAllNotifications().size() <= 0) {
                    mShowCount--;
                    mNotiClearFinishIv.setVisibility(View.VISIBLE);
                    GlideUtils.loadDrawble(getActivity(), R.drawable.icon_home_qq, mNotiClearIv);
                    mNotiClearTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_323232));
                    mNotiClearTv.setText(R.string.finished_clean_notify_hint);
                }
            }

            //超强省电
            if (AndroidUtil.getElectricityNum(getActivity()) <= 70) {
                if (!PreferenceUtil.isCleanPowerUsed()) {
                    mShowCount++;
                    GlideUtils.loadDrawble(getActivity(), R.drawable.icon_power_gif, mElectricityIv);
                    mElectricityTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_FF4545));
                    mElectricityTv.setText(getString(R.string.power_consumption_num, NumberUtils.mathRandom(8, 15)));
                } else {
                    mShowCount--;
                    mElectricityFinishIv.setVisibility(View.VISIBLE);
                    GlideUtils.loadDrawble(getActivity(), R.drawable.icon_power, mElectricityIv);
                    mElectricityTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_323232));
                    if (TextUtils.isEmpty(PreferenceUtil.getLengthenAwaitTime())) {
                        mElectricityTv.setText(getString(R.string.lengthen_time, "40"));
                    } else {
                        mElectricityTv.setText(getString(R.string.lengthen_time, PreferenceUtil.getLengthenAwaitTime()));
                    }
                }
            }
        } else if (getString(R.string.tool_notification_clean).contains(event.getTitle())) {//通知栏清理
            mShowCount--;
            mNotiClearFinishIv.setVisibility(View.VISIBLE);
            GlideUtils.loadDrawble(getActivity(), R.drawable.icon_home_qq, mNotiClearIv);
            mNotiClearTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_323232));
            mNotiClearTv.setText(R.string.finished_clean_notify_hint);

            //一键加速
            if (!PermissionUtils.isUsageAccessAllowed(getActivity())) {
                GlideUtils.loadDrawble(getActivity(), R.drawable.icon_yjjs_o, mAccIv);
                mAccTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_FFAC01));
                mAccTv.setText(getString(R.string.tool_one_key_speed));
            } else if (!PreferenceUtil.isCleanJiaSuUsed()) {
                GlideUtils.loadDrawble(getActivity(), R.drawable.icon_quicken, mAccIv);
                mAccTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_FF4545));
                mAccTv.setText(getString(R.string.internal_storage_scale, NumberUtils.mathRandom(70, 85)) + "%");
            }

            //超强省电
            if (!PermissionUtils.isUsageAccessAllowed(getActivity())) {
                mShowCount++;
                GlideUtils.loadDrawble(getActivity(), R.drawable.icon_power_o, mElectricityIv);
                mElectricityTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_FFAC01));
                mElectricityTv.setText(getString(R.string.tool_super_power_saving));
            } else if (AndroidUtil.getElectricityNum(getActivity()) <= 70) {
                if (!PreferenceUtil.isCleanPowerUsed()) {
                    mShowCount++;
                    GlideUtils.loadDrawble(getActivity(), R.drawable.icon_power_gif, mElectricityIv);
                    mElectricityTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_FF4545));
                    mElectricityTv.setText(getString(R.string.power_consumption_num, NumberUtils.mathRandom(8, 15)));
                } else {
                    mShowCount--;
                    mElectricityFinishIv.setVisibility(View.VISIBLE);
                    GlideUtils.loadDrawble(getActivity(), R.drawable.icon_power, mElectricityIv);
                    mElectricityTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_323232));
                    if (TextUtils.isEmpty(PreferenceUtil.getLengthenAwaitTime())) {
                        mElectricityTv.setText(getString(R.string.lengthen_time, "40"));
                    } else {
                        mElectricityTv.setText(getString(R.string.lengthen_time, PreferenceUtil.getLengthenAwaitTime()));
                    }
                }
            }

        } else if (getString(R.string.tool_super_power_saving).contains(event.getTitle())) { //超强省电
            mShowCount--;
            mElectricityFinishIv.setVisibility(View.VISIBLE);
            GlideUtils.loadDrawble(getActivity(), R.drawable.icon_power, mElectricityIv);
            mElectricityTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_323232));
            if (TextUtils.isEmpty(PreferenceUtil.getLengthenAwaitTime())) {
                mElectricityTv.setText(getString(R.string.lengthen_time, "40"));
            } else {
                mElectricityTv.setText(getString(R.string.lengthen_time, PreferenceUtil.getLengthenAwaitTime()));
            }

            //一键加速
            if (!PermissionUtils.isUsageAccessAllowed(getActivity())) {
                GlideUtils.loadDrawble(getActivity(), R.drawable.icon_yjjs_o, mAccIv);
                mAccTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_FFAC01));
                mAccTv.setText(getString(R.string.tool_one_key_speed));
            } else if (!PreferenceUtil.isCleanJiaSuUsed()) {
                GlideUtils.loadDrawble(getActivity(), R.drawable.icon_quicken, mAccIv);
                mAccTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_FF4545));
                mAccTv.setText(getString(R.string.internal_storage_scale, NumberUtils.mathRandom(70, 85)) + "%");
            }

            //通知栏清理
            if (!NotifyUtils.isNotificationListenerEnabled()) {
                mShowCount++;
                GlideUtils.loadDrawble(getActivity(), R.drawable.icon_home_qq_o, mNotiClearIv);
                mNotiClearTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_FFAC01));
                mNotiClearTv.setText(R.string.find_harass_notify);
            } else {
                if (!PreferenceUtil.isCleanNotifyUsed() && NotifyCleanManager.getInstance().getAllNotifications().size() > 0) {
                    mShowCount++;
                    GlideUtils.loadDrawble(getActivity(), R.drawable.icon_notify, mNotiClearIv);
                    mNotiClearTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_FF4545));
                    mNotiClearTv.setText(getString(R.string.find_harass_notify_num, NotifyCleanManager.getInstance().getAllNotifications().size() + ""));
                } else if (!PreferenceUtil.isCleanNotifyUsed() && NotifyCleanManager.getInstance().getAllNotifications().size() <= 0) {
                    GlideUtils.loadDrawble(getActivity(), R.drawable.icon_home_qq, mNotiClearIv);
                    mNotiClearTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_323232));
                    mNotiClearTv.setText(R.string.tool_notification_clean);
                } else if (NotifyCleanManager.getInstance().getAllNotifications().size() <= 0) {
                    mShowCount--;
                    mNotiClearFinishIv.setVisibility(View.VISIBLE);
                    GlideUtils.loadDrawble(getActivity(), R.drawable.icon_home_qq, mNotiClearIv);
                    mNotiClearTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_323232));
                    mNotiClearTv.setText(R.string.finished_clean_notify_hint);
                }
            }
        }

    }

    /**
     * 一键加速获取权限后通知首页一键加速状态改变
     */
    @Subscribe
    public void internalStoragePremEvent(InternalStoragePremEvent event) {
        GlideUtils.loadDrawble(getActivity(), R.drawable.icon_quicken, mAccIv);
        mAccTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_FF4545));
        mAccTv.setText(getString(R.string.internal_storage_scale, NumberUtils.mathRandom(70, 85)) + "%");
    }


    @Subscribe
    public void changeLifecyEvent(LifecycEvent lifecycEvent){
        if(lifecycEvent.isActivity()){
            tvNowClean.setVisibility(VISIBLE);
            mTvCleanType.setText(getString(R.string.tool_home_hint));
            mLottieHomeView.useHardwareAcceleration(true);
            mLottieHomeView.setAnimation("clean_home_top.json");
            mLottieHomeView.setImageAssetsFolder("images_home");
            mLottieHomeView.playAnimation();
            mLottieHomeView.setVisibility(VISIBLE);
        }
    }

    /**
     * 超强省电一键优化完成事件
     * <p>
     * //     * @param event
     */
   /* @Subscribe
    public void cleanPowerEvent(CleanPowerEvent event) {
        mElectricityFinishIv.setVisibility(View.VISIBLE);
        mElectricityIv.setImageResource(R.drawable.icon_power);
        mElectricityTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_323232));
        if (TextUtils.isEmpty(PreferenceUtil.getLengthenAwaitTime())) {
            mElectricityTv.setText(getString(R.string.lengthen_time, "40"));
        } else {
            mElectricityTv.setText(getString(R.string.lengthen_time, PreferenceUtil.getLengthenAwaitTime()));
        }
        PreferenceUtil.saveLengthenAwaitTime(event.getHour());
    }*/
    @Override
    protected void inject(FragmentComponent fragmentComponent) {
        fragmentComponent.inject(this);
    }

    @Override
    public void netError() {

    }

    /**
     * 立即清理
     */
    @OnClick(R.id.tv_now_clean)
    public void nowClean() {
        StatisticsUtils.trackClick("home_page_clean_click", "用户在首页点击【立即清理】", "home_page", "home_page");
        //PreferenceUtil.getNowCleanTime() || TextUtils.isEmpty(Constant.APP_IS_LIVE
        if (true) {
            startActivity(NowCleanActivity.class);
        } else {
            AppHolder.getInstance().setCleanFinishSourcePageId("home_page");
            boolean isOpen = false;
            //solve umeng error --> SwitchInfoList.getData()' on a null object reference
            if (null != AppHolder.getInstance().getSwitchInfoList() && null != AppHolder.getInstance().getSwitchInfoList().getData()
                    && AppHolder.getInstance().getSwitchInfoList().getData().size() > 0) {
                for (SwitchInfoList.DataBean switchInfoList : AppHolder.getInstance().getSwitchInfoList().getData()) {
                    if (PositionId.KEY_CLEAN_ALL.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_THREE_CODE.equals(switchInfoList.getAdvertPosition())) {
                        isOpen = switchInfoList.isOpen();
                    }
                }
            }
            EventBus.getDefault().post(new FinishCleanFinishActivityEvent());
            if (isOpen && PreferenceUtil.getShowCount(getActivity(), getString(R.string.tool_suggest_clean), mRamScale, mNotifySize, mPowerSize) < 3) {
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.tool_suggest_clean));
                startActivity(CleanFinishAdvertisementActivity.class, bundle);
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.tool_suggest_clean));
                bundle.putString("num", "");
                bundle.putString("unit", "");
                bundle.putString("home", "");
                startActivity(NewCleanFinishActivity.class, bundle);
            }
        }
    }

    /**
     * 文件管理
     */
    @OnClick(R.id.text_wjgl)
    public void wjgl() {
        textWjgl.setEnabled(false);
        ((MainActivity) getActivity()).commitJpushClickTime(4);
        StatisticsUtils.trackClick("file_clean_click", "用户在首页点击【文件清理】按钮", "home_page", "home_page");
        startActivity(FileManagerHomeActivity.class);
    }

    /**
     * 一键加速
     */
    @OnClick(R.id.text_acce)
    public void text_acce() {
        AppHolder.getInstance().setCleanFinishSourcePageId("home_page");
        AppHolder.getInstance().setOtherSourcePageId(SpCacheConfig.ONKEY);
        ((MainActivity) getActivity()).commitJpushClickTime(2);
        StatisticsUtils.trackClick("boost_click", "用户在首页点击【一键加速】按钮", "home_page", "home_page");
        //保存本次清理完成时间 保证每次清理时间间隔为3分钟
        if (!PreferenceUtil.getCleanTime()) {
            boolean isOpen = false;
            //solve umeng error --> SwitchInfoList.getData()' on a null object reference
            if (null != AppHolder.getInstance().getSwitchInfoList() && null != AppHolder.getInstance().getSwitchInfoList().getData()
                    && AppHolder.getInstance().getSwitchInfoList().getData().size() > 0) {
                for (SwitchInfoList.DataBean switchInfoList : AppHolder.getInstance().getSwitchInfoList().getData()) {
                    if (PositionId.KEY_JIASU.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_THREE_CODE.equals(switchInfoList.getAdvertPosition())) {
                        isOpen = switchInfoList.isOpen();
                    }
                }
            }
            EventBus.getDefault().post(new FinishCleanFinishActivityEvent());
            if (isOpen && PreferenceUtil.getShowCount(getActivity(), getString(R.string.tool_one_key_speed), mRamScale, mNotifySize, mPowerSize) < 3) {
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.tool_one_key_speed));
                startActivity(CleanFinishAdvertisementActivity.class, bundle);
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.tool_one_key_speed));
                bundle.putString("num", "");
                bundle.putString("unit", "");
                startActivity(NewCleanFinishActivity.class, bundle);
            }
        } else {
            Bundle bundle = new Bundle();
            bundle.putString(SpCacheConfig.ITEM_TITLE_NAME, getString(R.string.tool_one_key_speed));
            startActivity(PhoneAccessActivity.class, bundle);
        }
    }

    /**
     * 超强省电
     */
    @OnClick(R.id.line_shd)
    public void line_shd() {
        lineShd.setEnabled(false);
        AppHolder.getInstance().setCleanFinishSourcePageId("home_page");
        ((MainActivity) getActivity()).commitJpushClickTime(3);
        AppHolder.getInstance().setOtherSourcePageId(SpCacheConfig.SUPER_POWER_SAVING);
        StatisticsUtils.trackClick("powersave_click", "用户在首页点击【超强省电】按钮", "home_page", "home_page");
        if (PreferenceUtil.getPowerCleanTime()) {
            startActivity(PhoneSuperPowerActivity.class);
        } else {
            boolean isOpen = false;
            //solve umeng error --> SwitchInfoList.getData()' on a null object reference
            if (null != AppHolder.getInstance().getSwitchInfoList() && null != AppHolder.getInstance().getSwitchInfoList().getData()
                    && AppHolder.getInstance().getSwitchInfoList().getData().size() > 0) {
                for (SwitchInfoList.DataBean switchInfoList : AppHolder.getInstance().getSwitchInfoList().getData()) {
                    if (PositionId.KEY_CQSD.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_THREE_CODE.equals(switchInfoList.getAdvertPosition())) {
                        isOpen = switchInfoList.isOpen();
                    }
                }
            }

            if (isOpen && PreferenceUtil.getShowCount(getActivity(), getString(R.string.tool_super_power_saving), mRamScale, mNotifySize, mPowerSize) < 3) {
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.tool_super_power_saving));
                startActivity(CleanFinishAdvertisementActivity.class, bundle);
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.tool_super_power_saving));
                bundle.putString("num", "");
                bundle.putString("unit", "");
                startActivity(NewCleanFinishActivity.class, bundle);
            }
        }

    }

    /**
     * 新闻点击
     */
    @OnClick(R.id.view_news)
    public void ViewNewsClick() {
        viewNews.setEnabled(false);
        StatisticsUtils.trackClick("news_click", "用户在首页点击【头条新闻热点】按钮", "home_page", "home_page");
        startActivity(NewsActivity.class);
    }

    /**
     * 软件管理
     */
    @OnClick(R.id.view_phone_thin)
    public void ViewPhoneThinClick() {
        viewPhoneThin.setEnabled(false);
        Intent intent = new Intent(getActivity(), PhoneThinActivity.class);
        intent.putExtra(SpCacheConfig.ITEM_TITLE_NAME, getString(R.string.tool_soft_manager));
        startActivity(intent);
        StatisticsUtils.trackClick("app_manage_click", "用户在首页点击【软件管理】按钮", "home_page", "home_page");
    }

    /**
     * QQ专清
     */
    @OnClick(R.id.view_qq_clean)
    public void ViewQQCleanClick() {
        viewQqClean.setEnabled(false);
        AppHolder.getInstance().setOtherSourcePageId(SpCacheConfig.QQ_CLEAN);
        ((MainActivity) getActivity()).commitJpushClickTime(7);
        StatisticsUtils.trackClick("qqclean_click", "“用户在首页点击【qq专清】按钮", "home_page", "home_page");
        if (!AndroidUtil.isAppInstalled(SpCacheConfig.QQ_PACKAGE)) {
            ToastUtils.showShort(R.string.tool_no_install_qq);
            return;
        }
        if (QQUtil.audioList != null)
            QQUtil.audioList.clear();
        if (QQUtil.fileList != null)
            QQUtil.fileList.clear();
        Intent intent = new Intent();
        intent.putExtra("mRamScale", mRamScale);
        intent.putExtra("mNotifySize", mNotifySize);
        intent.putExtra("mPowerSize", mPowerSize);
        intent.setClass(getActivity(), QQCleanHomeActivity.class);
        getActivity().startActivity(intent);
    }

    /*    *//**
     * 权限设置
     *//*
    @OnClick(R.id.iv_permission)
    public void onClick() {
        startActivity(new Intent(getContext(), PermissionActivity.class));
        StatisticsUtils.trackClick("Triangular_yellow_mark_click", "三角黄标", AppHolder.getInstance().getSourcePageId(), "permission_page");
    }*/

    /**
     * 微信专清
     */
    @OnClick(R.id.line_wx)
    public void mClickWx() {
        AppHolder.getInstance().setCleanFinishSourcePageId("home_page");
        AppHolder.getInstance().setOtherSourcePageId(SpCacheConfig.WETCHAT_CLEAN);

        ((MainActivity) getActivity()).commitJpushClickTime(5);
        StatisticsUtils.trackClick("wxclean_click", "用户在首页点击【微信专清】按钮", "home_page", "home_page");
        if (!AndroidUtil.isAppInstalled(SpCacheConfig.CHAT_PACKAGE)) {
            ToastUtils.showShort(R.string.tool_no_install_chat);
            return;
        }
        if (PreferenceUtil.getWeChatCleanTime()) {
            // 每次清理间隔 至少3秒
            startActivity(WechatCleanHomeActivity.class);
        } else {
            boolean isOpen = false;
            //solve umeng error --> SwitchInfoList.getData()' on a null object reference
            if (null != AppHolder.getInstance().getSwitchInfoList() && null != AppHolder.getInstance().getSwitchInfoList().getData()
                    && AppHolder.getInstance().getSwitchInfoList().getData().size() > 0) {
                for (SwitchInfoList.DataBean switchInfoList : AppHolder.getInstance().getSwitchInfoList().getData()) {
                    if (PositionId.KEY_WECHAT.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_THREE_CODE.equals(switchInfoList.getAdvertPosition())) {
                        isOpen = switchInfoList.isOpen();
                    }
                }
            }

            if (isOpen && PreferenceUtil.getShowCount(getActivity(), getString(R.string.tool_chat_clear), mRamScale, mNotifySize, mPowerSize) < 3) {
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.tool_chat_clear));
                startActivity(CleanFinishAdvertisementActivity.class, bundle);
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.tool_chat_clear));
                bundle.putString("num", "");
                bundle.putString("unit", "");
                startActivity(NewCleanFinishActivity.class, bundle);
            }
        }
    }

    /**
     * 通知栏清理
     */
    @OnClick(R.id.line_super_power_saving)
    public void mClickQq() {
        AppHolder.getInstance().setCleanFinishSourcePageId("home_page");
        StatisticsUtils.trackClick("notification_clean_click", "用户在首页点击【通知清理】按钮", AppHolder.getInstance().getSourcePageId(), "home_page");
        if (!NotifyUtils.isNotificationListenerEnabled() || PreferenceUtil.getNotificationCleanTime() || mNotifySize > 0) {
            NotifyCleanManager.startNotificationCleanActivity(getActivity(), 0);
        } else {
            boolean isOpen = false;
            //solve umeng error --> SwitchInfoList.getData()' on a null object reference
            if (null != AppHolder.getInstance().getSwitchInfoList() && null != AppHolder.getInstance().getSwitchInfoList().getData()
                    && AppHolder.getInstance().getSwitchInfoList().getData().size() > 0) {
                for (SwitchInfoList.DataBean switchInfoList : AppHolder.getInstance().getSwitchInfoList().getData()) {
                    if (PositionId.KEY_NOTIFY.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_THREE_CODE.equals(switchInfoList.getAdvertPosition())) {
                        isOpen = switchInfoList.isOpen();
                    }
                }
            }

            if (isOpen && PreferenceUtil.getShowCount(getActivity(), getString(R.string.tool_notification_clean), mRamScale, mNotifySize, mPowerSize) < 3) {
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.tool_notification_clean));
                startActivity(CleanFinishAdvertisementActivity.class, bundle);
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.tool_notification_clean));
                bundle.putString("num", "");
                bundle.putString("unit", "");
                startActivity(NewCleanFinishActivity.class, bundle);
            }
        }
    }

    /**
     * 手机降温
     */
    @OnClick(R.id.line_jw)
    public void mClickJw() {
        AppHolder.getInstance().setCleanFinishSourcePageId("home_page");
        ((MainActivity) getActivity()).commitJpushClickTime(6);
        StatisticsUtils.trackClick("cooling_click", "用户在首页点击【手机降温】按钮", AppHolder.getInstance().getSourcePageId(), "home_page");

        if (PreferenceUtil.getCoolingCleanTime()) {
            startActivity(RouteConstants.PHONE_COOLING_ACTIVITY);
        } else {
            boolean isOpen = false;
            //solve umeng error --> SwitchInfoList.getData()' on a null object reference
            if (null != AppHolder.getInstance().getSwitchInfoList() && null != AppHolder.getInstance().getSwitchInfoList().getData()
                    && AppHolder.getInstance().getSwitchInfoList().getData().size() > 0) {
                for (SwitchInfoList.DataBean switchInfoList : AppHolder.getInstance().getSwitchInfoList().getData()) {
                    if (PositionId.KEY_COOL.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_THREE_CODE.equals(switchInfoList.getAdvertPosition())) {
                        isOpen = switchInfoList.isOpen();
                    }
                }
            }
            if (isOpen && PreferenceUtil.getShowCount(getActivity(), getString(R.string.tool_phone_temperature_low), mRamScale, mNotifySize, mPowerSize) < 3) {
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.tool_phone_temperature_low));
                startActivity(CleanFinishAdvertisementActivity.class, bundle);
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.tool_phone_temperature_low));
                bundle.putString("num", "");
                bundle.putString("unit", "");
                startActivity(NewCleanFinishActivity.class, bundle);
            }
        }
    }

    //低于Android O
    public void getAccessListBelow(ArrayList<FirstJunkInfo> listInfo) {
        if (listInfo == null) return;
        //悟空清理app加入默认白名单
        for (FirstJunkInfo firstJunkInfo : listInfo) {
            if (SpCacheConfig.APP_ID.equals(firstJunkInfo.getAppPackageName())) {
                listInfo.remove(firstJunkInfo);
            }
        }
        if (listInfo.size() != 0) {
            mRamScale = new FileQueryUtils().computeTotalSize(listInfo);
        }
    }

    public void onKeyBack() {
//        long currentTimeMillis = System.currentTimeMillis();
//        if (currentTimeMillis - firstTime > 1500) {
//            Toast.makeText(getActivity(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
//            firstTime = currentTimeMillis;
//        } else {
//            SPUtil.setInt(getContext(), "turnask", 0);
//            AppManager.getAppManager().AppExit(getContext(), false);
//        }
        Intent home = new Intent(Intent.ACTION_MAIN);
        home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        home.addCategory(Intent.CATEGORY_HOME);
        startActivity(home);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                StatusBarCompat.setStatusBarColor(getActivity(), getResources().getColor(R.color.color_27D698), true);

            } else {
                StatusBarCompat.setStatusBarColor(getActivity(), getResources().getColor(R.color.color_4690FD), false);
            }
        }
    }

    /**
     * EventBus 立即清理完成后，更新首页显示文案
     */
    @Subscribe
    public void onEventClean(CleanEvent cleanEvent) {
        if (cleanEvent != null) {
            if (cleanEvent.isCleanAminOver()) {
                mTvCleanType.setText(getString(R.string.tool_phone_already_clean));
                tvNowClean.setVisibility(View.GONE);

                mLottieHomeView.useHardwareAcceleration(true);
                mLottieHomeView.setAnimation("clean_home_top2.json");
                mLottieHomeView.setImageAssetsFolder("images_home_finish");
                mLottieHomeView.playAnimation();
                mLottieHomeView.addAnimatorListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mLottieHomeView.playAnimation();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
            }
        }
    }

    /**
     * 点击下载app
     *
     * @param view
     * @param downloadUrl
     */
    public void clickDownload(View view, String downloadUrl, int position) {
        view.setOnClickListener(v -> {
            //广告埋点
            StatisticsUtils.trackClickAD("ad_click", "\"广告点击", AppHolder.getInstance().getSourcePageId(), "home_page_clean_up_page", String.valueOf(position));
           /* Bundle bundle = new Bundle();
            bundle.putString(Constant.URL, downloadUrl);
            bundle.putBoolean(Constant.NoTitle, false);
            startActivity(UserLoadH5Activity.class, bundle);*/

            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri content_url = Uri.parse(downloadUrl);
            intent.setData(content_url);
            startActivity(intent);
        });
    }

    /**
     * 静止时动画
     */
    private void showHomeLottieView() {
        mLottieHomeView.useHardwareAcceleration(true);
        mLottieHomeView.setAnimation("clean_home_top.json");
        mLottieHomeView.setImageAssetsFolder("images_home");
        mLottieHomeView.playAnimation();
        mLottieHomeView.setVisibility(VISIBLE);
        mLottieHomeView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mTvCleanType.setVisibility(VISIBLE);
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
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
