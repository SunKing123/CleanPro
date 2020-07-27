package com.xiaoniu.cleanking.ui.deskpop.state;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.newclean.activity.ExternalSceneActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.FragmentManager;
import butterknife.BindView;

/**
 * Created by xinxiaolong on 2020/7/25.
 * email：xinxiaolong123@foxmail.com
 */
public class ExternalPhoneStateActivity extends BaseActivity {

    @BindView(R.id.scene_close)
    AppCompatImageView sceneClose;
    private FragmentManager mManager = getSupportFragmentManager();

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {

    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_external_phone_state_layout;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        initFragment();
        initView();
    }

    private void initView(){
        sceneClose.setOnClickListener(v -> finish());
    }

    private void initFragment(){
        mManager.beginTransaction()
                .add(R.id.frame_layout, new ExternalPhoneStateFragment())
                .commitAllowingStateLoss();
    }

    public static void start(Context context){
        Intent screenIntent = new Intent(context, ExternalPhoneStateActivity.class);
        screenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        screenIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        screenIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        screenIntent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
        screenIntent.putExtra(ExternalSceneActivity.SCENE,ExternalSceneActivity.SCENE_WIFI);
        context.startActivity(screenIntent);
    }

}
