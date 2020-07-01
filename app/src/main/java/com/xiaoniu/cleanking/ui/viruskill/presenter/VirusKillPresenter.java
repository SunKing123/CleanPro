/*
 * Copyright 2017 JessYan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xiaoniu.cleanking.ui.viruskill.presenter;

import android.app.Application;

import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.xiaoniu.cleanking.bean.VirusKillBean;
import com.xiaoniu.cleanking.ui.viruskill.contract.VirusKillContract;

import java.util.List;

import javax.inject.Inject;

import me.jessyan.rxerrorhandler.core.RxErrorHandler;

/**
 * 病毒查杀P层
 */
@ActivityScope
public class VirusKillPresenter extends BasePresenter<VirusKillContract.Model, VirusKillContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    AppManager mAppManager;
    @Inject
    Application mApplication;
    @Inject
    List<VirusKillBean> mBean;


    @Inject
    public VirusKillPresenter(VirusKillContract.Model model, VirusKillContract.View rootView) {
        super(model, rootView);
    }


    public void requestVirusKill() {

    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mBean = null;
        this.mErrorHandler = null;
        this.mAppManager = null;
        this.mApplication = null;
    }
}
