package com.xiaoniu.cleanking.ui.newclean.fragment;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.comm.jksdk.utils.DisplayUtil;
import com.jess.arms.utils.DeviceUtils;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.H5Urls;
import com.xiaoniu.cleanking.app.injector.component.FragmentComponent;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.BaseFragment;
import com.xiaoniu.cleanking.databinding.FragmentMineBinding;
import com.xiaoniu.cleanking.midas.MidasRequesCenter;
import com.xiaoniu.cleanking.midas.abs.SimpleViewCallBack;
import com.xiaoniu.cleanking.scheme.Constant.SchemeConstant;
import com.xiaoniu.cleanking.scheme.SchemeProxy;
import com.xiaoniu.cleanking.scheme.utils.SchemeUtils;
import com.xiaoniu.cleanking.ui.login.activity.LoginWeiChatActivity;
import com.xiaoniu.cleanking.ui.main.activity.QuestionReportActivity;
import com.xiaoniu.cleanking.ui.main.activity.WhiteListSettingActivity;
import com.xiaoniu.cleanking.ui.main.bean.BubbleCollected;
import com.xiaoniu.cleanking.ui.main.bean.BubbleConfig;
import com.xiaoniu.cleanking.ui.main.bean.DaliyTaskListData;
import com.xiaoniu.cleanking.ui.main.bean.DaliyTaskListEntity;
import com.xiaoniu.cleanking.ui.main.bean.MinePageInfoBean;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.event.LifecycEvent;
import com.xiaoniu.cleanking.ui.main.event.SwitchTabEvent;
import com.xiaoniu.cleanking.ui.main.widget.ViewHelper;
import com.xiaoniu.cleanking.ui.newclean.adapter.MineDaliyTaskAdapter;
import com.xiaoniu.cleanking.ui.newclean.contact.MineFragmentContact;
import com.xiaoniu.cleanking.ui.newclean.listener.IBullClickListener;
import com.xiaoniu.cleanking.ui.newclean.presenter.MinePresenter;
import com.xiaoniu.cleanking.ui.newclean.util.RequestUserInfoUtil;
import com.xiaoniu.cleanking.ui.newclean.util.ScrapingCardDataUtils;
import com.xiaoniu.cleanking.ui.tool.notify.event.LimitAwardRefEvent;
import com.xiaoniu.cleanking.ui.tool.notify.event.UserInfoEvent;
import com.xiaoniu.cleanking.ui.usercenter.activity.AboutInfoActivity;
import com.xiaoniu.cleanking.ui.usercenter.activity.PermissionActivity;
import com.xiaoniu.cleanking.utils.AndroidUtil;
import com.xiaoniu.cleanking.utils.CollectionUtils;
import com.xiaoniu.cleanking.utils.DaliyTaskInstance;
import com.xiaoniu.cleanking.utils.ImageUtil;
import com.xiaoniu.cleanking.utils.NumberUtils;
import com.xiaoniu.cleanking.utils.anim.AnimationScaleUtils;
import com.xiaoniu.cleanking.utils.user.UserHelper;
import com.xiaoniu.cleanking.widget.LuckBubbleView;
import com.xiaoniu.common.utils.DisplayUtils;
import com.xiaoniu.common.utils.NetworkUtils;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.utils.Toast;
import com.xiaoniu.common.utils.ToastUtils;
import com.xiaoniu.statistic.NiuDataAPI;
import com.xiaoniu.unitionadbase.model.AdInfoModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.OnClick;

import static com.xiaoniu.cleanking.utils.user.UserHelper.EXIT_SUCCESS;
import static com.xiaoniu.cleanking.utils.user.UserHelper.LOGIN_SUCCESS;


/**
 * Created by zhaoyingtao
 * Date: 2020/6/30
 * Describe:个人中心 替换之前的MeFragment页面
 */
public class MineFragment extends BaseFragment<MinePresenter> implements MineFragmentContact.View, IBullClickListener {

    MineDaliyTaskAdapter mineDaliyTaskAdapter;

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

        RelativeLayout.MarginLayoutParams params = (RelativeLayout.MarginLayoutParams) mBinding.relHealContent.getLayoutParams();
        params.topMargin = DeviceUtils.getStatusBarHeight(mContext) + 30;
        mBinding.relHealContent.setLayoutParams(params);

        if (AppHolder.getInstance().getAuditSwitch()) {//特殊情况隐藏
            mBinding.cashRl.setVisibility(View.GONE);
        } else {
            mBinding.cashRl.setVisibility(View.VISIBLE);
        }
        initTaskListView();
        ViewHelper.setTextViewCustomTypeFace(mBinding.goldCoinTv, "fonts/DIN-Medium.otf");
        ViewHelper.setTextViewCustomTypeFace(mBinding.moneyTv, "fonts/DIN-Medium.otf");
        AnimationScaleUtils.getInstance().playScaleAnimation(mBinding.ivAtonceCard, 500);
        showRewardViewListener();
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
//            mBinding.mineAdFf.setOutlineProvider(new OutlineProvider(DimenUtils.dp2px(getContext(), 8)));
//            mBinding.mineAdFf.setClipToOutline(true);
//        }
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

    /**
     * 热启动回调
     */
    @Subscribe
    public void changeLifeCycleEvent(LifecycEvent lifecycEvent) {
        operatingRef();//运营数据刷新
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            StatisticsUtils.customTrackEvent("my_page_custom", "我的页面曝光", "my_page", "my_page");
            RequestUserInfoUtil.getUserCoinInfo();
//            StatusBarCompat.translucentStatusBarForImage(getActivity(), false, true);
            //展示广告
            addBottomAdView();
            operatingRef();//运营数据刷新
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

    //刷新日常任务和限时金币
    @Subscribe
    public void limitAwardRef(LimitAwardRefEvent event) {
        operatingRef();//运营数据刷新
    }

    @OnClick({R.id.setting_ll, R.id.head_img_iv, R.id.phone_num_tv, R.id.llt_invite_friend,
            R.id.body_data_ll, R.id.step_record_ll, R.id.kefu_ll, R.id.withdrawal_ll, R.id.wallet_ll, R.id.rel_card_award})
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
            case R.id.rel_card_award:
                StatisticsUtils.trackClick("scratch_immediately", "立即刮卡点击", "my_page", "my_page");
                if (NetworkUtils.isNetConnected()) {
                    ScrapingCardDataUtils.init().scrapingCardNextAction(getActivity(), false);
                    EventBus.getDefault().post(new SwitchTabEvent(2));
                } else {
                    ToastUtils.showShort(R.string.notwork_error);
                }

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
//        mBinding.coinAbout.setVisibility(View.VISIBLE);
        mBinding.moneyTv.setVisibility(View.VISIBLE);
        mBinding.goldCoinTv.setVisibility(View.VISIBLE);
        if (gold > 99) {
            mBinding.moneyTv.setText("(约 " + NumberUtils.getFloatStr2(amount) + "元)");
            mBinding.goldCoinTv.setText(String.valueOf(gold));
        } else if (gold > 0) {
            mBinding.moneyTv.setText("(约 0.01元)");
            mBinding.goldCoinTv.setText(String.valueOf(gold));
        } else if (gold == 0) {
            mBinding.moneyTv.setText("(约 0元)");
            mBinding.goldCoinTv.setText("0");
        } else {
//            mBinding.coinAbout.setVisibility(View.GONE);
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

        MidasRequesCenter.requestAndShowAd(mActivity, AppHolder.getInstance().getMidasAdId(PositionId.KEY_PAGE_MINE, PositionId.DRAW_ONE_CODE), new SimpleViewCallBack(mBinding.mineAdFf) {
            @Override
            public void onAdExposure(AdInfoModel adInfoModel) {
                super.onAdExposure(adInfoModel);
                StatisticsUtils.customTrackEvent("ad_request", "我的页面广告请求（满足广告展现时机时向商业化sdk发起请求数）", "my_page", "my_page");
            }
        });
    }



    /*
     *********************************************************************************************************************************************************
     ************************************************************日常任务数据刷新 View初始化**********************************************************************
     *********************************************************************************************************************************************************
     */


    private void initTaskListView() {
        mBinding.rvDaliyTask.setNestedScrollingEnabled(false);
        mineDaliyTaskAdapter = new MineDaliyTaskAdapter(getActivity());
        mBinding.rvDaliyTask.setLayoutManager(new LinearLayoutManager(getActivity()));
        mBinding.rvDaliyTask.setAdapter(mineDaliyTaskAdapter);
        mBinding.rvDaliyTask.setFocusable(false);
        mineDaliyTaskAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                DaliyTaskListEntity itemdata = (DaliyTaskListEntity) adapter.getItem(position);
                if (null != itemdata && itemdata.getLinkType() == 1 && !TextUtils.isEmpty(itemdata.getLinkUrl()) && SchemeUtils.isScheme(itemdata.getLinkUrl())) {
                    try {
                        if (itemdata.getIsCollect() == 0) {
                            SchemeUtils.openScheme(mActivity, itemdata.getLinkUrl());
                            DaliyTaskInstance.getInstance().addTask(itemdata);
                        } else {
                            ToastUtils.showShort(R.string.toast_alerady_award);
                        }
                        StatisticsUtils.trackClick("daily_tasks_click_" + (position + 1), "日常任务" + (position + 1) + "点击", "my_page", "my_page");
                    } catch (Exception e) {
                        DaliyTaskInstance.getInstance().cleanTask();
                        e.printStackTrace();
                    }
                } else {
                    ToastUtils.showShort(getString(R.string.notwork_error));
                }
            }
        });
    }

    @Override
    public void setTaskData(DaliyTaskListData data) {
        if (null != data && !CollectionUtils.isEmpty(data.getData())) {
            mBinding.linearDaliyTask.setVisibility(View.VISIBLE);
            mineDaliyTaskAdapter.setNewData(data.getData());
        } else {
            mBinding.linearDaliyTask.setVisibility(View.GONE);
        }

    }

    /*
     *********************************************************************************************************************************************************
     ************************************************************刷新限时奖励金币位 View初始化**********************************************************************
     *********************************************************************************************************************************************************
     */
    private void showRewardViewListener() {
        LuckBubbleView luck01 = mBinding.rewardView.findViewById(R.id.iv_golde_06);
        LuckBubbleView luck02 = mBinding.rewardView.findViewById(R.id.iv_golde_07);
        LuckBubbleView luck03 = mBinding.rewardView.findViewById(R.id.iv_golde_08);
        LuckBubbleView luck04 = mBinding.rewardView.findViewById(R.id.iv_golde_09);
        luck01.setIBullListener(this);
        luck02.setIBullListener(this);
        luck03.setIBullListener(this);
        luck04.setIBullListener(this);

    }

    /*
     * *********************************************************************************************************************************************************
     * ********************************************************** 刷新限时奖励金币位 ***************************************************************************************
     * *********************************************************************************************************************************************************
     */

    /**
     * 刷新金币显示
     */
    @Override
    public void setBubbleView(BubbleConfig dataBean) {
        if (null != mBinding.rewardView && null != dataBean && !CollectionUtils.isEmpty(dataBean.getData()) && checkGoldData(dataBean.getData())) {
            mBinding.rewardView.refBubbleView(dataBean);
            mBinding.rewardView.setVisibility(View.VISIBLE);

            LinearLayout.LayoutParams cardlayoutParams = new LinearLayout.LayoutParams(mBinding.relCardAward.getLayoutParams());
            cardlayoutParams.setMargins(DisplayUtil.dip2px(mContext, 15), DisplayUtil.dp2px(mContext, 2), DisplayUtil.dip2px(mContext, 15), 0);
            mBinding.relCardAward.setLayoutParams(cardlayoutParams);
        } else {
            mBinding.rewardView.setVisibility(View.GONE);
            mBinding.rewardView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    LinearLayout.LayoutParams cardlayoutParams = new LinearLayout.LayoutParams(mBinding.relCardAward.getLayoutParams());
                    cardlayoutParams.setMargins(DisplayUtil.dip2px(mContext, 15), -DisplayUtil.dp2px(mContext, 70), DisplayUtil.dip2px(mContext, 15), 0);
                    mBinding.relCardAward.setLayoutParams(cardlayoutParams);
                    mBinding.rewardView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });

        }
    }

    /**
     * 金币位点击
     *
     * @param ballBean
     * @param pos
     */
    @Override
    public void clickBull(BubbleConfig.DataBean ballBean, int pos) {
        if (ballBean == null) {
            ToastUtils.showShort(R.string.net_error);
            return;
        }
        if (!AndroidUtil.isFastDoubleClick()) {
            mPresenter.rewardVideoAd(ballBean.getLocationNum());
            StatisticsUtils.trackClick("limited_time_reward_click", "限时奖励图标点击", "my_page", "my_page");

        }
    }


    /**
     * 金币领取成功
     */
    @Override
    public void bubbleCollected(BubbleCollected dataBean) {
        if (null != dataBean) {
            mPresenter.refBullList();//刷新金币列表；
        }
        assert dataBean != null && dataBean.getData() != null;
        mPresenter.showGetGoldCoinDialog(dataBean);
    }

    /**
     * 运营位数据刷新
     * 限时奖励、日常任务、刮刮卡
     */
    public void operatingRef() {
        if (!AppHolder.getInstance().getAuditSwitch()) {
            mBinding.relCardAward.setVisibility(View.VISIBLE);
            mPresenter.refBullList();     //金币配置刷新
            mPresenter.refDaliyTask();    //日常任务列表刷新
        } else {
            mBinding.relCardAward.setVisibility(View.GONE);
            mBinding.linearDaliyTask.setVisibility(View.GONE);
            mBinding.rewardView.setVisibility(View.GONE);
            mBinding.relCardAward.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (mBinding.relCardAward.getVisibility() == View.GONE) {
                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mBinding.constraintLayoutMore.getLayoutParams();
                        layoutParams.topMargin = -DisplayUtil.dip2px(mContext, 75);
                        mBinding.constraintLayoutMore.setLayoutParams(layoutParams);
                        mBinding.relCardAward.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    }
                }
            });
        }
    }

    /**
     * 验证是否有限时金币
     *
     * @param bubbleList
     */
    public boolean checkGoldData(List<BubbleConfig.DataBean> bubbleList) {
        Map<String, Object> mapdata = new HashMap<>();
        for (BubbleConfig.DataBean item : bubbleList) {
            mapdata.put(String.valueOf(item.getLocationNum()), item);
        }
        return mapdata.containsKey("6") || mapdata.containsKey("7") || mapdata.containsKey("8") || mapdata.containsKey("9");
    }

}
