package com.xiaoniu.cleanking.ui.newclean.fragment;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.databinding.DataBindingUtil;

import com.jess.arms.utils.DeviceUtils;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.H5Urls;
import com.xiaoniu.cleanking.app.injector.component.FragmentComponent;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.BaseFragment;
import com.xiaoniu.cleanking.databinding.FragmentMineBinding;
import com.xiaoniu.cleanking.midas.AdRequestParams;
import com.xiaoniu.cleanking.midas.MidasConstants;
import com.xiaoniu.cleanking.midas.MidasRequesCenter;
import com.xiaoniu.cleanking.scheme.Constant.SchemeConstant;
import com.xiaoniu.cleanking.scheme.SchemeProxy;
import com.xiaoniu.cleanking.ui.login.activity.LoginWeiChatActivity;
import com.xiaoniu.cleanking.ui.main.activity.QuestionReportActivity;
import com.xiaoniu.cleanking.ui.main.activity.WhiteListSettingActivity;
import com.xiaoniu.cleanking.ui.main.bean.MinePageInfoBean;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.newclean.contact.MineFragmentContact;
import com.xiaoniu.cleanking.ui.newclean.presenter.MinePresenter;
import com.xiaoniu.cleanking.ui.newclean.util.RequestUserInfoUtil;
import com.xiaoniu.cleanking.ui.tool.notify.event.UserInfoEvent;
import com.xiaoniu.cleanking.ui.usercenter.activity.AboutInfoActivity;
import com.xiaoniu.cleanking.ui.usercenter.activity.PermissionActivity;
import com.xiaoniu.cleanking.utils.AndroidUtil;
import com.xiaoniu.cleanking.utils.ImageUtil;
import com.xiaoniu.cleanking.utils.NumberUtils;
import com.xiaoniu.cleanking.utils.user.UserHelper;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.statistic.NiuDataAPI;
import com.xnad.sdk.ad.entity.AdInfo;
import com.xnad.sdk.ad.listener.AbsAdCallBack;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.OnClick;

import static com.xiaoniu.cleanking.utils.user.UserHelper.EXIT_SUCCESS;
import static com.xiaoniu.cleanking.utils.user.UserHelper.LOGIN_SUCCESS;


/**
 * Created by zhaoyingtao
 * Date: 2020/6/30
 * Describe:个人中心 替换之前的MeFragment页面
 */
public class MineFragment extends BaseFragment<MinePresenter> implements MineFragmentContact.View {

    @Override
    protected void inject(FragmentComponent fragmentComponent) {
        fragmentComponent.inject(this);
        mPresenter.setmContext(getContext());
    }

    @Override
    public void netError() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        mBinding = DataBindingUtil.bind(getView());
        mBinding.phoneNumTv.setText("未登录");
        setUserInfo();
        RequestUserInfoUtil.getUserCoinInfo();


        RelativeLayout.MarginLayoutParams params = (RelativeLayout.MarginLayoutParams) mBinding.settingLl.getLayoutParams();
        params.topMargin = DeviceUtils.getStatusBarHeight(mContext) + 30;
        mBinding.settingLl.setLayoutParams(params);
        if (AppHolder.getInstance().getAuditSwitch()) {//特殊情况隐藏
            mBinding.cashRl.setVisibility(View.GONE);
        } else {
            mBinding.cashRl.setVisibility(View.VISIBLE);
        }
//        Log.e("snow","状态栏高度====="+DeviceUtils.getStatusBarHeight(mContext));
    }

    FragmentMineBinding mBinding;

    public static MineFragment getInstance() {
        return new MineFragment();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshUserInfo(String string) {
        if (LOGIN_SUCCESS.equals(string) || EXIT_SUCCESS.equals(string)) {
//            mBinding.phoneNumTv.setText("UserHelper.init().getPhoneNum()");
            setUserInfo();
        }
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            StatisticsUtils.customTrackEvent("my_page_custom", "我的页面曝光", "my_page", "my_page");
            RequestUserInfoUtil.getUserCoinInfo();
            StatusBarCompat.translucentStatusBarForImage(getActivity(), true, true);
            //展示广告
            addBottomAdView();
        }
        if (hidden) {
            NiuDataAPI.onPageEnd("personal_center_view_page", "个人中心浏览");
        } else {
            NiuDataAPI.onPageStart("personal_center_view_page", "个人中心浏览");
        }
    }

    //刷新用户金币
    @Subscribe
    public void userInfoUpdate(UserInfoEvent event) {
        if (event != null && event.infoBean != null) {
            setUserCoinView(event.infoBean.getAmount(), event.infoBean.getGold());
        }
    }

    @OnClick({R.id.setting_ll, R.id.head_img_iv, R.id.phone_num_tv, R.id.llt_invite_friend,
            R.id.body_data_ll, R.id.step_record_ll, R.id.kefu_ll, R.id.withdrawal_ll, R.id.wallet_ll})
    public void onViewClicked(View view) {
        if (AndroidUtil.isFastDoubleClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.setting_ll:
                StatisticsUtils.trackClick("set_up_click", "\"设置\"点击", AppHolder.getInstance().getSourcePageId(), "personal_center_page");
                startActivity(new Intent(getContext(), WhiteListSettingActivity.class));
                break;
            case R.id.head_img_iv:
            case R.id.phone_num_tv:
                if (!UserHelper.init().isWxLogin()) {
                    startActivity(new Intent(getContext(), LoginWeiChatActivity.class));
                }
                if (!UserHelper.init().isLogin()) {
                    StatisticsUtils.trackClick("log_in_now_click", "立即登录点击", "my_page", "my_page");
                }
//                GoldCoinBean goldCoinBean = new GoldCoinBean();
//                goldCoinBean.context = getContext();
//                goldCoinBean.obtainCoinCount = 10;
//                goldCoinBean.totalCoinCount = 135;
//                goldCoinBean.dialogType = 3;
//                goldCoinBean.adId = "";
//                goldCoinBean.isDouble = true;
//                goldCoinBean.videoSource = 12;
//                GoldCoinDialog.showGoldCoinDialog(goldCoinBean);
//                ToastUtils.showShort("用户信息");
                break;
//            case R.id.iv_inter_ad:
//                ToastUtils.showShort("插入广告");
//                break;
            case R.id.withdrawal_ll://提现
                goToWALLETOrWithdrawal(1);
                StatisticsUtils.trackClick("cash_withdrawal_click", "提现点击", "my_page", "my_page");
                break;
            case R.id.wallet_ll://钱包详细
                goToWALLETOrWithdrawal(0);
                break;
            case R.id.llt_invite_friend:
                StatisticsUtils.trackClick("privilege_management_click", "\"权限管理\"点击", AppHolder.getInstance().getSourcePageId(), "personal_center_page");
                startActivity(new Intent(getActivity(), PermissionActivity.class));
                break;
            case R.id.body_data_ll:
                StatisticsUtils.trackClick("question_feedback_click", "\"问题反馈\"点击", AppHolder.getInstance().getSourcePageId(), "personal_center_page");
                startActivity(new Intent(getActivity(), QuestionReportActivity.class));
                break;
            case R.id.step_record_ll:
                StatisticsUtils.trackClick("about_click", "\"关于\"点击", "mine_page", "personal_center_page");
                startActivity(new Intent(getActivity(), AboutInfoActivity.class));
                break;
            case R.id.kefu_ll:
                StatisticsUtils.trackClick("Check_for_updates_click", "检查更新", "mine_page", "about_page");
                mPresenter.queryAppVersion(2, () -> {
                });
                break;
        }
    }

    private void goToWALLETOrWithdrawal(int type) {
        String url = H5Urls.WALLET_URL;
        boolean goToH5 = UserHelper.init().isLogin();
        if (type == 1) {
            url = H5Urls.WITHDRAWAL_URL;
            goToH5 = UserHelper.init().isWxLogin();
        }
        if (goToH5) {
//          String url = "http://192.168.85.61:9999/html/wallet/wallet.html";
            String scheme = SchemeConstant.SCHEME +
                    "://" + SchemeConstant.HOST + "/jump?url=" + url +
                    "&" + SchemeConstant.IS_FULL_SCREEN + "=1&jumpType=1";
            SchemeProxy.openScheme(getActivity(), scheme);
        } else {
            startActivity(new Intent(getActivity(), LoginWeiChatActivity.class));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void setUserInfo() {
        if (UserHelper.init().isLogin()) {
            String nickName = UserHelper.init().getNickName();
            if (TextUtils.isEmpty(nickName)) {
                nickName = UserHelper.init().getPhoneNum();
            }
            mBinding.phoneNumTv.setText(nickName);
            if (UserHelper.init().isWxLogin()) {
                ImageUtil.displayCircle(UserHelper.init().getUserHeadPortraitUrl(), mBinding.headImgIv, R.mipmap.default_head);
            } else {
                mBinding.headImgIv.setImageResource(R.mipmap.default_head);
            }
        } else {
            setUserCoinView(-1, -1);
            mBinding.headImgIv.setImageResource(R.mipmap.default_head);
            mBinding.phoneNumTv.setText("立即登录");
        }
    }

    private void setUserCoinView(double amount, int gold) {
        mBinding.coinAbout.setVisibility(View.VISIBLE);
        mBinding.moneyTv.setVisibility(View.VISIBLE);
        mBinding.goldCoinTv.setVisibility(View.VISIBLE);
        if (gold > 99) {
            mBinding.moneyTv.setText(NumberUtils.getFloatStr2(amount) + "元");
            mBinding.goldCoinTv.setText(String.valueOf(gold));
        } else if (gold > 0) {
            mBinding.moneyTv.setText("0.01元");
            mBinding.goldCoinTv.setText(String.valueOf(gold));
        } else if (gold == 0) {
            mBinding.moneyTv.setText("0元");
            mBinding.goldCoinTv.setText("0");
        } else {
            mBinding.coinAbout.setVisibility(View.GONE);
            mBinding.moneyTv.setVisibility(View.GONE);
            mBinding.moneyTv.setText("--");
            mBinding.goldCoinTv.setText("--");
        }
    }

    @Override
    public void getInfoDataSuccess(MinePageInfoBean infoBean) {
        if (infoBean != null && infoBean.getData() != null) {
            MinePageInfoBean.DataBean data = infoBean.getData();
            setUserCoinView(data.getAmount(), data.getGold());
            UserInfoEvent event = new UserInfoEvent();
            event.infoBean = data;
            EventBus.getDefault().post(event);
        }
    }

    /**
     * 底部广告样式--
     */
    private void addBottomAdView() {
        if (null == getActivity() || !AppHolder.getInstance().checkAdSwitch(PositionId.KEY_PAGE_MINE))
            return;
        StatisticsUtils.customADRequest("ad_request", "广告请求", "1", " ", " ", "all_ad_request", "my_page", "my_page");
        AdRequestParams params = new AdRequestParams.Builder()
                .setAdId(MidasConstants.ME_BOTTOM_ID)
                .setActivity(getActivity())
                .setViewContainer(mBinding.bannerAdLl).build();
        MidasRequesCenter.requestAd(params, new AbsAdCallBack() {
            @Override
            public void onAdShow(AdInfo adInfo) {
                super.onAdShow(adInfo);
                StatisticsUtils.customTrackEvent("ad_request", "我的页面广告请求（满足广告展现时机时向商业化sdk发起请求数）", "my_page", "my_page");
            }
        });
      /*  GeekAdSdk.getAdsManger().loadNativeTemplateAd(getActivity(), PositionId.AD_PERSONAL_CENTER_PAGE_BELOW_AD_MB, Float.valueOf(DisplayUtil.px2dp(getContext(), DisplayUtil.getScreenWidth(getContext())) - 24), new AdListener() {
            @Override
            public void adSuccess(AdInfo info) {
                if (null != info) {
                    Logger.i("adSuccess---1==" + info.getAdId());
                    StatisticsUtils.customADRequest("ad_request", "广告请求", "1", info.getAdId(), info.getAdSource(), "success", "my_page", "my_page");
                    if (null != info.getAdView()) {
                        mBinding.bannerAdLl.removeAllViews();
                        mBinding.bannerAdLl.addView(info.getAdView());
                        mBinding.bannerAdLl.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void adExposed(AdInfo info) {
                Logger.i("adExposed---1");
                if (null == info) return;
                StatisticsUtils.customAD("ad_show", "广告展示曝光", "1", info.getAdId(), info.getAdSource(), "my_page", "my_page", info.getAdTitle());
            }

            @Override
            public void adClicked(AdInfo info) {
                Logger.i("adClicked---1");
                if (null == info) return;
                StatisticsUtils.clickAD("ad_click", "广告点击", "1", info.getAdId(), info.getAdSource(), "my_page", "my_page", info.getAdTitle());
            }

            @Override
            public void adError(AdInfo info, int errorCode, String errorMsg) {
                Logger.i("adError---1---" + errorMsg);
                if (null != info) {
                    StatisticsUtils.customADRequest("ad_request", "广告请求", "1", info.getAdId(), info.getAdSource(), "fail", "my_page", "my_page");
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mBinding.bannerAdLl.setVisibility(View.GONE);
                    }
                });
            }
        });*/
    }

}
