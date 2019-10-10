package com.xiaoniu.cleanking.ui.tool.wechat.activity;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.SimpleActivity;
import com.xiaoniu.cleanking.ui.newclean.activity.NewCleanFinishActivity;
import com.xiaoniu.cleanking.ui.main.bean.CountEntity;
import com.xiaoniu.cleanking.ui.main.widget.CleanAnimView;
import com.xiaoniu.cleanking.utils.CleanUtil;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;
import com.xiaoniu.common.utils.DisplayUtils;

import butterknife.BindView;

public class WechatCleanResultActivity extends SimpleActivity {

    @BindView(R.id.acceview)
    CleanAnimView mCleanAnimView;

    String mTitle;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_wxclean_result;
    }

    @Override
    protected void initView() {
        showBarColor(getResources().getColor(R.color.color_FD6F46));
        CountEntity countEntity = CleanUtil.formatShortFileSize(getIntent().getExtras().getLong("data",0));
        startCleanAnim(countEntity);
        mCleanAnimView.setOnColorChangeListener(this::showBarColor);

    }

    /**
     * 开始清理动画
     *
     * @param countEntity
     */
    public void startCleanAnim(CountEntity countEntity) {
        String title =  getIntent().getExtras().getString("title", "");
        if (!TextUtils.isEmpty(title)) {
            mTitle = getString(R.string.tool_qq_clear);
        } else {
            mTitle = getString(R.string.tool_chat_clear_n);
        }
        mCleanAnimView.setTitle(mTitle);
        mCleanAnimView.setIcon(R.mipmap.icon_wx_cleaned, DisplayUtils.dip2px(49), DisplayUtils.dip2px(49));
        mCleanAnimView.setData(countEntity, CleanAnimView.page_file_wxclean);
        mCleanAnimView.setVisibility(View.VISIBLE);
        mCleanAnimView.startTopAnim(true);
        mCleanAnimView.setListener(() -> finish());
        mCleanAnimView.setAnimationEnd(() -> {
            Bundle bundle = new Bundle();
            bundle.putString("title", mTitle);
            bundle.putString("num", "");
            bundle.putString("unit", "");
            startActivity( NewCleanFinishActivity.class,bundle);
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
