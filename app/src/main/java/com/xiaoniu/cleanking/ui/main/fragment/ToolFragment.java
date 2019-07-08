package com.xiaoniu.cleanking.ui.main.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.RouteConstants;
import com.xiaoniu.cleanking.app.injector.component.FragmentComponent;
import com.xiaoniu.cleanking.base.BaseFragment;
import com.xiaoniu.cleanking.base.SimpleActivity;
import com.xiaoniu.cleanking.base.SimpleFragment;
import com.xiaoniu.cleanking.ui.main.presenter.CleanMainPresenter;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;

import butterknife.OnClick;

public class ToolFragment extends SimpleFragment {



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        StatusBarCompat.translucentStatusBar(getActivity(), true);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tool;
    }

    @Override
    protected void initView() {
    }


    @OnClick(R.id.text_cooling)
    public void onCoolingViewClicked() {
        //手机降温
        startActivity(RouteConstants.PHONE_COOLING_ACTIVITY);
    }

}
