/*
 * Copyright 2016 czy1121
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xiaoniu.cleanking.utils.update;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.main.bean.UpdateInfoEntity;
import com.xiaoniu.cleanking.utils.update.listener.IDownloadAgent;
import com.xiaoniu.cleanking.utils.update.listener.IUpdateAgent;
import com.xiaoniu.cleanking.utils.update.listener.IUpdateDownloader;
import com.xiaoniu.cleanking.utils.update.listener.IUpdatePrompter;
import com.xiaoniu.cleanking.utils.update.listener.OnCancelListener;
import com.xiaoniu.cleanking.utils.update.listener.OnDownloadListener;
import com.xiaoniu.cleanking.utils.update.listener.OnFailureListener;

import java.io.File;

public class UpdateAgent implements IUpdateAgent, IDownloadAgent {
    public static final int REQUEST_APP_UPDATE = 5344;
    private static boolean mForce;
    private Activity mActivity;
    private static boolean mIsShowDialogProgress;
    private Context mContext;
    private File mTmpFile;
    private File mApkFile;

    private static UpdateInfoEntity.DataBean mInfo;
    private UpdateError mError = null;

    //private IUpdateChecker mChecker = new DefaultUpdateChecker();
    private IUpdateDownloader mDownloader;
    private IUpdatePrompter mPrompter;

    private OnFailureListener mOnFailureListener;

    private OnDownloadListener mOnDownloadListener;
    private OnDownloadListener mOnNotificationDownloadListener;

    private OnCancelListener mOnCancelListener;

    ActivityCompat.OnRequestPermissionsResultCallback callback = new ActivityCompat.OnRequestPermissionsResultCallback() {
        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            if (requestCode == REQUEST_APP_UPDATE) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    update();
                }
            }
        }
    };

    public ActivityCompat.OnRequestPermissionsResultCallback getCallback() {
        return callback;
    }

    public UpdateAgent(Activity activity, UpdateInfoEntity.DataBean updateInfo, OnCancelListener onCancelListener) {
        initParams(activity, updateInfo, false, onCancelListener);
    }


    public UpdateAgent(Activity activity, UpdateInfoEntity.DataBean updateInfo, boolean isShowDialogProgress, OnCancelListener onCancelListener) {
        initParams(activity, updateInfo, isShowDialogProgress, onCancelListener);
    }

    public void initParams(Activity activity, UpdateInfoEntity.DataBean updateInfo, boolean isShowDialogProgress, OnCancelListener onCancelListener) {
        mContext = activity;
        mActivity = activity;
        mInfo = updateInfo;
        mOnCancelListener = onCancelListener;
        mDownloader = new DefaultUpdateDownloader(mContext);
        mPrompter = new DefaultUpdatePrompter(activity);
        mOnFailureListener = new DefaultFailureListener(activity);
        mOnDownloadListener = new DefaultDialogDownloadListener(activity);
        //是否显示下载进度条
        mIsShowDialogProgress = isShowDialogProgress;
        //是否强更
        mForce = "1".equals(updateInfo.getIsForcedUpdate());

        mOnNotificationDownloadListener = new DefaultNotificationDownloadListener(mContext, 1);
    }

    public void dissmiss() {
        if (mPrompter != null) {
            mPrompter.dismiss();
        }
    }

    @Override
    public void setError(UpdateError error) {
        mError = error;
    }

    /**
     * 开始更新
     */
    @Override
    public void update() {
//        mApkFile = new File(UpdateUtil.getFilePath(mContext));
        mApkFile = UpdateUtil.makeFile(mContext);
//        if (UpdateUtil.verify(mApkFile)) {
//            doInstall();
//        } else {
        doDownload();
//        }
        //关闭弹窗
        dissmiss();
    }

    @Override
    public OnCancelListener getCancelListener() {
        return mOnCancelListener;
    }

    /**
     * 开始下载
     */
    @Override
    public void onStart() {
        if (mForce || mIsShowDialogProgress) {
            //强更弹出下载对话框
            mOnDownloadListener.onStart();
        } else {
            //不抢更弹出notification
            mOnNotificationDownloadListener.onStart();
        }
    }

    /**
     * 下载的进度
     *
     * @param progress
     */
    @Override
    public void onProgress(int progress) {
        if (mForce || mIsShowDialogProgress) {
            mOnDownloadListener.onProgress(progress);
        } else {
            mOnNotificationDownloadListener.onProgress(progress);
        }
    }

    /**
     * 下载结束
     */
    @Override
    public void onFinish() {
        if (mForce || mIsShowDialogProgress) {
            mOnDownloadListener.onFinish();
        } else {
            mOnNotificationDownloadListener.onFinish();
        }
        if (mError != null) {
            mOnFailureListener.onFailure(mError);
        } else {
            mTmpFile.renameTo(mApkFile);
            doInstall();
        }

    }

    /**
     * 弹出更新弹窗。。
     */
    public void check() {
        check(true);
    }

    /**
     * 弹出更新弹窗。。
     *
     * @param showPrompt 是否显示提示更新信息
     */
    public void check(boolean showPrompt) {
        UpdateError error = mError;
        if (error != null) {
            doFailure(error);
            return;
        }
        if (mInfo == null) {
            doFailure(new UpdateError(UpdateError.CHECK_UNKNOWN));
            return;
        }
        mApkFile = UpdateUtil.makeFile(mContext);
        mTmpFile = new File(UpdateUtil.getTempPath(mContext));
        if (showPrompt) {
            doPrompt();
        } else {
            requestPermission(this);
        }
    }

    //弹窗操作
    void doPrompt() {
        mPrompter.prompt(this);
    }

    //下载操作
    void doDownload() {
        //"https://ucan.25pp.com/PPAssistant_PP_102.apk"
        mDownloader.download(this, mInfo.getDownloadUrl(), mTmpFile);
    }

    //安装操作
    void doInstall() {
        UpdateUtil.install(mContext, mApkFile, mForce || mIsShowDialogProgress);
    }

    //失败
    void doFailure(UpdateError error) {
        if (error.isError()) {
            mOnFailureListener.onFailure(error);
        }
    }

    //下载
    private static class DefaultUpdateDownloader implements IUpdateDownloader {
        final Context mContext;

        public DefaultUpdateDownloader(Context context) {
            mContext = context;
        }

        @Override
        public void download(IDownloadAgent agent, String url, File temp) {
            new UpdateDownloader(agent, mContext, url, temp).execute();
        }
    }


    //更新弹窗
    private static class DefaultUpdatePrompter implements IUpdatePrompter {

        private Activity mActivity;

        private TextView update_title;
        private TextView update_content;
        private Button update_id_ok;
        private ImageView update_id_cancel;
        private TextView update_version_num;

        private Dialog dialog;

        public DefaultUpdatePrompter(Activity activity) {
            mActivity = activity;
        }


        @Override
        public void prompt(final IUpdateAgent agent) {
            if (mActivity.isFinishing()) {
                return;
            }

            dialog = new Dialog(mActivity, R.style.dialog_2_button);

            //dialog.setTitle("应用更新");
            dialog.setCanceledOnTouchOutside(true);

            View inflate = View.inflate(mActivity, R.layout.activity_update_dialog, null);

//            dialog.setView(inflate, (int) (25 * density), (int) (15 * density), (int) (25 * density), 0);
            this.dialog.setContentView(inflate, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            update_title = inflate.findViewById(R.id.update_title);
            update_content = inflate.findViewById(R.id.update_content);
            update_id_ok = inflate.findViewById(R.id.update_id_ok);
            update_id_cancel = inflate.findViewById(R.id.pop_close);
            update_version_num = inflate.findViewById(R.id.update_version);
//            View divider = inflate.findViewById(R.id.view_divider);
            //设置title
            //update_title.setText("当前版本:" + mInfo.getOldVersionNumber() + ",最新版本:" + mInfo.getVersionNumber());
            //设置内容
            String temp = mInfo.getChangeCopy().replace("\\n", "\n");
            update_content.setText(temp);
            update_version_num.setText(mInfo.getVersionNumber());
            //设置点击监听
            //升级
            update_id_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestPermission(agent);
                }
            });
            //取消
            update_id_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            if (mForce) {
                //弹窗无法关闭
                dialog.setCancelable(false);
                update_id_cancel.setVisibility(View.GONE);
                //  divider.setVisibility(View.GONE);
            }
            this.dialog.show();

            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (!mForce && !mIsShowDialogProgress) {
                        agent.getCancelListener().onCancel();
                    }
                }
            });
        }

        @Override
        public void dismiss() {
            if(null!=dialog){
                dialog.dismiss();
            }
        }

        /**
         * android 6.0请求写入sd权限
         *
         * @param agent
         */
        public void requestPermission(IUpdateAgent agent) {
            if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_APP_UPDATE);
            } else {
                //开始更新
                agent.update();
            }
        }

    }

    /**
     * android 6.0请求写入sd权限
     *
     * @param agent
     */
    public void requestPermission(IUpdateAgent agent) {
        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_APP_UPDATE);
        } else {
            //开始更新
            this.update();
        }
    }

    //失败
    private static class DefaultFailureListener implements OnFailureListener {

        private Context mContext;

        public DefaultFailureListener(Context context) {
            mContext = context;
        }

        @Override
        public void onFailure(UpdateError error) {
            UpdateUtil.log(error.toString());
            Toast.makeText(mContext, error.toString(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 下载进度条对话框回调
     */
    private static class DefaultDialogDownloadListener implements OnDownloadListener {
        private Context mContext;
        private Dialog mDialog;
        private ProgressBar pgBar;
        private TextView tvPg;

        public DefaultDialogDownloadListener(Context context) {
            mContext = context;
        }

        /**
         * 开始下载
         */
        @Override
        public void onStart() {
            if (mContext instanceof Activity && !((Activity) mContext).isFinishing()) {
                Dialog dialog = new Dialog(mContext, R.style.dialog_2_button);

                View inflate = View.inflate(mContext, R.layout.custom_download_dialog, null);

                dialog.setContentView(inflate, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                pgBar = inflate.findViewById(R.id.jjdxm_update_progress_bar);
                tvPg = inflate.findViewById(R.id.jjdxm_update_progress_text);
                dialog.setCancelable(false);
                dialog.show();
                mDialog = dialog;
            }
        }

        /**
         * 进度回调
         *
         * @param i
         */
        @Override
        public void onProgress(int i) {
            if (mDialog != null) {
                pgBar.setProgress(i);
                tvPg.setText(i + "%");
            }
        }

        /**
         * 下载完成以后回调
         */
        @Override
        public void onFinish() {
            if (mDialog != null) {
                mDialog.dismiss();
                mDialog = null;
            }
        }
    }

    //Notification
    private static class DefaultNotificationDownloadListener implements OnDownloadListener {
        private Context mContext;
        private int mNotifyId;
        private NotificationCompat.Builder mBuilder;

        public DefaultNotificationDownloadListener(Context context, int notifyId) {
            mContext = context;
            mNotifyId = notifyId;
        }

        @Override
        public void onStart() {
            if (mBuilder == null) {
                String title = "下载中 - " + mContext.getString(mContext.getApplicationInfo().labelRes);
                mBuilder = new NotificationCompat.Builder(mContext);
                mBuilder.setOngoing(true)
                        .setAutoCancel(false)
                        .setPriority(Notification.PRIORITY_MAX)
                        .setDefaults(Notification.DEFAULT_VIBRATE)
                        .setSmallIcon(mContext.getApplicationInfo().icon)
                        .setTicker(title)
                        .setContentTitle(title);

                // 此处必须兼容android O设备，否则系统版本在O以上可能不展示通知栏
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(mContext.getPackageName(), "app_download", NotificationManager.IMPORTANCE_DEFAULT);
                    ((NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
                    // channelId非常重要，不设置通知栏不展示
                    mBuilder.setChannelId(mContext.getPackageName());
                }
            }
            onProgress(0);
        }

        @Override
        public void onProgress(int progress) {
            if (mBuilder != null) {
                if (progress > 0) {
                    mBuilder.setPriority(Notification.PRIORITY_DEFAULT);
                    mBuilder.setDefaults(0);
                }
                mBuilder.setProgress(100, progress, false);

                NotificationManager nm = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                nm.notify(mNotifyId, mBuilder.build());
            }
        }

        @Override
        public void onFinish() {
            //下载完成后更改布局，点击安装
            NotificationManager nm = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            //nm.cancel(mNotifyId);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                intent.setDataAndType(Uri.fromFile(new File(UpdateUtil.getFilePath())), "application/vnd.android.package-archive");
            } else {
                Uri uri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".updatefileprovider", new File(UpdateUtil.getFilePath()));
                intent.setDataAndType(uri, "application/vnd.android.package-archive");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            try {
                mBuilder.setContentTitle("下载完成").setContentText("点击安装").setContentIntent(pendingIntent).setProgress(100, 100, false).setAutoCancel(true);
                nm.notify(mNotifyId, mBuilder.build());

                File file = new File(UpdateUtil.getFilePath());
                if (!file.exists()) {
                    nm.cancel(mNotifyId);
                }
            } catch (Exception e) {

            }


        }
    }
}