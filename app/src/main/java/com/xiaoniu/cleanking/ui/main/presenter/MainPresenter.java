package com.xiaoniu.cleanking.ui.main.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Toast;

import com.geek.push.GeekPush;
import com.google.gson.Gson;
import com.tencent.tinker.lib.tinker.Tinker;
import com.tencent.tinker.lib.tinker.TinkerLoadResult;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.xiaoniu.cleanking.BuildConfig;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.base.BaseEntity;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.scheme.Constant.SchemeConstant;
import com.xiaoniu.cleanking.ui.main.activity.MainActivity;
import com.xiaoniu.cleanking.ui.main.bean.AppVersion;
import com.xiaoniu.cleanking.ui.main.bean.DeviceInfo;
import com.xiaoniu.cleanking.ui.main.bean.Patch;
import com.xiaoniu.cleanking.ui.main.bean.PushSettingList;
import com.xiaoniu.cleanking.ui.main.bean.WebUrlEntity;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.model.MainModel;
import com.xiaoniu.cleanking.utils.FileUtils;
import com.xiaoniu.cleanking.utils.LogUtils;
import com.xiaoniu.cleanking.utils.net.Common4Subscriber;
import com.xiaoniu.cleanking.utils.prefs.NoClearSPHelper;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.cleanking.utils.update.UpdateAgent;
import com.xiaoniu.cleanking.utils.update.UpdateUtil;
import com.xiaoniu.cleanking.utils.update.listener.OnCancelListener;
import com.xiaoniu.common.hotfix.listener.MyPatchListener;
import com.xiaoniu.common.hotfix.log.HotfixLogcat;
import com.xiaoniu.common.utils.AppUtils;
import com.xiaoniu.common.utils.ChannelUtil;
import com.xiaoniu.common.utils.DeviceUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import io.reactivex.Observable;
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
        final String currentVersionName = AppUtils.getVersionName(mActivity, mActivity.getPackageName());
        final String channel = ChannelUtil.getChannel();
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

    //动态获取后台WebUrl+
    public void getWebUrl() {
        mModel.getWebUrl(new Common4Subscriber<WebUrlEntity>() {
            @Override
            public void showExtraOp(String code, String message) {

            }

            @Override
            public void getData(WebUrlEntity webUrlEntity) {
                if (webUrlEntity == null)
                    return;
                if (!TextUtils.isEmpty(webUrlEntity.getData())) {
                    //保存后台webView URL
                    PreferenceUtil.saveWebViewUrl(webUrlEntity.getData());
                }
            }

            @Override
            public void showExtraOp(String message) {

            }

            @Override
            public void netConnectError() {

            }
        });
    }

    /**
     * 激活极光
     */
    public void commitJPushAlias() {
        if (PreferenceUtil.getIsSaveJPushAlias(AppApplication.getInstance()))
            return;
        GeekPush.bindAlias(DeviceUtils.getUdid());
        GeekPush.addTag(BuildConfig.PUSH_TAG);//区分推送环境
        mModel.commitJPushAlias(new Common4Subscriber<BaseEntity>() {
            @Override
            public void showExtraOp(String code, String message) {

            }

            @Override
            public void getData(BaseEntity baseEntity) {
                PreferenceUtil.saveJPushAlias(true);
            }

            @Override
            public void showExtraOp(String message) {

            }

            @Override
            public void netConnectError() {

            }
        });
    }

    /**
     * 操作记录(PUSH消息)
     *
     * @param type（1-立即清理 2-一键加速 3-手机清理 4-文件清理 5-微信专清 6-手机降温 7-qq专清）
     */
    public void commitJpushClickTime(int type) {
        mModel.commitJPushClickTime(type, new Common4Subscriber<BaseEntity>() {
            @Override
            public void showExtraOp(String code, String message) {

            }

            @Override
            public void getData(BaseEntity baseEntity) {

            }

            @Override
            public void showExtraOp(String message) {

            }

            @Override
            public void netConnectError() {

            }
        });
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

        Observable.create((ObservableOnSubscribe<String>) emitter -> {
            //storage/emulated/0  内置SD卡路径下
            final String path = Environment.getExternalStorageDirectory().getPath();
            //scanMusicFile(path);
            scanAllFile(path);
            queryAllMusic();
            emitter.onNext("");
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(String value) {
                        SharedPreferences sharedPreferences = mActivity.getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putStringSet(SpCacheConfig.CACHES_KEY_MUSCI, cachesMusicFiles);
                        editor.putStringSet(SpCacheConfig.CACHES_KEY_APK, cachesApkFies);
                        editor.putStringSet(SpCacheConfig.CACHES_KEY_VIDEO, cachesVideo);
                        editor.commit();

                        mView.onScanFileSuccess();
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });

    }

    private void scanAllFile(String path) {
        File file = new File(path);
        if (file.isDirectory()) {
            File[] f = file.listFiles();
            if (null != f) {
                for (File file1 : f) {
                    String fileName = file1.getName().toLowerCase();
                    if (file1.isDirectory()) {
                        scanAllFile(path + "/" + file1.getName());
                    } else if (fileName.endsWith(".mp4") && file1.length() != 0) {
                        cachesVideo.add(file1.getPath());
                    } else if (fileName.endsWith(".mp3") && file1.length() != 0) {
                        //cachesMusicFiles.add(file1.getPath());
                    } else if (fileName.endsWith(".apk")) {
                        cachesApkFies.add(file1.getPath());
                    }
                }
            }
        }
    }

    private void queryAllMusic() {
        Cursor cursor = mActivity.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.DATA}, null
                , null, null);

        try {
            //solve umeng error -> Caused by: java.lang.OutOfMemoryError
            while (cursor.moveToNext()) {
                String url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                File file = new File(url);
                if (null != file) {
                    cachesMusicFiles.add(file.getPath());
                }
            }
        } catch (OutOfMemoryError e) {
        }

        try {
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setAppVersion(AppVersion result) {
        if (result != null && result.getData() != null) {
            if (TextUtils.equals("1", result.getData().popup))
                if (mUpdateAgent == null) {
                    mUpdateAgent = new UpdateAgent(mActivity, result, () -> {
                    });
                    mUpdateAgent.check();
                } else {
                    mUpdateAgent.check();
                }
        }
    }


    /**
     * 本地Push配置
     */
    public void getPushSetList() {
        mModel.getLocalPushSet(new Common4Subscriber<PushSettingList>() {
            @Override
            public void showExtraOp(String code, String message) {

            }

            @Override
            public void getData(PushSettingList pushSettingList) {
                List<PushSettingList.DataBean> list = pushSettingList.getData();
                if (list != null && list.size() > 0) {
                    //添加通知栏类型_作为状态栏更新条件
                    /* /**
                     * code : push_1
                     * title : 垃圾清理
                     * content : 垃圾过多严重影响手机使用
                     * position : 立即清理页面
                     * url : cleanking://com.xiaoniu.cleanking/native?name=main&main_index=4
                     * thresholdSign : 1
                     * thresholdNum : 200
                     * interValTime : 2
                     * dailyLimit : 12
                     */
                    PushSettingList.DataBean dataBean = new PushSettingList.DataBean();
                    dataBean.setCodeX("push10");//通知栏类型
                    dataBean.setTitle("通知栏");
                    dataBean.setContent("通知栏");
                    dataBean.setUrl(SchemeConstant.LocalPushScheme.SCHEME_NOTIFY_ACTIVITY);

                    dataBean.setThresholdNum(60);
                    dataBean.setInterValTime(60);//每个小时监测
                    dataBean.setLastTime(0);
                    pushSettingList.getData().add(dataBean);
                    PreferenceUtil.saveCleanLog(new Gson().toJson(pushSettingList.getData()));
                } else {//网络配置异常时读取本地
                    PreferenceUtil.saveCleanLog(FileUtils.readJSONFromAsset(mActivity, "action_log.json"));
                }
                //启动保活进程
                mView.start();
            }

            @Override
            public void showExtraOp(String message) {

            }

            @Override
            public void netConnectError() {

            }
        });
    }

    /**
     * 上报设备信息
     * @param deviceInfo
     */
    public void pushDeviceInfo(DeviceInfo deviceInfo){
        mModel.pushDeviceInfo(deviceInfo,new Common4Subscriber<BaseEntity>() {
            @Override
            public void showExtraOp(String code, String message) {
//                LogUtils.i("--zzh---"+message);
                PreferenceUtil.saveIsPushDeviceInfo();

            }

            @Override
            public void getData(BaseEntity baseEntity) {
//                LogUtils.i("--zzh---"+baseEntity.code);
            }

            @Override
            public void showExtraOp(String message) {
//                LogUtils.i("--zzh---"+message);
            }

            @Override
            public void netConnectError() {

            }
        });
    }

}
