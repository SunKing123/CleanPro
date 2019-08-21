package com.xiaoniu.cleanking.ui.main.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.xiaoniu.cleanking.AppConstants;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppManager;
import com.xiaoniu.cleanking.base.SimpleFragment;
import com.xiaoniu.cleanking.ui.main.activity.QuestionReportActivity;
import com.xiaoniu.cleanking.ui.main.activity.WhiteListSettingActivity;
import com.xiaoniu.cleanking.ui.tool.wechat.util.QueryFileUtil;
import com.xiaoniu.cleanking.ui.tool.wechat.util.WxQqUtil;
import com.xiaoniu.cleanking.ui.usercenter.activity.AboutActivity;
import com.xiaoniu.cleanking.ui.usercenter.activity.PermissionActivity;
import com.xiaoniu.cleanking.utils.DeviceUtils;
import com.xiaoniu.cleanking.utils.StatisticsUtils;
import com.xiaoniu.cleanking.utils.ToastUtils;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;
import com.xiaoniu.statistic.NiuDataAPI;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 我的
 * Created on 2018/3/21.
 */
public class MeFragment extends SimpleFragment {
    @BindView(R.id.iv_top)
    ImageView iv_top;
    @BindView(R.id.viewmid)
    View viewmid;
    @BindView(R.id.line_about)
    LinearLayout line_about;
    @BindView(R.id.line_permisson)
    LinearLayout line_permisson;

    public static MeFragment getIntance() {
        MeFragment fragment = new MeFragment();
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_me;
    }

    public void b() {
        Long oneAppCache = new QueryFileUtil().getOneAppCache(getActivity(), "com.tencent.mm", -1);
        Log.e("asd", "" + oneAppCache);

    }

    WxQqUtil f;
    public String c;

    @Override
    protected void initView() {
        line_about.setOnClickListener(v -> {
            Log.e("fdsa", "dsd");
            StatisticsUtils.trackClick("about_click", "\"关于\"点击", "mine_page", "personal_center_page");
            startActivity(AboutActivity.class);
        });

        ConstraintLayout.LayoutParams clp = (ConstraintLayout.LayoutParams) iv_top.getLayoutParams();
        clp.height = DeviceUtils.getScreenHeight() * 26 / 100;
        iv_top.setLayoutParams(clp);

        ConstraintLayout.LayoutParams clpt = (ConstraintLayout.LayoutParams) viewmid.getLayoutParams();
        clpt.topMargin = DeviceUtils.getScreenHeight() * 26 / 100 - DeviceUtils.dip2px(15);
        viewmid.setLayoutParams(clpt);
    }

    public long e = 0;

    public void changeHomeNum() {
        this.e = WxQqUtil.d.getTotalSize() + WxQqUtil.g.getTotalSize() + WxQqUtil.f.getTotalSize() + WxQqUtil.e.getTotalSize();
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                StatusBarCompat.setStatusBarColor(getActivity(), getResources().getColor(R.color.color_4690FD), true);
            } else {
                StatusBarCompat.setStatusBarColor(getActivity(), getResources().getColor(R.color.color_4690FD), false);
            }
        }
        if (hidden) {
            NiuDataAPI.onPageEnd("personal_center_view_page", "个人中心浏览");
        } else {
            NiuDataAPI.onPageStart("personal_center_view_page", "个人中心浏览");
        }
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            StatusBarCompat.setStatusBarColor(getActivity(), getResources().getColor(R.color.color_4690FD), true);
//        } else {
//            StatusBarCompat.setStatusBarColor(getActivity(), getResources().getColor(R.color.color_4690FD), false);
//        }
//        StatusBarCompat.translucentStatusBar(getActivity(), false, iv_top, true);
    }


    @OnClick({R.id.line_share,R.id.ll_setting, R.id.line_permisson, R.id.ll_question_report})
    public void onClickView(View view) {
        int ids = view.getId();
        if (ids == R.id.ll_setting) {
            StatisticsUtils.trackClick("set_up_click", "\"设置\"点击", "mine_page", "personal_center_page");
            startActivity(new Intent(getContext(), WhiteListSettingActivity.class));
        } else if (ids == R.id.ll_question_report) {
            StatisticsUtils.trackClick("question_feedback_click", "\"问题反馈\"点击", "mine_page", "personal_center_page");

            startActivity(new Intent(getContext(), QuestionReportActivity.class));
        } else if (ids == R.id.line_permisson) {
            StatisticsUtils.trackClick("privilege_management_click", "\"权限管理\"点击", "mine_page", "personal_center_page");
            startActivity(new Intent(getContext(), PermissionActivity.class));

            long ls = e + WxQqUtil.i.getTotalSize() + WxQqUtil.l.getTotalSize() + WxQqUtil.h.getTotalSize() + WxQqUtil.k.getTotalSize() + WxQqUtil.j.getTotalSize() + WxQqUtil.n.getTotalSize();
            Log.e("fdsa", "" + ls);
        }else if (ids == R.id.line_share) {
            String shareContent = "HI，我发现了一款清理手机垃圾神器！推荐给你，帮你清理垃圾，从此再也不怕手机空间不够用来！";
            share("", AppConstants.Base_H5_Host+"/share.html", "悟空清理", shareContent, -1);
            StatisticsUtils.trackClick("Sharing_friends_click", "分享好友", "mine_page", "about_page");

        }
    }


/*    @OnClick({R.id.ll_clean, R.id.ll_video,R.id.ll_qq,R.id.ll_qq_video})
    public void onClickS(View view) {
        int ids = view.getId();
        if (ids == R.id.ll_clean) {
            startActivity(new Intent(getContext(), WXCleanImgActivity.class));
        } else if (ids == R.id.ll_video) {
            startActivity(new Intent(getContext(), WXCleanVideoActivity.class));
        }else if(ids==R.id.ll_qq){
            startActivity(new Intent(getContext(), QQCleanImgActivity.class));
        }else if(ids==R.id.ll_qq_video){
            startActivity(new Intent(getContext(), QQCleanVideoActivity.class));
        }
    }*/

    public final static int SHARE_SUCCESS = 0;
    public final static int SHARE_CANCEL = 1;
    public final static int SHARE_WECHAT = 2;
    public final static int SHARE_QQ = 3;
    public final static int SHARE_SINA = 4;
    private SHARE_MEDIA[] platform = {SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.SINA};

    public void share(String picurl, String linkurl, String title, String content, int type) {
        //分享链接
        UMWeb web = new UMWeb(linkurl);
        //标题
        web.setTitle(title);
        if (TextUtils.isEmpty(picurl)) {
            //缩略图
            web.setThumb(new UMImage(getActivity(), R.mipmap.applogo));
        } else {
            web.setThumb(new UMImage(getActivity(), picurl));
        }
        //描述
        web.setDescription(content);
        ShareAction shareAction = new ShareAction(getActivity()).withMedia(web);
        shareAction.setCallback(new UMShareListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {

            }

            @Override
            public void onResult(SHARE_MEDIA share_media) {
                handler.sendEmptyMessage(SHARE_SUCCESS);
                String pageName = "";
                if (AppManager.getAppManager().preActivityName().contains("MainActivity")) {
                    pageName = "mine_page";
                }

                if (share_media == SHARE_MEDIA.WEIXIN) {
                    StatisticsUtils.trackClick("Wechat_friends_click", "微信好友", pageName, "Sharing_page");
                } else if (SHARE_MEDIA.WEIXIN_CIRCLE == share_media) {
                    StatisticsUtils.trackClick("Circle_of_friends_click", "朋友圈", pageName, "Sharing_page");
                } else if (share_media == SHARE_MEDIA.QZONE) {
                    StatisticsUtils.trackClick("qq_space_click", "QQ空间", pageName, "Sharing_page");
                } else if (SHARE_MEDIA.QQ == share_media) {
                    StatisticsUtils.trackClick("qq_friends_click", "QQ好友", pageName, "Sharing_page");
                }
            }

            @Override
            public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                System.out.println("----------------------------------" + throwable.getCause());
                if (share_media == SHARE_MEDIA.WEIXIN || share_media == SHARE_MEDIA.WEIXIN_CIRCLE) {
                    handler.sendEmptyMessage(SHARE_WECHAT);
                } else if (share_media == SHARE_MEDIA.QQ || share_media == SHARE_MEDIA.QZONE) {
                    handler.sendEmptyMessage(SHARE_QQ);
                } else if (share_media == SHARE_MEDIA.SINA) {
                    handler.sendEmptyMessage(SHARE_SINA);
                }
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media) {
                handler.sendEmptyMessage(SHARE_CANCEL);
            }
        });
        switch (type) {
            case -1:
                shareAction.setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE,SHARE_MEDIA.SINA);
                shareAction.open();
//                shareAction.setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE);
//                shareAction.open();
                break;
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
                shareAction.setPlatform(platform[type]);
                shareAction.share();
                break;
            default:
                shareAction.setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE,SHARE_MEDIA.SINA);
                shareAction.open();
        }
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHARE_SUCCESS:
                    ToastUtils.showShort("分享成功");
                    break;
                case SHARE_CANCEL:
                    ToastUtils.showShort("已取消");
                    break;
                case SHARE_WECHAT:
                    ToastUtils.showShort("没有安装微信，请先安装应用");
                    break;
                case SHARE_QQ:
                    ToastUtils.showShort("没有安装QQ，请先安装应用");
                    break;
                case SHARE_SINA:
                    ToastUtils.showShort("没有安装新浪微博，请先安装应用");
                    break;
                default:
                    break;
            }
        }
    };

}
