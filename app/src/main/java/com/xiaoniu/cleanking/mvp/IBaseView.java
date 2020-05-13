package com.xiaoniu.cleanking.mvp;

import android.content.Context;
import android.support.annotation.NonNull;

import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;

public interface IBaseView {

    Context getContext();

    boolean isActive();

    <T> LifecycleTransformer<T> bindLifecycle();

    <T> LifecycleTransformer<T> bindActivityEvent(@NonNull ActivityEvent event);

    <T> LifecycleTransformer<T> bindFragmentEvent(@NonNull FragmentEvent event);
}
