package com.comm.jksdk.ad.view.chjview;//package com.comm.jksdk.ad.view.chjview;
//
//import android.app.Activity;
//import android.content.Context;
//
//import androidx.annotation.RequiresApi;
//
//import android.view.View;
//
//import com.bytedance.sdk.openadsdk.AdSlot;
//import com.bytedance.sdk.openadsdk.TTAdConstant;
//import com.bytedance.sdk.openadsdk.TTAdNative;
//import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
//import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
//import com.comm.jksdk.R;
//import com.comm.jksdk.ad.entity.AdInfo;
//import com.comm.jksdk.config.TTAdManagerHolder;
//import com.comm.jksdk.constant.Constants;
//import com.comm.jksdk.http.utils.LogUtils;
//
//import java.util.List;
//
///**
// * 穿山甲模板渲染插屏广告<p>
// *
// * @author zixuefei
// * @since 2019/11/18 11:24
// */
//public class CsjTemplateInsertScreenAdView extends CHJAdView {
//    private Activity activity;
//    private TTAdNative mTTAdNative;
//    private TTNativeExpressAd mTTAd;
//
//    public CsjTemplateInsertScreenAdView(Context context) {
//        super(context);
//    }
//
//    @Override
//    public int getLayoutId() {
//        return R.layout.csj_full_screen_video_ad_view;
//    }
//
//    @Override
//    public void initView() {
//    }
//
//    /**
//     * 获取插屏广告并展示
//     */
//    public void loadTemplateInsertScreenAd(final Activity activity, final boolean isFullScreen, final int showTimeSeconds, String adId) {
//        if (activity == null) {
//            throw new NullPointerException("loadCustomInsertScreenAd activity is null");
//        }
//        LogUtils.d(TAG, "isFullScreen:" + isFullScreen + " adId:" + adId + " showTimeSeconds:" + showTimeSeconds);
//        //step3:创建TTAdNative对象,用于调用广告请求接口
//        this.activity = activity;
//        mAdInfo = new AdInfo();
//        mAdInfo.setAdSource(Constants.AdType.ChuanShanJia);
//        mAdInfo.setAdAppid(mAppId);
//        mAdInfo.setAdId(adId);
//
//        mTTAdNative = TTAdManagerHolder.get().createAdNative(activity.getApplicationContext());
//
//        float expressViewWidth = 300;
//        float expressViewHeight = 300;
//        AdSlot adSlot = new AdSlot.Builder()
//                .setCodeId(adId) //广告位id
//                .setSupportDeepLink(true)
//                .setAdCount(1) //请求广告数量为1到3条
//                .setExpressViewAcceptedSize(expressViewWidth,expressViewHeight) //期望模板广告view的size,单位dp
//                .setImageAcceptedSize(640,320 )//这个参数设置即可，不影响模板广告的size
//                .build();
//        //step5:请求广告，对请求回调的广告作渲染处理
//        mTTAdNative.loadInteractionExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
//            @Override
//            public void onError(int code, String message) {
//                adError(code, message);
//                firstAdError(code, message);
//            }
//
//            @Override
//            public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
//                if (ads == null || ads.size() == 0){
//                    return;
//                }
//                mTTAd = ads.get(0);
//                bindAdListener(mTTAd);
////                startTime = System.currentTimeMillis();
//                mTTAd.render();
//            }
//        });
//
//    }
//
//    private void bindAdListener(TTNativeExpressAd ad) {
//        ad.setExpressInteractionListener(new TTNativeExpressAd.AdInteractionListener() {
//            @Override
//            public void onAdDismiss() {
////                TToast.show(mContext, "广告关闭");
//                adClose(mAdInfo);
//            }
//
//            @Override
//            public void onAdClicked(View view, int type) {
////                TToast.show(mContext, "广告被点击");
//                adClicked(mAdInfo);
//            }
//
//            @Override
//            public void onAdShow(View view, int type) {
////                TToast.show(mContext, "广告展示");
//                adExposed(mAdInfo);
//            }
//
//            @Override
//            public void onRenderFail(View view, String msg, int code) {
////                Log.e("ExpressView","render fail:"+(System.currentTimeMillis() - startTime));
////                TToast.show(mContext, msg+" code:"+code);
//                adError(code, msg);
//            }
//
//            @Override
//            public void onRenderSuccess(View view, float width, float height) {
////                Log.e("ExpressView","render suc:"+(System.currentTimeMillis() - startTime));
////                //返回view的宽高 单位 dp
////                TToast.show(mContext, "渲染成功");
//                adSuccess(mAdInfo);
//                mTTAd.showInteractionExpressAd(activity);
//
//            }
//        });
//
//        if (ad.getInteractionType() != TTAdConstant.INTERACTION_TYPE_DOWNLOAD){
//            return;
//        }
//        ad.setDownloadListener(new TTAppDownloadListener() {
//            @Override
//            public void onIdle() {
////                TToast.show(InteractionExpressActivity.this, "点击开始下载", Toast.LENGTH_LONG);
//            }
//
//            @Override
//            public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
////                if (!mHasShowDownloadActive) {
////                    mHasShowDownloadActive = true;
////                    TToast.show(InteractionExpressActivity.this, "下载中，点击暂停", Toast.LENGTH_LONG);
////                }
//            }
//
//            @Override
//            public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
////                TToast.show(InteractionExpressActivity.this, "下载暂停，点击继续", Toast.LENGTH_LONG);
//            }
//
//            @Override
//            public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
////                TToast.show(InteractionExpressActivity.this, "下载失败，点击重新下载", Toast.LENGTH_LONG);
//            }
//
//            @Override
//            public void onInstalled(String fileName, String appName) {
////                TToast.show(InteractionExpressActivity.this, "安装完成，点击图片打开", Toast.LENGTH_LONG);
//            }
//
//            @Override
//            public void onDownloadFinished(long totalBytes, String fileName, String appName) {
////                TToast.show(InteractionExpressActivity.this, "点击安装", Toast.LENGTH_LONG);
//            }
//        });
//    }
//}
