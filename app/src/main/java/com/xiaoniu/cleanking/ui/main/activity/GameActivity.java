package com.xiaoniu.cleanking.ui.main.activity;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.chuanshanjia.TTAdManagerHolder;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.adapter.GameSelectAdapter;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.presenter.GamePresenter;
import com.xiaoniu.cleanking.ui.tool.notify.event.SelectGameEvent;
import com.xiaoniu.cleanking.utils.ExtraConstant;
import com.xiaoniu.common.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @author XiLei
 * @date 2019/10/18.
 * description：游戏加速
 */
public class GameActivity extends BaseActivity<GamePresenter> implements View.OnClickListener, GameSelectAdapter.onCheckListener {

    @BindView(R.id.recycleview)
    RecyclerView mRecyclerView;
    @BindView(R.id.tv_open)
    TextView mOpenTv;

    private ArrayList<String> mSelectNameList;
    private ArrayList<FirstJunkInfo> mSelectList; //选择的应用列表
    private GameSelectAdapter mGameSelectAdapter;

    private TTAdNative mTTAdNative;
    private TTRewardVideoAd mttRewardVideoAd;
    private boolean mHasShowDownloadActive = false;

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
        mSelectNameList = new ArrayList<>();
        findViewById(R.id.iv_back).setOnClickListener(this);
        EventBus.getDefault().register(this);
        mOpenTv.setOnClickListener(this);
        mPresenter.getSwitchInfoList();
        initRecyclerView();
    }

    private void initRecyclerView() {
        mRecyclerView.setNestedScrollingEnabled(false);
        mGameSelectAdapter = new GameSelectAdapter(this);
        mGameSelectAdapter.setmOnCheckListener(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(mGameSelectAdapter);
        FirstJunkInfo firstJunkInfo = new FirstJunkInfo();
        firstJunkInfo.setGarbageIcon(getResources().getDrawable(R.drawable.icon_add));
        mSelectList = new ArrayList<>();
        mSelectList.add(firstJunkInfo);
        mGameSelectAdapter.setData(mSelectList);
        mOpenTv.setEnabled(false);
        mOpenTv.getBackground().setAlpha(75);
    }

    @Override
    public void onCheck(List<FirstJunkInfo> listFile, int pos) {
//        startActivity(new Intent(GameActivity.this, GameListActivity.class));
//        EventBus.getDefault().post(new SelectGameToListEvent(mSelectList));
        Intent intent = new Intent();
        intent.putExtra(ExtraConstant.SELECT_GAME_LIST, mSelectNameList);
        intent.setClass(this, GameListActivity.class);
        startActivity(intent);
    }

    @Subscribe
    public void selectGameEvent(SelectGameEvent event) {
        Log.d("XiLei", "selectGameEvent");
        Log.d("XiLei", "event.getList()=" + event.getList().size());
        if (null == mSelectList || null == event || null == event.getList())
            return;
        mSelectList.clear();
        FirstJunkInfo firstJunkInfo = new FirstJunkInfo();
        firstJunkInfo.setGarbageIcon(getResources().getDrawable(R.drawable.icon_add));
        mSelectList = new ArrayList<>();
        mSelectList.addAll(event.getList());
        mSelectList.add(firstJunkInfo);
//        Collections.reverse(mSelectList);
        mGameSelectAdapter.setData(mSelectList);
        mGameSelectAdapter.notifyDataSetChanged();
        for (int i = 0; i < mSelectList.size(); i++) {
            mSelectNameList.add(mSelectList.get(i).getAppName());
        }
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
        //step1:初始化sdk
        TTAdManager ttAdManager = TTAdManagerHolder.get();
        //step2:(可选，强烈建议在合适的时机调用):申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
        TTAdManagerHolder.get().requestPermissionIfNecessary(this);
        //step3:创建TTAdNative对象,用于调用广告请求接口
        mTTAdNative = ttAdManager.createAdNative(getApplicationContext());
        loadAd(id, TTAdConstant.VERTICAL);
    }


    /**
     * 拉取插屏广告开关成功
     *
     * @return
     */
    public void getScreentSwitchSuccess(SwitchInfoList list) {
        for (SwitchInfoList.DataBean switchInfoList : list.getData()) {
            /*if (getString(R.string.tool_suggest_clean).contains(mTitle) && PositionId.KEY_CLEAN_ALL.equals(switchInfoList.getConfigKey())) { //建议清理
                isScreenSwitchOpen = switchInfoList.isOpen();
                mScreenShowCount = switchInfoList.getShowRate();
            } else if (getString(R.string.tool_one_key_speed).contains(mTitle) && PositionId.KEY_JIASU.equals(switchInfoList.getConfigKey())) { //一键加速
                isScreenSwitchOpen = switchInfoList.isOpen();
                mScreenShowCount = switchInfoList.getShowRate();
            } else if (getString(R.string.tool_super_power_saving).contains(mTitle) && PositionId.KEY_CQSD.equals(switchInfoList.getConfigKey())) { //超强省电
                isScreenSwitchOpen = switchInfoList.isOpen();
                mScreenShowCount = switchInfoList.getShowRate();
            } else if (getString(R.string.tool_notification_clean).contains(mTitle) && PositionId.KEY_NOTIFY.equals(switchInfoList.getConfigKey())) {//通知栏清理
                isScreenSwitchOpen = switchInfoList.isOpen();
                mScreenShowCount = switchInfoList.getShowRate();
            } else if (getString(R.string.tool_chat_clear).contains(mTitle)) { //微信清理
                if (PositionId.KEY_WECHAT.equals(switchInfoList.getConfigKey())) {
                    isScreenSwitchOpen = switchInfoList.isOpen();
                    mScreenShowCount = switchInfoList.getShowRate();
                }
            } else if (getString(R.string.tool_phone_temperature_low).contains(mTitle) && PositionId.KEY_COOL.equals(switchInfoList.getConfigKey())) { //手机降温
                isScreenSwitchOpen = switchInfoList.isOpen();
                mScreenShowCount = switchInfoList.getShowRate();
            } else if (getString(R.string.tool_qq_clear).contains(mTitle) && PositionId.KEY_QQ.equals(switchInfoList.getConfigKey())) { //QQ专清
                isScreenSwitchOpen = switchInfoList.isOpen();
                mScreenShowCount = switchInfoList.getShowRate();
            } else if (getString(R.string.tool_phone_clean).contains(mTitle) && PositionId.KEY_PHONE.equals(switchInfoList.getConfigKey())) { //手机清理
                isScreenSwitchOpen = switchInfoList.isOpen();
                mScreenShowCount = switchInfoList.getShowRate();
            }*/
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_open:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                View view = View.inflate(this, R.layout.dialog_game, null);   // 账号、密码的布局文件，自定义
                AlertDialog dialog = builder.create();
                dialog.setView(view);
                view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                view.findViewById(R.id.btn_open).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showChuanShanJia();
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

    /**
     * 加载穿山甲激励视频广告
     *
     * @param codeId
     * @param orientation
     */
    private void loadAd(String codeId, int orientation) {
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
                        Log.d(TAG, "rewardVideoAd show");
                    }

                    @Override
                    public void onAdVideoBarClick() {
                        Log.d(TAG, "rewardVideoAd bar click");
                    }

                    @Override
                    public void onAdClose() {
                        Log.d(TAG, "rewardVideoAd close");
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.getScreentSwitch();
    }

    @Override
    protected void onPause() {
        super.onPause();
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
