package com.xiaoniu.cleanking.ui.newclean.presenter;

import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.ui.newclean.fragment.CleanFragment;
import com.xiaoniu.cleanking.ui.newclean.model.NewScanModel;
import com.xiaoniu.cleanking.utils.prefs.NoClearSPHelper;

import javax.inject.Inject;

public class CleanPresenter extends RxPresenter<CleanFragment, NewScanModel> {

    @Inject
    NoClearSPHelper mSPHelper;

    @Inject
    public CleanPresenter() {
    }
}
