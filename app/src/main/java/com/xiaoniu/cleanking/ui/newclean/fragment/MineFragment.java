package com.xiaoniu.cleanking.ui.newclean.fragment;

import android.content.Intent;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.comm.jksdk.GeekAdSdk;
import com.comm.jksdk.ad.entity.AdInfo;
import com.comm.jksdk.ad.listener.AdListener;
import com.comm.jksdk.utils.DisplayUtil;
import com.orhanobut.logger.Logger;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.FragmentComponent;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.BaseFragment;
import com.xiaoniu.cleanking.databinding.FragmentMineBinding;
import com.xiaoniu.cleanking.ui.login.activity.BindPhoneActivity;
import com.xiaoniu.cleanking.ui.login.activity.LoginWeiChatActivity;
import com.xiaoniu.cleanking.ui.main.activity.QuestionReportActivity;
import com.xiaoniu.cleanking.ui.main.activity.WhiteListSettingActivity;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.newclean.bean.GoldCoinBean;
import com.xiaoniu.cleanking.ui.newclean.dialog.GoldCoinDialog;
import com.xiaoniu.cleanking.ui.newclean.presenter.MinePresenter;
import com.xiaoniu.cleanking.ui.usercenter.activity.AboutActivity;
import com.xiaoniu.cleanking.ui.usercenter.activity.PermissionActivity;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.utils.ToastUtils;
import com.xiaoniu.statistic.NiuDataAPI;

import butterknife.OnClick;


/**
 * Created by zhaoyingtao
 * Date: 2020/6/30
 * Describe:个人中心 替换之前的MeFragment页面
 */
public class MineFragment extends BaseFragment<MinePresenter> {

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
        mBinding = DataBindingUtil.bind(getView());
        mBinding.phoneNumTv.setText("未登录请登录");
    }

    FragmentMineBinding mBinding;

    public static MineFragment getInstance() {
        return new MineFragment();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            //放在initView中无效
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

    @OnClick({R.id.setting_ll, R.id.head_img_iv, R.id.phone_num_tv, R.id.iv_inter_ad, R.id.llt_invite_friend,
            R.id.body_data_ll, R.id.step_record_ll, R.id.kefu_ll, R.id.withdrawal_ll})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.setting_ll:
                StatisticsUtils.trackClick("set_up_click", "\"设置\"点击", AppHolder.getInstance().getSourcePageId(), "personal_center_page");
                startActivity(new Intent(getContext(), WhiteListSettingActivity.class));
                break;
            case R.id.head_img_iv:
            case R.id.phone_num_tv:
                startActivity(new Intent(getContext(), LoginWeiChatActivity.class));
//                GoldCoinBean goldCoinBean = new GoldCoinBean();
//                goldCoinBean.context = getContext();
//                goldCoinBean.obtainCoinCount = 10;
//                goldCoinBean.totalCoinCount = 135;
//                goldCoinBean.dialogType = 2;
//                goldCoinBean.adId = "";
//                goldCoinBean.isDouble = true;
//                goldCoinBean.videoSource = 12;
//                GoldCoinDialog.showGoldCoinDialog(goldCoinBean);
//                ToastUtils.showShort("用户信息");
                break;
            case R.id.iv_inter_ad:
                ToastUtils.showShort("插入广告");
                break;
            case R.id.withdrawal_ll:
                ToastUtils.showShort("提现操作");
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
                startActivity(new Intent(getActivity(), AboutActivity.class));
                break;
            case R.id.kefu_ll:
                StatisticsUtils.trackClick("Check_for_updates_click", "检查更新", "mine_page", "about_page");
                mPresenter.queryAppVersion(2, () -> {
                });
                break;
        }
    }

//    private void handUserInfo() {
//        if (AndroidUtil.isLogin()) {
//            mBinding.phoneNumTv.setText(mPreferencesHelper.getNickName());
//            if (AndroidUtil.isWxLogin()) {
//                ImageUtil.display(mPreferencesHelper.getAvaterUrl(), mHeadImgIv);
//            } else {
//                mBinding.headImgIv.setImageResource(R.mipmap.default_head);
//            }
////            mPresenter.getUserInfo();
//        } else {
//            mBinding.headImgIv.setImageResource(R.mipmap.default_head);
//            mBinding.phoneNumTv.setText("立即登录");
//        }
//    }

    /**
     * 底部广告样式--
     */
    private void addBottomAdView() {
        if (null == getActivity() || !AppHolder.getInstance().checkAdSwitch(PositionId.KEY_PAGE_MINE))
            return;
        StatisticsUtils.customADRequest("ad_request", "广告请求", "1", " ", " ", "all_ad_request", "my_page", "my_page");
        GeekAdSdk.getAdsManger().loadNativeTemplateAd(getActivity(), PositionId.AD_PERSONAL_CENTER_PAGE_BELOW_AD_MB, Float.valueOf(DisplayUtil.px2dp(getContext(), DisplayUtil.getScreenWidth(getContext())) - 24), new AdListener() {
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
        });
    }
}
