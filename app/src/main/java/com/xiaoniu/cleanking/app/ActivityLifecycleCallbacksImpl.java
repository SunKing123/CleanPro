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
package com.xiaoniu.cleanking.app;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;


import com.xiaoniu.cleanking.scheme.utils.ActivityCollector;
import com.xiaoniu.cleanking.ui.deskpop.BatteryPopActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import timber.log.Timber;


/**
 * ================================================
 * 展示 {@link Application.ActivityLifecycleCallbacks} 的用法
 */
public class ActivityLifecycleCallbacksImpl implements Application.ActivityLifecycleCallbacks {
    public static final String EXTRA_ISINITTOOLBAR = "isInitToolbar";  // Compliant

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Timber.i(activity + " - onActivityCreated");
        ActivityCollector.addActivity(activity, activity.getClass());
    }


    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {

        Timber.i(activity + " - onActivityDestroyed");
        ActivityCollector.removeActivity(activity);
        //横竖屏切换或配置改变时, Activity 会被重新创建实例, 但 Bundle 中的基础数据会被保存下来,移除该数据是为了保证重新创建的实例可以正常工作
        activity.getIntent().removeExtra(EXTRA_ISINITTOOLBAR);

    }
}
