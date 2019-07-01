package com.xiaoniu.cleanking.ui.main.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.RouteConstants;
import com.xiaoniu.cleanking.app.injector.component.FragmentComponent;
import com.xiaoniu.cleanking.base.BaseFragment;
import com.xiaoniu.cleanking.ui.main.presenter.CleanMainPresenter;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;

import butterknife.OnClick;

public class CleanMainFragment extends BaseFragment<CleanMainPresenter> {

    @Override
    protected void inject(FragmentComponent fragmentComponent) {
        fragmentComponent.inject(this);
    }

    @Override
    public void netError() {

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        StatusBarCompat.translucentStatusBar(getActivity(), true);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_clean_main;
    }

    @Override
    protected void initView() {

    }

    @OnClick(R.id.layout_clean_right_now)
    public void onViewClicked() {
        //立即清理
        startActivity(RouteConstants.JUNK_CLEAN_ACTIVITY);
    }
}
