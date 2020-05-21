package com.xiaoniu.cleanking.ui.newclean.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import com.tbruyelle.rxpermissions2.RxPermissions;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.Constant;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.tool.wechat.activity.WechatCleanHomeActivity;
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

    boolean goToSetting =false;
    String formBtnMark="";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_jurisdiction_guide;
    }

    @Override
    public void onResume() {
        super.onResume();
        checkPermissionAndJump();
        StatisticsUtils.onPageStart("system_permission_guide_page_view_page", "系统权限引导页浏览页");
    }

    @Override
    public void onPause() {
        super.onPause();
        StatisticsUtils.onPageEnd("system_permission_guide_page_view_page", "系统权限引导页浏览页", "home_page", "system_permission_guide_page");
    }


    @Override
    protected void initView() {
        formBtnMark=getIntent().getStringExtra("formBtnMark");

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
                            allowPermissionGoToPage();
                        } else {
                            if (UpdateAgent.permissionDeniedForever(JurisdictionGuideActivity.this,false, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                //永久拒绝权限
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
                                intent.setData(uri);
                                try {
                                    mContext.startActivity(intent);
                                    sendDelayedUpdateGoToSettingTag();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

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

    public void sendDelayedUpdateGoToSettingTag(){
        handler.sendEmptyMessageDelayed(1,1000);
    }

    DelayedUpdateGoToSettingMark handler=new DelayedUpdateGoToSettingMark();

    final class DelayedUpdateGoToSettingMark extends Handler{
        @Override
        public void handleMessage(Message msg) {
            goToSetting =true;
        }
}

    public void checkPermissionAndJump(){
        if(!goToSetting){
            return;
        }
        goToSetting=false;
        new RxPermissions(JurisdictionGuideActivity.this).request(permissions).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean) {
                    finish();
                    allowPermissionGoToPage();
                }
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

    public static void goToPage(String fromButton, Context context){
        Intent intent=new Intent();
        intent.putExtra("formBtnMark",fromButton);
        intent.setClass(context,JurisdictionGuideActivity.class);
        context.startActivity(intent);
    }

    //获取权限后进行页面跳转
    public void allowPermissionGoToPage(){
        if(formBtnMark.equals(Constant.WX_CLEAN_BTN)){
            startActivity(WechatCleanHomeActivity.class);
        }else {
            startActivity(NowCleanActivity.class);
        }
    }
}
