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
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.RxPermissions;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.main.bean.AppVersion;
import com.xiaoniu.cleanking.utils.NotificationUtils;
import com.xiaoniu.cleanking.utils.update.listener.IDownloadAgent;
import com.xiaoniu.cleanking.utils.update.listener.IUpdateAgent;
import com.xiaoniu.cleanking.utils.update.listener.IUpdateDownloader;
import com.xiaoniu.cleanking.utils.update.listener.IUpdatePrompter;
import com.xiaoniu.cleanking.utils.update.listener.OnCancelListener;
import com.xiaoniu.cleanking.utils.update.listener.OnDownloadListener;
import com.xiaoniu.cleanking.utils.update.listener.OnFailureListener;
import com.xiaoniu.common.utils.ToastUtils;

import java.io.File;

import io.reactivex.functions.Consumer;

public class UpdateAgent implements IUpdateAgent, IDownloadAgent {

    private static boolean mForce;
    private final Activity mActivity;
    private Context mContext;
    private File mTmpFile;
    private File mApkFile;

    private static AppVersion mInfo;
    private UpdateError mError = null;

    //private IUpdateChecker mChecker = new DefaultUpdateChecker();
    private IUpdateDownloader mDownloader;
    private IUpdatePrompter mPrompter;

    private OnFailureListener mOnFailureListener;

    private OnDownloadListener mOnDownloadListener;
    private OnDownloadListener mOnNotificationDownloadListener;

    private OnCancelListener mOnCancelListener;

    ActivityCompat.OnRequestPermissionsResultCallback callback = (requestCode, permissions, grantResults) -> {
        if (requestCode == 341) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                update();
            }
        }

    };

    public ActivityCompat.OnRequestPermissionsResultCallback getCallback() {
        return callback;
    }


    public UpdateAgent(Activity activity, AppVersion updateInfo, OnCancelListener onCancelListener) {
        mContext = activity;
        mActivity = activity;
        mInfo = updateInfo;
        mOnCancelListener = onCancelListener;
        mDownloader = new DefaultUpdateDownloader(mContext);
        mPrompter = new DefaultUpdatePrompter(activity);
        mOnFailureListener = new DefaultFailureListener(activity);
        mOnDownloadListener = new DefaultDialogDownloadListener(activity);
        //是否强更
        mForce = "1".equals(updateInfo.getData().changeProperties);

        mOnNotificationDownloadListener = new DefaultNotificationDownloadListener(mContext, 1);

    }

    public void dissmiss() {
        if (mPrompter != null)
            mPrompter.dismiss();
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
//        mApkFile = UpdateUtil.makeFile(mContext);
//        if (UpdateUtil.verify(mApkFile)) {
//            doInstall();
//        } else {
        doDownload();
//        }
        //关闭弹窗
        mPrompter.dismiss();
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
        if (mForce) {
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
        if (mForce) {
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
        if (mForce) {
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

    //弹出更新弹窗。。
    public void check() {
        UpdateError error = mError;
        if (error != null) {
            doFailure(error);
        } else {
            if (mInfo == null) {
                doFailure(new UpdateError(UpdateError.CHECK_UNKNOWN));
            } else {
                //UpdateUtil.setUpdate(mContext);
//                mTmpFile = new File(UpdateUtil.getTempPath(mContext));
//                mApkFile = new File(UpdateUtil.getFilePath(mContext));
//                mTmpFile = UpdateUtil.makeFile(mContext);
                mApkFile = UpdateUtil.makeFile(mContext);
                mTmpFile = new File(UpdateUtil.getTempPath(mContext));

                doPrompt();
            }
        }

    }

    /**
     * 自定义下载广告文件
     */
    public void customerDownload() {
        long currentTimes = System.currentTimeMillis();
        mApkFile = UpdateUtil.makeFile(String.valueOf(currentTimes), false);
        mTmpFile = UpdateUtil.makeFile(String.valueOf(currentTimes), true);

        requestPermission(this);
    }

    //弹窗操作
    void doPrompt() {
        mPrompter.prompt(this);
    }

    //下载操作
    void doDownload() {
        mDownloader.download(this, mInfo.getData().downloadUrl, mTmpFile);
    }

    //安装操作
    void doInstall() {
        UpdateUtil.install(mContext, mApkFile, mForce);
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
    private class DefaultUpdatePrompter implements IUpdatePrompter {

        private Activity mActivity;

        private TextView update_content;
        private TextView update_id_ok;
        private ImageView update_id_cancel;
        private TextView update_version_num;

        private Dialog dialog;

        public DefaultUpdatePrompter(Activity activity) {
            mActivity = activity;
        }


        @Override
        public void prompt(final IUpdateAgent agent) {
            if (mActivity.isFinishing())
                return;
            if (dialog != null && dialog.isShowing())
                return;
            dialog = new Dialog(mActivity, R.style.dialog_2_button);

            //dialog.setTitle("应用更新");
            dialog.setCanceledOnTouchOutside(true);

            View inflate = View.inflate(mActivity, R.layout.activity_update_dialog, null);

//            dialog.setView(inflate, (int) (25 * density), (int) (15 * density), (int) (25 * density), 0);
            this.dialog.setContentView(inflate, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            update_content = (TextView) inflate.findViewById(R.id.update_content);
            update_id_ok = (TextView) inflate.findViewById(R.id.update_id_ok);
            update_id_cancel = (ImageView) inflate.findViewById(R.id.update_id_cancel);
            update_version_num = (TextView) inflate.findViewById(R.id.update_version_num);
            //设置title
            //update_title.setText("当前版本:" + mInfo.getOldVersionNumber() + ",最新版本:" + mInfo.getVersionNumber());
            //设置内容
            String temp = mInfo.getData().changeDesc.replace("\\n", "\n");
            update_content.setText(temp);
            update_version_num.setText("V " + mInfo.getData().versionNumber);
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
            }
            this.dialog.show();

            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (!mForce)
                        agent.getCancelListener().onCancel();
                }
            });
        }

        @Override
        public void dismiss() {
            if (dialog != null) {
                dialog.dismiss();
            }
        }

    }

    /**
     * android 6.0请求写入sd权限
     *
     * @param agent
     */
    private void requestPermission(IUpdateAgent agent) {
        String permissionsHint = "需要打开文件读写权限";
        String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        new RxPermissions(mActivity).request(permissions).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean) {
                    //开始更新
                    agent.update();
                } else {
                    if (hasPermissionDeniedForever(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        ToastUtils.showShort(permissionsHint);
                        mPrompter.dismiss();
                    }
                }
            }
        });
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
                pgBar = (ProgressBar) inflate.findViewById(R.id.jjdxm_update_progress_bar);
                tvPg = (TextView) inflate.findViewById(R.id.jjdxm_update_progress_text);
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
                if (mContext == null) {
                    return;
                }
                if (!(mContext instanceof Activity)) {
                    return;
                }
                if (((Activity) mContext).isDestroyed()) {
                    return;
                }
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
                mBuilder = new NotificationUtils(mContext, true).getNotification(title, "");
                mBuilder.setSound(null);
                mBuilder.setVibrate(null);
                mBuilder.setAutoCancel(false);
//                mBuilder = new NotificationCompat.Builder(mContext);
//                mBuilder.setOngoing(true)
//                        .setAutoCancel(false)
//                        .setPriority(Notification.PRIORITY_MAX)
//                        .setDefaults(Notification.DEFAULT_VIBRATE)
//                        .setSmallIcon(mContext.getApplicationInfo().icon)
//                        .setTicker(title)
//                        .setContentTitle(title);
            }
            onProgress(0);
        }

        @Override
        public void onProgress(int progress) {
            if (mBuilder != null) {
                if (progress > 0) {
                    mBuilder.setPriority(Notification.PRIORITY_LOW);
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
                intent.setDataAndType(Uri.fromFile(new File(UpdateUtil.getFilePath(mContext))), "application/vnd.android.package-archive");
            } else {
                Uri uri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".updatefileprovider", new File(UpdateUtil.getFilePath(mContext)));
                intent.setDataAndType(uri, "application/vnd.android.package-archive");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            try {
                mBuilder.setContentTitle("下载完成").setContentText("点击安装").setContentIntent(pendingIntent).setProgress(100, 100, false).setAutoCancel(true);
                nm.notify(mNotifyId, mBuilder.build());

                File file = new File(UpdateUtil.getFilePath(mContext));
                if (!file.exists()) {
                    nm.cancel(mNotifyId);
                }
            } catch (Exception e) {

            }


        }
    }

    /**
     * 是否有权限被永久拒绝
     *
     * @param activity   当前activity
     * @param permission 权限
     * @return
     */
    private static boolean hasPermissionDeniedForever(Activity activity, String permission) {
        boolean hasDeniedForever = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!activity.shouldShowRequestPermissionRationale(permission)) {
                hasDeniedForever = true;
            }
        }
        return hasDeniedForever;
    }
}