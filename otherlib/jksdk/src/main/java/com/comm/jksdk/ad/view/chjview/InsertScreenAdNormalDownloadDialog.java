package com.comm.jksdk.ad.view.chjview;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTImage;
import com.bytedance.sdk.openadsdk.TTNativeAd;
import com.comm.jksdk.R;
import com.comm.jksdk.http.utils.LogUtils;
import com.comm.jksdk.widget.RoundImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * 半屏插屏广告下载类<p>
 *
 * @author zixuefei
 * @since 2019/11/18 21:33
 */
public class InsertScreenAdNormalDownloadDialog extends AlertDialog implements View.OnClickListener {
    private final String TAG = InsertScreenAdNormalDownloadDialog.class.getSimpleName();
    private TextView adName, adDes, adShowTime, adDownloadBtn, progressTv;
    private ImageView adCover, adClose;
    private RoundImageView appIcon;
    private CountDownTimer countDownTimer;
    private ConstraintLayout adContainer;

    private int showTimeSecond;
    private String mProgress;


    private OnClickListenr mListenr;

    public void setListenr(OnClickListenr listenr) {
        this.mListenr = listenr;
    }

    public void setProgress(String mProgress) {
        this.mProgress = mProgress;
        if (!TextUtils.isEmpty(mProgress)) {
            progressTv.setText("已提速"+mProgress+"%");
        }
    }

    protected InsertScreenAdNormalDownloadDialog(Context context, int showTimeSecond) {
        super(context, R.style.InsertScreenAdDialog);
        this.showTimeSecond = showTimeSecond;
        setCancelable(false);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
        window.setGravity(Gravity.CENTER);
        setContentView(R.layout.csj_insert_screen_normal_download_ad_view);
        initView();
    }

    private void initView() {
        adContainer = findViewById(R.id.insert_ad_container);
        adName = findViewById(R.id.full_screen_insert_ad_app_name);
        adDes = findViewById(R.id.full_screen_insert_ad_des);
        adShowTime = findViewById(R.id.full_screen_insert_ad_show_time_txt);
        adDownloadBtn = findViewById(R.id.full_screen_insert_ad_download);
        adCover = findViewById(R.id.full_screen_insert_ad_view);
        adClose = findViewById(R.id.full_screen_insert_ad_close);
        appIcon = findViewById(R.id.full_screen_insert_ad_app_icon);
        progressTv = findViewById(R.id.progree_tv);
        adShowTime.setText(showTimeSecond + "s");
        adShowTime.setVisibility(View.VISIBLE);
        countDownTimer = new CountDownTimer(showTimeSecond * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                adShowTime.setText(millisUntilFinished / 1000 + "s");

            }

            @Override
            public void onFinish() {
                adShowTime.setVisibility(View.GONE);
                adClose.setVisibility(View.VISIBLE);
            }
        };
        adClose.setOnClickListener(this);
    }

    public void loadAd(TTNativeAd ttNativeAd) {
        if (adCover == null) {
            dismiss();
            return;
        }
        if (ttNativeAd.getImageList() != null && !ttNativeAd.getImageList().isEmpty()) {
            TTImage image = ttNativeAd.getImageList().get(0);
            if (image != null && image.isValid()) {
                Glide.with(getContext()).load(image.getImageUrl()).into(adCover);
            }
        }

        TTImage icon = ttNativeAd.getIcon();
        if (icon != null && icon.isValid()) {
            Glide.with(getContext()).load(icon.getImageUrl()).into(appIcon);
        }

        adName.setText(ttNativeAd.getTitle());
        adDes.setText(ttNativeAd.getDescription());
        adDownloadBtn.setText(ttNativeAd.getInteractionType() == TTAdConstant.INTERACTION_TYPE_DOWNLOAD ? "下载" : "查看详情");
        countDownTimer.start();
        bindAd(ttNativeAd);
    }

    private void bindAd(TTNativeAd ttNativeAd) {
        //可以被点击的view, 比如标题、icon等,点击后尝试打开落地页，也可以把nativeView放进来意味整个广告区域可被点击
        List<View> clickViewList = new ArrayList<>();
        clickViewList.add(adName);
        clickViewList.add(adDes);
        clickViewList.add(adCover);
        clickViewList.add(appIcon);
        //触发创意广告的view（点击下载或拨打电话），比如可以设置为一个按钮，按钮上文案根据广告类型设定提示信息
        List<View> creativeViewList = new ArrayList<>();
        //如果需要点击图文区域也能进行下载或者拨打电话动作，请将图文区域的view传入
        //creativeViewList.add(nativeView);
        creativeViewList.add(adDownloadBtn);

        //重要! 这个涉及到广告计费，必须正确调用。convertView必须使用ViewGroup。
        ttNativeAd.registerViewForInteraction(adContainer, clickViewList, creativeViewList, new TTNativeAd.AdInteractionListener() {
            @Override
            public void onAdClicked(View view, TTNativeAd ad) {
                if (ad != null) {
                    LogUtils.d(TAG, "广告" + ad.getTitle() + "被点击");
                }
                if (mListenr != null) {
                    mListenr.onClick();
                }
            }

            @Override
            public void onAdCreativeClick(View view, TTNativeAd ad) {
                if (ad != null) {
                    LogUtils.d(TAG, "广告" + ad.getTitle() + "被创意按钮被点击");
                }
                if (mListenr != null) {
                    mListenr.onClick();
                }
            }

            @Override
            public void onAdShow(TTNativeAd ad) {
                if (ad != null) {
                    LogUtils.d(TAG, "广告" + ad.getTitle() + "展示");
                }
                if (mListenr != null) {
                    mListenr.onAdShow();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.full_screen_insert_ad_close) {
            dismiss();
            if (mListenr != null) {
                mListenr.onClose();
            }
        }
    }

    /**
     * 模板广告点击回调(内部使用)
     */
    public interface OnClickListenr {
        void onClick();
        void onAdShow();
        void onClose();
    }
}
