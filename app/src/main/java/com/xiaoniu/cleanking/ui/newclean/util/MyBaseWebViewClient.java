package com.xiaoniu.cleanking.ui.newclean.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import com.just.agentweb.WebViewClient;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.scheme.Constant.SchemeConstant;
import com.xiaoniu.cleanking.scheme.SchemeProxy;
import com.xiaoniu.cleanking.scheme.utils.ActivityUtils;
import com.xiaoniu.cleanking.scheme.utils.Parameters;
import com.xiaoniu.cleanking.scheme.utils.SchemeUtils;
import com.xiaoniu.cleanking.scheme.utils.UrlUtils;
import com.xiaoniu.cleanking.ui.main.activity.MainActivity;
import com.xiaoniu.cleanking.ui.main.fragment.BaseBrowserFragment;
import com.xiaoniu.cleanking.ui.newclean.presenter.ScratchCardAvdPresenter;
import com.xiaoniu.cleanking.utils.NumberUtils;
import com.xiaoniu.cleanking.utils.anim.AnimationsContainer;
import com.xiaoniu.cleanking.utils.user.UserHelper;
import com.xiaoniu.common.utils.ToastUtils;
import com.xiaoniu.statusview.StatusView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

/**
 * Desc:web client 基类
 * <p>
 * Author: AnYaBo
 * Date: 2019/4/16
 * Copyright: Copyright (c) 2016-2020
 * Company: @小牛科技
 * Update Comments:
 *
 * @author anyabo
 */
public class MyBaseWebViewClient extends WebViewClient {
    private Activity mActivity;
    private ImageView mLoadImg;

    BaseBrowserFragment mBaseBrowserFragment;
    AnimationsContainer.FrameseAnim animaDra;
    ScratchCardAvdPresenter cardAvdPresenter;
    StatusView errorView;

    public MyBaseWebViewClient(BaseBrowserFragment mBaseBrowserFragment, ScratchCardAvdPresenter cardAvdPresenter, Activity activity, ImageView mLoadImg, StatusView errorView) {
        if (activity != null) {
            this.mBaseBrowserFragment = mBaseBrowserFragment;
            this.mActivity = activity;
            this.mLoadImg = mLoadImg;
            this.cardAvdPresenter = cardAvdPresenter;
            this.errorView = errorView;
            animaDra = AnimationsContainer.getInstance(R.array.loading_coin, 100).createAnim(mLoadImg);
            showLoadAnim();
        }
    }

    //加载动画
    private void showLoadAnim() {
        if (mLoadImg != null && animaDra != null) {
            mLoadImg.setVisibility(View.VISIBLE);
            animaDra.start();
            if (downTimer != null) {
                downTimer.start();
            }
        }
    }

    /**
     * 页面退出时调用此方法
     */
    public void destroy() {
        if (downTimer != null) {
            downTimer.cancel();
        }
    }

    CountDownTimer downTimer = new CountDownTimer(7000, 7000) {
        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            ToastUtils.showShort("网络异常，请重试");
        }
    };

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
//        if (errorView != null) {
//            errorView.setVisibility(View.GONE);
//        }
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
//        if (errorView != null) {
//            errorView.setVisibility(View.VISIBLE);
//            errorView.showErrorView();
//        }
        Log.e("snow", "=====onReceivedError=========");
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        //接受所有网站的证书 Android5.0 WebView中Http和Https混合问题 显示空白
        handler.proceed();
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (parseUrl(view, url)) {
            return true;
        } else if (url.contains("alipays://") || url.contains("weixin://") || url.contains("alipayqr://")) {
            try {
                Intent it = new Intent(Intent.ACTION_VIEW);
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                it.setData(Uri.parse(url));
                view.getContext().startActivity(it);
            } catch (Exception e) {
                ToastUtils.showShort(url.contains("weixin://") ? "您未安装微信" : "您未安装支付宝");
            }
            return true;
        } else if (url.contains("undefined")) {
            return true;
        }
        return super.shouldOverrideUrlLoading(view, url);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        String url = request.getUrl().toString();
        if (parseUrl(view, url)) {
            return true;
        } else if (url.contains("alipays://") || url.contains("weixin://") || url.contains("alipayqr://")) {
            try {
                Intent it = new Intent(Intent.ACTION_VIEW);
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                it.setData(Uri.parse(url));
                view.getContext().startActivity(it);
            } catch (Exception e) {
                ToastUtils.showShort(url.contains("weixin://") ? "您未安装微信" : "您未安装支付宝");
            }
            return true;
        } else if (url.contains("xianwan") && !url.contains("https://h5.17xianwan.com/androidten") && !url.contains("https://h5.17xianwan.com/try/try_list_plus")) {
            SchemeProxy.openScheme(mActivity, url);
            return true;
        } else if (url.contains("undefined")) {
            return true;
        }
        try {
            boolean temp = super.shouldOverrideUrlLoading(view, request);
            return temp;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    boolean isFirst = true;

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        Log.e("snow", "=============onPageFinished=====");
        if (downTimer != null) {
            downTimer.cancel();
        }
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (!url.contains("http")) {//不是正常连接不对
            return;
        }
        //解决网速慢，手速快，快速切到赚赚tab页，refreshWebView代码无效导致无法自动签到问题
        if (url.contains("html/zhuanzhuan/index") && isFirst && mBaseBrowserFragment != null && mBaseBrowserFragment.isShow) {
            isFirst = false;
            mBaseBrowserFragment.refreshWebView();
        }
        if (animaDra != null && mLoadImg != null) {
            animaDra.stop();
            mLoadImg.setVisibility(View.GONE);
        }
    }

    /**
     * 解析url协议
     */
    public boolean parseUrl(WebView webView, String url) {
        if (SchemeUtils.isScheme(url)) {
            Uri uri = Uri.parse(url);
            String path = uri.getPath();
            Parameters parameters = UrlUtils.getParamsFromUrl(url);
            parseVideoCallBackParams(parameters);
            boolean needLogin = TextUtils.equals("1", parameters.getParameter(SchemeConstant.NEED_LOGIN));
            if (needLogin) {
                if (!UserHelper.init().isWxLogin() && mActivity != null && !mActivity.isFinishing()) {
                    UserHelper.init().startToLogin(mActivity);
                    return true;
                }
            }
            if (SchemeConstant.REWARD_TOP.equals(path)) {
                //激励后的弹窗 插屏广告
                parseRewardPop(webView, url, parameters);
            } else if (SchemeConstant.CLOSE.equals(path)) {
                //关闭H5协议
                parseClose(webView, url, parameters);
            } else if (SchemeConstant.HOME.equals(path)) {
                //首页tab切换
                parseHome(webView, url, parameters);
            } else {
                SchemeUtils.openScheme(mActivity, url, null, ActivityUtils.REQUEST_CODE_FROM_BROWSER);
            }
            return true;
        }
        return false;
    }

    private void parseVideoCallBackParams(Parameters parameters) {
        //关闭激励广告
        try {
            JSONObject jsonObject = new JSONObject();
            Set<String> set = parameters.getParameterNames();
            for (String key : set) {
                if (!TextUtils.equals(SchemeConstant.NEED_LOGIN, key)) {
                    String value = parameters.getParameter(key);
                    jsonObject.put(key, value);
                }
            }
            if (mBaseBrowserFragment != null) {
                mBaseBrowserFragment.setVideoCallBackParams(jsonObject.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 激励后的弹窗 插屏广告
     */
    private void parseRewardPop(WebView webView, String url, Parameters parameters) {
        if (mActivity == null || mActivity.isFinishing() || webView == null && parameters == null) {
            return;
        }
        String codeId = parameters.getParameter(SchemeConstant.AD_CODEID);//插屏广告code
        int source = NumberUtils.getInteger(parameters.getParameter(SchemeConstant.AD_SOURCE));//广告来源
        ADUtils.adSource = parameters.getParameter(SchemeConstant.AD_ADDES);//点击的哪个任务了
        String ad_position_id = parameters.getParameter(SchemeConstant.AD_AD_POSITION_ID);
        //总金币数
        String totalCoin = parameters.getParameter(SchemeConstant.TOTAL_COIN);
        //当前奖励金币数
        String coin = parameters.getParameter(SchemeConstant.COIN);
        //area=1  区域1
        //area=2  区域2
        String area = parameters.getParameter(SchemeConstant.AREA);
        //是否需要翻倍
        boolean isDouble = TextUtils.equals("1", parameters.getParameter(SchemeConstant.IS_DOUBLE));
        //h5 可能需要传这个id
        String taskId = parameters.getParameter(SchemeConstant.TASK_ID);
        //签到天数
        String signDay = parameters.getParameter(SchemeConstant.SIGNDAY);
        String cardPosition = parameters.getParameter(SchemeConstant.CARD_POSITION);
        if (!TextUtils.isEmpty(totalCoin) && !TextUtils.isEmpty(coin) && !TextUtils.isEmpty(cardPosition) && cardAvdPresenter != null) {
            int obtainCoinCount = Integer.parseInt(coin);//获得的金币
            int totalCoinCount = Integer.parseInt(totalCoin);//用户总金币金额===这里totalCoinCount在h5已经添加过了
            cardAvdPresenter.showDialog(Integer.parseInt(cardPosition), obtainCoinCount, totalCoinCount, isDouble, "1".equals(area));
        }
    }

    /**
     * 关闭H5协议
     */
    private void parseClose(WebView webView, String url, Parameters parameters) {
        if (mActivity == null || mActivity.isFinishing() || webView == null) {
            return;
        }
        String target = parameters.getParameter(SchemeConstant.TARGET);
        if (!TextUtils.isEmpty(target) && SchemeUtils.isScheme(target)) {
            SchemeUtils.openScheme(mActivity, target, null, ActivityUtils.REQUEST_CODE_FROM_BROWSER);
        }
        mActivity.finish();
    }

    /**
     * 首页tab切换
     */
    private void parseHome(WebView webView, String url, Parameters parameters) {
        if (mActivity == null || mActivity.isFinishing() || webView == null) {
            return;
        }
        String tabIndex = parameters.getParameter(SchemeConstant.TAB_INDEX);
        if (null != mActivity && !mActivity.isFinishing()) {
            if (!(mActivity instanceof MainActivity)) {
                mActivity.finish();
                //TODO 刷新 首页
//                EventBus.getDefault().post(new RefreshUIEvent(RefreshUIEvent.EVENT_TAB_CHECK, tabIndex));
            }
        }
    }


    //插屏广告
    private String needLoadAD(boolean isDouble, String taskId) {
        String adId = "";
        //签到翻倍后 给的taskid 就是空
        if (TextUtils.isEmpty(taskId)) {
            ADUtils.adSource = "签到翻倍奖励";
            adId = "7";
        }

        if (!TextUtils.isEmpty(taskId)) {
            switch (taskId) {
                //福利弹窗
                case "2":
                    ADUtils.adSource = "看福利视频赚金币";
                    adId = "10";
                    break;
                case "3":
                    //绑微信
                    ADUtils.adSource = "微信绑定";
                    adId = "13";
                    break;
                case "4":
                    //绑手机号
                    ADUtils.adSource = "手机号绑定";
                    adId = "14";
                    break;
                //每日分享
                case "5":
                    ADUtils.adSource = "每日分享";
                    adId = "15";
                    break;
                //首次提现
                case "6":
                    ADUtils.adSource = "提现";
                    adId = "16";
                    break;
                // 邀请好友—插屏广告
                case "7":
                    ADUtils.adSource = "邀请好友";
                    adId = "21";
                    break;
                //神秘彩蛋—插屏广告
                case "8":
                    ADUtils.adSource = "神秘彩蛋";
                    adId = "23";
                    break;
                //补领金币—插屏广告
                case "9":
                    ADUtils.adSource = "补领金币";
                    adId = "27";
                    break;
                case "10": //达标赛奖励—插屏广告
                    ADUtils.adSource = "达标赛奖励";
                    adId = "29";
                    break;
                case "11"://下载app任务—插屏广告
                    ADUtils.adSource = "下载app奖励";
                    adId = "25";
                    break;
                case "13"://喝水活动—插屏广告
                    ADUtils.adSource = "喝水活动";
                    adId = "33";
                    break;
            }
        } else if (isDouble) {
            ADUtils.adSource = "签到奖励";
            //签到弹窗
            adId = "1";
        }


        return adId;
    }

    //插屏广告
    private int getSource(boolean isDouble, String taskId) {
        String adId = "";
        //签到翻倍后 给的taskid 就是空
        if (TextUtils.isEmpty(taskId)) {
            ADUtils.adSource = "签到翻倍奖励";
            adId = "7";
        }
        if (!TextUtils.isEmpty(taskId)) {
            switch (taskId) {
                //福利弹窗
                case "2":
                    ADUtils.adSource = "看福利视频赚金币";
                    adId = "10";
                    break;
                case "3":
                    //绑微信
                    ADUtils.adSource = "微信绑定";
                    adId = "13";
                    break;
                case "4":
                    //绑手机号
                    ADUtils.adSource = "手机号绑定";
                    adId = "14";
                    break;
                //每日分享
                case "5":
                    ADUtils.adSource = "每日分享";
                    adId = "15";
                    break;
                //首次提现
                case "6":
                    ADUtils.adSource = "提现";
                    adId = "16";
                    break;
                // 邀请好友—插屏广告
                case "7":
                    ADUtils.adSource = "邀请好友";
                    adId = "21";
                    break;
                //神秘彩蛋—插屏广告
                case "8":
                    ADUtils.adSource = "神秘彩蛋";
                    adId = "23";
                    break;
                //补领金币—插屏广告
                case "9":
                    ADUtils.adSource = "补领金币";
                    adId = "27";
                    break;
                case "10": //达标赛奖励—插屏广告
                    ADUtils.adSource = "达标赛奖励";
                    adId = "29";
                    break;
                case "11"://下载app任务—插屏广告
                    ADUtils.adSource = "下载app奖励";
                    adId = "25";
                    break;
                case "13"://喝水活动—插屏广告
                    ADUtils.adSource = "喝水活动";
                    adId = "33";
                    break;
            }
        } else if (isDouble) {
            ADUtils.adSource = "签到奖励";
            //签到弹窗
            adId = "1";
        }
        return ADUtils.getAdSource(adId);
    }

    private void callBackJs(Parameters parameters, WebView webView) {
        //关闭激励广告
        JSONObject jsonObject = new JSONObject();
        try {
            Set<String> set = parameters.getParameterNames();
            for (String key : set) {
                if (!TextUtils.equals(SchemeConstant.NEED_LOGIN, key)) {
                    String value = parameters.getParameter(key);
                    jsonObject.put(key, value);
                }
            }
            Log.e("777", "jsonObject : " + jsonObject.toString());
            webView.loadUrl("javascript:videoCallBack(" + jsonObject.toString() + ")");
        } catch (Exception e) {
            Log.e("777", e.getMessage() + "");
        }
    }

    /**
     * 判断是否缺少权限
     */
    private boolean lacksPermission(String... permission) {
        if (mActivity == null || mActivity.isFinishing()) {
            return false;
        }
        boolean mIsHaveAllPermission = true;
//        mNeedPermissionNames.clear();
        if (ContextCompat.checkSelfPermission(mActivity, permission[0]) == PackageManager.PERMISSION_DENIED) {
            mIsHaveAllPermission = false;
//            mNeedPermissionNames.add(getString(R.string.get_phone_permission));
        }
        if (ContextCompat.checkSelfPermission(mActivity, permission[1]) == PackageManager.PERMISSION_DENIED) {
            mIsHaveAllPermission = false;
//            mNeedPermissionNames.add(getString(R.string.get_phone_permission));
        }
        return mIsHaveAllPermission;
    }
}
