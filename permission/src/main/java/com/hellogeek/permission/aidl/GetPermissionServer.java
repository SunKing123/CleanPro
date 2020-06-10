package com.hellogeek.permission.aidl;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;

;

public class GetPermissionServer extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
//    private RemoteCallbackList<ITestCallBack> mCallBacks = new RemoteCallbackList<>();
//    private String tag = "hy";
//
//    public TestService() {
//    }
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        // TODO: Return the communication channel to the service.
//        return iTestInterface;
//    }
//
//    @Override
//    public boolean onUnbind(Intent intent) {
//        return super.onUnbind(intent);
//    }
//
//    ITestInterface.Stub iTestInterface = new ITestInterface.Stub() {
//        @Override
//        public boolean isTagValid(String tag) throws RemoteException {
//            if (tag.equals(TestService.this.tag)) {
//                callBack();
//                return true;
//            }
//            return false;
//        }
//
//        @Override
//        public void registerCallback(String tag, ITestCallBack callback) throws RemoteException {
//            if (null != mCallBacks && null != callback) {
//                mCallBacks.register(callback);
//            }
//        }
//
//        @Override
//        public void unRegisterCallback(String tag, ITestCallBack callback) throws RemoteException {
//            if (null != mCallBacks && null != callback) {
//                mCallBacks.unregister(callback);
//            }
//        }
//    };
//
//    public void callBack() {
//        if (mCallBacks == null) {
//            return;
//        }
//        int num = mCallBacks.beginBroadcast();
//        for (int i = 0; i < num; i++) {
//            try {
//                mCallBacks.getBroadcastItem(i).onTagValid("congratulation callback success " + tag);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        mCallBacks.finishBroadcast();
//    }
}
