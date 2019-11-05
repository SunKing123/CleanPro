package com.xiaoniu.cleanking.ui.main.activity;

import android.animation.Animator;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.ApplicationDelegate;
import com.xiaoniu.cleanking.app.chuanshanjia.TTAdManagerHolder;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.adapter.GameSelectAdapter;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.bean.GameSelectEntity;
import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.presenter.GamePresenter;
import com.xiaoniu.cleanking.ui.newclean.activity.CleanFinishAdvertisementActivity;
import com.xiaoniu.cleanking.ui.newclean.activity.NewCleanFinishActivity;
import com.xiaoniu.cleanking.ui.tool.notify.event.FinishCleanFinishActivityEvent;
import com.xiaoniu.cleanking.ui.tool.notify.event.SelectGameEvent;
import com.xiaoniu.cleanking.ui.tool.notify.manager.NotifyCleanManager;
import com.xiaoniu.cleanking.utils.CleanUtil;
import com.xiaoniu.cleanking.utils.DisplayImageUtils;
import com.xiaoniu.cleanking.utils.ExtraConstant;
import com.xiaoniu.cleanking.utils.FileQueryUtils;
import com.xiaoniu.cleanking.utils.NiuDataAPIUtil;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.utils.ToastUtils;
import com.xiaoniu.statistic.NiuDataAPI;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @author XiLei
 * @date 2019/10/18.
 * description：游戏加速
 */
public class GameActivity extends BaseActivity<GamePresenter> implements View.OnClickListener, GameSelectAdapter.onCheckListener {

    @BindView(R.id.recycleview)
    RecyclerView mRecyclerView;
    @BindView(R.id.v_content)
    View mContentView;
    @BindView(R.id.v_open)
    View mOpenView;
    @BindView(R.id.tv_open)
    TextView mOpenTv;
    @BindView(R.id.acceview_yindao)
    LottieAnimationView mLottieAnimationViewY;
    @BindView(R.id.acceview_yindao2)
    LottieAnimationView mLottieAnimationViewY2;
    @BindView(R.id.acceview)
    LottieAnimationView mLottieAnimationView;
    @BindView(R.id.acceview2)
    LottieAnimationView mLottieAnimationView2;

    private List<FirstJunkInfo> mAllList; //所有应用列表
    private ArrayList<String> mSelectNameList;
    private ArrayList<FirstJunkInfo> mSelectList; //选择的应用列表
    private GameSelectAdapter mGameSelectAdapter;

    private TTAdNative mTTAdNative;
    private TTRewardVideoAd mttRewardVideoAd;
    private boolean mHasShowDownloadActive = false;
    private boolean mIsOpen;
    private int mNotifySize; //通知条数
    private int mPowerSize; //耗电应用数
    private int mRamScale; //所有应用所占内存大小
    private boolean mIsStartClean; //是否开始加速

    private static final String TAG = "ChuanShanJia";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_game;
    }

    @Override
    public void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    protected void initView() {
        findViewById(R.id.iv_back).setOnClickListener(this);
        EventBus.getDefault().register(this);
        mOpenTv.setOnClickListener(this);
        if (!PreferenceUtil.getGameQuikcenStart()) {
            NiuDataAPI.onPageStart("gameboost_guidance_page_view_page", "游戏加速引导页浏览");
            NiuDataAPIUtil.onPageEnd(AppHolder.getInstance().getCleanFinishSourcePageId(), "gameboost_guidance_page", "gameboost_guidance_page_view_page", "游戏加速引导页浏览");
            initLottieYinDao();
        } else {
            NiuDataAPI.onPageStart("gameboost_add_page_view_page", "游戏加速添加页浏览");
            NiuDataAPIUtil.onPageEnd(AppHolder.getInstance().getCleanFinishSourcePageId(), "gameboost_add_page", "gameboost_add_page_view_page", "游戏加速添加页浏览");
            mContentView.setVisibility(View.VISIBLE);
            mOpenView.setVisibility(View.VISIBLE);
        }

        mPresenter.getSwitchInfoList();
        initRecyclerView();
        mNotifySize = NotifyCleanManager.getInstance().getAllNotifications().size();
        mPowerSize = new FileQueryUtils().getRunningProcess().size();
        if (Build.VERSION.SDK_INT < 26) {
            mPresenter.getAccessListBelow();
        }
    }

    private void initLottieYinDao() {
        mLottieAnimationViewY.setVisibility(View.VISIBLE);
        mLottieAnimationViewY.useHardwareAcceleration(true);
        mLottieAnimationViewY.setAnimation("yindao1.json");
        mLottieAnimationViewY.setImageAssetsFolder("images_game_yindao");
        mLottieAnimationViewY.playAnimation();
        mLottieAnimationViewY.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                initLottieYinDao2();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private void initLottieYinDao2() {
        mLottieAnimationViewY2.setVisibility(View.VISIBLE);
        mLottieAnimationViewY2.useHardwareAcceleration(true);
        mLottieAnimationViewY2.setAnimation("yindao2.json");
        mLottieAnimationViewY2.setImageAssetsFolder("images_game_yindao2");
        mLottieAnimationViewY2.playAnimation();
        mLottieAnimationViewY2.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mOpenView.setVisibility(View.VISIBLE);
                mOpenTv.setEnabled(true);
                mOpenTv.getBackground().setAlpha(255);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private void initRecyclerView() {
        mSelectList = new ArrayList<>();
        mSelectNameList = new ArrayList<>();
        mRecyclerView.setNestedScrollingEnabled(false);
        mGameSelectAdapter = new GameSelectAdapter(this);
        mGameSelectAdapter.setmOnCheckListener(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(mGameSelectAdapter);

        //展示之前加速过的应用
        if (null != ApplicationDelegate.getAppDatabase() && null != ApplicationDelegate.getAppDatabase().gameSelectDao()
                && null != ApplicationDelegate.getAppDatabase().gameSelectDao().getAll()
                && ApplicationDelegate.getAppDatabase().gameSelectDao().getAll().size() > 0) {
            Observable<List<GameSelectEntity>> observable = Observable.create(new ObservableOnSubscribe<List<GameSelectEntity>>() {
                @Override
                public void subscribe(ObservableEmitter<List<GameSelectEntity>> emitter) throws Exception {
                    Log.d("XiLei", "subscribe2222:" + Thread.currentThread().getName());
                    emitter.onNext(ApplicationDelegate.getAppDatabase().gameSelectDao().getAll());
                }
            });
            Consumer<List<GameSelectEntity>> consumer = new Consumer<List<GameSelectEntity>>() {
                @Override
                public void accept(List<GameSelectEntity> list) throws Exception {
                    Log.d("XiLei", "accept:" + list.size() + ":" + Thread.currentThread().getName());
                    if (null == mSelectList || null == mSelectNameList || null == list || list.size() <= 0)
                        return;
                    for (int i = 0; i < list.size(); i++) {
                        FirstJunkInfo firstJunkInfo = new FirstJunkInfo();
                        firstJunkInfo.setAppName(list.get(i).getAppName());
                        firstJunkInfo.setGarbageIcon(DisplayImageUtils.bytesToDrawable(list.get(i).getGarbageIcon()));
                        mSelectList.add(firstJunkInfo);
                        mSelectNameList.add(list.get(i).getAppName());
                    }
                    FirstJunkInfo firstJunkInfo = new FirstJunkInfo();
                    firstJunkInfo.setGarbageIcon(getResources().getDrawable(R.drawable.icon_add));
                    mSelectList.add(firstJunkInfo);
                    Log.d("XiLei", "缓存的--mSelectList=" + mSelectList.size());
                    mGameSelectAdapter.setData(mSelectList);
                }
            };
            observable.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(consumer);
            mOpenTv.setEnabled(true);
            mOpenTv.getBackground().setAlpha(255);
        } else {
            mOpenTv.setEnabled(false);
            mOpenTv.getBackground().setAlpha(75);
            FirstJunkInfo firstJunkInfo = new FirstJunkInfo();
            firstJunkInfo.setGarbageIcon(getResources().getDrawable(R.drawable.icon_add));
            mSelectList.add(firstJunkInfo);
            mGameSelectAdapter.setData(mSelectList);
        }
    }

    @Override
    public void onCheck(List<FirstJunkInfo> listFile, int pos) {
        Intent intent = new Intent();
        intent.putExtra(ExtraConstant.SELECT_GAME_LIST, mSelectNameList);
        intent.setClass(this, GameListActivity.class);
        startActivity(intent);
    }

    /**
     * 选择游戏列表event事件
     *
     * @param event
     */
    @Subscribe
    public void selectGameEvent(SelectGameEvent event) {
        mAllList = event.getAllList();
        if (null == mSelectList) {
            mSelectList = new ArrayList<>();
        }
        Log.d("XiLei", "mSelectList=" + mSelectList.size());
        Log.d("XiLei", "mAllList=" + mAllList.size());
        Log.d("XiLei", "event.getList()=" + event.getList().size());
        if (null == event || null == event.getList() || event.getList().size() <= 0) {
            if (null != mSelectList && mSelectList.size() > 1 && event.isNotSelectAll()) {
                mOpenTv.setEnabled(false);
                mOpenTv.getBackground().setAlpha(75);
                mSelectList.clear();
                mSelectNameList.clear();
                FirstJunkInfo firstJunkInfo = new FirstJunkInfo();
                firstJunkInfo.setGarbageIcon(getResources().getDrawable(R.drawable.icon_add));
                mSelectList.add(firstJunkInfo);
            }
        } else {
            mOpenTv.setEnabled(true);
            mOpenTv.getBackground().setAlpha(255);
            mSelectList.clear();
            mSelectNameList.clear();
            FirstJunkInfo firstJunkInfo = new FirstJunkInfo();
            firstJunkInfo.setGarbageIcon(getResources().getDrawable(R.drawable.icon_add));
            mSelectList.addAll(event.getList());
            mSelectList.add(firstJunkInfo);
            for (int i = 0; i < mSelectList.size(); i++) {
                mSelectNameList.add(mSelectList.get(i).getAppName());
            }
        }
        mGameSelectAdapter.setData(mSelectList);
    }

    /**
     * 拉取广告开关成功
     *
     * @return
     */
    public void getSwitchInfoListSuccess(SwitchInfoList list) {
        if (null == list || null == list.getData() || list.getData().size() <= 0)
            return;
        for (SwitchInfoList.DataBean switchInfoList : list.getData()) {
            if (PositionId.KEY_GAME_JILI.equals(switchInfoList.getConfigKey())) {
                initChuanShanJia(switchInfoList.getAdvertId());
            }
            if (PositionId.KEY_GAME.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_THREE_CODE.equals(switchInfoList.getAdvertPosition())) {
                mIsOpen = switchInfoList.isOpen();
            }
        }
    }

    /**
     * 拉取广告开关失败
     *
     * @return
     */
    public void getSwitchInfoListFail() {
        ToastUtils.showShort(getString(R.string.net_error));
        mOpenTv.setEnabled(false);
        mOpenTv.getBackground().setAlpha(75);
    }

    /**
     * 初始化穿山甲
     */
    private void initChuanShanJia(String id) {
        NiuDataAPI.onPageStart("gameboost_incentive_video_page_view_page", "游戏加速激励视频页浏览");
        NiuDataAPIUtil.onPageEnd(AppHolder.getInstance().getCleanFinishSourcePageId(), "gameboost_incentive_video_page", "gameboost_incentive_video_page_view_page", "游戏加速激励视频页浏览");
        //step1:初始化sdk
        TTAdManager ttAdManager = TTAdManagerHolder.get();
        //step2:(可选，强烈建议在合适的时机调用):申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
        TTAdManagerHolder.get().requestPermissionIfNecessary(this);
        //step3:创建TTAdNative对象,用于调用广告请求接口
        mTTAdNative = ttAdManager.createAdNative(getApplicationContext());
        loadAd(id, TTAdConstant.VERTICAL);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                if (!PreferenceUtil.getGameQuikcenStart()) {
                    StatisticsUtils.trackClick("return_click", "游戏加速引导页返回按钮点击", AppHolder.getInstance().getCleanFinishSourcePageId(), "gameboost_guidance_page");
                } else if (mIsStartClean) {
                    StatisticsUtils.trackClick("return_click", "游戏加速动画页返回", AppHolder.getInstance().getCleanFinishSourcePageId(), "gameboost_animation_page");
                } else {
                    StatisticsUtils.trackClick("return_click", "游戏加速添加页返回", AppHolder.getInstance().getCleanFinishSourcePageId(), "gameboost_add_page");
                }
                finish();
                break;
            case R.id.tv_open:
                if (!PreferenceUtil.getGameQuikcenStart()) {
                    StatisticsUtils.trackClick("game_open_immediately_click", "游戏加速引导页立即开启点击", AppHolder.getInstance().getCleanFinishSourcePageId(), "gameboost_guidance_page");
                    mLottieAnimationViewY.setVisibility(View.GONE);
                    mLottieAnimationViewY2.setVisibility(View.GONE);
                    mOpenTv.setEnabled(false);
                    mOpenTv.getBackground().setAlpha(75);
                    mContentView.setVisibility(View.VISIBLE);
                    PreferenceUtil.saveGameQuikcenStart(true);
                    return;
                }
                NiuDataAPI.onPageStart("gameboost_video_popup_page_view_page", "游戏加速视频弹窗页浏览");
                NiuDataAPIUtil.onPageEnd(AppHolder.getInstance().getCleanFinishSourcePageId(), "gameboost_video_popup_page", "gameboost_video_popup_page_view_page", "游戏加速视频弹窗页浏览");
                StatisticsUtils.trackClick("gameboost_click", "游戏加速添加页点击加速按钮", AppHolder.getInstance().getCleanFinishSourcePageId(), "gameboost_add_page");
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                View view = View.inflate(this, R.layout.dialog_game, null);   // 账号、密码的布局文件，自定义
                AlertDialog dialog = builder.create();
                dialog.setView(view);
                view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        StatisticsUtils.trackClick("gameboost_cancel_click", "游戏加速视频弹窗页取消点击", AppHolder.getInstance().getCleanFinishSourcePageId(), "gameboost_video_popup_page");
                        dialog.dismiss();
                    }
                });
                view.findViewById(R.id.btn_open).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        StatisticsUtils.trackClick("gameboost_open_click", "游戏加速视频弹窗页开启点击", AppHolder.getInstance().getCleanFinishSourcePageId(), "gameboost_video_popup_page");
                        showChuanShanJia();
                        saveSelectApp();
                        dialog.dismiss();
                    }
                });
                dialog.show();
                Window dialogWindow = dialog.getWindow();//获取window对象
                dialogWindow.setGravity(Gravity.BOTTOM);//设置对话框位置
                dialogWindow.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);//设置横向全屏
                dialogWindow.setWindowAnimations(R.style.share_animation);//设置动画
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!PreferenceUtil.getGameQuikcenStart()) {
                StatisticsUtils.trackClick("system_return_click", "游戏加速引导页返回按钮点击", AppHolder.getInstance().getCleanFinishSourcePageId(), "gameboost_guidance_page");
            } else if (mIsStartClean) {
                StatisticsUtils.trackClick("system_return_click", "游戏加速动画页返回", AppHolder.getInstance().getCleanFinishSourcePageId(), "gameboost_animation_page");
            } else {
                StatisticsUtils.trackClick("system_return_click", "游戏加速添加页返回", AppHolder.getInstance().getCleanFinishSourcePageId(), "gameboost_add_page");
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 加载穿山甲激励视频广告
     *
     * @param codeId
     * @param orientation
     */
    private void loadAd(String codeId, int orientation) {
        StatisticsUtils.customADRequest("ad_request", "广告请求", "1", codeId, "穿山甲", "success", AppHolder.getInstance().getCleanFinishSourcePageId(), "gameboost_incentive_video_page");
        //step4:创建广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(codeId)
                .setSupportDeepLink(true)
                .setImageAcceptedSize(1080, 1920)
                .setRewardName("金币") //奖励的名称
                .setRewardAmount(3)  //奖励的数量
                .setUserID("user123")//用户id,必传参数
                .setMediaExtra("media_extra") //附加参数，可选
                .setOrientation(orientation) //必填参数，期望视频的播放方向：TTAdConstant.HORIZONTAL 或 TTAdConstant.VERTICAL
                .build();
        //step5:请求广告
        mTTAdNative.loadRewardVideoAd(adSlot, new TTAdNative.RewardVideoAdListener() {
            @Override
            public void onError(int code, String message) {
                Log.d(TAG, "message=" + message);
            }

            //视频广告加载后，视频资源缓存到本地的回调，在此回调后，播放本地视频，流畅不阻塞。
            @Override
            public void onRewardVideoCached() {
                Log.d(TAG, "rewardVideoAd video cached");
            }

            //视频广告的素材加载完毕，比如视频url等，在此回调后，可以播放在线视频，网络不好可能出现加载缓冲，影响体验。
            @Override
            public void onRewardVideoAdLoad(TTRewardVideoAd ad) {
                Log.d(TAG, "rewardVideoAd loaded");
                mttRewardVideoAd = ad;
                mttRewardVideoAd.setRewardAdInteractionListener(new TTRewardVideoAd.RewardAdInteractionListener() {

                    @Override
                    public void onAdShow() {
                        StatisticsUtils.customADRequest("ad_show", "广告展示曝光", "1", codeId, "穿山甲", "success", AppHolder.getInstance().getCleanFinishSourcePageId(), "gameboost_incentive_video_page");
                        Log.d(TAG, "rewardVideoAd show");
                    }

                    @Override
                    public void onAdVideoBarClick() {
                        StatisticsUtils.clickAD("ad_click", "广告点击", "1", codeId, "穿山甲", AppHolder.getInstance().getCleanFinishSourcePageId(), "gameboost_incentive_video_page", "");
                        Log.d(TAG, "rewardVideoAd bar click");
                    }

                    @Override
                    public void onAdClose() {
                        Log.d(TAG, "rewardVideoAd close");
                        startClean();
                    }

                    //视频播放完成回调
                    @Override
                    public void onVideoComplete() {
                        Log.d(TAG, "rewardVideoAd complete");
                    }

                    @Override
                    public void onVideoError() {
                        Log.d(TAG, "rewardVideoAd error");
                    }

                    //视频播放完成后，奖励验证回调，rewardVerify：是否有效，rewardAmount：奖励梳理，rewardName：奖励名称
                    @Override
                    public void onRewardVerify(boolean rewardVerify, int rewardAmount, String rewardName) {
                        Log.d(TAG, "verify:" + rewardVerify + " amount:" + rewardAmount + " name:" + rewardName);
                    }

                    @Override
                    public void onSkippedVideo() {
                        Log.d(TAG, "rewardVideoAd has onSkippedVideo");
                    }
                });
                mttRewardVideoAd.setDownloadListener(new TTAppDownloadListener() {
                    @Override
                    public void onIdle() {
                        mHasShowDownloadActive = false;
                    }

                    @Override
                    public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                        if (!mHasShowDownloadActive) {
                            mHasShowDownloadActive = true;
                            Log.d(TAG, "下载中，点击下载区域暂停");
                        }
                    }

                    @Override
                    public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                        Log.d(TAG, "下载暂停，点击下载区域继续");
                    }

                    @Override
                    public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                        Log.d(TAG, "下载失败，点击下载区域重新下载");
                    }

                    @Override
                    public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                        Log.d(TAG, "下载完成，点击下载区域重新下载");
                    }

                    @Override
                    public void onInstalled(String fileName, String appName) {
                        Log.d(TAG, "安装完成，点击下载区域打开");
                    }
                });
            }
        });
    }

    /**
     * 保存加速的应用
     */
    private void saveSelectApp() {
        mSelectList.remove(mSelectList.size() - 1);
        Log.d("XiLei", "mSelectList------333=" + mSelectList.size());
        ArrayList<GameSelectEntity> selectSaveList = new ArrayList<>();
        for (int i = 0; i < mSelectList.size(); i++) {
            if (null != mSelectList.get(i).getGarbageIcon() && mSelectList.get(i).getGarbageIcon().getIntrinsicWidth() > 0) {
                selectSaveList.add(new GameSelectEntity(i, mSelectList.get(i).getAppName(), DisplayImageUtils.drawableToBytes(mSelectList.get(i).getGarbageIcon())));
            }
        }
        Log.d("XiLei", "selectSaveList=" + selectSaveList.size());
        if (null == selectSaveList || selectSaveList.size() <= 0) return;
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                Log.d("XiLei", "subscribe:" + Thread.currentThread().getName());
                ApplicationDelegate.getAppDatabase().gameSelectDao().deleteAll();
                ApplicationDelegate.getAppDatabase().gameSelectDao().insertAll(selectSaveList);
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    /**
     * 展示穿山甲激励视频广告
     */
    private void showChuanShanJia() {
        if (mttRewardVideoAd != null) {
            //step6:在获取到广告后展示
            //该方法直接展示广告
//                    mttRewardVideoAd.showRewardVideoAd(RewardVideoActivity.this);
            //展示广告，并传入广告展示的场景
            mttRewardVideoAd.showRewardVideoAd(GameActivity.this, TTAdConstant.RitScenes.CUSTOMIZE_SCENES, "GameActivity");
            mttRewardVideoAd = null;
        } else {
            Log.d(TAG, "请先加载广告");
        }
    }

    /**
     * 开始加速
     */
    private void startClean() {
        mIsStartClean = true;
        NiuDataAPI.onPageStart("gameboost_animation_page_view_page", "游戏加速动画页浏览");
        NiuDataAPIUtil.onPageEnd(AppHolder.getInstance().getCleanFinishSourcePageId(), "gameboost_animation_page", "gameboost_animation_page_view_page", "游戏加速动画页浏览");
        PreferenceUtil.saveCleanGameUsed(true);
        mLottieAnimationView.setVisibility(View.VISIBLE);
        mLottieAnimationView.useHardwareAcceleration(true);
        mLottieAnimationView.setAnimation("huojian1.json");
        mLottieAnimationView.setImageAssetsFolder("images_game_one");
        mLottieAnimationView.playAnimation();
        mLottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                showAnimal2();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        if (null == mAllList || null == mSelectList || mAllList.size() <= 0 || mSelectList.size() <= 0)
            return;
        mSelectList.remove(mSelectList.size() - 1);
        Log.d("XiLei", "开始清理  mSelectList=" + mSelectList.size());
        Log.d("XiLei", "mAllList=" + mAllList.size());
        for (int i = 0; i < mAllList.size(); i++) {
            for (int j = 0; j < mSelectList.size(); j++) {
                if (mAllList.get(i).getAppName().equals(mSelectList.get(j).getAppName())) {
                    mAllList.remove(i);
                }
            }
        }
        Log.d("XiLei", "mAllList222=" + mAllList.size());
        for (FirstJunkInfo info : mAllList) {
            CleanUtil.killAppProcesses(info.getAppPackageName(), info.getPid());
        }
    }

    private void showAnimal2() {
        if (null == mLottieAnimationView2) return;
        mLottieAnimationView2.setVisibility(View.VISIBLE);
        mLottieAnimationView2.useHardwareAcceleration(true);
        mLottieAnimationView2.setAnimation("huojian2.json");
        mLottieAnimationView2.setImageAssetsFolder("images_game_two");
        mLottieAnimationView2.setRepeatCount(1); //动画再次执行的次数
        mLottieAnimationView2.playAnimation();
        mLottieAnimationView2.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //保存本次清理完成时间 保证每次清理时间间隔为3分钟
                if (PreferenceUtil.getGameTime()) {
                    PreferenceUtil.saveGameTime();
                }
                EventBus.getDefault().post(new FinishCleanFinishActivityEvent());
                if (mIsOpen && PreferenceUtil.getShowCount(GameActivity.this, getString(R.string.game_quicken), mRamScale, mNotifySize, mPowerSize) < 3) {
                    Bundle bundle = new Bundle();
                    bundle.putString("title", getString(R.string.game_quicken));
                    startActivity(CleanFinishAdvertisementActivity.class, bundle);
                } else {
                    AppHolder.getInstance().setOtherSourcePageId("once_accelerate_page");
                    Bundle bundle = new Bundle();
                    bundle.putString("title", getString(R.string.game_quicken));
                    startActivity(NewCleanFinishActivity.class, bundle);
                }
                finish();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void netError() {

    }

}