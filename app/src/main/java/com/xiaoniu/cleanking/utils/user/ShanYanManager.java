package com.xiaoniu.cleanking.utils.user;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chuanglan.shanyan_sdk.OneKeyLoginManager;
import com.chuanglan.shanyan_sdk.listener.ActionListener;
import com.chuanglan.shanyan_sdk.listener.GetPhoneInfoListener;
import com.chuanglan.shanyan_sdk.listener.OneKeyLoginListener;
import com.chuanglan.shanyan_sdk.listener.OpenLoginAuthListener;
import com.chuanglan.shanyan_sdk.listener.ShanYanCustomInterface;
import com.chuanglan.shanyan_sdk.tool.ShanYanUIConfig;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.api.CommonApiService;
import com.xiaoniu.cleanking.mvp.DefaultRetrofitProxyImpl;
import com.xiaoniu.cleanking.ui.login.activity.BindPhoneManualActivity;
import com.xiaoniu.cleanking.ui.login.bean.RequestPhoneBean;
import com.xiaoniu.common.utils.DisplayUtils;
import com.xiaoniu.common.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;

import static com.xiaoniu.cleanking.utils.user.UserHelper.BIND_PHONE_SUCCESS;

/**
 * Created by zhaoyingtao
 * Date: 2020/7/5
 * Describe:闪验调用管理类
 */
public class ShanYanManager {
    /* 集成文档
     * https://shanyan.253.com/document/details?lid=519&cid=93&pc=28&pn=%25E9%2597%25AA%25E9%25AA%258CSDK
     * 设置授权页协议复选框是否选中
     * OneKeyLoginManager.getInstance().setCheckBoxValue(false);
     * 销毁授权页
     * OneKeyLoginManager.getInstance().finishAuthActivity();
     */

    /**
     * 预取号
     * 建议在判断当前用户属于未登录状态时使用，已登录状态用户请不要调用该方法
     * 建议在执行拉取授权登录页的方法前，提前一段时间调用预取号方法，中间最好有2-3秒的缓冲（因为预取号方法需要1~3s的时间取得临时凭证），比如放在启动页的onCreate（）方法中，或者app启动的application中的onCreate（）方法中去调用，不建议放在用户登录时和拉取授权登录页方法一起调用，会影响用户体验和成功率。
     * 请勿频繁的多次调用、请勿与拉起授权登录页同时和之后调用。
     * 避免大量资源下载时调用，例如游戏中加载资源或者更新补丁的时候要顺序执行
     */
    public static void getPhoneInfo(ShanYanCallBack shanYanCallBack) {
        OneKeyLoginManager.getInstance().getPhoneInfo(new GetPhoneInfoListener() {
            @Override
            public void getPhoneInfoStatus(int code, String result) {
                //code为1022：成功；其他：失败
                // result 返回信息
                if (shanYanCallBack != null) {
                    shanYanCallBack.optionCallBackResult(-1, code, result);
                }
            }
        });
    }

    /**
     * 拉起授权页
     * 调用拉起授权页方法后将会调起运营商授权页面。已登录状态请勿调用 。
     * 每次调用拉起授权页方法前均需先调用授权页配置方法，否则授权页可能会展示异常。
     * 1秒之内只能调用一次，而且必须保证上一次拉起的授权页已经销毁再调用，否则SDK会返回请求频繁。
     */
    public static void openLoginAuth(ShanYanCallBack shanYanCallBack) {
        OneKeyLoginManager.getInstance().openLoginAuth(false, new OpenLoginAuthListener() {
            @Override
            public void getOpenLoginAuthStatus(int code, String result) {
                //code为1000：授权页成功拉起 其他：失败
                // result 返回信息
                if (shanYanCallBack != null) {
                    shanYanCallBack.optionCallBackResult(102, code, result);
                }
            }
        }, new OneKeyLoginListener() {
            @Override
            public void getOneKeyLoginStatus(int code, String result) {
                // code为1000：成功  其他：失败 (包含点击返回键 code=1011)
                // result 返回信息
                if (shanYanCallBack != null) {
                    shanYanCallBack.optionCallBackResult(103, code, result);
                }
            }
        });
    }

    /**
     * 授权页点击事件监听
     * <p>
     * <p>
     * 需要对授权页点击事件监听的用户，可调用此方法监听授权页点击事件，无此需求可以不写。
     */
    public static void setActionListener(ShanYanCallBack shanYanCallBack) {
        OneKeyLoginManager.getInstance().setActionListener(new ActionListener() {
            @Override
            public void ActionListner(int type, int code, String message) {
               /* type=1 ，隐私协议点击事件
                  type=2 ，checkbox点击事件
                  type=3 ，一键登录按钮点击事件
                code
                type=1 ，隐私协议点击事件，code分为0,1,2,3（协议页序号）
                type=2 ，checkbox点击事件，code分为0,1（0为未选中，1为选中）
                type=3 ，一键登录点击事件，code分为0,1（0为协议未勾选时，1为协议勾选时）

                message String点击事件的详细信息*/
                if (shanYanCallBack != null) {
                    shanYanCallBack.optionCallBackResult(type, code, message);
                }
            }
        });
    }

    /**
     * 设置一键授权页面信息
     * OneKeyLoginManager.getInstance().setAuthThemeConfig(ShanYanManager.getCJSConfig(getApplicationContext()));
     *
     * @param context
     * @return
     */
    public static ShanYanUIConfig getCJSConfig(final Context context) {
        /************************************************自定义控件**************************************************************/
        Drawable authNavHidden = context.getResources().getDrawable(R.drawable.bg_white_fillet_06);
        Drawable logoImgPath = context.getResources().getDrawable(R.mipmap.icon_shan_yan_login_logo);
//        Drawable logBtnImgPath = context.getResources().getDrawable(R.mipmap.sysdk_login_safe_bg);
        Drawable logBtnImgPath = context.getResources().getDrawable(R.drawable.bg_green_confirm_fillet_30);
        Drawable uncheckedImgPath = context.getResources().getDrawable(R.mipmap.icon_login_no_check);
        Drawable checkedImgPath = context.getResources().getDrawable(R.mipmap.icon_login_check);
        Drawable navReturnImgPath = context.getResources().getDrawable(R.mipmap.icon_gray_back);

        TextView otherTV = new TextView(context);
        otherTV.setText("验证码绑定");
        otherTV.setTextColor(Color.parseColor("#5CD0FF"));
        otherTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        otherTV.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

        RelativeLayout.LayoutParams mLayoutParams1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        mLayoutParams1.setMargins(0, DisplayUtils.dip2px(330), 0, 0);
        mLayoutParams1.addRule(RelativeLayout.CENTER_HORIZONTAL);
        otherTV.setLayoutParams(mLayoutParams1);

        /****************************************************设置授权页*********************************************************/
        ShanYanUIConfig uiConfig = new ShanYanUIConfig.Builder()
                //授权页导航栏：
                .setNavColor(Color.parseColor("#ffffff"))  //设置导航栏颜色
                .setNavText("")  //设置导航栏标题文字
                .setNavTextSize(18)
                .setNavTextColor(Color.parseColor("#262626")) //设置标题栏文字颜色
                .setAuthBGImgPath(authNavHidden)
                .setNavReturnImgPath(navReturnImgPath)
                .setNavReturnBtnWidth(21)
                .setNavReturnBtnHeight(21)
                .setNavReturnBtnOffsetX(10)

                //.setFullScreen(true)
                //.setAuthBgVideoPath("android.resource://" + context.getPackageName() + "/" + R.raw.testvideo)

                //授权页logo（logo的层级在次底层，仅次于自定义控件）
                .setLogoImgPath(logoImgPath)  //设置logo图片
                .setLogoWidth(115)   //设置logo宽度
                .setLogoHeight(123)   //设置logo高度
                .setLogoOffsetY(106)  //设置logo相对于标题栏下边缘y偏移
                .setLogoHidden(false)   //是否隐藏logo
//                .setLogoOffsetX(20)

                //授权页手机号码栏：
                .setNumberColor(Color.parseColor("#575757"))  //设置手机号码字体颜色
                .setNumFieldOffsetY(244)    //设置号码栏相对于标题栏下边缘y偏移
                .setNumFieldWidth(257)
                .setNumberSize(18)
//                .setNumFieldOffsetX(120)


                //授权页登录按钮：
                .setLogBtnText("本机号码一键绑定")  //设置登录按钮文字
                .setLogBtnTextColor(Color.parseColor("#ffffff"))   //设置登录按钮文字颜色
                .setLogBtnImgPath(logBtnImgPath)   //设置登录按钮图片
                .setLogBtnOffsetY(280)   //设置登录按钮相对于标题栏下边缘y偏移
                .setLogBtnTextSize(15)
                .setLogBtnHeight(40)
                .setLogBtnWidth(256)

                //授权页隐私栏：
//                .setAppPrivacyOne("闪验用户协议", "https://api.253.com/api_doc/yin-si-zheng-ce/wei-hu-wang-luo-an-quan-sheng-ming.html")  //设置开发者隐私条款1名称和URL(名称，url)
//                .setAppPrivacyTwo("闪验隐私政策", "https://api.253.com/api_doc/yin-si-zheng-ce/ge-ren-xin-xi-bao-hu-sheng-ming.html")  //设置开发者隐私条款2名称和URL(名称，url)
//                .setAppPrivacyThree("用户服务条款", "https://api.253.com/api_doc/yin-si-zheng-ce/ge-ren-xin-xi-bao-hu-sheng-ming.html")
                .setPrivacyText("同意安全绑定则代表同意", "", "", "",
                        "授权" + context.getResources().getString(R.string.app_name) + "获得本机号码")
                .setPrivacyTextSize(13)
                .setPrivacyState(true)
                .setCheckBoxMargin(0, 0, 4, 20)
                .setAppPrivacyColor(Color.parseColor("#A4A4A4"), Color.parseColor("#5CD0FF"))   //	设置隐私条款名称颜色(基础文字颜色，协议文字颜色)
                .setPrivacyOffsetBottomY(60)//设置隐私条款相对于屏幕下边缘y偏
                .setUncheckedImgPath(uncheckedImgPath)
                .setCheckedImgPath(checkedImgPath)

                //授权页slogan：
                .setSloganTextColor(Color.parseColor("#A4A4A4"))  //设置slogan文字颜色
                .setSloganTextSize(11)
                .setSloganOffsetBottomY(40)  //设置slogan相对于标题栏下边缘y偏移
                .setSloganHidden(false)
                //授权页创蓝slogan
                .setShanYanSloganOffsetBottomY(20)

                // 添加自定义控件:
                .addCustomView(otherTV, false, false, new ShanYanCustomInterface() {
                    @Override
                    public void onClick(Context context, View view) {
                        Intent intent = new Intent(context, BindPhoneManualActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                })
//                .addCustomView(relativeLayout, false, false, null)
                .build();
        return uiConfig;

    }

    /**
     * 检验手机状态权限
     */
    public static void requestPhonePermission(Activity mActivity) {
        String[] permissions = new String[]{Manifest.permission.READ_PHONE_STATE};
        new RxPermissions(mActivity).request(permissions).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) {

            }
        });
    }

    /**
     * 调起闪验一键登录页面及后续操作
     */
    public static void oneBindingOption(Context mContext) {
        if (mContext == null) {
            return;
        }
        //设置授权的样式
        OneKeyLoginManager.getInstance().setAuthThemeConfig(ShanYanManager.getCJSConfig(mContext));
        ShanYanManager.getPhoneInfo((type, code, message) -> {
            if (code == 1022) {
                openLoginAuth(mContext);
            } else {
                goToShouDongBinding(mContext);
            }
        });
    }

    private static void goToShouDongBinding(Context mContext) {
        Intent intent = new Intent(mContext, BindPhoneManualActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    private static void openLoginAuth(Context mContext) {
        ShanYanManager.openLoginAuth((typeL, codeL, messageL) -> {
            if (typeL == 102) {//拉取页面结果
                if (codeL != 1000) {
                    goToShouDongBinding(mContext);
                }
            } else if (typeL == 103) {//登录结果
                if (codeL == 1000) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(messageL);
                        String token = jsonObject.optString("token");
                        if (!TextUtils.isEmpty(token)) {
                            getPhoneNumFromShanYan(token);
                        } else {
                            goToShouDongBinding(mContext);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private static void getPhoneNumFromShanYan(String token) {
        DefaultRetrofitProxyImpl.getRetrofit().create(CommonApiService.class).getPhoneNumFromShanYanApi(token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {
                })
                .subscribe(new ErrorHandleSubscriber<RequestPhoneBean>(RxErrorHandler.builder().build()) {
                    @Override
                    public void onNext(RequestPhoneBean phoneBean) {
                        if (phoneBean != null && "200".equals(phoneBean.code)) {
                            RequestPhoneBean.DataBean dataBean = phoneBean.getData();
                            if (dataBean != null && !TextUtils.isEmpty(dataBean.getPhone())) {
                                UserHelper.init().setUserPhoneNum(dataBean.getPhone());
                                EventBus.getDefault().post(BIND_PHONE_SUCCESS);
                                OneKeyLoginManager.getInstance().finishAuthActivity();
                                ToastUtils.showShort("绑定成功");
                            } else {
                                OneKeyLoginManager.getInstance().finishAuthActivity();
                            }
                        } else {
                            ToastUtils.showShort(phoneBean.msg);
                        }
                    }
                });
    }
}
