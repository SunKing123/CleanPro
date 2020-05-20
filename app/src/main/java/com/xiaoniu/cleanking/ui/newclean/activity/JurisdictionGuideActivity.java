package com.xiaoniu.cleanking.ui.newclean.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.View;

import com.tbruyelle.rxpermissions2.RxPermissions;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.utils.update.UpdateAgent;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;
import com.xiaoniu.common.utils.StatisticsUtils;

import io.reactivex.functions.Consumer;

/**
 * @ProjectName: clean
 * @Package: com.xiaoniu.cleanking.ui.newclean.activity
 * @ClassName: JurisdictionGuideActivity
 * @Description: 权限引导页
 * @Author: LiDing
 * @CreateDate: 2020/5/9 19:02
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/5/9 19:02
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class JurisdictionGuideActivity extends BaseActivity {


    private String[] permissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_jurisdiction_guide;
    }

    @Override
    public void onResume() {
        super.onResume();
        StatisticsUtils.onPageStart("system_permission_guide_page_view_page", "系统权限引导页浏览页");
    }

    @Override
    public void onPause() {
        super.onPause();
        StatisticsUtils.onPageEnd("system_permission_guide_page_view_page", "系统权限引导页浏览页", "home_page", "system_permission_guide_page");
    }


    @Override
    protected void initView() {
        showBarColor(R.color.color_ff000000);

        findViewById(R.id.btn_open_now).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new RxPermissions(JurisdictionGuideActivity.this).request(permissions).subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            // 权限获取成功
                            finish();
                            StatisticsUtils.trackClick("system_read_file_permission_popup_agree_click", "系统读取文件权限弹窗同意点击", "home_page", "system_permission_guide_page");
                        } else {
                            if (UpdateAgent.hasPermissionDeniedForever(JurisdictionGuideActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                //永久拒绝权限
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + JurisdictionGuideActivity.this.getPackageName()));
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            } else {
                                //拒绝权限
                            }
//                            if (NewScanPresenter.hasPermissionDeniedForever(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                                //永久拒绝权限
//                                showPermissionDialog(mView.getContext());
//                            } else {
//                                //拒绝权限
//                                checkPermission();
//                            }
                            StatisticsUtils.trackClick("system_read_file_permission_popup_no_agree_click", "系统读取文件权限弹窗不同意点击", "home_page", "system_permission_guide_page");
                        }
                    }
                });
                StatisticsUtils.trackClick("system_permission_guide_page_open_click", "系统权限引导页开启点击", "home_page", "system_permission_guide_page");
            }
        });
    }

    @Override
    public void inject(ActivityComponent activityComponent) {

    }

    @Override
    public void netError() {

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
