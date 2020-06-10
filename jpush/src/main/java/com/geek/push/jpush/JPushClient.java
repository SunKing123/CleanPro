package com.geek.push.jpush;

import android.content.Context;
import android.os.Handler;

import com.geek.push.GeekPush;
import com.geek.push.core.IPushClient;
import com.geek.push.log.LogUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;

/**
 * JPush Client
 * Created by pengyuantao on 2017/10/25 18:05.
 */

public class JPushClient implements IPushClient {
    private final static String TAG = "pushLog " + JPushClient.class.getSimpleName();
    private Context context;

    private HashSet<String> tagSet = new HashSet<String>();

    private Handler handler = new Handler();

    private Runnable setTagRunnable = new Runnable() {
        @Override
        public void run() {
            JPushInterface.addTags(context, GeekPush.TYPE_ADD_TAG, (Set<String>) tagSet.clone());
            tagSet.clear();
        }
    };

    @Override
    public void initContext(Context context) {
        LogUtils.d(TAG, "-----init push------");
        this.context = context.getApplicationContext();
        JPushInterface.setDebugMode(LogUtils.isDebug());
        JPushInterface.init(context);
    }

    @Override
    public void register() {
        LogUtils.d(TAG, "-----register push------");
//        if (JPushInterface.isPushStopped(this.context)) {
//            JPushInterface.resumePush(this.context);
//        }
//        String token = JPushInterface.getRegistrationID(this.context);
//        if (!TextUtils.isEmpty(token)) {
//            PushMsgHandler.transmitCommandResult(this.context, GeekPush.TYPE_REGISTER, 200, token, null,
//                    null);
//        }
    }

    @Override
    public void unRegister() {
        LogUtils.d(TAG, "-----unregister push------");
        JPushInterface.stopPush(this.context);
    }

    @Override
    public void bindAlias(String alias) {
        LogUtils.d(TAG, "-----bindAlias------" + alias);
        JPushInterface.setAlias(this.context, GeekPush.TYPE_BIND_ALIAS, alias);
    }

    @Override
    public void unBindAlias(String alias) {
        LogUtils.d(TAG, "-----unBindAlias------" + alias);
        JPushInterface.deleteAlias(this.context, GeekPush.TYPE_UNBIND_ALIAS);
    }

    @Override
    public void checkTagState(String tag) {
        LogUtils.d(TAG, "-----checkTagState------" + tag);
        JPushInterface.checkTagBindState(context, GeekPush.TYPE_CHECK_TAG, tag);
    }

    @Override
    public void addTag(final String tag) {
        LogUtils.d(TAG, "-----addTag------" + tag);
        handler.removeCallbacks(setTagRunnable);
        tagSet.add(tag);
        handler.postDelayed(setTagRunnable, 300);
    }

    @Override
    public void deleteTag(String tag) {
        LogUtils.d(TAG, "-----deleteTag------" + tag);
        JPushInterface.deleteTags(this.context, GeekPush.TYPE_DEL_TAG, Collections.singleton(tag));
    }

}
