package com.xiaoniu.cleanking.ui.tool.wechat.activity;

import android.os.Build;
import android.view.View;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.SimpleActivity;
import com.xiaoniu.cleanking.ui.main.bean.CountEntity;
import com.xiaoniu.cleanking.ui.main.widget.CleanAnimView;
import com.xiaoniu.cleanking.ui.main.widget.CleanedAnimView;
import com.xiaoniu.cleanking.utils.CleanUtil;
import com.xiaoniu.cleanking.utils.DeviceUtils;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;

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
        mCleanAnimView.setTitle("微信清理");
        mCleanAnimView.setIcon(R.mipmap.icon_wx_cleaned, DeviceUtils.dip2px(49), DeviceUtils.dip2px(49));
        mCleanAnimView.setData(countEntity, CleanAnimView.page_file_wxclean);
        mCleanAnimView.setVisibility(View.VISIBLE);
//        mCleanAnimView.startTopAnim(true);
        mCleanAnimView.setListener(new CleanedAnimView.onBackClickListener() {
            @Override
            public void onClick() {
                finish();
            }
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
