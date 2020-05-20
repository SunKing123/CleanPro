package com.xiaoniu.cleanking.ui.main.activity;

import android.content.Intent;
import android.view.View;

import com.suke.widget.SwitchButton;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.notifition.NotificationService;
import com.xiaoniu.cleanking.utils.NotificationsUtils;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;

import butterknife.OnClick;

/**
 *
 * 白名单设置
 * Created by lang.chen on 2019/7/5
 */
public class WhiteListSettingActivity extends BaseActivity {

    private SwitchButton mSbtnScreenTag;
    private SwitchButton mSbtnNotificationTag;
    @Override
    public void inject(ActivityComponent activityComponent) {

    }

    @Override
    public void netError() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_white_list_setting;
    }

    @Override
    protected void initView() {
        mSbtnScreenTag = findViewById(R.id.s_btn_screen_tag);
        mSbtnNotificationTag = findViewById(R.id.s_notification_tag);
        mSbtnScreenTag.setChecked(PreferenceUtil.getScreenTag());
        mSbtnScreenTag.setOnCheckedChangeListener((view, isChecked) -> PreferenceUtil.saveScreenTag(isChecked));

        mSbtnNotificationTag.setOnCheckedChangeListener((view, isChecked) -> {
            Intent intent= new Intent(this, NotificationService.class);
            if (isChecked) {
                if (!NotificationsUtils.isNotificationEnabled(this)){
                    NotificationsUtils.showDialogGetNotificationPremission(WhiteListSettingActivity.this);
                }else {
                    startService(intent);
                }
            }else {
               intent.putExtra(NotificationService.IS_CLOSE,true);
               startService(intent);
            }
            PreferenceUtil.saveIsNotificationEnabled(isChecked);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSbtnNotificationTag.setChecked((NotificationsUtils.isNotificationEnabled(this) && PreferenceUtil.getIsNotificationEnabled()));
        if (mSbtnNotificationTag.isChecked()){
            //开启常驻通知栏服务
            startService(new Intent(this, NotificationService.class));
        }
    }

    @OnClick({R.id.img_back,R.id.ll_install_package, R.id.ll_speed_list,R.id.ll_soft_package})
    public void onClickView(View view){
        Intent intent = null;
     int ids=view.getId();
     if(ids==R.id.img_back){
         finish();
     }else if(ids==R.id.ll_install_package){
         //安装包保护名单
         intent = new Intent(this,WhiteListInstallPackgeManageActivity.class);
         startActivity(intent);
     }else if(ids==R.id.ll_speed_list){
         //加速白名单
         intent = new Intent(this,WhiteListSpeedManageActivity.class);
         intent.putExtra("type","white_list");
         startActivity(intent);
     }else if(ids==R.id.ll_soft_package){
         //管理软件白名单
         intent = new Intent(this,WhiteListSpeedManageActivity.class);
         intent.putExtra("type","soft_white_list");
         startActivity(intent);
     }
    }
}
