package com.xiaoniu.cleanking.ui.weather.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.comm.jksdk.utils.DisplayUtil;
import com.google.gson.Gson;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.jess.arms.utils.LogUtils;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.midas.AdRequestParams;
import com.xiaoniu.cleanking.midas.MidasConstants;
import com.xiaoniu.cleanking.midas.MidasRequesCenter;
import com.xiaoniu.cleanking.ui.main.bean.WeatherForecastResponseEntity;
import com.xiaoniu.cleanking.ui.weather.contract.WeatherForecastContract;
import com.xiaoniu.cleanking.ui.weather.di.component.DaggerWeatherForecastComponent;
import com.xiaoniu.cleanking.ui.weather.presenter.WeatherForecastPresenter;
import com.xnad.sdk.ad.entity.AdInfo;
import com.xnad.sdk.ad.listener.AbsAdCallBack;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

import org.song.videoplayer.IVideoPlayer;
import org.song.videoplayer.JKQSVideoView;
import org.song.videoplayer.PlayListener;
import org.song.videoplayer.media.AndroidMedia;

import static com.jess.arms.utils.Preconditions.checkNotNull;


/**
 * ================================================
 * Description:  天气预报视频播放页面
 * <p>
 * Created on 07/08/2020 09:13
 */
public class WeatherForecastActivity extends BaseActivity<WeatherForecastPresenter> implements WeatherForecastContract.View {

    @BindView(R.id.iv_weather_forecast_back)
    ImageView ivWeatherForecastBack;
    @BindView(R.id.rl_weather_forecast_back)
    RelativeLayout rlWeatherForecastBack;
    @BindView(R.id.fl_weather_forecast_head_layout)
    FrameLayout flWeatherForecastHeadLayout;
    @BindView(R.id.qs_videoview)
    JKQSVideoView qsVideoview;
    @BindView(R.id.tv_weather_forecast_publish_time)
    TextView tvWeatherForecastPublishTime;
    @BindView(R.id.rel_root_ad)
    RelativeLayout relRootAd;

    private static final String weatherForecastResponseEntityKey = "WeatherForecastResponseEntity";
    private static final String weatherForecastPublishTimeKey = "WeatherForecastPublishTime";
    private WeatherForecastResponseEntity weatherForecastResponseEntity;
    private String publishSource;

    private JKQSVideoView qsVideoView;
    private boolean playFlag;//记录退出时播放状态 回来的时候继续播放
    private int position;//记录销毁时的进度 回来继续该进度播放
    private Handler handler = new Handler();


    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerWeatherForecastComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .view(this)
                .build()
                .inject(this);
    }

    /**
     * 启动
     * @param context
     * @param weatherForecastResponseEntity
     * @param publishSource
     */
    public static void launch(@NonNull Context context, @NonNull WeatherForecastResponseEntity weatherForecastResponseEntity, String publishSource) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent(context, WeatherForecastActivity.class);
        intent.putExtra(weatherForecastResponseEntityKey, weatherForecastResponseEntity);
        intent.putExtra(weatherForecastPublishTimeKey, publishSource);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVideoPlayer();
    }



    @Override
    public void onBackPressedSupport() {
        //全屏和系统浮窗不finish
        if (null != qsVideoView && qsVideoView.onBackPressed()) {
            return;
        }
        super.onBackPressedSupport();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (playFlag) {
            qsVideoView.play();
        }
        handler.removeCallbacks(runnable);
        if (position > 0) {
            qsVideoView.seekTo(position);
            position = 0;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(null == qsVideoView )
            return;
        if (qsVideoView.isSystemFloatMode()) {
            return;
        }
        //暂停
        playFlag = qsVideoView.isPlaying();
        qsVideoView.pause();
    }


    @Override
    protected void onStop() {
        super.onStop();
        if(null == qsVideoView )
            return;
        if (qsVideoView.isSystemFloatMode()) {
            return;
        }
        //进入后台不马上销毁,延时15秒
        handler.postDelayed(runnable, 1000 * 15);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();//销毁
        if(null == qsVideoView )
            return;
        if (qsVideoView.isSystemFloatMode()) {
            qsVideoView.quitWindowFloat();
        }
        qsVideoView.release();
        qsVideoView.destroy();
        handler.removeCallbacks(runnable);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(null == qsVideoView )
                return;
            if (qsVideoView.getCurrentState() != IVideoPlayer.STATE_AUTO_COMPLETE) {
                position = qsVideoView.getPosition();
            }
            qsVideoView.release();
        }
    };


    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_weather_forecast; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        weatherForecastResponseEntity = getIntent().getParcelableExtra(weatherForecastResponseEntityKey);
        publishSource = getIntent().getStringExtra(weatherForecastPublishTimeKey);

        LogUtils.debugInfo("zz----" + publishSource + "----" + new Gson().toJson(weatherForecastResponseEntity));
        rlWeatherForecastBack.setOnClickListener(v-> {
           finish();
        });
        requestAd();
        if (weatherForecastResponseEntity != null) {
            tvWeatherForecastPublishTime = findViewById(R.id.tv_weather_forecast_publish_time);
            if(TextUtils.isEmpty(publishSource)){
                tvWeatherForecastPublishTime.setVisibility(View.GONE);
            }else{
                tvWeatherForecastPublishTime.setVisibility(View.VISIBLE);
                tvWeatherForecastPublishTime.setText(publishSource);
            }
        }
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showMessage(@NonNull String message) {
        checkNotNull(message);
        ArmsUtils.snackbarText(message);
    }

    @Override
    public void launchActivity(@NonNull Intent intent) {
        checkNotNull(intent);
        ArmsUtils.startActivity(intent);
    }

    @Override
    public void killMyself() {
        finish();
    }

    /*---------------------------------------------------------广告请求--------------------------------------------------------------------------------------------------*/
    public void requestAd(){
        //todo_zz
        AdRequestParams params=new AdRequestParams.Builder().setAdId(MidasConstants.WEATHER_VIDEO_PAGE_BELOW)
                .setActivity(this).setViewContainer(relRootAd).build();
        MidasRequesCenter.requestAd(params, new AbsAdCallBack() {
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



    protected void initVideoPlayer() {
        qsVideoView = (JKQSVideoView) findViewById(R.id.qs_videoview);
        qsVideoView.setLayoutParams(new LinearLayout.LayoutParams(-1, DisplayUtil.getWidthPixels(this) * 9 / 16));
        qsVideoView.release();
        qsVideoView.setDecodeMedia(AndroidMedia.class);
        if (!TextUtils.isEmpty(weatherForecastResponseEntity.getVideoUrl())) {
            qsVideoView.setUp(weatherForecastResponseEntity.getVideoUrl(), "");
            //封面
//            Glide.with(this)
//                    .load(R.mipmap.zx_weather_forecast_bg)
//                    .into(qsVideoView.getCoverImageView());//封面
            qsVideoView.getCoverImageView().setImageResource(R.drawable.weather_forecast_bg);
            //设置监听
            qsVideoView.setPlayListener(new PlayListener() {
                @Override
                public void onStatus(int status) {//播放器的ui状态
                    if (status == IVideoPlayer.STATE_AUTO_COMPLETE) {
                        qsVideoView.quitWindowFullscreen();//播放完成退出全屏
                    }

                    if (status == IVideoPlayer.STATE_PLAYING) {
//                        WeatherForecastEvent playEvent = WeatherForecastEvent.getVideoPlayEvent(Statistic.ForecastVideoPage.PlayType.VIDEO_PLAY);
//                        WeatherForecastEventUtils.playClick(playEvent);
                    }

                    if (status == IVideoPlayer.STATE_PAUSE) {
//                        WeatherForecastEvent pauseEvent = WeatherForecastEvent.getVideoPlayEvent(Statistic.ForecastVideoPage.PlayType.VIDEO_STOP);
//                        WeatherForecastEventUtils.playClick(pauseEvent);
                    }

//                    if (statusView != null) {
//                        if (status == IVideoPlayer.STATE_ERROR) {
//                            statusView.showErrorView();
//                        } else {
//                            statusView.showContentView();
//                        }
//                    }
                }

                @Override//全屏/普通/浮窗...
                public void onMode(int mode) {

                }

                @Override//播放事件 初始化完成/缓冲/出错/点击事件...
                public void onEvent(int what, Integer... extra) {
                    if (what == JKQSVideoView.EVENT_CLICK_VIEW && extra != null) {
                        if (extra[0] == R.id.help_float_close) {

                        }
                    }
                }

            });
            //进入全屏的模式 0横屏 1竖屏 2传感器自动横竖屏 3根据视频比例自动确定横竖屏      -1什么都不做
            qsVideoView.enterFullMode = 0;
//            qsVideoView.openCache();//缓存配置见最后,缓存框架可能会出错,
            qsVideoView.play();
//            statusView.setVisibility(View.VISIBLE);
        } else {
//            statusView.showErrorView();
        }

//        initStatusView();
//        return qsVideoView;
}


}
