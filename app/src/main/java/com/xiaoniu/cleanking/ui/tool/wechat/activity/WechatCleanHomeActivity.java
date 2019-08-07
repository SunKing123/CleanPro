package com.xiaoniu.cleanking.ui.tool.wechat.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.activity.CleanInstallPackageActivity;
import com.xiaoniu.cleanking.ui.main.activity.CleanMusicManageActivity;
import com.xiaoniu.cleanking.ui.main.activity.CleanVideoManageActivity;
import com.xiaoniu.cleanking.ui.main.activity.WXCleanImgActivity;
import com.xiaoniu.cleanking.ui.main.activity.WXCleanVideoActivity;
import com.xiaoniu.cleanking.ui.main.adapter.PhoneAccessBelowAdapter;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.presenter.PhoneAccessPresenter;
import com.xiaoniu.cleanking.ui.main.widget.AccessAnimView;
import com.xiaoniu.cleanking.ui.main.widget.ViewHelper;
import com.xiaoniu.cleanking.ui.tool.wechat.bean.CleanWxEasyInfo;
import com.xiaoniu.cleanking.ui.tool.wechat.bean.CleanWxHeadInfo;
import com.xiaoniu.cleanking.ui.tool.wechat.presenter.WechatCleanHomePresenter;
import com.xiaoniu.cleanking.ui.tool.wechat.util.WxQqUtil;
import com.xiaoniu.cleanking.utils.CleanAllFileScanUtil;
import com.xiaoniu.cleanking.utils.DeviceUtils;
import com.xiaoniu.cleanking.utils.NumberUtils;
import com.xiaoniu.cleanking.utils.StatisticsUtils;
import com.xiaoniu.statistic.NiuDataAPI;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

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
            finish();
            StatisticsUtils.trackClick("wechat_cleaning_return_click", "微信清理返回点击", "home_page", "wechat_cleaning_page");
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
            if (!tvSelect.isSelected() && !tvSelect.isSelected()) return;
            mPresenter.onekeyCleanDelete(tvSelect1.isSelected(), tvSelect.isSelected());
            StatisticsUtils.trackClick("cleaning_click", "清理点击", "home_page", "wechat_cleaning_page");
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
        NiuDataAPI.onPageStart("wechat_ceaning_view_page", "微信清理页面浏览");
    }

    @Override
    protected void onPause() {
        super.onPause();
        NiuDataAPI.onPageEnd("wechat_ceaning_view_page", "微信清理页面浏览");
    }


    //获取扫描结果
    public void getScanResult() {
        setScanStatus(false);

        CleanWxEasyInfo wxprogramInfo = WxQqUtil.f;  //微信小程序
        CleanWxEasyInfo headCacheInfo = WxQqUtil.e;  //缓存表情   浏览聊天记录产生的表情
        CleanWxEasyInfo gabageFileInfo = WxQqUtil.d;  //垃圾文件   不含聊天记录建议清理
        CleanWxEasyInfo wxCircleInfo = WxQqUtil.g;  //朋友圈缓存
        tvWxgabageSize.setText("已选" + CleanAllFileScanUtil.byte2FitSizeOne(headCacheInfo.getTotalSize() + gabageFileInfo.getTotalSize() + wxCircleInfo.getTotalSize()));
        tvWxprogram.setText("已选" + CleanAllFileScanUtil.byte2FitSizeOne(wxprogramInfo.getTotalSize()));
        getSelectCacheSize();


        Log.e("asdfg", "图片：" + WxQqUtil.h.getTotalSize() + "：数量：" + WxQqUtil.h.getTotalNum());
        Log.e("asdfg", "视频：" + WxQqUtil.i.getTotalSize() + "：数量：" + WxQqUtil.i.getTotalNum());
        Log.e("asdfg", "语音：" + WxQqUtil.k.getTotalSize() + "：数量：" + WxQqUtil.k.getTotalNum());
        Log.e("asdfg", "文件：" + WxQqUtil.n.getTotalSize() + "：数量：" + WxQqUtil.n.getTotalNum());

        Log.e("asdfg", "拍摄及保存的图片：" + WxQqUtil.l.getTotalSize() + "：数量：" + WxQqUtil.l.getTotalNum());
        Log.e("asdfg", "拍摄以及保存的视频：" + WxQqUtil.m.getTotalSize() + "：数量：" + WxQqUtil.m.getTotalNum());
        Log.e("asdfg", "收藏的表情：" + WxQqUtil.j.getTotalSize() + "：数量：" + WxQqUtil.j.getTotalNum());
        tvPicSize.setText(CleanAllFileScanUtil.byte2FitSizeOne(WxQqUtil.h.getTotalSize()));
        tvVideoSize.setText(CleanAllFileScanUtil.byte2FitSizeOne(WxQqUtil.i.getTotalSize()));
        tvAudSize.setText(CleanAllFileScanUtil.byte2FitSizeOne(WxQqUtil.k.getTotalSize()));
        tvFileSize.setText(CleanAllFileScanUtil.byte2FitSizeOne(WxQqUtil.n.getTotalSize()));
        String str_totalSize = CleanAllFileScanUtil.byte2FitSizeOne(wxprogramInfo.getTotalSize() + headCacheInfo.getTotalSize() + gabageFileInfo.getTotalSize() + wxCircleInfo.getTotalSize());
        if (str_totalSize.endsWith("KB")) return;
        //数字动画转换，GB转成Mb播放，kb太小就不扫描
        float sizeMb = 0;
        if (str_totalSize.endsWith("MB")) {
            sizeMb = NumberUtils.getFloat(str_totalSize.substring(0, str_totalSize.length() - 2));

        } else if (str_totalSize.endsWith("GB")) {
            sizeMb = NumberUtils.getFloat(str_totalSize.substring(0, str_totalSize.length() - 2));
            sizeMb *= 1024;
        }

        ValueAnimator valueAnimator = mPresenter.setTextAnim(tvGabsize, 0, sizeMb);
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                ivScanFrame.setVisibility(View.GONE);
                if (objectAnimatorScanIng != null) objectAnimatorScanIng.cancel();
                lineSming.setVisibility(View.GONE);
                lineSmed.setVisibility(View.VISIBLE);
                mPresenter.setTextSizeAnim(tvGabsize, 110, 55);
                mPresenter.setTextSizeAnim(tvGb, 24, 12);
                mPresenter.setViewHeightAnim(relGasize, relSelects, tv_ql, iv_dun, DeviceUtils.dip2px(263), DeviceUtils.dip2px(123));
            }
        });

    }

    //是否在扫描中状态
    public void setScanStatus(boolean isScaning) {
        ivHua1.setImageResource(isScaning ? R.mipmap.icon_pro : R.drawable.icon_select);
        ivHua2.setImageResource(isScaning ? R.mipmap.icon_pro : R.drawable.icon_select);
        ivHua3.setImageResource(isScaning ? R.mipmap.icon_pro : R.drawable.icon_select);
    }

    //垃圾选中的大小
    public void getSelectCacheSize() {
        long selectSize = 0;
        if (tvSelect.isSelected()) selectSize += WxQqUtil.f.getTotalSize();
        if (tvSelect1.isSelected())
            selectSize += WxQqUtil.e.getTotalSize() + WxQqUtil.d.getTotalSize() + WxQqUtil.g.getTotalSize();
        tvSelectSize.setText("已经选择：" + CleanAllFileScanUtil.byte2FitSizeOne(selectSize));
        tvDelete.setText("清理 " + CleanAllFileScanUtil.byte2FitSizeOne(selectSize));
        tvDelete.setBackgroundResource(tvSelect.isSelected() || tvSelect1.isSelected() ? R.drawable.delete_select_bg : R.drawable.delete_unselect_bg);
        SharedPreferences sp = mContext.getSharedPreferences(SpCacheConfig.CACHES_NAME_WXQQ_CACHE, Context.MODE_PRIVATE);
        sp.edit().putLong(SpCacheConfig.WX_CACHE_SIZE, selectSize).commit();
    }

    public void deleteResult(long result) {
        Intent intent = new Intent(WechatCleanHomeActivity.this, WechatCleanResultActivity.class);
        intent.putExtra("data", result);
        startActivity(intent);
        finish();
    }

    @Override
    public void netError() {

    }
}
