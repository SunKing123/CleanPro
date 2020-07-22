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
package com.xiaoniu.cleanking.ui.viruskill.old.di.module;


import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jess.arms.di.scope.ActivityScope;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.xiaoniu.cleanking.bean.User;
import com.xiaoniu.cleanking.ui.viruskill.old.adapter.UserAdapter;
import com.xiaoniu.cleanking.ui.viruskill.old.contract.UserContract;
import com.xiaoniu.cleanking.ui.viruskill.old.model.UserModelArm;

import java.util.ArrayList;
import java.util.List;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

/**
 * ================================================
 * 展示 Module 的用法
 *
 * ================================================
 */
@Module
public abstract class UserModule {

    @Binds
    abstract UserContract.Model bindUserModel(UserModelArm model);

    @ActivityScope
    @Provides
    static RxPermissions provideRxPermissions(UserContract.View view) {
        return new RxPermissions((FragmentActivity) view.getActivity());
    }

    @ActivityScope
    @Provides
    static RecyclerView.LayoutManager provideLayoutManager(UserContract.View view) {
        return new GridLayoutManager(view.getActivity(), 2);
    }

    @ActivityScope
    @Provides
    static List<User> provideUserList() {
        return new ArrayList<>();
    }

    @ActivityScope
    @Provides
    static RecyclerView.Adapter provideUserAdapter(List<User> list){
        return new UserAdapter(list);
    }
}
