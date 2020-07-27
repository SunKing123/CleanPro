package com.xiaoniu.cleanking.ui.external;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.xiaoniu.cleanking.R;

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
        Intent intent=new Intent();
        intent.setClass(context,ExternalPhoneStateActivity.class);
        context.startActivity(intent);
    }
}
