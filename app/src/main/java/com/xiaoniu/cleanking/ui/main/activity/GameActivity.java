package com.xiaoniu.cleanking.ui.main.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.comm.jksdk.GeekAdSdk;
import com.comm.jksdk.ad.entity.AdInfo;
import com.comm.jksdk.ad.listener.AdManager;
import com.comm.jksdk.ad.listener.VideoAdListener;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppLifecyclesImpl;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.adapter.GameSelectAdapter;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.bean.GameSelectEntity;
import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.presenter.GamePresenter;
import com.xiaoniu.cleanking.ui.newclean.activity.ScreenFinishBeforActivity;
import com.xiaoniu.cleanking.ui.tool.notify.event.FinishCleanFinishActivityEvent;
import com.xiaoniu.cleanking.ui.tool.notify.event.SelectGameEvent;
import com.xiaoniu.cleanking.utils.CleanUtil;
import com.xiaoniu.cleanking.utils.ExtraConstant;
import com.xiaoniu.cleanking.utils.NiuDataAPIUtil;
import com.xiaoniu.cleanking.utils.NumberUtils;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.utils.StatusBarUtil;
import com.xiaoniu.statistic.NiuDataAPI;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

    @BindView(R.id.v_title)
    View mTitleView;
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
    @BindView(R.id.acceview)
    LottieAnimationView mLottieAnimationView;
    @BindView(R.id.acceview3)
    LottieAnimationView mLottieAnimationView3;
    @BindView(R.id.v_acceview3)
    View mView3;
    @BindView(R.id.v_bg_lottie)
    View mViewBgLottie;
    @BindView(R.id.iv_scan_bg03)
    ImageView ivScanBg03;
    @BindView(R.id.iv_scan_bg02)
    ImageView ivScanBg02;
    @BindView(R.id.iv_scan_bg01)
    ImageView ivScanBg01;

    private List<FirstJunkInfo> mAllList; //所有应用列表
    private ArrayList<String> mSelectNameList;
    private ArrayList<FirstJunkInfo> mSelectList; //选择的应用列表
    private GameSelectAdapter mGameSelectAdapter;

    private boolean mIsOpen; //激励视频开关
    private boolean mIsStartClean; //是否开始加速
    private boolean mIsAdError; //激励视频加载失败
    private boolean mIsYinDaoFinish; //引导动画是否结束
    private AdManager mAdManager;
    private ImageView[] ivs;
    private boolean mIsFromHomeMain; //是否来自首页主功能区
    private boolean mAdExposed; //广告是否曝光

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
        StatusBarUtil.setTransparentForWindow(this);
        mIsFromHomeMain = getIntent().getBooleanExtra("main", false);
        findViewById(R.id.iv_back).setOnClickListener(this);
        EventBus.getDefault().register(this);
        mOpenTv.setOnClickListener(this);
        if (!mIsYinDaoFinish && !PreferenceUtil.getGameQuikcenStart()) {
            NiuDataAPI.onPageStart("gameboost_guidance_page_view_page", "游戏加速引导页浏览");
            NiuDataAPIUtil.onPageEnd(AppHolder.getInstance().getCleanFinishSourcePageId(), "gameboost_guidance_page", "gameboost_guidance_page_view_page", "游戏加速引导页浏览");
        } else {
            NiuDataAPI.onPageStart("gameboost_add_page_view_page", "游戏加速添加页浏览");
            NiuDataAPIUtil.onPageEnd("gameboost_guidance_page", "gameboost_add_page", "gameboost_add_page_view_page", "游戏加速添加页浏览");
        }
        initRecyclerView();
        //暂时注释
       /* if (!PreferenceUtil.getGameQuikcenStart() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            initLottieYinDao();
        } else {*/
        mContentView.setVisibility(View.VISIBLE);
        mOpenView.setVisibility(View.VISIBLE);
//        }
        ivs = new ImageView[]{ivScanBg01, ivScanBg02, ivScanBg03};
        initGeekAdSdk();
    }

    /**
     * 广告sdk
     */
    private void initGeekAdSdk() {
        if (mIsFromHomeMain) {
            StatisticsUtils.customADRequest("ad_request", "广告请求", "1", " ", " ", "all_ad_request", "main_function_area_gameboost_add_page", "main_function_area_gameboost_incentive_video_page");
        } else {
            StatisticsUtils.customADRequest("ad_request", "广告请求", "1", " ", " ", "all_ad_request", "gameboost_add_page", "gameboost_incentive_video_page");
        }
        mAdManager = GeekAdSdk.getAdsManger();
        if (null != AppHolder.getInstance().getSwitchInfoList() && null != AppHolder.getInstance().getSwitchInfoList().getData()
                && AppHolder.getInstance().getSwitchInfoList().getData().size() > 0) {
            for (SwitchInfoList.DataBean switchInfoList : AppHolder.getInstance().getSwitchInfoList().getData()) {
                if (PositionId.KEY_GAME_JILI.equals(switchInfoList.getConfigKey())) {
                    mIsOpen = switchInfoList.isOpen();
                }
            }
        }
    }

    private void initLottieYinDao() {
        mLottieAnimationViewY.setVisibility(View.VISIBLE);
        if (!mLottieAnimationViewY.isAnimating()) {
            mLottieAnimationViewY.setAnimation("yindao1.json");
            mLottieAnimationViewY.setImageAssetsFolder("images_game_yindao");
            mLottieAnimationViewY.playAnimation();
        }
        mLottieAnimationViewY.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mIsFromHomeMain) {
                    NiuDataAPI.onPageStart("main_function_area_gameboost_add_page_view_page", "主功能区游戏加速添加页浏览");
                    NiuDataAPIUtil.onPageEnd("main_function_area_gameboost_guidance_page", "main_function_area_gameboost_add_page", "main_function_area_gameboost_add_page_view_page", "主功能区游戏加速添加页浏览");
                } else {
                    NiuDataAPI.onPageStart("gameboost_add_page_view_page", "游戏加速添加页浏览");
                    NiuDataAPIUtil.onPageEnd("gameboost_guidance_page", "gameboost_add_page", "gameboost_add_page_view_page", "游戏加速添加页浏览");
                }
                mIsYinDaoFinish = true;
                if (null != mLottieAnimationViewY) {
                    mLottieAnimationViewY.cancelAnimation();
                    mLottieAnimationViewY.clearAnimation();
                    mLottieAnimationViewY.setVisibility(View.GONE);
                }
                mOpenView.setVisibility(View.VISIBLE);
                mContentView.setVisibility(View.VISIBLE);
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

        try {
            //展示之前加速过的应用
            if (null != AppLifecyclesImpl.getAppDatabase() && null != AppLifecyclesImpl.getAppDatabase().gameSelectDao()
                    && null != AppLifecyclesImpl.getAppDatabase().gameSelectDao().getAll()
                    && AppLifecyclesImpl.getAppDatabase().gameSelectDao().getAll().size() > 0) {
                Observable<List<GameSelectEntity>> observable = Observable.create(new ObservableOnSubscribe<List<GameSelectEntity>>() {
                    @Override
                    public void subscribe(ObservableEmitter<List<GameSelectEntity>> emitter) throws Exception {
                        emitter.onNext(AppLifecyclesImpl.getAppDatabase().gameSelectDao().getAll());
                    }
                });
                Consumer<List<GameSelectEntity>> consumer = new Consumer<List<GameSelectEntity>>() {
                    @Override
                    public void accept(List<GameSelectEntity> list) throws Exception {
                        if (null == mSelectList || null == mSelectNameList || null == list || list.size() <= 0)
                            return;

                        ArrayList<FirstJunkInfo> aboveListInfo = new ArrayList<>();
                        List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
                        for (int i = 0; i < packages.size(); i++) {
                            PackageInfo packageInfo = packages.get(i);
                            //判断是否系统应用
                            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) { //非系统应用
                                FirstJunkInfo tmpInfo = new FirstJunkInfo();
                                tmpInfo.setAppName(packageInfo.applicationInfo.loadLabel(getPackageManager()).toString());
                                tmpInfo.setGarbageIcon(packageInfo.applicationInfo.loadIcon(getPackageManager()));
                                tmpInfo.setAppPackageName(packageInfo.applicationInfo.packageName);
                                tmpInfo.setAppProcessName(packageInfo.applicationInfo.processName);
                                aboveListInfo.add(tmpInfo);
                            }
                        }
                        if (!aboveListInfo.isEmpty()) {
                            for (int i = 0; i < aboveListInfo.size(); i++) {
                                for (int j = 0; j < list.size(); j++) {
                                    if (aboveListInfo.get(i).getAppName().equals(list.get(j).getAppName())) {
                                        FirstJunkInfo firstJunkInfo = new FirstJunkInfo();
                                        firstJunkInfo.setAppName(aboveListInfo.get(i).getAppName());
                                        firstJunkInfo.setGarbageIcon(aboveListInfo.get(i).getGarbageIcon());
                                        mSelectList.add(firstJunkInfo);
                                        mSelectNameList.add(aboveListInfo.get(i).getAppName());
                                    }
                                }
                            }
                        }
                        FirstJunkInfo firstJunkInfo = new FirstJunkInfo();
                        firstJunkInfo.setGarbageIcon(getResources().getDrawable(R.drawable.icon_add));
                        mSelectList.add(firstJunkInfo);
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
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCheck(List<FirstJunkInfo> listFile, int pos) {
        Intent intent = new Intent();
        intent.putExtra(ExtraConstant.SELECT_GAME_LIST, mSelectNameList);
        intent.setClass(this, GameListActivity.class);
        intent.putExtra("main", getIntent().getBooleanExtra("main", false));
        startActivity(intent);
    }

    /**
     * 选择游戏列表event事件
     *
     * @param event
     */
    @Subscribe
    public void selectGameEvent(SelectGameEvent event) {
        if (null == event || null == event.getList()) return;
        mAllList = event.getAllList();
        if (null == mSelectList) {
            mSelectList = new ArrayList<>();
        }
        if (event.getList().size() <= 0) {
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
            for (int i = 0; i < event.getList().size(); i++) {
                mSelectNameList.add(event.getList().get(i).getAppName());
            }
            mSelectList.addAll(event.getList());
            FirstJunkInfo firstJunkInfo = new FirstJunkInfo();
            firstJunkInfo.setGarbageIcon(getResources().getDrawable(R.drawable.icon_add));
            mSelectList.add(firstJunkInfo);
        }
        mGameSelectAdapter.setData(mSelectList);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                if (!mIsYinDaoFinish && !PreferenceUtil.getGameQuikcenStart()) {
                    if (mIsFromHomeMain) {
                        StatisticsUtils.trackClick("return_click", "主功能区游戏加速引导页返回按钮点击", AppHolder.getInstance().getCleanFinishSourcePageId(), "main_function_area_gameboost_guidance_page");
                    } else {
                        StatisticsUtils.trackClick("return_click", "游戏加速引导页返回按钮点击", AppHolder.getInstance().getCleanFinishSourcePageId(), "gameboost_guidance_page");
                    }
                } else if (mIsStartClean) {
                    if (mIsFromHomeMain) {
                        StatisticsUtils.trackClick("return_click", "主功能区游戏加速动画页返回", "main_function_area_gameboost_incentive_video_end_page", "main_function_area_gameboost_animation_page");
                    } else {
                        StatisticsUtils.trackClick("return_click", "游戏加速动画页返回", "gameboost_incentive_video_end_page", "gameboost_animation_page");
                    }
                } else {
                    if (mIsFromHomeMain) {
                        StatisticsUtils.trackClick("return_click", "主功能区游戏加速添加页返回", "main_function_area_gameboost_guidance_page", "main_function_area_gameboost_add_page");
                    } else {
                        StatisticsUtils.trackClick("return_click", "游戏加速添加页返回", "gameboost_guidance_page", "gameboost_add_page");
                    }
                }
                finish();
                break;
            case R.id.tv_open:
                if (!mIsOpen) {
                    startClean();
                    return;
                }
                if (PreferenceUtil.getGameQuikcenStart()) {
                    if (mIsFromHomeMain) {
                        NiuDataAPIUtil.onPageEnd("main_function_area_gameboost_add_page", "main_function_area_gameboost_video_popup_page", "main_function_area_gameboost_video_popup_page_view_page", "主功能区游戏加速视频弹窗页浏览");
                        StatisticsUtils.trackClick("main_function_area_gameboost_open_click", "主功能区游戏加速视频弹窗页开启点击", "main_function_area_gameboost_add_page", "main_function_area_gameboost_video_popup_page");
                    } else {
                        NiuDataAPIUtil.onPageEnd("gameboost_add_page", "gameboost_video_popup_page", "gameboost_video_popup_page_view_page", "游戏加速视频弹窗页浏览");
                        StatisticsUtils.trackClick("gameboost_open_click", "游戏加速视频弹窗页开启点击", "gameboost_add_page", "gameboost_video_popup_page");
                    }
                    loadGeekAd();
                    saveSelectApp();
                    return;
                }
                if (mIsFromHomeMain) {
                    NiuDataAPI.onPageStart("main_function_area_gameboost_video_popup_page_view_page", "主功能区游戏加速视频弹窗页浏览");
                    StatisticsUtils.trackClick("gameboost_click", "主功能区游戏加速添加页点击加速按钮", "main_function_area_gameboost_guidance_page", "main_function_area_gameboost_add_page");
                } else {
                    NiuDataAPI.onPageStart("gameboost_video_popup_page_view_page", "游戏加速视频弹窗页浏览");
                    StatisticsUtils.trackClick("gameboost_click", "游戏加速添加页点击加速按钮", "gameboost_guidance_page", "gameboost_add_page");
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                View view = View.inflate(this, R.layout.dialog_game, null);   // 账号、密码的布局文件，自定义
                AlertDialog dialog = builder.create();
                dialog.setView(view);
                view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mIsFromHomeMain) {
                            NiuDataAPIUtil.onPageEnd("main_function_area_gameboost_add_page", "main_function_area_gameboost_video_popup_page", "main_function_area_gameboost_video_popup_page_view_page", "主功能区游戏加速视频弹窗页浏览");
                            StatisticsUtils.trackClick("gameboost_cancel_click", "主功能区游戏加速视频弹窗页取消点击", "main_function_area_gameboost_add_page", "main_function_area_gameboost_video_popup_page");
                        } else {
                            NiuDataAPIUtil.onPageEnd("gameboost_add_page", "gameboost_video_popup_page", "gameboost_video_popup_page_view_page", "游戏加速视频弹窗页浏览");
                            StatisticsUtils.trackClick("gameboost_cancel_click", "游戏加速视频弹窗页取消点击", "gameboost_add_page", "gameboost_video_popup_page");
                        }
                        dialog.dismiss();
                    }
                });
                view.findViewById(R.id.btn_open).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!mIsOpen) {
                            startClean();
                        } else {
                            if (mIsFromHomeMain) {
                                NiuDataAPIUtil.onPageEnd("main_function_area_gameboost_add_page", "main_function_area_gameboost_video_popup_page", "main_function_area_gameboost_video_popup_page_view_page", "主功能区游戏加速视频弹窗页浏览");
                                StatisticsUtils.trackClick("gameboost_open_click", "主功能区游戏加速视频弹窗页开启点击", "main_function_area_gameboost_add_page", "main_function_area_gameboost_video_popup_page");
                            } else {
                                NiuDataAPIUtil.onPageEnd("gameboost_add_page", "gameboost_video_popup_page", "gameboost_video_popup_page_view_page", "游戏加速视频弹窗页浏览");
                                StatisticsUtils.trackClick("gameboost_open_click", "游戏加速视频弹窗页开启点击", "gameboost_add_page", "gameboost_video_popup_page");
                            }
                            loadGeekAd();
                            saveSelectApp();
                        }
                        dialog.dismiss();
                    }
                });
                dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                        if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.getRepeatCount() == 0) {
                            if (mIsFromHomeMain) {
                                NiuDataAPIUtil.onPageEnd("main_function_area_gameboost_add_page", "main_function_area_gameboost_video_popup_page", "main_function_area_gameboost_video_popup_page_view_page", "主功能区游戏加速视频弹窗页浏览");
                                StatisticsUtils.trackClick("return_click", "主功能区游戏加速视频弹窗页返回", "main_function_area_gameboost_add_page", "main_function_area_gameboost_video_popup_page");
                            } else {
                                NiuDataAPIUtil.onPageEnd("gameboost_add_page", "gameboost_video_popup_page", "gameboost_video_popup_page_view_page", "游戏加速视频弹窗页浏览");
                                StatisticsUtils.trackClick("return_click", "游戏加速视频弹窗页返回", "gameboost_add_page", "gameboost_video_popup_page");
                            }
                            return true;
                        }
                        return false;
                    }
                });
                // umeng --  android.view.WindowManager$BadTokenException: Unable to add window -- token android.os.BinderProxy@1cb2817 is not valid; is your activity running?
                if (!isFinishing()) {
                    dialog.show();
                    Window dialogWindow = dialog.getWindow();//获取window对象
                    dialogWindow.setGravity(Gravity.BOTTOM);//设置对话框位置
                    dialogWindow.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);//设置横向全屏
                    dialogWindow.setWindowAnimations(R.style.share_animation);//设置动画
                }
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!mIsYinDaoFinish && !PreferenceUtil.getGameQuikcenStart()) {
                if (mIsFromHomeMain) {
                    StatisticsUtils.trackClick("system_return_click", "主功能区游戏加速引导页返回按钮点击", AppHolder.getInstance().getCleanFinishSourcePageId(), "main_function_area_gameboost_guidance_page");
                } else {
                    StatisticsUtils.trackClick("system_return_click", "游戏加速引导页返回按钮点击", AppHolder.getInstance().getCleanFinishSourcePageId(), "gameboost_guidance_page");
                }
            } else if (mIsStartClean) {
                if (mIsFromHomeMain) {
                    StatisticsUtils.trackClick("system_return_click", "主功能区游戏加速动画页返回", "main_function_area_gameboost_incentive_video_end_page", "main_function_area_gameboost_animation_page");
                } else {
                    StatisticsUtils.trackClick("system_return_click", "游戏加速动画页返回", "gameboost_incentive_video_end_page", "gameboost_animation_page");
                }
            } else {
                if (mIsFromHomeMain) {
                    StatisticsUtils.trackClick("system_return_click", "主功能区游戏加速添加页返回", "main_function_area_gameboost_guidance_page", "main_function_area_gameboost_add_page");
                } else {
                    StatisticsUtils.trackClick("system_return_click", "游戏加速添加页返回", "gameboost_guidance_page", "gameboost_add_page");
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 保存加速的应用
     */
    private void saveSelectApp() {
        if (null == mSelectList || mSelectList.size() <= 0) return;
        mSelectList.remove(mSelectList.size() - 1);
        ArrayList<GameSelectEntity> selectSaveList = new ArrayList<>();
        for (int i = 0; i < mSelectList.size(); i++) {
            selectSaveList.add(new GameSelectEntity(i, mSelectList.get(i).getAppName()));
        }
        if (null == selectSaveList || selectSaveList.size() <= 0) return;
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                //Log.d("XiLei", "subscribe:" + Thread.currentThread().getName());
                try {
                    AppLifecyclesImpl.getAppDatabase().gameSelectDao().deleteAll();
                    AppLifecyclesImpl.getAppDatabase().gameSelectDao().insertAll(selectSaveList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    /**
     * 激励视频
     */
    private void loadGeekAd() {
        if (null == mAdManager) return;
        if (mIsFromHomeMain) {
            NiuDataAPI.onPageStart("main_function_area_gameboost_incentive_video_page_view_page", "主功能区游戏加速激励视频页浏览");
            NiuDataAPIUtil.onPageEnd("main_function_area_gameboost_add_page", "main_function_area_gameboost_incentive_video_page", "main_function_area_gameboost_incentive_video_page_view_page", "主功能区游戏加速激励视频页浏览");
        } else {
            NiuDataAPI.onPageStart("gameboost_incentive_video_page_view_page", "游戏加速激励视频页浏览");
            NiuDataAPIUtil.onPageEnd("gameboost_add_page", "gameboost_incentive_video_page", "gameboost_incentive_video_page_view_page", "游戏加速激励视频页浏览");
        }
        mAdManager.loadRewardVideoAd(this, PositionId.AD_GAMEBOOST_VIDEO_AD, "user123", 1, new VideoAdListener() {
            @Override
            public void onVideoResume(AdInfo info) {

            }

            @Override
            public void onVideoRewardVerify(AdInfo info, boolean rewardVerify, int rewardAmount, String rewardName) {

            }

            @Override
            public void onVideoComplete(AdInfo info) {
                if (mIsFromHomeMain) {
                    NiuDataAPI.onPageStart("main_function_area_gameboost_incentive_video_end_page_view_page", "主功能区游戏加速激励视频结束页浏览");
                } else {
                    NiuDataAPI.onPageStart("gameboost_incentive_video_end_page_view_page", "游戏加速激励视频结束页浏览");
                }
            }

            @Override
            public void adSuccess(AdInfo info) {
                Log.d(TAG, "-----adSuccess-----");
                if (null == info) return;
                if (mIsFromHomeMain) {
                    StatisticsUtils.customADRequest("ad_request", "广告请求", "1", info.getAdId(), info.getAdSource(), "success", "main_function_area_gameboost_add_page", "main_function_area_gameboost_incentive_video_page");
                } else {
                    StatisticsUtils.customADRequest("ad_request", "广告请求", "1", info.getAdId(), info.getAdSource(), "success", "gameboost_add_page", "gameboost_incentive_video_page");
                }
            }

            @Override
            public void adExposed(AdInfo info) {
                Log.d(TAG, "-----adExposed-----");
                PreferenceUtil.saveShowAD(true);
                if (null == info) return;
                mAdExposed = true;
                if (mIsFromHomeMain) {
                    StatisticsUtils.customAD("ad_show", "广告展示曝光", "1", info.getAdId(), info.getAdSource(), "main_function_area_gameboost_add_page", "main_function_area_gameboost_incentive_video_page", info.getAdTitle());
                } else {
                    StatisticsUtils.customAD("ad_show", "广告展示曝光", "1", info.getAdId(), info.getAdSource(), "gameboost_add_page", "gameboost_incentive_video_page", info.getAdTitle());
                }
            }

            @Override
            public void adClicked(AdInfo info) {
                Log.d(TAG, "-----adClicked-----");
                if (null == info) return;
                if (mIsFromHomeMain) {
                    if (mAdExposed) {
                        StatisticsUtils.clickAD("ad_click", "主功能区游戏加速激励视频结束页下载点击", "1", info.getAdId(), info.getAdSource(), "main_function_area_gameboost_incentive_video_page", "main_function_area_gameboost_incentive_video_end_page", info.getAdTitle());
                    } else {
                        StatisticsUtils.clickAD("ad_click", "广告点击", "1", info.getAdId(), info.getAdSource(), "main_function_area_gameboost_add_page", "main_function_area_gameboost_incentive_video_page", info.getAdTitle());
                    }
                } else {
                    if (mAdExposed) {
                        StatisticsUtils.clickAD("ad_click", "游戏加速激励视频结束页下载点击", "1", info.getAdId(), info.getAdSource(), "gameboost_incentive_video_page", "gameboost_incentive_video_end_page", info.getAdTitle());
                    } else {
                        StatisticsUtils.clickAD("ad_click", "广告点击", "1", info.getAdId(), info.getAdSource(), "gameboost_add_page", "gameboost_incentive_video_page", info.getAdTitle());
                    }
                }
            }

            @Override
            public void adClose(AdInfo info) {
                Log.d(TAG, "-----adClose-----");
                PreferenceUtil.saveShowAD(false);
                if (mIsFromHomeMain) {
                    StatisticsUtils.trackClick("close_click", "主功能区游戏加速激励视频结束页关闭点击", "main_function_area_gameboost_incentive_video_page", "gmain_function_area_ameboost_incentive_video_end_page");
                    NiuDataAPIUtil.onPageEnd("main_function_area_gameboost_incentive_video_page", "main_function_area_gameboost_incentive_video_end_page", "main_function_area_gameboost_incentive_video_end_page_view_page", "主功能区游戏加速激励视频结束页浏览");
                } else {
                    StatisticsUtils.trackClick("close_click", "游戏加速激励视频结束页关闭点击", "gameboost_incentive_video_page", "gameboost_incentive_video_end_page");
                    NiuDataAPIUtil.onPageEnd("gameboost_incentive_video_page", "gameboost_incentive_video_end_page", "gameboost_incentive_video_end_page_view_page", "游戏加速激励视频结束页浏览");
                }
                startClean();
            }

            @Override
            public void adError(AdInfo info, int errorCode, String errorMsg) {
                Log.d(TAG, "-----adError-----" + errorMsg);
                if (null != info) {
                    if (mIsFromHomeMain) {
                        StatisticsUtils.customADRequest("ad_request", "广告请求", "1", info.getAdId(), info.getAdSource(), "fail", "main_function_area_gameboost_add_page", "main_function_area_gameboost_incentive_video_page");
                    } else {
                        StatisticsUtils.customADRequest("ad_request", "广告请求", "1", info.getAdId(), info.getAdSource(), "fail", "gameboost_add_page", "gameboost_incentive_video_page");
                    }
                }
                startClean();
            }

        });
    }

    /**
     * 开始加速
     */
    private void startClean() {

        /*if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) { //暂时注释
            clean();
            return;
        }*/
        if (null == mViewBgLottie) return;
        mIsStartClean = true;
        NiuDataAPI.onPageStart("gameboost_animation_page_view_page", "游戏加速动画页浏览");
        NiuDataAPIUtil.onPageEnd("gameboost_incentive_video_end_page", "gameboost_animation_page", "gameboost_animation_page_view_page", "游戏加速动画页浏览");
        PreferenceUtil.saveCleanGameUsed(true);
        mViewBgLottie.setVisibility(View.VISIBLE);
        showColorChange01(2);
        mTitleView.setVisibility(View.GONE);
        mLottieAnimationView.setVisibility(View.VISIBLE);
        if (!mLottieAnimationView.isAnimating()) {
            mLottieAnimationView.setAnimation("youxijiasu.json");
            mLottieAnimationView.setImageAssetsFolder("images_game_jiasu");
            mLottieAnimationView.playAnimation();
        }
        mLottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (null != mLottieAnimationView) {
                    mLottieAnimationView.cancelAnimation();
                    mLottieAnimationView.clearAnimation();
                }
                showAnimal3();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        if (null == mAllList || null == mSelectNameList || mAllList.size() <= 0 || mSelectNameList.size() <= 0
                || mAllList.size() < mSelectNameList.size())
            return;
        for (int i = 0; i < mAllList.size(); i++) {
            for (int j = 0; j < mSelectNameList.size(); j++) {
                if (i < mAllList.size() && j < mSelectNameList.size() && mAllList.get(i).getAppName().equals(mSelectNameList.get(j))) {
                    mAllList.remove(i);
                }
            }
        }
        for (FirstJunkInfo info : mAllList) {
            CleanUtil.killAppProcesses(info.getAppPackageName(), info.getPid());
        }
    }

    private void showAnimal3() {
        if (null == mLottieAnimationView3) return;
        mLottieAnimationView.setVisibility(View.GONE);
        mView3.setVisibility(View.VISIBLE);
        if (!mLottieAnimationView3.isAnimating()) {
            mLottieAnimationView3.setAnimation("yindao2.json");
            mLottieAnimationView3.setImageAssetsFolder("images_game_yindao2");
            mLottieAnimationView3.playAnimation();
        }
        mLottieAnimationView3.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (null != mLottieAnimationView3) {
                    mLottieAnimationView3.cancelAnimation();
                    mLottieAnimationView3.clearAnimation();
                }
                clean();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

    }

    private void clean() {
        //保存本次清理完成时间 保证每次清理时间间隔为3分钟
        if (PreferenceUtil.getGameTime()) {
            PreferenceUtil.saveGameTime();
        }
        PreferenceUtil.saveGameQuikcenStart(true);
        EventBus.getDefault().post(new FinishCleanFinishActivityEvent());
        AppHolder.getInstance().setCleanFinishSourcePageId("gameboost_animation_page");
        String num = NumberUtils.mathRandom(25, 50);
        PreferenceUtil.saveGameCleanPer(num);
        startActivity(new Intent(GameActivity.this, ScreenFinishBeforActivity.class)
                .putExtra(ExtraConstant.TITLE, getString(R.string.game_quicken))
                .putExtra(ExtraConstant.NUM, num)
                .putExtra("main", getIntent().getBooleanExtra("main", false)));
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (null != mLottieAnimationViewY && mLottieAnimationViewY.isAnimating()) {
            mLottieAnimationViewY.cancelAnimation();
            mLottieAnimationViewY.clearAnimation();
        }
        if (null != mLottieAnimationView && mLottieAnimationView.isAnimating()) {
            mLottieAnimationView.cancelAnimation();
            mLottieAnimationView.clearAnimation();
        }
        if (null != mLottieAnimationView3 && mLottieAnimationView3.isAnimating()) {
            mLottieAnimationView3.cancelAnimation();
            mLottieAnimationView3.clearAnimation();
        }
    }

    @Override
    public void netError() {

    }

    public void showColorChange01(int index) {
        if (ivs.length == 3 && index <= 2 && index > 0) {
            Drawable drawable = ivs[index].getBackground();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(drawable, PropertyValuesHolder.ofInt("alpha", 0));
                animator.setTarget(drawable);
                animator.setDuration(2000);
                if (!animator.isRunning()) {
                    animator.start();
                }
                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (index == 1) {
                            Log.v("onAnimationEnd", "onAnimationEnd ");
                            if (animator != null)
                                animator.cancel();
                        } else {
                            showColorChange01((index - 1));
                        }

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
}