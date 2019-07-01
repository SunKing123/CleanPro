package com.xiaoniu.cleanking.ui.main.presenter;


import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.hotfix.listener.MyPatchListener;
import com.xiaoniu.cleanking.hotfix.log.HotfixLogcat;
import com.xiaoniu.cleanking.ui.main.activity.MainActivity;
import com.xiaoniu.cleanking.ui.main.bean.Patch;
import com.xiaoniu.cleanking.ui.main.bean.UpdateInfoEntity;
import com.xiaoniu.cleanking.ui.main.model.MainModel;
import com.xiaoniu.cleanking.utils.AndroidUtil;
import com.xiaoniu.cleanking.utils.net.Common4Subscriber;
import com.xiaoniu.cleanking.utils.prefs.NoClearSPHelper;
import com.xiaoniu.cleanking.utils.update.UpdateAgent;
import com.xiaoniu.cleanking.utils.update.UpdateUtil;
import com.xiaoniu.cleanking.utils.update.listener.OnCancelListener;
import com.tencent.tinker.lib.tinker.Tinker;
import com.tencent.tinker.lib.tinker.TinkerLoadResult;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

/**
 * Created by tie on 2017/5/15.
 */
public class MainPresenter extends RxPresenter<MainActivity, MainModel> implements UpdateUtil.PatchCallback {

    private final RxAppCompatActivity mActivity;

    private UpdateAgent mUpdateAgent;
    @Inject
    NoClearSPHelper mPreferencesHelper;
    private MyPatchListener mMyPatchListener;

    @Inject
    public MainPresenter(RxAppCompatActivity activity) {
        mActivity = activity;
    }

    /**
     * 版本更新
     */
    public void queryAppVersion(final OnCancelListener onCancelListener) {
        mModel.queryAppVersion(new Common4Subscriber<UpdateInfoEntity>() {


            @Override
            public void getData(UpdateInfoEntity updateInfoEntity) {
                    if (updateInfoEntity.getData() != null) {
                        if (updateInfoEntity.getData().getDownloadUrl() != null
                                && TextUtils.equals(String.valueOf(1),updateInfoEntity.getData().getIsPopup())) {
                            mUpdateAgent = new UpdateAgent(mActivity, updateInfoEntity.getData(), onCancelListener);
                            mUpdateAgent.check();
                        }
                    }
            }

            @Override
            public void showExtraOp(String code, String message) {
                Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void showExtraOp(String message) {
            }

            @Override
            public void netConnectError() {

            }
        });
    }

    private String path;
    private boolean isLoaded;
    private boolean isInstalled;
    private String patchVersion;

    public void queryPatch() {
        isLoaded = Tinker.with(mActivity).isTinkerLoaded();
        isInstalled = Tinker.with(mActivity).isTinkerInstalled();
        final String currentVersionName = AndroidUtil.getAppVersionName();
        final String channel = AndroidUtil.getMarketId();
        if (isLoaded) {
            TinkerLoadResult loadResult = Tinker.with(mActivity).getTinkerLoadResultIfPresent();
            Map<String, String> config = loadResult.packageConfig;
            patchVersion = config.get("patchVersion");
        }
        Map<String, String> queryParams = new HashMap<>();
       // queryParams.put("channel", channel);
        queryParams.put("baseVersionName", currentVersionName);
        queryParams.put("clientType", "1");
        if (!TextUtils.isEmpty(patchVersion)) {
            queryParams.put("patchVersion", patchVersion);
        } else {
            queryParams.put("patchVersion", "");
        }
        mModel.getPatch(queryParams, new Common4Subscriber<Patch>() {
            @Override
            public void getData(Patch patch) {
                if (patch != null && patch.getData() != null) {
                    if (currentVersionName.equals(patch.getData().getBaseVersion()) && !TextUtils.isEmpty(patch.getData().getPatchUrl())) {
                        //补丁版本要么等于空是首先安装补丁，要么补丁的版本有升级
                        if (isInstalled && TextUtils.isEmpty(patchVersion) || !patchVersion.equals(patch.getData().getPatchVersion())) {
                            //Log.v(MyPatchListener.TAG, "current version has new patch, current version is " +patchVersion +" new version is " + result.patchVersion);
                            HotfixLogcat.log("current version has new patch, current version is " + patchVersion + " new version is " + patch.getData().getPatchVersion());
                            Thread thread = new Thread(new LoadFileTask(mActivity, patch, MainPresenter.this));
                            thread.start();
                        } else {
                            HotfixLogcat.log("current version has patch,but already fixed");
                            //Log.v(MyPatchListener.TAG, "current version has patch,but already fixed");
                            //Toast.makeText(context, "该版本下有补丁包，但是已修复补丁 ", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    HotfixLogcat.log("current version don't have patch");
                    //Log.v(MyPatchListener.TAG, "current version don't have patch");
                    //该版本下没有补丁包
                    //Toast.makeText(context, "该版本下没有补丁包 ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void showExtraOp(String code, String message) {
                Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void showExtraOp(String message) {
                //Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void netConnectError() {
                //Toast.makeText(mActivity, "netConnectError", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void downloadSuccess(String path) {
        this.path = path;
        File file = new File(path);
        if (file.canRead()) {
            mMyPatchListener = MyPatchListener.getInstance(mActivity);
            mMyPatchListener.setPath(path);
            if (mMyPatchListener != null && !mMyPatchListener.isPatching()) {
                mMyPatchListener.patching();
            }
        }
    }

    @Override
    public void downloadError(String message) {

    }

    static class LoadFileTask implements Runnable {
        private Patch result;
        private WeakReference<Context> weakReference;
        private UpdateUtil.PatchCallback callback;

        public LoadFileTask(Context context, final Patch result, UpdateUtil.PatchCallback callback) {
            this.result = result;
            weakReference = new WeakReference<>(context);
            this.callback = callback;
        }

        @Override
        public void run() {
            Activity activity = (RxAppCompatActivity) weakReference.get();
            UpdateUtil.loadFile(activity, result.getData().getPatchUrl(), result.getData().getPatchEncryption(), callback);
        }
    }
}
