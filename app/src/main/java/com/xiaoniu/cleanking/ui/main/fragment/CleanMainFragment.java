package com.xiaoniu.cleanking.ui.main.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.RouteConstants;
import com.xiaoniu.cleanking.app.injector.component.FragmentComponent;
import com.xiaoniu.cleanking.base.BaseFragment;
import com.xiaoniu.cleanking.ui.main.activity.FileManagerHomeActivity;
import com.xiaoniu.cleanking.ui.main.activity.PhoneAccessActivity;
import com.xiaoniu.cleanking.ui.main.bean.JunkGroup;
import com.xiaoniu.cleanking.ui.main.presenter.CleanMainPresenter;
import com.xiaoniu.cleanking.utils.CleanUtil;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;

public class CleanMainFragment extends BaseFragment<CleanMainPresenter> {

    @BindView(R.id.text_count)
    TextView mTextCount;

    private boolean isScanFinish = false;
    public static HashMap<Integer, JunkGroup> mJunkGroups;

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
        return R.layout.fragment_clean_mainnew;
    }

    @Override
    protected void initView() {
        mPresenter.startScan();
    }


//    @OnClick(R.id.text_cooling)
//    public void onCoolingViewClicked() {
//        //手机降温
//        startActivity(RouteConstants.PHONE_COOLING_ACTIVITY);
//    }
    @OnClick(R.id.text_wjgl)
    public void wjgl() {
        //文件管理
        startActivity(FileManagerHomeActivity.class);
    }
    @OnClick(R.id.text_acce)
    public void text_acce() {
        //一键加速
        startActivity(PhoneAccessActivity.class);
    }
    @OnClick(R.id.line_ql)
    public void line_ql() {
        //手机清理
        startActivity(RouteConstants.JUNK_CLEAN_ACTIVITY);
    }
    @OnClick(R.id.btn_ljql)
    public void btn_ljql() {
        //立即清理
        startActivity(RouteConstants.JUNK_CLEAN_ACTIVITY);
    }
    /**
     * 扫描完成
     * @param junkGroups
     */
    public void scanFinish(HashMap<Integer, JunkGroup> junkGroups) {
        isScanFinish = true;
        mJunkGroups = junkGroups;
    }

    /**
     * 统计总数
     * @param total
     */
    public void showCountNumber(long total) {
        getActivity().runOnUiThread(() -> mTextCount.setText(CleanUtil.formatShortFileSize(getActivity(), total)));
    }
}
