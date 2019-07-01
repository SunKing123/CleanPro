package com.xiaoniu.cleanking.ui.main.presenter;

import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.ui.main.fragment.CleanMainFragment;
import com.xiaoniu.cleanking.ui.main.model.CleanMainModel;

import javax.inject.Inject;

public class CleanMainPresenter extends RxPresenter<CleanMainFragment,CleanMainModel> {

    @Inject
    public CleanMainPresenter() {
    }

    /**
     * 文件扫描
     */
    public void fileScan() {

    }
}
