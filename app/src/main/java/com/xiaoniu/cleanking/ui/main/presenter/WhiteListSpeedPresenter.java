package com.xiaoniu.cleanking.ui.main.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.util.Log;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.umeng.commonsdk.debug.E;
import com.xiaoniu.cleanking.base.BaseModel;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.ui.main.activity.WhiteListSpeedManageActivity;
import com.xiaoniu.cleanking.ui.main.bean.AppInfoBean;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.model.MainModel;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

/**
 * Created by lang.chen on 2019/7/4
 */
public class WhiteListSpeedPresenter extends RxPresenter<WhiteListSpeedManageActivity, MainModel> {


    private RxAppCompatActivity mContext;

    private List<AppInfoBean> apps = new ArrayList<>();


    @Inject
    public WhiteListSpeedPresenter(RxAppCompatActivity activity) {
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
        caches.add("com.xiaoniu.cleanking");
        for (AppInfoBean appInfoBean : apps) {
            caches.add(appInfoBean.packageName);
        }
        SharedPreferences sp = mContext.getSharedPreferences(SpCacheConfig.CACHES_NAME_WHITE_LIST_INSTALL_PACKE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putStringSet(SpCacheConfig.WHITE_LIST_KEY_INSTALL_PACKE_NAME, caches);
        editor.commit();

    }


    /**
     * 获取缓存白名单
     */
    public Set<String> getCacheWhite() {
        SharedPreferences sp = mContext.getSharedPreferences(SpCacheConfig.CACHES_NAME_WHITE_LIST_INSTALL_PACKE, Context.MODE_PRIVATE);
        Set<String> sets = sp.getStringSet(SpCacheConfig.WHITE_LIST_KEY_INSTALL_PACKE_NAME, new HashSet<>());
        return sets;
    }

    //扫描已安装的apk信息
    public void scanData() {
        try {
            getApplicaionInfo();
        } catch (Exception e) {

        }
    }


    public List<AppInfoBean> getData() {
        return this.apps;
    }

    /**
     * 获取已安装应用的信息，
     * 包括icon ,名称，apk大小，首次安装时间，存储大小
     * <p>
     * 存储大小对应的是 packname/files;
     */
    public void getApplicaionInfo() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        List<PackageInfo> packages = mContext.getPackageManager().getInstalledPackages(0);
        Set<String> caches = getCacheWhite();
        apps.clear();
        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {

                boolean isExist = false;
                for (String packeName : caches) {
                    if (packeName.equals(packageInfo.packageName)){
                        isExist = true;
                    }
                }
                if(isExist){
                    AppInfoBean appInfoBean = new AppInfoBean();
                    appInfoBean.name = packageInfo.applicationInfo.loadLabel(mContext.getPackageManager()).toString();
                    //应用icon
                    appInfoBean.icon = packageInfo.applicationInfo.loadIcon(mContext.getPackageManager());
                    appInfoBean.installTime = packageInfo.firstInstallTime;
                    appInfoBean.packageName = packageInfo.packageName;
                    apps.add(appInfoBean);
                }
            }
        }
    }
}
