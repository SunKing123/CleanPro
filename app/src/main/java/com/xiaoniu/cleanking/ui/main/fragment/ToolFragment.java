package com.xiaoniu.cleanking.ui.main.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.RouteConstants;
import com.xiaoniu.cleanking.base.SimpleFragment;
import com.xiaoniu.cleanking.ui.main.activity.PhoneThinActivity;

import butterknife.OnClick;

public class ToolFragment extends SimpleFragment {


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tool;
    }

    @Override
    protected void initView() {
    }


    @OnClick({R.id.text_cooling,R.id.text_phone_thin})
    public void onCoolingViewClicked(View view) {
        int ids=view.getId();
        if(ids==R.id.text_cooling){
            //手机降温
            startActivity(RouteConstants.PHONE_COOLING_ACTIVITY);
        }else if(ids==R.id.text_phone_thin){
            startActivity(new Intent(getContext(),PhoneThinActivity.class));
        }
    }

}
