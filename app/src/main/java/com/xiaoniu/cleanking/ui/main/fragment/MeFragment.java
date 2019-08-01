package com.xiaoniu.cleanking.ui.main.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.SimpleFragment;
import com.xiaoniu.cleanking.ui.main.activity.QuestionReportActivity;
import com.xiaoniu.cleanking.ui.main.activity.WhiteListSettingActivity;
import com.xiaoniu.cleanking.ui.tool.wechat.bean.Constants;
import com.xiaoniu.cleanking.ui.tool.wechat.util.PrefsCleanUtil;
import com.xiaoniu.cleanking.ui.tool.wechat.util.QueryFileUtil;
import com.xiaoniu.cleanking.ui.tool.wechat.util.WxQqUtil;
import com.xiaoniu.cleanking.ui.usercenter.activity.PermissionActivity;
import com.xiaoniu.cleanking.utils.CleanAllFileScanUtil;
import com.xiaoniu.cleanking.utils.DeviceUtils;
import com.xiaoniu.cleanking.utils.StatisticsUtils;
import com.xiaoniu.cleanking.utils.ThreadTaskUtil;
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
//    @BindView(R.id.line_feedback)
//    LinearLayout line_feedback;

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
        line_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("fdsa", "dsd");
                PrefsCleanUtil.getInstance().init(getContext(), "xnpre", Context.MODE_APPEND);
                getActivity().getWindow().getDecorView().post(new Runnable() {
                    public void run() {
                        ThreadTaskUtil.executeNormalTask("准备扫描微信", new Runnable() {
                            public void run() {
                                f = new WxQqUtil();
                                f.startScanWxGarbage(c, new WxQqUtil.a() {
                                    @Override
                                    public void changeHomeNum() {
                                        e = WxQqUtil.d.getTotalSize() + WxQqUtil.g.getTotalSize() + WxQqUtil.f.getTotalSize() + WxQqUtil.e.getTotalSize();
                                    }

                                    @Override
                                    public void wxEasyScanFinish() {
                                        long ls = e + WxQqUtil.m.getTotalSize() + WxQqUtil.i.getTotalSize() + WxQqUtil.l.getTotalSize() + WxQqUtil.h.getTotalSize() + WxQqUtil.k.getTotalSize() + WxQqUtil.j.getTotalSize() + WxQqUtil.n.getTotalSize();
                                        Log.e("fdsa", "" + ls);
                                        long st = 1024 * 1024;
                                        Log.e("fdsa", "垃圾文件不含聊天记录建议清理" + CleanAllFileScanUtil.byte2FitSize(WxQqUtil.d.getTotalSize()));
                                        Log.e("fdsa", "朋友圈缓存" + CleanAllFileScanUtil.byte2FitSize(WxQqUtil.g.getTotalSize()));
                                        Log.e("fdsa", "其他缓存浏览公众号小程序产生" + CleanAllFileScanUtil.byte2FitSize(WxQqUtil.f.getTotalSize()));
                                        Log.e("fdsa", "缓存表情浏览聊天记录产生的表情" + CleanAllFileScanUtil.byte2FitSize(WxQqUtil.e.getTotalSize()));
                                        Log.e("fdsa", "总缓存大小" + CleanAllFileScanUtil.byte2FitSize(e));
                                    }
                                });
                                PrefsCleanUtil.getInstance().putLong(Constants.CLEAN_WX_TOTAL_SIZE, WxQqUtil.i.getTotalSize() + WxQqUtil.l.getTotalSize() + WxQqUtil.h.getTotalSize() + WxQqUtil.k.getTotalSize() + WxQqUtil.j.getTotalSize() + WxQqUtil.n.getTotalSize());
                            }
                        });
                        ThreadTaskUtil.executeNormalTask("-CleanWxClearNewActivity-run-184--", new Runnable() {
                            public void run() {
                                SystemClock.sleep(200);
                                b();
                            }
                        });

                    }
                });


//                StatisticsUtils.trackClick("about_click", "\"关于\"点击", "mine_page", "personal_center_page");
//                startActivity(AboutActivity.class);
            }
        });

        ConstraintLayout.LayoutParams clp = (ConstraintLayout.LayoutParams) iv_top.getLayoutParams();
        clp.height = DeviceUtils.getScreenHeight() * 26 / 100;
        iv_top.setLayoutParams(clp);

        ConstraintLayout.LayoutParams clpt = (ConstraintLayout.LayoutParams) viewmid.getLayoutParams();
        clpt.topMargin = DeviceUtils.getScreenHeight() * 26 / 100 - DeviceUtils.dip2px(15);
        viewmid.setLayoutParams(clpt);

//        line_feedback.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(FeedBackActivity.class);
//            }
//        });
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


    @OnClick({R.id.ll_setting, R.id.line_permisson, R.id.ll_question_report})
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
        }
    }

}
