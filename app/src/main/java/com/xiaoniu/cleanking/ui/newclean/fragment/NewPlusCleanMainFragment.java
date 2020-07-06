package com.xiaoniu.cleanking.ui.newclean.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.widget.NestedScrollView;

import com.comm.jksdk.utils.MmkvUtil;
import com.google.gson.Gson;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.FragmentComponent;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.BaseFragment;
import com.xiaoniu.cleanking.base.ScanDataHolder;
import com.xiaoniu.cleanking.constant.RouteConstants;
import com.xiaoniu.cleanking.midas.MidasConstants;
import com.xiaoniu.cleanking.ui.main.activity.AgentWebViewActivity;
import com.xiaoniu.cleanking.ui.main.activity.CleanMusicManageActivity;
import com.xiaoniu.cleanking.ui.main.activity.CleanVideoManageActivity;
import com.xiaoniu.cleanking.ui.main.activity.ImageActivity;
import com.xiaoniu.cleanking.ui.main.activity.NetWorkActivity;
import com.xiaoniu.cleanking.ui.main.activity.PhoneAccessActivity;
import com.xiaoniu.cleanking.ui.main.activity.PhoneSuperPowerActivity;
import com.xiaoniu.cleanking.ui.main.bean.CountEntity;
import com.xiaoniu.cleanking.ui.main.bean.InteractionSwitchList;
import com.xiaoniu.cleanking.ui.main.bean.JunkGroup;
import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.event.CleanEvent;
import com.xiaoniu.cleanking.ui.main.event.LifecycEvent;
import com.xiaoniu.cleanking.ui.main.widget.ScreenUtils;
import com.xiaoniu.cleanking.ui.newclean.activity.CleanFinishAdvertisementActivity;
import com.xiaoniu.cleanking.ui.newclean.activity.NewCleanFinishActivity;
import com.xiaoniu.cleanking.ui.newclean.activity.NowCleanActivity;
import com.xiaoniu.cleanking.ui.newclean.bean.ScanningResultType;
import com.xiaoniu.cleanking.ui.newclean.presenter.NewPlusCleanMainPresenter;
import com.xiaoniu.cleanking.ui.tool.notify.event.FinishCleanFinishActivityEvent;
import com.xiaoniu.cleanking.ui.tool.notify.event.FromHomeCleanFinishEvent;
import com.xiaoniu.cleanking.ui.tool.notify.event.FunctionCompleteEvent;
import com.xiaoniu.cleanking.ui.tool.notify.manager.NotifyCleanManager;
import com.xiaoniu.cleanking.ui.tool.wechat.activity.WechatCleanHomeActivity;
import com.xiaoniu.cleanking.ui.view.HomeInteractiveView;
import com.xiaoniu.cleanking.ui.view.HomeMainTableView;
import com.xiaoniu.cleanking.ui.view.HomeToolTableView;
import com.xiaoniu.cleanking.ui.viruskill.ArmVirusKillActivity;
import com.xiaoniu.cleanking.utils.AndroidUtil;
import com.xiaoniu.cleanking.utils.CleanUtil;
import com.xiaoniu.cleanking.utils.ExtraConstant;
import com.xiaoniu.cleanking.utils.FileQueryUtils;
import com.xiaoniu.cleanking.utils.LogUtils;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.cleanking.widget.ClearCardView;
import com.xiaoniu.cleanking.widget.OneKeyCircleButtonView;
import com.xiaoniu.common.utils.AppUtils;
import com.xiaoniu.common.utils.Points;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.utils.ToastUtils;
import com.xiaoniu.statistic.NiuDataAPI;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by xinxiaolong on 2020/6/30.
 * email：xinxiaolong123@foxmail.com
 */
public class NewPlusCleanMainFragment extends BaseFragment<NewPlusCleanMainPresenter> {

    private static final String TAG = "GeekSdk";

    @BindView(R.id.view_lottie_top)
    OneKeyCircleButtonView view_lottie_top;
    @BindView(R.id.home_main_table)
    HomeMainTableView homeMainTableView;
    @BindView(R.id.home_tool_table)
    HomeToolTableView homeToolTableView;
    @BindView(R.id.layout_clean_top)
    RelativeLayout layoutCleanTop;

    @BindView(R.id.clear_card_video)
    ClearCardView clearVideoLayout;
    @BindView(R.id.clear_card_image)
    ClearCardView clearImageLayout;
    @BindView(R.id.clear_card_sound)
    ClearCardView clearSoundLayout;

    @BindView(R.id.ad_one)
    FrameLayout adLayoutOne;
    @BindView(R.id.ad_two)
    FrameLayout adLayoutTwo;
    @BindView(R.id.ad_three)
    FrameLayout adLayoutThree;
    @BindView(R.id.image_interactive)
    HomeInteractiveView imageInteractive;

    @BindView(R.id.layout_scroll)
    NestedScrollView mScrollView;

    private boolean isThreeAdvOpen = false;
    private boolean hasInitThreeAdvOnOff = false;

    private int mNotifySize; //通知条数
    private int mPowerSize; //耗电应用数
    private int mRamScale; //使用内存占总RAM的比例
    private RxPermissions rxPermissions;

    private AlertDialog dlg;
    private CompositeDisposable compositeDisposable;

    private boolean isDenied = false;
    private boolean isFirst = true;

    @Override
    protected void inject(FragmentComponent fragmentComponent) {
        fragmentComponent.inject(this);
    }

    @Override
    public void netError() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_new_plus_clean_main;
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        rxPermissions = new RxPermissions(requireActivity());
        compositeDisposable = new CompositeDisposable();
        mPresenter.getInteractionSwitch();
        mPresenter.refBullList();
        homeMainTableView.initViewState();
        homeToolTableView.initViewState();

        initEvent();
        showHomeLottieView();
        initClearItemCard();
        checkAndUploadPoint();


        imageInteractive.setClickListener(new HomeInteractiveView.OnClickListener() {
            @Override
            public void onClick(InteractionSwitchList.DataBean.SwitchActiveLineDTOList data) {
                AppHolder.getInstance().setCleanFinishSourcePageId("home_page");
                StatisticsUtils.trackClick("Interaction_ad_click", "用户在首页点击互动式广告按钮（首页右上角图标）", "home_page", "home_page");
                if (data != null)
                    startActivity(new Intent(getActivity(), AgentWebViewActivity.class)
                            .putExtra(ExtraConstant.WEB_URL, data.getLinkUrl()));
            }
        });
    }


    private void initClearItemCard() {

        clearVideoLayout.setLeftTitle("视频文件");
        clearVideoLayout.setLeftIcon(R.mipmap.clear_icon_video);
        clearVideoLayout.setCommonItemImageRes(R.mipmap.clear_item_video);
        clearVideoLayout.getButton().setOnClickListener(view -> {
            //跳转到视频清理
            startActivity(new Intent(getActivity(), CleanVideoManageActivity.class));
        });

        clearImageLayout.setLeftTitle("图片");
        clearImageLayout.setLeftIcon(R.mipmap.clear_icon_img);
        clearImageLayout.setCommonItemImageRes(R.mipmap.clear_item_img);
        clearImageLayout.getButton().setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), ImageActivity.class);
            startActivity(intent);
        });

        clearSoundLayout.setLeftTitle("音频文件");
        clearSoundLayout.setLeftIcon(R.mipmap.clear_icon_sound);
        clearSoundLayout.setCommonItemImageRes(R.mipmap.clear_item_sound);
        clearSoundLayout.getButton().setOnClickListener(view -> {
            //跳转到音乐清理
            startActivity(new Intent(getActivity(), CleanMusicManageActivity.class));
        });
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

    }

    /**
     * 权限埋点上报
     */
    private void checkAndUploadPoint() {
        //SD卡读写权限是否打开埋点上报
        String storagePrmStatus = AppUtils.checkStoragePermission(getActivity()) ? "open" : "close";
        StatisticsUtils.customCheckPermission(Points.STORAGE_PERMISSION_EVENT_CODE, Points.STORAGE_PERMISSION_EVENT_NAME, storagePrmStatus, "", "home_page");
        //读取手机状态权限是否打开埋点上报
        String phoneStatePrmStatus = AppUtils.checkPhoneStatePermission(getActivity()) ? "open" : "close";
        StatisticsUtils.customCheckPermission(Points.DEVICE_IDENTIFICATION_EVENT_CODE, Points.DEVICE_IDENTIFICATION_EVENT_NAME, phoneStatePrmStatus, "", "home_page");
    }

    private void initEvent() {
        imageInteractive.setClickListener(new HomeInteractiveView.OnClickListener() {
            @Override
            public void onClick(InteractionSwitchList.DataBean.SwitchActiveLineDTOList data) {
                AppHolder.getInstance().setCleanFinishSourcePageId("home_page");
                StatisticsUtils.trackClick("Interaction_ad_click", "用户在首页点击互动式广告按钮（首页右上角图标）", "home_page", "home_page");
                if (data != null)
                    startActivity(new Intent(getActivity(), AgentWebViewActivity.class)
                            .putExtra(ExtraConstant.WEB_URL, data.getLinkUrl()));
            }
        });

        homeMainTableView.setOnItemClickListener(new HomeMainTableView.OnItemClick() {
            @Override
            public void onClick(int item) {
                switch (item) {
                    case HomeMainTableView.ITEM_ONE_KEY:
                        onOneKeySpeedClick();
                        break;
                    case HomeMainTableView.ITEM_KILL_VIRUS:
                        onKillVirusClick();
                        break;
                    case HomeMainTableView.ITEM_ELECTRIC:
                        onElectricClick();
                        break;
                }
            }
        });
        homeToolTableView.setOnItemClickListener(new HomeToolTableView.OnItemClick() {
            @Override
            public void onClick(int item) {
                switch (item) {
                    case HomeToolTableView.ITEM_WX:
                        onCleanWxClick();
                        break;
                    case HomeToolTableView.ITEM_TEMPERATURE:
                        onCoolingClick();
                        break;
                    case HomeToolTableView.ITEM_NOTIFY:
                        onCleanNotifyClick();
                        break;
                    case HomeToolTableView.ITEM_NETWORK:
                        onNetworkSpeedClick();
                        break;
                    case HomeToolTableView.ITEM_FOLDER:
                        onCleanFolderClick();
                        break;
                }
            }
        });
    }


    /*
     *********************************************************************************************************************************************************
     ************************************************************load one advInfo*****************************************************************************
     *********************************************************************************************************************************************************
     */

    private void loadOneAdvInfo() {
    }

    /*
     *********************************************************************************************************************************************************
     ************************************************************activity lifecycle*****************************************************************************
     *********************************************************************************************************************************************************
     */

    @Override
    public void onResume() {
        super.onResume();
        checkScanState();
        mNotifySize = NotifyCleanManager.getInstance().getAllNotifications().size();
        mPowerSize = new FileQueryUtils().getRunningProcess().size();
        imageInteractive.loadNextDrawable();
        NiuDataAPI.onPageStart("home_page_view_page", "首页浏览");

        if (isVisible() || isFirst) {
            isFirst = false;
            refreshAdvice();
        }
    }

    private void refreshAdvice() {
        if (AppHolder.getInstance().checkAdSwitch(PositionId.KEY_MAIN_ONE_AD)) {
            mPresenter.showAdviceLayout(adLayoutOne, MidasConstants.MAIN_ONE_ID);
        }
        if (AppHolder.getInstance().checkAdSwitch(PositionId.KEY_MAIN_TWO_AD)) {
            mPresenter.showAdviceLayout(adLayoutTwo, MidasConstants.MAIN_TWO_AD_ID);
        }
        if (AppHolder.getInstance().checkAdSwitch(PositionId.KEY_MAIN_THREE_AD)) {
            mPresenter.showAdviceLayout(adLayoutThree, MidasConstants.MAIN_THREE_AD_ID);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    /*
     *********************************************************************************************************************************************************
     ************************************************************eventBus notify******************************************************************************
     *********************************************************************************************************************************************************
     */

    //功能使用完成通知
    @Subscribe
    public void fromFunctionCompleteEvent(FunctionCompleteEvent event) {
        if (event == null || event.getTitle() == null) {
            return;
        }
        switch (event.getTitle()) {
            case "一键加速":
                homeMainTableView.oneKeySpeedUsedStyle();
                break;
            case "超强省电":
                homeMainTableView.electricUsedStyle();
                break;
            case "病毒查杀":
                homeMainTableView.killVirusUsedStyle();
                break;
            case "通知栏清理":
                homeToolTableView.postDelayed(() -> {
                    homeToolTableView.notifyUsedStyle();
                }, 2000);
                break;
            case "手机降温":
                homeToolTableView.coolingUsedStyle();
                break;
            case "微信专清":
                homeToolTableView.wxCleanUsedStyle();
                break;
            case "网络加速":
                //文案一直显示“有效提高20%”,暂不做刷新

                break;
        }
    }

    /**
     * 热启动回调
     */
    @Subscribe
    public void changeLifeCycleEvent(LifecycEvent lifecycEvent) {
        if (null == view_lottie_top) return;
        if (lifecycEvent.isActivity()) {

        }
        //热启动后重新检测权限
        isDenied = false;
        homeMainTableView.initViewState();
        homeToolTableView.initViewState();
    }

    //完成页返回通知
    @Subscribe
    public void fromHomeCleanFinishEvent(FromHomeCleanFinishEvent event) {


    }


    /*
     *********************************************************************************************************************************************************
     ************************************************************head oneKey clean start*********************************************************************
     *********************************************************************************************************************************************************
     */
//    @Deprecated
    private void showHomeLottieView() {
        int screenWidth = ScreenUtils.getScreenWidth(mContext);
        RelativeLayout.LayoutParams textLayout = (RelativeLayout.LayoutParams) view_lottie_top.getLayoutParams();
        textLayout.setMargins(0, 0 - Float.valueOf(screenWidth * 0.1f).intValue(), 0, 0);
        view_lottie_top.setLayoutParams(textLayout);
    }

    /**
     * EventBus 立即清理完成后，更新首页显示文案
     */
    @Subscribe
    public void onEventClean(CleanEvent cleanEvent) {
        if (cleanEvent != null) {
            if (cleanEvent.isCleanAminOver()) {
                String cleanedCache = MmkvUtil.getString(SpCacheConfig.MKV_KEY_HOME_CLEANED_DATA, "");
                CountEntity countEntity = new Gson().fromJson(cleanedCache, CountEntity.class);
                view_lottie_top.setClendedState(countEntity);
            }
        }
    }

    public void setScanningFinish(LinkedHashMap<ScanningResultType, JunkGroup> junkGroups) {
        long totalJunkSize = 0;
        for (Map.Entry<ScanningResultType, JunkGroup> map : junkGroups.entrySet()) {
            totalJunkSize += map.getValue().mSize;
        }
        CountEntity mCountEntity = CleanUtil.formatShortFileSize(totalJunkSize);
        ScanDataHolder.getInstance().setTotalSize(totalJunkSize);
        ScanDataHolder.getInstance().setmCountEntity(mCountEntity);
        ScanDataHolder.getInstance().setmJunkGroups(junkGroups);
        ScanDataHolder.getInstance().setScanState(1);
        if (view_lottie_top != null)
            view_lottie_top.scanFinish(totalJunkSize);
    }

    public void setScanningJunkTotal(long totalSize) {
        if (null != view_lottie_top)
            view_lottie_top.setTotalSize(totalSize);
    }

    public void permissionDenied() {//授权被拒绝
        if (null != view_lottie_top)
            view_lottie_top.setNoSize();
        isDenied = true;
    }

    //重新检测头部扫描状态
    public void checkScanState() {
        if (AppUtils.checkStoragePermission(getActivity())) {//已经获得权限
            if (PreferenceUtil.getNowCleanTime()) {  //清理结果五分钟以外
                if (ScanDataHolder.getInstance().getScanState() > 0 && null != ScanDataHolder.getInstance().getmCountEntity() && ScanDataHolder.getInstance().getTotalSize() > 50 * 1024 * 1024) {//扫描缓存5分钟内_展示缓存结果
                    setScanningJunkTotal(ScanDataHolder.getInstance().getTotalSize()); //展示缓存结果
                    view_lottie_top.scanFinish(ScanDataHolder.getInstance().getTotalSize());
                } else {//重新开始扫描
//                    mPresenter.cleanData();
                    mPresenter.readyScanningJunk();
                    mPresenter.scanningJunk();
                }
            } else { //清理结果五分钟以内
                String cleanedCache = MmkvUtil.getString(SpCacheConfig.MKV_KEY_HOME_CLEANED_DATA, "");
                CountEntity countEntity = new Gson().fromJson(cleanedCache, CountEntity.class);
                view_lottie_top.setClendedState(countEntity);

            }
        } else {//未取得权限
            LogUtils.i("--checkScanState()");
            //避免重复弹出
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!isDenied) {
                        mPresenter.checkStoragePermission();  //重新开始扫描
                    }
                }
            }, 200);
            //未授权默认样式——存在大量垃圾；
            if (null != view_lottie_top)
                view_lottie_top.setNoSize();

        }
    }


    /**
     * 点击立即清理
     */
    @OnClick(R.id.iv_center)
    public void nowClean() {
        StatisticsUtils.trackClick("home_page_clean_click", "用户在首页点击【立即清理】", "home_page", "home_page");
        if (PreferenceUtil.getNowCleanTime()) { //清理缓存五分钟_未扫过或者间隔五分钟以上
            if (ScanDataHolder.getInstance().getScanState() > 0 && ScanDataHolder.getInstance().getmJunkGroups().size() > 0) {//扫描缓存5分钟内——直接到扫描结果页
                //读取扫描缓存
                startActivity(NowCleanActivity.class);
            } else {    //scanState ==0: 扫描中
                checkStoragePermission();
            }
        } else {
            String cleanedCache = MmkvUtil.getString(SpCacheConfig.MKV_KEY_HOME_CLEANED_DATA, "");
            CountEntity countEntity = new Gson().fromJson(cleanedCache, CountEntity.class);
            if (null != countEntity && getActivity() != null && this.isAdded()) {
                Bundle bundle = new Bundle();
                bundle.putString("title", getResources().getString(R.string.tool_suggest_clean));
                bundle.putString("num", countEntity.getTotalSize());
                bundle.putString("unit", countEntity.getUnit());
                Intent intent = new Intent(requireActivity(), NewCleanFinishActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            } else {
                //判断扫描缓存；
                if (ScanDataHolder.getInstance().getScanState() > 0 && ScanDataHolder.getInstance().getmJunkGroups().size() > 0) {//扫描缓存5分钟内——直接到扫描结果页
                    //读取扫描缓存
                    startActivity(NowCleanActivity.class);
                } else {                //scanState ==0: 扫描中
                    checkStoragePermission();
                }
            }
        }
    }


    /**
     * 检查文件存贮权限
     */
    private void checkStoragePermission() {
        String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        Disposable disposable = rxPermissions.request(permissions)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        mPresenter.stopScanning();
                        startActivity(NowCleanActivity.class);
                    } else {
                        if (hasPermissionDeniedForever()) {  //点击拒绝
                            startActivity(NowCleanActivity.class);
                        } else {                            //点击永久拒绝
                            showPermissionDialog();
                        }
                    }
                });
        compositeDisposable.add(disposable);
    }

    public void showPermissionDialog() {
        dlg = new AlertDialog.Builder(requireContext()).create();
        if (requireActivity().isFinishing()) {
            return;
        }
        dlg.show();
        Window window = dlg.getWindow();
        if (window != null) {
            window.setContentView(R.layout.alite_redp_send_dialog);
            WindowManager.LayoutParams lp = window.getAttributes();
            //这里设置居中
            lp.gravity = Gravity.CENTER;
            window.setAttributes(lp);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            TextView btnOk = window.findViewById(R.id.btnOk);

            TextView btnCancle = window.findViewById(R.id.btnCancle);
            TextView tipTxt = window.findViewById(R.id.tipTxt);
            TextView content = window.findViewById(R.id.content);
            btnCancle.setText("取消");
            btnOk.setText("去设置");
            tipTxt.setText("提示!");
            content.setText("清理功能无法使用，请先开启文件读写权限。");
            btnOk.setOnClickListener(v -> {
                dlg.dismiss();
                goSetting();
            });
            btnCancle.setOnClickListener(v -> {
                dlg.dismiss();
            });
        }
    }


    public void goSetting() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + requireActivity().getPackageName()));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            requireActivity().startActivity(intent);
        }
    }

    /**
     * 是否有权限被永久拒绝
     */
    private boolean hasPermissionDeniedForever() {
        boolean hasDeniedForever = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                hasDeniedForever = true;
            }
        }
        return hasDeniedForever;
    }

    /*
     *********************************************************************************************************************************************************
     ************************************************************oneKey speed start************************************************************************
     *********************************************************************************************************************************************************
     */

    //click one key speed
    public void onOneKeySpeedClick() {
        AppHolder.getInstance().setCleanFinishSourcePageId("home_page");
        AppHolder.getInstance().setOtherSourcePageId(SpCacheConfig.ONKEY);
        StatisticsUtils.trackClick("boost_click", "用户在首页点击【一键加速】按钮", "home_page", "home_page");
        //保存本次清理完成时间 保证每次清理时间间隔为3分钟
        if (!PreferenceUtil.getCleanTime()) {
            initThreeAdvOnOffInfo();
            EventBus.getDefault().post(new FinishCleanFinishActivityEvent());
            if (isThreeAdvOpen && PreferenceUtil.getShowCount(getActivity(), getString(R.string.tool_one_key_speed), mRamScale, mNotifySize, mPowerSize) < 3) {
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

    private void initThreeAdvOnOffInfo() {
        if (hasInitThreeAdvOnOff) {
            return;
        }
        hasInitThreeAdvOnOff = true;
        if (null != AppHolder.getInstance().getSwitchInfoList() && null != AppHolder.getInstance().getSwitchInfoList().getData()
                && AppHolder.getInstance().getSwitchInfoList().getData().size() > 0) {
            for (SwitchInfoList.DataBean switchInfoList : AppHolder.getInstance().getSwitchInfoList().getData()) {
                if (PositionId.KEY_AD_PAGE_FINISH.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_THREE_CODE.equals(switchInfoList.getAdvertPosition())) {
                    isThreeAdvOpen = switchInfoList.isOpen();
                    break;
                }
            }
        }
    }



    /*
     * *********************************************************************************************************************************************************
     * **********************************************************kill virus start*******************************************************************************
     * *********************************************************************************************************************************************************
     */

    //click kill virus
    private void onKillVirusClick() {
        StatisticsUtils.trackClick("virus_killing_click", "用户在首页点击【病毒查杀】按钮", "home_page", "home_page");
        startKillVirusActivity();
    }

    //start kill virus page
    private void startKillVirusActivity() {
        if (PreferenceUtil.getVirusKillTime()) {
            startActivity(ArmVirusKillActivity.class);
        } else {
            Intent intent = new Intent(getActivity(), NewCleanFinishActivity.class);
            intent.putExtra("title", "病毒查杀");
            intent.putExtra("main", false);
            startActivity(intent);
        }
    }


    /*
     * *********************************************************************************************************************************************************
     * **********************************************************electric start*********************************************************************************
     * *********************************************************************************************************************************************************
     */

    public void onElectricClick() {
        AppHolder.getInstance().setCleanFinishSourcePageId("home_page");
        AppHolder.getInstance().setOtherSourcePageId(SpCacheConfig.SUPER_POWER_SAVING);
        StatisticsUtils.trackClick("powersave_click", "用户在首页点击【超强省电】按钮", "home_page", "home_page");
        if (PreferenceUtil.getPowerCleanTime()) {
            startActivity(PhoneSuperPowerActivity.class);
        } else {
            initThreeAdvOnOffInfo();
            if (isThreeAdvOpen && PreferenceUtil.getShowCount(getActivity(), getString(R.string.tool_super_power_saving), mRamScale, mNotifySize, mPowerSize) < 3) {
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

    /*
     * *********************************************************************************************************************************************************
     * **********************************************************folder clean start*****************************************************************************
     * *********************************************************************************************************************************************************
     */

    public void onCleanFolderClick() {
        StatisticsUtils.trackClick("cell_phone_clean_click", "\"手机清理\"点击", AppHolder.getInstance().getSourcePageId(), "acceleration_page");
        startActivity(RouteConstants.CLEAN_BIG_FILE_ACTIVITY);
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
            imageInteractive.setDataList(switchInfoList.getData().get(0).getSwitchActiveLineDTOList());
            imageInteractive.loadNextDrawable();
        } else {
            imageInteractive.setVisibility(View.GONE);
        }
    }



    /*
     * *********************************************************************************************************************************************************
     * **********************************************************wxClean start**********************************************************************************
     * *********************************************************************************************************************************************************
     */

    /**
     * 微信专清
     */
    public void onCleanWxClick() {
        AppHolder.getInstance().setCleanFinishSourcePageId("home_page");
        AppHolder.getInstance().setOtherSourcePageId(SpCacheConfig.WETCHAT_CLEAN);
        StatisticsUtils.trackClick("wxclean_click", "用户在首页点击【微信专清】按钮", "home_page", "home_page");
        if (!AndroidUtil.isAppInstalled(SpCacheConfig.CHAT_PACKAGE)) {
            ToastUtils.showShort(R.string.tool_no_install_chat);
            return;
        }
        if (PreferenceUtil.getWeChatCleanTime()) {
            // 每次清理间隔 至少3秒
            startActivity(WechatCleanHomeActivity.class);
        } else {
            initThreeAdvOnOffInfo();
            if (isThreeAdvOpen && PreferenceUtil.getShowCount(getActivity(), getString(R.string.tool_chat_clear), mRamScale, mNotifySize, mPowerSize) < 3) {
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


    /*
     * *********************************************************************************************************************************************************
     * **********************************************************notifyClean start******************************************************************************
     * *********************************************************************************************************************************************************
     */
    public void onCleanNotifyClick() {
//        ADUtilsKt.preloadingSplashAd(getActivity(), PositionId.AD_FINISH_BEFOR);
        AppHolder.getInstance().setCleanFinishSourcePageId("home_page");

        StatisticsUtils.trackClick("notification_clean_click", "用户在首页点击【通知清理】按钮", AppHolder.getInstance().getSourcePageId(), "home_page");
        if (PreferenceUtil.getNotificationCleanTime()) {
            NotifyCleanManager.startNotificationCleanActivity(getActivity(), 0);
        } else {
            initThreeAdvOnOffInfo();
            if (isThreeAdvOpen && PreferenceUtil.getShowCount(getActivity(), getString(R.string.tool_notification_clean), mRamScale, mNotifySize, mPowerSize) < 3) {
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


    /*
     * *********************************************************************************************************************************************************
     * **********************************************************cooling start**********************************************************************************
     * *********************************************************************************************************************************************************
     */

    public void onCoolingClick() {
        AppHolder.getInstance().setCleanFinishSourcePageId("home_page");
        StatisticsUtils.trackClick("cooling_click", "用户在首页点击【手机降温】按钮", AppHolder.getInstance().getSourcePageId(), "home_page");

        if (PreferenceUtil.getCoolingCleanTime()) {
            startActivity(RouteConstants.PHONE_COOLING_ACTIVITY);
        } else {
            initThreeAdvOnOffInfo();
            if (isThreeAdvOpen && PreferenceUtil.getShowCount(getActivity(), getString(R.string.tool_phone_temperature_low), mRamScale, mNotifySize, mPowerSize) < 3) {
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

    /*
     * *********************************************************************************************************************************************************
     * **********************************************************network  speed start***************************************************************************
     * *********************************************************************************************************************************************************
     */
    private void onNetworkSpeedClick() {
        if (PreferenceUtil.getSpeedNetWorkTime()) {
            startActivity(NetWorkActivity.class);
        } else {
            Intent intent = new Intent(getActivity(), NewCleanFinishActivity.class);
            String num = PreferenceUtil.getSpeedNetworkValue();
            intent.putExtra("title", "网络加速");
            intent.putExtra("main", false);
            intent.putExtra("num", num);
            startActivity(intent);
        }
    }


    /*
     * *********************************************************************************************************************************************************
     * ********************************************************** others ***************************************************************************************
     * *********************************************************************************************************************************************************
     */

    public View.OnClickListener getOnHomeTabClickListener() {
        return onClickListener;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //initGeekSdkCenter();
        }
    };
}
