package com.xiaoniu.cleanking.ui.tool.wechat.activity;

import android.content.Intent;
import android.os.Build;
import android.view.View;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.SimpleActivity;
import com.xiaoniu.cleanking.ui.main.bean.CountEntity;
import com.xiaoniu.cleanking.ui.main.widget.CleanAnimView;
import com.xiaoniu.cleanking.ui.main.widget.CleanedAnimView;
import com.xiaoniu.cleanking.utils.CleanUtil;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;
import com.xiaoniu.common.utils.DisplayUtils;
import com.xiaoniu.common.utils.KeyboardUtils;

import butterknife.BindView;

public class WechatCleanedResultActivity extends SimpleActivity {

    @BindView(R.id.acceview)
    CleanedAnimView mCleanAnimView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_wxcleaned_result;
    }

    @Override
    protected void initView() {
        Intent intent = getIntent();
        if (intent != null){
           String title = intent.getStringExtra("title");
           mCleanAnimView.setTitle(title);
        }

        CountEntity countEntity = CleanUtil.formatShortFileSize(0);
        startCleanAnim(countEntity);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color_4690FD), true);
        } else {
            StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color_4690FD), false);
        }
    }

    /**
     * 开始清理动画
     *
     * @param countEntity
     */
    public void startCleanAnim(CountEntity countEntity) {
        mCleanAnimView.setIcon(R.mipmap.icon_wx_cleaned, DisplayUtils.dip2px(49), DisplayUtils.dip2px(49));
        mCleanAnimView.setData(countEntity, CleanAnimView.page_file_wxclean);
        mCleanAnimView.setVisibility(View.VISIBLE);
//        mCleanAnimView.startTopAnim(true);
        mCleanAnimView.setListener(() -> finish());

        mCleanAnimView.showWebView();
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

    @Override
    protected void onPause() {
        super.onPause();
        KeyboardUtils.closeKeyboard(mCleanAnimView);
    }
}
