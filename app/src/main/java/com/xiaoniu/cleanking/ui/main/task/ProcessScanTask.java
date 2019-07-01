package com.xiaoniu.cleanking.ui.main.task;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;

import com.jaredrummler.android.processes.ProcessManager;
import com.jaredrummler.android.processes.models.AndroidAppProcess;
import com.jaredrummler.android.processes.models.Statm;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.callback.IScanCallback;
import com.xiaoniu.cleanking.ui.main.model.JunkInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by mazhuang on 1/20.
 */
public class ProcessScanTask extends AsyncTask<Void, Void, Void> {

    private IScanCallback mCallback;

    public ProcessScanTask(IScanCallback mCallback) {
        this.mCallback = mCallback;
    }

    @Override
    protected Void doInBackground(Void... params) {
        mCallback.onBegin();

        List<AndroidAppProcess> processes = ProcessManager.getRunningAppProcesses();

        ArrayList<JunkInfo> junks = new ArrayList<>();

        for (AndroidAppProcess process : processes) {
            JunkInfo info = new JunkInfo();
            info.mIsChild = false;
            info.mIsVisible = true;
            info.mPackageName = process.getPackageName();

            try {
                Statm statm = process.statm();
                info.mSize = statm.getResidentSetSize();
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }

            try {
                PackageManager pm = AppApplication.getInstance().getPackageManager();
                PackageInfo packageInfo = process.getPackageInfo(AppApplication.getInstance(), 0);
                info.name = packageInfo.applicationInfo.loadLabel(pm).toString();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                continue;
            }

            mCallback.onProgress(info);

            junks.add(info);
        }

        Collections.sort(junks);
        Collections.reverse(junks);
        mCallback.onFinish(junks);

        return null;
    }
}
