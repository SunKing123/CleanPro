package com.xiaoniu.cleanking.ui.main.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.ui.main.activity.WhiteListInstallPackgeManageActivity;
import com.xiaoniu.cleanking.ui.main.activity.WhiteListSpeedManageActivity;
import com.xiaoniu.cleanking.ui.main.bean.AppInfoBean;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.model.MainModel;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

/**
 * Created by lang.chen on 2019/7/4
 */
public class WhiteListIntallPackagePresenter extends RxPresenter<WhiteListInstallPackgeManageActivity, MainModel> {


    private RxAppCompatActivity mContext;

    private List<AppInfoBean> apps = new ArrayList<>();


    @Inject
    public WhiteListIntallPackagePresenter(RxAppCompatActivity activity) {
        this.mContext = activity;
    }


    /**
     * 更新缓存
     *
     * @param lists 需要移除的缓存
     */
    public void updateCache(List<AppInfoBean> lists) {
        apps.removeAll(lists);
        Set<String> caches = new HashSet<>();
        for (AppInfoBean appInfoBean : apps) {
            caches.add(appInfoBean.name);
        }
        SharedPreferences sp = mContext.getSharedPreferences(SpCacheConfig.CACHES_NAME_WHITE_LIST_INSTALL_PACKE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putStringSet(SpCacheConfig.WHITE_LIST_KEY_DIRECTORY, caches);
        editor.commit();

    }


    /**
     * 获取缓存白名单
     */
    public Set<String> getCacheWhite() {
        SharedPreferences sp = mContext.getSharedPreferences(SpCacheConfig.CACHES_NAME_WHITE_LIST_INSTALL_PACKE, Context.MODE_PRIVATE);
        Set<String> sets = sp.getStringSet(SpCacheConfig.WHITE_LIST_KEY_DIRECTORY, new HashSet<>());
        return sets;
    }


    public void getData() {
        apps.clear();
        Set<String> caches = getCacheWhite();
        for (String path : caches) {
            AppInfoBean appInfoBean = new AppInfoBean();
            appInfoBean.name = path;
            apps.add(appInfoBean);
        }
        mView.setViewData(apps);
    }


}
