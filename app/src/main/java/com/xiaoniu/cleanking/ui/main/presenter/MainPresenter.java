package com.xiaoniu.cleanking.ui.main.presenter;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Toast;

import com.tencent.tinker.lib.tinker.Tinker;
import com.tencent.tinker.lib.tinker.TinkerLoadResult;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.hotfix.listener.MyPatchListener;
import com.xiaoniu.cleanking.hotfix.log.HotfixLogcat;
import com.xiaoniu.cleanking.ui.main.activity.MainActivity;
import com.xiaoniu.cleanking.ui.main.bean.AppVersion;
import com.xiaoniu.cleanking.ui.main.bean.Patch;
import com.xiaoniu.cleanking.ui.main.bean.UpdateInfoEntity;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.model.MainModel;
import com.xiaoniu.cleanking.utils.AndroidUtil;
import com.xiaoniu.cleanking.utils.net.Common4Subscriber;
import com.xiaoniu.cleanking.utils.prefs.NoClearSPHelper;
import com.xiaoniu.cleanking.utils.update.UpdateAgent;
import com.xiaoniu.cleanking.utils.update.UpdateUtil;
import com.xiaoniu.cleanking.utils.update.listener.OnCancelListener;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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
        mModel.queryAppVersion(new Common4Subscriber<AppVersion>() {


            @Override
            public void getData(AppVersion updateInfoEntity) {
                setAppVersion(updateInfoEntity);
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
//            UpdateUtil.loadFile(activity, result.getData().getPatchUrl(), result.getData().getPatchEncryption(), callback);
        }
    }


    //音乐文件
    private Set<String> cachesMusicFiles = new HashSet<>();
    //apk文件
    private Set<String> cachesApkFies = new HashSet<>();
    //视频文件
    private Set<String> cachesVideo = new HashSet<>();


    /**
     * 文件缓存
     */
    public void saveCacheFiles() {


        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                final String path = Environment.getExternalStorageDirectory().getPath();
                scanMusicFile(path);
                emitter.onNext("");
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(String value) {
                        //
                        SharedPreferences sharedPreferences = mActivity.getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putStringSet(SpCacheConfig.CACHES_KEY_MUSCI, cachesMusicFiles);
                        editor.commit();
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });


        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                final String path = Environment.getExternalStorageDirectory().getPath();
                scanApkFile(path);
                emitter.onNext("");
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(String value) {
                        //
                        SharedPreferences sharedPreferences = mActivity.getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putStringSet(SpCacheConfig.CACHES_KEY_APK, cachesApkFies);
                        editor.commit();
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });


        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                String path = Environment.getExternalStorageDirectory().getPath();
                scanViodeFile(path);
                emitter.onNext("");
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(String value) {
                        //
                        SharedPreferences sharedPreferences = mActivity.getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putStringSet(SpCacheConfig.CACHES_KEY_VIDEO, cachesVideo);
                        editor.commit();
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });


    }


    /**
     * 文件扫描
     */
    private void scanMusicFile(String path) {

        File file = new File(path);
        if (file.isDirectory()) {
            File[] f = file.listFiles();
            if (null != f) {
                for (File file1 : f) {
                    if (file1.isDirectory()) {
                        scanMusicFile(path + "/" + file1.getName());
                    } else if (file1.getName().endsWith(".mp3") && file.length() != 0) {
                        cachesMusicFiles.add(file1.getPath());
                    }
                }
            }
        }

    }

    private void scanApkFile(String path) {

        File file = new File(path);
        if (file.isDirectory()) {
            File[] f = file.listFiles();
            if (null != f) {
                for (File file1 : f) {
                    if (file1.isDirectory()) {
                        scanApkFile(path + "/" + file1.getName());
                    } else if (file1.getName().endsWith(".apk") && file.length() != 0) {
                        cachesApkFies.add(file1.getPath());
                    }
                }
            }
        }

    }


    /**
     * mp4    mov    mkv    avi    wmv    m4v    mpg    vob    webm    ogv    3gp    flv    f4v    swf    gif
     *
     * @param path
     */
    private void scanViodeFile(String path) {
        File file = new File(path);
        if (file.isDirectory()) {
            File[] f = file.listFiles();
            if (null != f) {
                for (File file1 : f) {
                    String fileName = file1.getName().toLowerCase();
                    if (file1.isDirectory()) {
                        scanViodeFile(path + "/" + file1.getName());
                    } else if (fileName.endsWith(".mp4")) {
                        cachesVideo.add(file1.getPath());
                    }
                }
            }
        }
    }

    public void setAppVersion(AppVersion result) {
        if (result != null) {
            //根据版本号判断是否需要更新
            String versionName = AndroidUtil.getAppVersionName();
            int versionCode = AndroidUtil.getVersionCode();
            //默认可以下载
            int code = 0;
            if (!TextUtils.isEmpty(result.code)) {
                code = Integer.parseInt(result.code);
            }
            if (!TextUtils.isEmpty(versionName) && !TextUtils.equals(versionName, result.getData().versionNumber)  && !TextUtils.isEmpty(result.getData().downloadUrl)) {
                boolean isForced = false;
                if (TextUtils.equals(result.getData().forcedUpdate, "1")) {//强更
                    isForced = true;
                } else {//手动更新
                    isForced = false;
                }
                if (mUpdateAgent == null) {
                    mUpdateAgent = new UpdateAgent(mActivity, result, new OnCancelListener() {
                        @Override
                        public void onCancel() {
                        }
                    });
                    mUpdateAgent.check();
                }


            } else {//清空版本信息状态
            }
        } else {
        }
    }

}
