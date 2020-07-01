package com.xiaoniu.cleanking.ui.newclean.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.comm.jksdk.GeekAdSdk;
import com.comm.jksdk.ad.listener.AdManager;
import com.comm.jksdk.utils.MmkvUtil;
import com.google.gson.Gson;
import com.jzp.rotate3d.Rotate3D;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.FragmentComponent;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.BaseFragment;
import com.xiaoniu.cleanking.base.ScanDataHolder;
import com.xiaoniu.cleanking.ui.main.activity.PhoneAccessActivity;
import com.xiaoniu.cleanking.ui.main.activity.PhoneSuperPowerActivity;
import com.xiaoniu.cleanking.ui.main.bean.CountEntity;
import com.xiaoniu.cleanking.ui.main.bean.JunkGroup;
import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.event.CleanEvent;
import com.xiaoniu.cleanking.ui.main.event.LifecycEvent;
import com.xiaoniu.cleanking.ui.main.widget.ScreenUtils;
import com.xiaoniu.cleanking.ui.newclean.activity.CleanFinishAdvertisementActivity;
import com.xiaoniu.cleanking.ui.newclean.activity.NewCleanFinishActivity;
import com.xiaoniu.cleanking.ui.newclean.bean.ScanningResultType;
import com.xiaoniu.cleanking.ui.newclean.presenter.NewPlusCleanMainPresenter;
import com.xiaoniu.cleanking.ui.tool.notify.event.FinishCleanFinishActivityEvent;
import com.xiaoniu.cleanking.ui.tool.notify.event.FromHomeCleanFinishEvent;
import com.xiaoniu.cleanking.ui.tool.notify.event.FunctionCompleteEvent;
import com.xiaoniu.cleanking.ui.view.HomeMainTableView;
import com.xiaoniu.cleanking.ui.viruskill.ArmVirusKillActivity;
import com.xiaoniu.cleanking.utils.CleanUtil;
import com.xiaoniu.cleanking.utils.LogUtils;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.cleanking.widget.ClearCardView;
import com.xiaoniu.cleanking.widget.OneKeyCircleButtonView;
import com.xiaoniu.common.utils.AppUtils;
import com.xiaoniu.common.utils.Points;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.statistic.NiuDataAPI;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.BindView;

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
    @BindView(R.id.framelayout_top_ad)
    FrameLayout mTopAdFrameLayout;
    @BindView(R.id.layout_clean_top)
    RelativeLayout layoutCleanTop;

    //one key speed adv onOff info
    boolean isSpeedAdvOpen = false;
    boolean isSpeedAdvOpenThree = false;
    boolean hasInitSpeedAdvOnOff = false;

    //electric adv onOff info
    boolean isElectricAdvOpen = false;
    boolean isElectricAdvOpenThree = false;
    boolean hasInitElectricAdvOnOff = false;

    private int mNotifySize; //通知条数
    private int mPowerSize; //耗电应用数
    private int mRamScale; //使用内存占总RAM的比例

    private AdManager mAdManager;
    private Rotate3D anim;

    @BindView(R.id.clear_card_video)
    ClearCardView clearVideoLayout;
    @BindView(R.id.clear_card_image)
    ClearCardView clearImageLayout;
    @BindView(R.id.clear_card_sound)
    ClearCardView clearSoundLayout;

    @BindView(R.id.ffff)
    FrameLayout frameLayout;

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
        mAdManager = GeekAdSdk.getAdsManger();
        showHomeLottieView();
        initClearItemCard();
        refreshHomeMainTableView();
        initEvent();
        checkAndUploadPoint();

        anim = new Rotate3D.Builder(getActivity())
                .setParentView(layoutCleanTop)
                .setPositiveView(view_lottie_top)
                .setNegativeView(mTopAdFrameLayout)
                .create();


    }

    private void initClearItemCard() {
        clearVideoLayout.setLeftTitle("视频文件");
        clearVideoLayout.setLeftIcon(R.mipmap.clear_icon_video);
        clearVideoLayout.setCommonItemImageRes(R.mipmap.clear_item_video);
        clearVideoLayout.setOnClickListener(view -> {

        });

        clearImageLayout.setLeftTitle("图片");
        clearImageLayout.setLeftIcon(R.mipmap.clear_icon_img);
        clearImageLayout.setCommonItemImageRes(R.mipmap.clear_item_img);
        clearImageLayout.setOnClickListener(view -> {

        });

        clearSoundLayout.setLeftTitle("音频文件");
        clearSoundLayout.setLeftIcon(R.mipmap.clear_icon_sound);
        clearSoundLayout.setCommonItemImageRes(R.mipmap.clear_item_sound);
        clearSoundLayout.setOnClickListener(view -> {

        });
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

    private void refreshHomeMainTableView() {
        if (PreferenceUtil.getCleanTime()) {
            homeMainTableView.oneKeySpeedUnusedStyle();
        } else {
            homeMainTableView.oneKeySpeedUsedStyle();
        }
        if (PreferenceUtil.getVirusKillTime()) {
            homeMainTableView.killVirusUnusedStyle();
        } else {
            homeMainTableView.killVirusUsedStyle();
        }
        if (PreferenceUtil.getPowerCleanTime()) {
            homeMainTableView.electricUnusedStyle();
        } else {
            homeMainTableView.electricUsedStyle();
        }
    }

    private void initEvent() {
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
    }

    @Override
    public void onResume() {
        super.onResume();
        checkScanState();
        NiuDataAPI.onPageStart("home_page_view_page", "首页浏览");
    }

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
            case "通知栏清理":

                break;
            case "超强省电":
                homeMainTableView.electricUsedStyle();
                break;
            case "病毒查杀":
                homeMainTableView.killVirusUsedStyle();
                break;
        }
    }

    /**
     * 热启动回调
     */
    @Subscribe
    public void changeLifeCycleEvent(LifecycEvent lifecycEvent) {
        if (null == mTopAdFrameLayout || null == view_lottie_top) return;
        if (lifecycEvent.isActivity()) {
            closeAd();
        }
        //热启动后重新检测权限
        isDenied = false;

        refreshHomeMainTableView();
    }


    private void showAd(View adView) {
        mTopAdFrameLayout.removeAllViews();
        mTopAdFrameLayout.addView(adView);
        if (!anim.isOpen()) {
            anim.transform();
        }
    }

    private void closeAd() {
        if (anim.isOpen()) {
            anim.transform();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mTopAdFrameLayout != null)
                        mTopAdFrameLayout.removeAllViews();
                }
            }, 500);
        }
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

    /**
     * 静止时动画
     */
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

    private boolean isDenied = false;

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

    /*
     *********************************************************************************************************************************************************
     ************************************************************head oneKey clean end************************************************************************
     *********************************************************************************************************************************************************
     */


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
            initSpeedAdvOnOffInfo();
            EventBus.getDefault().post(new FinishCleanFinishActivityEvent());
            if (isSpeedAdvOpenThree) {
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.tool_one_key_speed));
                startActivity(CleanFinishAdvertisementActivity.class, bundle);
            } else if (isSpeedAdvOpen && PreferenceUtil.getShowCount(getActivity(), getString(R.string.tool_one_key_speed), mRamScale, mNotifySize, mPowerSize) < 3) {
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

    private void initSpeedAdvOnOffInfo() {
        if (hasInitSpeedAdvOnOff) {
            return;
        }
        hasInitSpeedAdvOnOff = true;
        if (null != AppHolder.getInstance().getSwitchInfoList() && null != AppHolder.getInstance().getSwitchInfoList().getData()
                && AppHolder.getInstance().getSwitchInfoList().getData().size() > 0) {
            for (SwitchInfoList.DataBean switchInfoList : AppHolder.getInstance().getSwitchInfoList().getData()) {
                if (PositionId.KEY_FINISH_SWITCH.equals(switchInfoList.getConfigKey())) {
                    isSpeedAdvOpenThree = switchInfoList.isOpen();
                }
                if (PositionId.KEY_JIASU.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_THREE_CODE.equals(switchInfoList.getAdvertPosition())) {
                    isSpeedAdvOpen = switchInfoList.isOpen();
                }
            }
        }
    }

    /*
     *********************************************************************************************************************************************************
     ************************************************************oneKey speed end*****************************************************************************
     *********************************************************************************************************************************************************
     */


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
     * **********************************************************kill virus end*********************************************************************************
     * *********************************************************************************************************************************************************
     */


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
            initElectricAdvOnOffInfo();
            if (isElectricAdvOpenThree) {
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.tool_super_power_saving));
                startActivity(CleanFinishAdvertisementActivity.class, bundle);
            } else if (isElectricAdvOpen && PreferenceUtil.getShowCount(getActivity(), getString(R.string.tool_super_power_saving), mRamScale, mNotifySize, mPowerSize) < 3) {
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

    private void initElectricAdvOnOffInfo() {
        if (hasInitElectricAdvOnOff) {
            return;
        }
        hasInitElectricAdvOnOff = true;
        if (null != AppHolder.getInstance().getSwitchInfoList() && null != AppHolder.getInstance().getSwitchInfoList().getData()
                && AppHolder.getInstance().getSwitchInfoList().getData().size() > 0) {
            for (SwitchInfoList.DataBean switchInfoList : AppHolder.getInstance().getSwitchInfoList().getData()) {
                if (PositionId.KEY_FINISH_SWITCH.equals(switchInfoList.getConfigKey())) {
                    isElectricAdvOpenThree = switchInfoList.isOpen();
                }
                if (PositionId.KEY_CQSD.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_THREE_CODE.equals(switchInfoList.getAdvertPosition())) {
                    isElectricAdvOpen = switchInfoList.isOpen();
                }
            }
        }
    }
    /*
     * *********************************************************************************************************************************************************
     * ********************************************************** electric end *********************************************************************************
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
