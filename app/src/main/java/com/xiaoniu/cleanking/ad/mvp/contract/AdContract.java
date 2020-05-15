package com.xiaoniu.cleanking.ad.mvp.contract;


import com.bytedance.sdk.openadsdk.TTAdNative;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.splash.SplashADListener;
import com.xiaoniu.cleanking.ad.bean.AdRequestBean;
import com.xiaoniu.cleanking.ad.bean.AdRequestParamentersBean;
import com.xiaoniu.cleanking.ad.interfaces.AdShowCallBack;
import com.xiaoniu.cleanking.base.BaseView;


/**
 * @ProjectName: clean
 * @Package: com.xiaoniu.cleanking.ad
 * @ClassName: AdContract
 * @Description:
 * @Author: youkun_zhou
 * @CreateDate: 2020/5/9 14:52
 */
public interface AdContract {

    interface View extends BaseView {

    }

    interface Presenter {
        void requestAd(AdRequestParamentersBean adRequestParamentersBean, AdShowCallBack adShowCallBack);
    }


    interface Model {
        void getYLHTemplateAd(AdRequestParamentersBean adRequestParamentersBean,AdRequestBean adRequestBean, NativeExpressAD.NativeExpressADListener adShowCallBack);

        void getCSJTemplateAd(AdRequestParamentersBean adRequestParamentersBean, AdRequestBean adRequestBean, TTAdNative.NativeExpressAdListener adShowCallBack);

        void getYLHSplashAd(AdRequestParamentersBean adRequestParamentersBean, AdRequestBean adRequestBean, SplashADListener adShowCallBack);

        void getCSJSplashAd(AdRequestParamentersBean adRequestParamentersBean, AdRequestBean adRequestBean, TTAdNative.SplashAdListener adShowCallBack);
    }
}
