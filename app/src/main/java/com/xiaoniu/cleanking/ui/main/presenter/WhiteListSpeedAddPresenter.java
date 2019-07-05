package com.xiaoniu.cleanking.ui.main.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.ui.main.activity.WhiteListSpeedAddActivity;
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
public class WhiteListSpeedAddPresenter extends RxPresenter<WhiteListSpeedAddActivity, MainModel> {

    RxAppCompatActivity mContext;

    private List<AppInfoBean> apps = new ArrayList<>();

    @Inject
    public WhiteListSpeedAddPresenter(RxAppCompatActivity activity) {
        this.mContext = activity;
    }


    /**
     * 更新缓存
     *
     * @param lists 需要移除的缓存
     */
    public void updateCache(List<AppInfoBean> lists) {
        Set<String> caches = new HashSet<>();
        for (AppInfoBean appInfoBean : lists) {
            caches.add(appInfoBean.packageName);
        }
        SharedPreferences sp = mContext.getSharedPreferences(SpCacheConfig.CACHES_NAME_WHITE_LIST_INSTALL_PACKE, Context.MODE_PRIVATE);
        Set<String> sets = sp.getStringSet(SpCacheConfig.WHITE_LIST_KEY_INSTALL_PACKE_NAME, new HashSet<>());
        SharedPreferences.Editor editor = sp.edit();
        caches.addAll(sets);
        editor.putStringSet(SpCacheConfig.WHITE_LIST_KEY_INSTALL_PACKE_NAME, caches);
        editor.commit();

    }


    /**
     * 移除本地apps,添加缓存
     */
    public void addWhiteList(List<AppInfoBean> lists) {
        apps.remove(lists);
        updateCache(lists);
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
            apps.clear();
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
        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {

                boolean isExist = false;
                for (String packeName : caches) {
                    if (packeName.equals(packageInfo.packageName)){
                        isExist = true;
                    }
                }
                if(!isExist){
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
