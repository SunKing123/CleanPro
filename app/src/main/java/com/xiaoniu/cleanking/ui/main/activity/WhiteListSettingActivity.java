package com.xiaoniu.cleanking.ui.main.activity;

import android.content.Intent;
import android.view.View;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;

import butterknife.OnClick;

/**
 *
 * 白名单设置
 * Created by lang.chen on 2019/7/5
 */
public class WhiteListSettingActivity extends BaseActivity {



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

    }

    @OnClick({R.id.img_back,R.id.ll_install_package, R.id.ll_speed_list})
    public void onClickView(View view){
     int ids=view.getId();
     if(ids==R.id.img_back){
         finish();
     }else if(ids==R.id.ll_install_package){
         //安装包保护名单
         startActivity(new Intent(this,WhiteListInstallPackgeManageActivity.class));
     }else if(ids==R.id.ll_speed_list){
         //加速白名单
         startActivity(new Intent(this,WhiteListSpeedManageActivity.class));
     }
    }
}
