package com.xiaoniu.cleanking.ui.tool.wechat.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppManager;
import com.xiaoniu.cleanking.app.Constant;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.activity.WXCleanImgActivity;
import com.xiaoniu.cleanking.ui.main.activity.WXCleanVideoActivity;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.event.WxQqCleanEvent;
import com.xiaoniu.cleanking.ui.main.widget.ViewHelper;
import com.xiaoniu.cleanking.ui.newclean.activity.CleanFinishAdvertisementActivity;
import com.xiaoniu.cleanking.ui.newclean.activity.NewCleanFinishActivity;
import com.xiaoniu.cleanking.ui.tool.notify.event.FinishCleanFinishActivityEvent;
import com.xiaoniu.cleanking.ui.tool.notify.manager.NotifyCleanManager;
import com.xiaoniu.cleanking.ui.tool.wechat.bean.CleanWxEasyInfo;
import com.xiaoniu.cleanking.ui.tool.wechat.presenter.WechatCleanHomePresenter;
import com.xiaoniu.cleanking.ui.tool.wechat.util.WxQqUtil;
import com.xiaoniu.cleanking.utils.CleanAllFileScanUtil;
import com.xiaoniu.cleanking.utils.FileQueryUtils;
import com.xiaoniu.cleanking.utils.NiuDataAPIUtil;
import com.xiaoniu.cleanking.utils.NumberUtils;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.common.utils.DisplayUtils;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.statistic.NiuDataAPI;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 微信清理首页
 */
public class WechatCleanHomeActivity extends BaseActivity<WechatCleanHomePresenter> {
    @BindView(R.id.tv_gabsize)
    TextView tvGabsize;
    @BindView(R.id.tv_gb)
    TextView tvGb;
    @BindView(R.id.rel_gasize)
    RelativeLayout relGasize;
    @BindView(R.id.rel_selects)
    RelativeLayout relSelects;
    @BindView(R.id.tv_ql)
    TextView tv_ql;
    @BindView(R.id.tv1_top)
    TextView tv1Top;
    @BindView(R.id.tv_video_size)
    TextView tvVideoSize;
    @BindView(R.id.tv_pic_size)
    TextView tvPicSize;
    @BindView(R.id.tv_file_size)
    TextView tvFileSize;
    @BindView(R.id.tv_aud_size)
    TextView tvAudSize;
    @BindView(R.id.tv_wxprogram)
    TextView tvWxprogram;
    @BindView(R.id.tv_wxgabage_size)
    TextView tvWxgabageSize;
    @BindView(R.id.tv1_file)
    TextView tv1File;
    @BindView(R.id.tv_delete)
    TextView tvDelete;
    @BindView(R.id.tv_select1)
    TextView tvSelect1;
    @BindView(R.id.tv_select)
    TextView tvSelect;
    @BindView(R.id.tv_select_size)
    TextView tvSelectSize;
    @BindView(R.id.iv_dun)
    ImageView iv_dun;
    @BindView(R.id.iv_gabcache)
    ImageView ivGabcache;
    @BindView(R.id.iv_wxxcx)
    ImageView ivWxxcx;
    @BindView(R.id.iv_scan_frame)
    ImageView ivScanFrame;
    @BindView(R.id.iv_chatfile)
    ImageView ivChatfile;
    @BindView(R.id.iv_hua3)
    ImageView ivHua3;
    @BindView(R.id.iv_hua2)
    ImageView ivHua2;
    @BindView(R.id.iv_hua1)
    ImageView ivHua1;
    @BindView(R.id.cons_gabcache)
    ConstraintLayout consGabcache;
    @BindView(R.id.cons_wxxcx)
    ConstraintLayout consWxxcx;
    @BindView(R.id.cons_allfiles)
    ConstraintLayout consAllfiles;
    @BindView(R.id.line_sming)
    LinearLayout lineSming;
    @BindView(R.id.line_smed)
    LinearLayout lineSmed;
    ObjectAnimator objectAnimatorScanIng;
    private int mNotifySize; //通知条数
    private int mPowerSize; //耗电应用数
    private int mRamScale; //所有应用所占内存大小

    /*埋点相关*/
    private String returnEventName = "";
    private String sysReturnEventName = "";

    private String currentPageId = "";
    private String sourcePageId = "";

    private String viewPageEventName = "";
    private String viewPageEventCode = "";


    @Override
    public int getLayoutId() {
        return R.layout.activity_wxclean_home;
    }

    @Override
    public void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    public void initView() {

        //埋点参数相关
        String preName = AppManager.getAppManager().preActivityName();
        if (preName.contains("MainActivity")) {
            sourcePageId = "home_page";
        }
        currentPageId = "wxclean_scan_page";
        returnEventName = "用户在微信清理扫描页点击返回";
        sysReturnEventName = "用户在微信清理扫描页点击返回";
        viewPageEventName = "用户在微信清理扫描页浏览";
        viewPageEventCode = "wxclean_scan_page_view_page";
        NiuDataAPI.onPageStart(viewPageEventCode, viewPageEventName);
        NiuDataAPIUtil.onPageEnd(sourcePageId, currentPageId, viewPageEventCode, viewPageEventName);

        mNotifySize = NotifyCleanManager.getInstance().getAllNotifications().size();
        mPowerSize = new FileQueryUtils().getRunningProcess().size();
        if (Build.VERSION.SDK_INT < 26) {
            mPresenter.getAccessListBelow();
        }
        //注册订阅者
        EventBus.getDefault().register(this);
        ViewHelper.setTextViewToDDINOTF(tvGabsize);
        ViewHelper.setTextViewToDDINOTF(tvGb);
        tvSelect.setSelected(true);
        tvSelect1.setSelected(true);
        lineSming.setVisibility(View.VISIBLE);
        lineSmed.setVisibility(View.GONE);
        setScanStatus(true);
        mPresenter.scanWxGabage();
        objectAnimatorScanIng = mPresenter.setScaningAnim(ivScanFrame);
    }

    @OnClick({R.id.cons_aud, R.id.iv_gabcache, R.id.tv1_top, R.id.tv1_wxxcx, R.id.iv_wxxcx, R.id.tv1_file, R.id.iv_chatfile
            , R.id.iv_back, R.id.tv_delete, R.id.tv_select, R.id.tv_select1, R.id.cons_file
            , R.id.cons_wxsp, R.id.cons_pic})
    public void onClickView(View view) {
        int ids = view.getId();
        if (ids == R.id.iv_back) {
            StatisticsUtils.trackClick("return_click", returnEventName, sourcePageId, currentPageId);
            finish();
        } else if (ids == R.id.iv_gabcache) {
            consGabcache.setVisibility(consGabcache.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            ivGabcache.setImageResource(consGabcache.getVisibility() == View.VISIBLE ? R.mipmap.arrow_up : R.mipmap.arrow_down);
        } else if (ids == R.id.tv1_top) {
            consGabcache.setVisibility(consGabcache.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            ivGabcache.setImageResource(consGabcache.getVisibility() == View.VISIBLE ? R.mipmap.arrow_up : R.mipmap.arrow_down);
        } else if (ids == R.id.tv1_wxxcx) {
            consWxxcx.setVisibility(consWxxcx.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            ivWxxcx.setImageResource(consWxxcx.getVisibility() == View.VISIBLE ? R.mipmap.arrow_up : R.mipmap.arrow_down);
        } else if (ids == R.id.iv_wxxcx) {
            consWxxcx.setVisibility(consWxxcx.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            ivWxxcx.setImageResource(consWxxcx.getVisibility() == View.VISIBLE ? R.mipmap.arrow_up : R.mipmap.arrow_down);
        } else if (ids == R.id.tv1_file) {
            consAllfiles.setVisibility(consAllfiles.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            ivChatfile.setImageResource(consAllfiles.getVisibility() == View.VISIBLE ? R.mipmap.arrow_up : R.mipmap.arrow_down);
        } else if (ids == R.id.iv_chatfile) {
            consAllfiles.setVisibility(consAllfiles.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            ivChatfile.setImageResource(consAllfiles.getVisibility() == View.VISIBLE ? R.mipmap.arrow_up : R.mipmap.arrow_down);
        } else if (ids == R.id.tv_delete) {
            //清理点击事件
            StatisticsUtils.trackClick("wx_cleaning_button_click", "用户在微信清理诊断页点击【清理】按钮", sourcePageId, currentPageId);

            sourcePageId = "wxclean_scan_result_page";
            currentPageId = "wxclean_animation_page";
            returnEventName = "用户在微信清理诊断页返回";
            sysReturnEventName = "用户在微信清理动画页返回";
            viewPageEventName = "用户在微信清理动画页浏览";
            viewPageEventCode = "wxclean_animation_page_view_page";
            NiuDataAPI.onPageStart(viewPageEventCode, viewPageEventName);
            NiuDataAPIUtil.onPageEnd(sourcePageId, currentPageId, viewPageEventCode, viewPageEventName);

            if (WxQqUtil.e.getTotalSize() + WxQqUtil.d.getTotalSize() + WxQqUtil.g.getTotalSize() + WxQqUtil.f.getTotalSize() == 0) {
                PreferenceUtil.saveCleanWechatUsed(true);
                boolean isOpen = false;
                //solve umeng error --> SwitchInfoList.getData()' on a null object reference
                if (null != AppHolder.getInstance().getSwitchInfoList() && null != AppHolder.getInstance().getSwitchInfoList().getData()
                        && AppHolder.getInstance().getSwitchInfoList().getData().size() > 0) {
                    for (SwitchInfoList.DataBean switchInfoList : AppHolder.getInstance().getSwitchInfoList().getData()) {
                        if (PositionId.KEY_WECHAT.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_THREE_CODE.equals(switchInfoList.getAdvertPosition())) {
                            isOpen = switchInfoList.isOpen();
                        }
                    }
                }
                AppHolder.getInstance().setCleanFinishSourcePageId("wxclean_finish_annimation_page");
                EventBus.getDefault().post(new FinishCleanFinishActivityEvent());
                if (isOpen && PreferenceUtil.getShowCount(this, getString(R.string.tool_chat_clear), mRamScale, mNotifySize, mPowerSize) < 3) {
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
                finish();
            } else {
                if (!tvDelete.isSelected()) return;
                mPresenter.onekeyCleanDelete(tvSelect1.isSelected(), tvSelect.isSelected());

            }
            //保存微信专清清理时间
            PreferenceUtil.saveWeChatCleanTime();
        } else if (ids == R.id.tv_select) {
            tvSelect.setSelected(tvSelect.isSelected() ? false : true);
            getSelectCacheSize();
            StatisticsUtils.trackClick("Icon_caching_click", "图标缓存点击", "home_page", "wechat_cleaning_page");
        } else if (ids == R.id.tv_select1) {
            tvSelect1.setSelected(tvSelect1.isSelected() ? false : true);
            getSelectCacheSize();
            StatisticsUtils.trackClick("Wechat_garbage_click", "微信垃圾点击", "home_page", "wechat_cleaning_page");
        } else if (ids == R.id.cons_aud) {
            Intent intent = new Intent(WechatCleanHomeActivity.this, WechatCleanAudActivity.class);
            startActivity(intent);
            StatisticsUtils.trackClick("Wechat_Voice", "微信语音点击", "home_page", "wechat_cleaning_page");
        } else if (ids == R.id.cons_file) {
            Intent intent = new Intent(WechatCleanHomeActivity.this, WechatCleanFileActivity.class);
            startActivity(intent);
            StatisticsUtils.trackClick("receive_files_click", "接收文件点击", "home_page", "wechat_cleaning_page");
        } else if (ids == R.id.cons_wxsp) {
            //微信视频跳转
            startActivity(new Intent(this, WXCleanVideoActivity.class));
            StatisticsUtils.trackClick("Wechat_Video_click", "微信视频点击", "home_page", "wechat_cleaning_page");
        } else if (ids == R.id.cons_pic) {
            //微信图片跳转
            startActivity(new Intent(this, WXCleanImgActivity.class));
            StatisticsUtils.trackClick("Chat_pictures_click", "聊天图片点击", "home_page", "wechat_cleaning_page");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        NiuDataAPI.onPageStart(viewPageEventCode, viewPageEventName);
        SharedPreferences sp = mContext.getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        long wxCatheSizeImg = sp.getLong(Constant.WX_CACHE_SIZE_IMG, 0L);
        long wxCatheSizeVideo = sp.getLong(Constant.WX_CACHE_SIZE_VIDEO, 0L);
        tvPicSize.setText(CleanAllFileScanUtil.byte2FitSizeOne(wxCatheSizeImg));
        tvVideoSize.setText(CleanAllFileScanUtil.byte2FitSizeOne(wxCatheSizeVideo));
    }

    @Override
    protected void onPause() {
        super.onPause();
        NiuDataAPIUtil.onPageEnd(sourcePageId, currentPageId, viewPageEventCode, viewPageEventName);
    }

    //获取扫描结果
    public void getScanResult() {

        sourcePageId = "wxclean_scan_page";

        currentPageId = "wxclean_scan_result_page";
        returnEventName = "用户在微信清理诊断页返回";
        sysReturnEventName = "用户在微信清理诊断页返回";
        viewPageEventName = "用户在微信清理诊断页浏览";
        viewPageEventCode = "wxclean_scan_result_pag_view_page";

        NiuDataAPI.onPageStart(viewPageEventCode, viewPageEventName);
        NiuDataAPIUtil.onPageEnd(sourcePageId, currentPageId, viewPageEventCode, viewPageEventName);

        setScanStatus(false);
        if (tvWxgabageSize == null) return;
        CleanWxEasyInfo wxprogramInfo = WxQqUtil.f;  //微信小程序
        CleanWxEasyInfo headCacheInfo = WxQqUtil.e;  //缓存表情   浏览聊天记录产生的表情
        CleanWxEasyInfo gabageFileInfo = WxQqUtil.d;  //垃圾文件   不含聊天记录建议清理
        CleanWxEasyInfo wxCircleInfo = WxQqUtil.g;  //朋友圈缓存
        tvWxgabageSize.setText("已选" + CleanAllFileScanUtil.byte2FitSizeOne(headCacheInfo.getTotalSize() + gabageFileInfo.getTotalSize() + wxCircleInfo.getTotalSize()));
        tvWxprogram.setText("已选" + CleanAllFileScanUtil.byte2FitSizeOne(wxprogramInfo.getTotalSize()));
        getSelectCacheSize();

        tvAudSize.setText(CleanAllFileScanUtil.byte2FitSizeOne(mPresenter.getAudioSize()));
        tvFileSize.setText(CleanAllFileScanUtil.byte2FitSizeOne(WxQqUtil.n.getTotalSize()));
        SharedPreferences sp = mContext.getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        long wxCatheSizeImg = sp.getLong(Constant.WX_CACHE_SIZE_IMG, 0L);
        long wxCatheSizeVideo = sp.getLong(Constant.WX_CACHE_SIZE_VIDEO, 0L);
        tvPicSize.setText(CleanAllFileScanUtil.byte2FitSizeOne(wxCatheSizeImg));
        tvVideoSize.setText(CleanAllFileScanUtil.byte2FitSizeOne(wxCatheSizeVideo));
        String str_totalSize = CleanAllFileScanUtil.byte2FitSizeOne(wxprogramInfo.getTotalSize() + headCacheInfo.getTotalSize() + gabageFileInfo.getTotalSize() + wxCircleInfo.getTotalSize() + mPresenter.getAudioSize() + WxQqUtil.n.getTotalSize() + wxCatheSizeImg + wxCatheSizeVideo);
        String strGb = "MB";
        //数字动画转换，GB转成Mb播放，kb太小就不扫描
        float sizeMb = 0;
        if (str_totalSize.endsWith("MB")) {
            sizeMb = NumberUtils.getFloat(str_totalSize.substring(0, str_totalSize.length() - 2));
            strGb = "MB";
        } else if (str_totalSize.endsWith("GB")) {
            sizeMb = NumberUtils.getFloat(str_totalSize.substring(0, str_totalSize.length() - 2));
            strGb = "GB";
        } else if (str_totalSize.endsWith("KB")) {
            sizeMb = NumberUtils.getFloat(str_totalSize.substring(0, str_totalSize.length() - 2));
            strGb = "KB";
        } else {
            sizeMb = NumberUtils.getFloat(str_totalSize.substring(0, str_totalSize.length() - 1));
            strGb = "B";
        }
        //存储垃圾以及文件总大小
        SharedPreferences spAll = mContext.getSharedPreferences(SpCacheConfig.CACHES_NAME_WXQQ_CACHE, Context.MODE_PRIVATE);
        spAll.edit().putLong(SpCacheConfig.WX_CACHE_SIZE, wxprogramInfo.getTotalSize() + headCacheInfo.getTotalSize() + gabageFileInfo.getTotalSize() + wxCircleInfo.getTotalSize() + mPresenter.getAudioSize() + WxQqUtil.n.getTotalSize() + wxCatheSizeImg + wxCatheSizeVideo).commit();
        tvGb.setText(strGb);
        ValueAnimator valueAnimator = mPresenter.setTextAnim(tvGabsize, 0, sizeMb);
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (ivScanFrame == null) return;
                ivScanFrame.setVisibility(View.GONE);
                if (objectAnimatorScanIng != null) objectAnimatorScanIng.cancel();
                lineSming.setVisibility(View.GONE);
                lineSmed.setVisibility(View.VISIBLE);
                mPresenter.setTextSizeAnim(tvGabsize, 110, 55);
                mPresenter.setTextSizeAnim(tvGb, 24, 12);
                mPresenter.setViewHeightAnim(relGasize, relSelects, tv_ql, iv_dun, DisplayUtils.dip2px(263), DisplayUtils.dip2px(123));
            }
        });

    }

    //是否在扫描中状态
    ObjectAnimator roundAnim1;
    ObjectAnimator roundAnim2;
    ObjectAnimator roundAnim3;

    public void setScanStatus(boolean isScaning) {
        if (ivHua1 == null)
            return;
        ivHua1.setImageResource(isScaning ? R.mipmap.icon_pro : R.mipmap.icon_round);
        ivHua2.setImageResource(isScaning ? R.mipmap.icon_pro : R.mipmap.icon_round);
        ivHua3.setImageResource(isScaning ? R.mipmap.icon_pro : R.mipmap.icon_round);

        if (isScaning) {
            roundAnim1 = mPresenter.playRoundAnim(ivHua1);
            roundAnim2 = mPresenter.playRoundAnim(ivHua2);
            roundAnim3 = mPresenter.playRoundAnim(ivHua3);
        } else {
            roundAnim1.cancel();
            roundAnim2.cancel();
            roundAnim3.cancel();
            ivHua1.animate().rotation(0).setDuration(10).start();
            ivHua2.animate().rotation(0).setDuration(10).start();
            ivHua3.animate().rotation(0).setDuration(10).start();
        }

    }

    //垃圾选中的大小
    public void getSelectCacheSize() {
        long selectSize = 0;
        if (tvSelect.isSelected()) selectSize += WxQqUtil.f.getTotalSize();
        if (tvSelect1.isSelected())
            selectSize += WxQqUtil.e.getTotalSize() + WxQqUtil.d.getTotalSize() + WxQqUtil.g.getTotalSize();
        tvSelectSize.setText("已经选择：" + CleanAllFileScanUtil.byte2FitSizeOne(selectSize));
        tvDelete.setText("清理 " + CleanAllFileScanUtil.byte2FitSizeOne(selectSize));
        tvDelete.setBackgroundResource(selectSize != 0 ? R.drawable.delete_select_bg : R.drawable.delete_unselect_bg);
        tvDelete.setSelected(selectSize != 0 ? true : false);


        if (WxQqUtil.e.getTotalSize() + WxQqUtil.d.getTotalSize() + WxQqUtil.g.getTotalSize() + WxQqUtil.f.getTotalSize() == 0) {
            tvDelete.setText("完成 ");
            tvDelete.setBackgroundResource(R.drawable.delete_select_bg);
            tvDelete.setSelected(true);
        }
    }

    @Override
    public void onBackPressed() {
        StatisticsUtils.trackClick("system_return_click", sysReturnEventName, sourcePageId, currentPageId);
        super.onBackPressed();
    }

    public void deleteResult(long result) {
        SharedPreferences sp = mContext.getSharedPreferences(SpCacheConfig.CACHES_NAME_WXQQ_CACHE, Context.MODE_PRIVATE);
        long qqCatheSize = sp.getLong(SpCacheConfig.WX_CACHE_SIZE, 0L);
        sp.edit().putLong(SpCacheConfig.WX_CACHE_SIZE, qqCatheSize - result).commit();
        Intent intent = new Intent(WechatCleanHomeActivity.this, WechatCleanResultActivity.class);
        intent.putExtra("data", result);
        startActivity(intent);
        finish();
    }

    /**
     * 文件扫描后更新size
     */
    @Subscribe
    public void onUpdateSize(WxQqCleanEvent fileCleanSizeEvent) {
        if (fileCleanSizeEvent.type == WxQqCleanEvent.WX_CLEAN_AUDIO) {
            tvAudSize.setText(CleanAllFileScanUtil.byte2FitSizeOne(fileCleanSizeEvent.cleanSize));
        } else if (fileCleanSizeEvent.type == WxQqCleanEvent.WX_CLEAN_FILE) {
            tvFileSize.setText(CleanAllFileScanUtil.byte2FitSizeOne(fileCleanSizeEvent.cleanSize));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void netError() {
    }

    //低于Android O
    public void getAccessListBelow(ArrayList<FirstJunkInfo> listInfo) {
        if (listInfo == null || listInfo.size() <= 0) return;
        mRamScale = new FileQueryUtils().computeTotalSize(listInfo);
    }

}
