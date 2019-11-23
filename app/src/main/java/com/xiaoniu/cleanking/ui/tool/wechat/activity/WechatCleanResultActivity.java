package com.xiaoniu.cleanking.ui.tool.wechat.activity;

import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.SimpleActivity;
import com.xiaoniu.cleanking.ui.main.bean.CountEntity;
import com.xiaoniu.cleanking.ui.main.widget.CleanAnimView;
import com.xiaoniu.cleanking.ui.newclean.activity.ScreenFinishBeforActivity;
import com.xiaoniu.cleanking.ui.tool.notify.event.FinishCleanFinishActivityEvent;
import com.xiaoniu.cleanking.utils.CleanUtil;
import com.xiaoniu.cleanking.utils.ExtraConstant;
import com.xiaoniu.cleanking.utils.NiuDataAPIUtil;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;
import com.xiaoniu.common.utils.DisplayUtils;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.statistic.NiuDataAPI;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;

/*
 *  微信清理完成后动画页
 *
 * */
public class WechatCleanResultActivity extends SimpleActivity {

    @BindView(R.id.acceview)
    CleanAnimView mCleanAnimView;

    String mTitle;
    private boolean mIsFinish; //是否点击了返回键

    @Override
    protected int getLayoutId() {
        return R.layout.activity_wxclean_result;
    }

    @Override
    protected void initView() {
        showBarColor(getResources().getColor(R.color.color_FF6862));
        CountEntity countEntity = CleanUtil.formatShortFileSize(getIntent().getExtras().getLong("data", 0));
        startCleanAnim(countEntity);
        mCleanAnimView.setOnColorChangeListener(this::showBarColor);
    }

    @Override
    protected void onResume() {
        super.onResume();
        NiuDataAPI.onPageStart("wxclean_finish_annimation_page_view_page", "微信清理完成动画展示页浏览");
    }

    @Override
    protected void onPause() {
        super.onPause();
        NiuDataAPIUtil.onPageEnd("wxclean_animation_page", "wxclean_finish_annimation_page", "wxclean_finish_annimation_page_view_page", "微信清理完成动画展示页浏览");
    }


    @Override
    public void onBackPressed() {
        mIsFinish = true;
        StatisticsUtils.trackClick("system_return_click", "微信清理完成动画展示页返回", "wxclean_animation_page", "wxclean_finish_annimation_page");
        super.onBackPressed();
    }

    /**
     * 开始清理动画
     *
     * @param countEntity
     */
    public void startCleanAnim(CountEntity countEntity) {
        String title = getIntent().getExtras().getString("title", "");
        if (!TextUtils.isEmpty(title)) {
            mTitle = getString(R.string.tool_qq_clear);
        } else {
            mTitle = getString(R.string.tool_chat_clear);
        }
        mCleanAnimView.setTitle(mTitle);
        mCleanAnimView.setIcon(R.mipmap.icon_wx_cleaned, DisplayUtils.dip2px(49), DisplayUtils.dip2px(49));
        mCleanAnimView.setData(countEntity, CleanAnimView.page_file_wxclean);
        mCleanAnimView.setVisibility(View.VISIBLE);
        mCleanAnimView.startTopAnim(true);
        mCleanAnimView.setListener(() -> finish());

        mCleanAnimView.setAnimationEnd(() -> {
            if (mIsFinish) return;
            AppHolder.getInstance().setCleanFinishSourcePageId("wxclean_finish_annimation_page");
            if (mTitle.equals(getString(R.string.tool_chat_clear))) {
                PreferenceUtil.saveCleanWechatUsed(true);
            }
            EventBus.getDefault().post(new FinishCleanFinishActivityEvent());
            AppHolder.getInstance().setCleanFinishSourcePageId("wxclean_finish_annimation_page");
            EventBus.getDefault().post(new FinishCleanFinishActivityEvent());
            startActivity(new Intent(this, ScreenFinishBeforActivity.class)
                    .putExtra(ExtraConstant.TITLE, getString(R.string.tool_chat_clear)));
            finish();
        });
    }

    /**
     * 状态栏颜色变化
     *
     * @param animatedValue
     */
    public void showBarColor(int animatedValue) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarCompat.setStatusBarColor(this, animatedValue, true);
        } else {
            StatusBarCompat.setStatusBarColor(this, animatedValue, false);
        }
    }

}
