package com.xiaoniu.common.utils;
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import android.content.Context;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.util.Log;
import com.bun.miitmdid.core.JLibrary;
import com.bun.miitmdid.core.MdidSdkHelper;
import com.bun.supplier.IIdentifierListener;
import com.bun.supplier.IdSupplier;

public class MiitHelper implements IIdentifierListener {
    private MiitHelper.AppIdsUpdater _listener;

    public MiitHelper(MiitHelper.AppIdsUpdater callback) {
        this._listener = callback;
    }

    public void getDeviceIds(Context cxt) {
        try {
            if (VERSION.SDK_INT < 24) {
                return;
            }

            JLibrary.InitEntry(cxt);
            long timeb = System.currentTimeMillis();
            int nres = this.CallFromReflect(cxt);
            long timee = System.currentTimeMillis();
            long var10000 = timee - timeb;
            if (nres != 1008612 && nres != 1008613 && nres != 1008611 && nres != 1008614 && nres == 1008615) {
            }

            Log.d(this.getClass().getSimpleName(), "return value: " + String.valueOf(nres));
        } catch (Throwable var9) {
            var9.printStackTrace();
        }

    }

    private int CallFromReflect(Context cxt) {
        return MdidSdkHelper.InitSdk(cxt, true, this);
    }

    private int DirectCall(Context cxt) {
        return 0;
    }

    public void OnSupport(boolean isSupport, IdSupplier _supplier) {
        try {
            if (_supplier == null) {
                return;
            }

            String oaid = _supplier.getOAID();
            if (this._listener != null) {
                this._listener.OnIdsAvalid(oaid);
            }
        } catch (Throwable var4) {
            var4.printStackTrace();
        }

    }

    public interface AppIdsUpdater {
        void OnIdsAvalid(@NonNull String var1);
    }
}
