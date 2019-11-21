package com.xiaoniu.cleanking.jsbridge.module;

import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;

import com.apkfuns.jsbridge.module.JBCallback;
import com.apkfuns.jsbridge.module.JSBridgeMethod;
import com.apkfuns.jsbridge.module.JsModule;
import com.geek.webpage.entity.WebPageEntity;
import com.geek.webpage.eventbus.BaseEventBus;
import com.geek.webpage.eventbus.BaseEventBusConstant;
import com.geek.webpage.web.model.WebDialogManager;
import com.google.gson.Gson;
import com.xiaoniu.cleanking.jsbridge.JsBridgeEnums;
import com.xiaoniu.cleanking.jsbridge.entity.JsBridgeEntity;
import com.xiaoniu.cleanking.jsbridge.entity.RedEntity;

import org.simple.eventbus.EventBus;

/**
 * 代码描述<p>
 *
 * @author anhuiqing
 * @since 2019/6/7 10:56
 */
public class JsBridgeModule extends JsModule {
    @Override
    public String getModuleName() {
        return "jsToNative";
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @JSBridgeMethod(methodName = "doAction")
    public void doAction(final String data, final JBCallback jsCallback) {
        if (TextUtils.isEmpty(data))
            return;
        try {
            final Gson gson = new Gson();
            JsBridgeEntity jsBridgeEntity = gson.fromJson(data, JsBridgeEntity.class);
            if (jsBridgeEntity != null && !TextUtils.isEmpty(jsBridgeEntity.action)) {
                JsBridgeEnums jsBridgeEnums = JsBridgeEnums.val(jsBridgeEntity.action);
                switch (jsBridgeEnums) {
                    case HEADERS://头部信息
                      /*  if (jsCallback != null){
                            jsCallback.apply(ParamUtils.getHeaders());
                        }*/
                        break;
                    case TOKEN_OVERDUA://token 过期
                        //清除本地数据
                       /* PersonLoginCacheUtils.clearLogin();
                        //选择登录拦截器
                        SingleCall.getInstance()
                                .addAction(new Action() {
                                    @Override
                                    public void call() {
                                        //登录成功后逻辑处理
                                        if (jsCallback != null){
                                            jsCallback.apply(ParamUtils.getHeaders());
                                        }
                                    }
                                })
                                .addValid(new LoginValid(getContext()))
                                .doCall();*/
                        break;
                    case SHARE://微信分享
                       /* if (jsBridgeEntity.data != null){
                            WebShareEntity webShareEntity = gson.fromJson(data, WebShareEntity.class);
                            if (webShareEntity != null && webShareEntity.data != null){
                                CustomShareView customShareView = new CustomShareView((Activity) getContext(),jsCallback,"",webShareEntity.data);
                                if (webShareEntity.data.getShareType() != -1){
                                    if (!TextUtils.isEmpty(webShareEntity.data.getcallbackMethods())){
                                        getWebView().evaluateJavascript(webShareEntity.data.getcallbackMethods(), new ValueCallback<String>() {
                                            @Override
                                            public void onReceiveValue(String value) {
                                                if (!TextUtils.isEmpty(value)){
                                                    webShareEntity.data.setShareImage(new UMImage(getContext(), BitmapUtils.stringtoBitmap(value)));
                                                }
                                                customShareView.singleShare(mContext,  webShareEntity.data);
                                            }
                                        });
                                    }else {
                                        customShareView.singleShare(mContext,  webShareEntity.data);
                                    }

                                }else {
                                    customShareView.show();
                                }
                            }
                        }*/
                        break;
                    case OPNE_DIALOG_WEBVIEW://打开红包webview
                        if (jsBridgeEntity.data != null) {
                            RedEntity redEntity = gson.fromJson(data, RedEntity.class);
                            if (redEntity != null) {
                                WebPageEntity webPageEntity = redEntity.data;
                                if (webPageEntity != null && !TextUtils.isEmpty(webPageEntity.url)) {
                                    WebDialogManager.getInstance().showWebDialog(getContext(), webPageEntity.url);
                                }
                            }
                        }
                        break;
                    case CLOSE_DIALOG_WEBVIEW://关闭红包webview
                        WebDialogManager.getInstance().dismissWebDialog();
                        break;
                    case REFRESH_RMB_COIN://刷新人民币和金币
//                        EventBus.getDefault().post(new GoldRefreshEvent());
                        break;
                    case WEB_PAGE_FINISH://直接Finish WebpageActivity
                       /* BaseEvent baseEvent = new BaseEvent();
                        baseEvent.setCode(BaseEventEnums.WEB_PAGE_FINISH);
                        EventBus.getDefault().post(baseEvent);*/
                        break;
                    case WEB_PAGE_GO_BACK://直接go back
                      /*  BaseEvent baseEvent1 = new BaseEvent();
                        baseEvent1.setCode(BaseEventEnums.WEB_PAGE_GO_BACK);
                        EventBus.getDefault().post(baseEvent1);*/
                        break;
                    case LOGIN://直接打开登录
                     /*   SingleCall.getInstance()
                                .addAction(new Action() {
                                    @Override
                                    public void call() {
                                        if (jsCallback != null){
                                            jsCallback.apply();
                                        }
                                    }
                                })
                                .addValid(new LoginValid(BaseApplication.getContext()))
                                .doCall();*/
//                        LoginActivity.startLoginActivity(getContext());
                        break;
                    case ISLOGIN://h5回调是否登录 false 表示未登录
                       /* if (jsCallback != null){
                            jsCallback.apply(SPUtils.getBoolean(GlobalConstant.ISLOGIN,false));
                        }*/
                        break;
                    case START_WEBVIEW://直接webview
                        /*if (jsBridgeEntity.data != null){
                            RedEntity redEntity = gson.fromJson(data, RedEntity.class);
                            if (redEntity != null){
                                WebPageEntity webPageEntity = redEntity.data;
                                if (webPageEntity != null){
                                    WebpageActivity.startWebpageActivity(getContext(),webPageEntity);
                                }
                            }
                        }*/
                        break;
                    case START_INVITECODE://直接打开邀请码
//                        InviteCodeActivity.startInviteCodeActivity(getContext());
                        break;
                    case WEB_REDPACKET_AD://点击红包播放激励视频
                        BaseEventBus baseEvent = new BaseEventBus();
                        baseEvent.setAction(BaseEventBusConstant.WEB_REDPACKET_AD);
                        EventBus.getDefault().post(baseEvent);
                        break;

                }
            }
        } catch (Exception e) {
//            ToastUtils.setToastStrShort("参数错误");
        }
    }
}
