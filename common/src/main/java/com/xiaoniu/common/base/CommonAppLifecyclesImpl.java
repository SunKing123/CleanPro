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
package com.xiaoniu.common.base;

import android.app.Application;
import android.content.Context;

import com.jess.arms.base.delegate.AppLifecycles;
import com.xiaoniu.common.http.EHttp;
import com.xiaoniu.common.http.RequestParamInterceptor;
import com.xiaoniu.common.http.model.CommonResult;
import com.xiaoniu.common.utils.ContextUtils;

import androidx.annotation.NonNull;
import androidx.multidex.MultiDex;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * ================================================
 * 展示 {@link AppLifecycles} 的用法
 * ================================================
 */
public class CommonAppLifecyclesImpl implements AppLifecycles {

    @Override
    public void attachBaseContext(@NonNull Context base) {
        ContextUtils.init(base);

    }

    @Override
    public void onCreate(@NonNull Application application) {
        initHttp();
    }


    private void initHttp() {
        //全局设置，所有请求起作用
        EHttp.Builder builder = new EHttp.Builder();
        builder.apiResult(CommonResult.class);
        builder.addInterceptor(new RequestParamInterceptor());
        EHttp.init(builder);
    }

    @Override
    public void onTerminate(@NonNull Application application) {
        // Do nothing because of nothing
    }
}
