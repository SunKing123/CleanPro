package com.xiaoniu.cleanking.ui.main.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.xiaoniu.cleanking.BuildConfig;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.Constant;
import com.xiaoniu.cleanking.base.SimpleActivity;
import com.xiaoniu.cleanking.ui.main.fragment.dialog.ConfirmDialogFragment;
import com.xiaoniu.cleanking.ui.main.fragment.dialog.MessageDialogFragment;
import com.xiaoniu.cleanking.ui.main.widget.SPUtil;
import com.xiaoniu.cleanking.ui.usercenter.activity.UserLoadH5Activity;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;
import com.xiaoniu.common.utils.StatisticsUtils;

import java.util.ArrayList;

import butterknife.BindView;


public class NavigationActivity extends SimpleActivity {

    @BindView(R.id.activity_navigation_viewpager)
    ViewPager mViewPager;
    @BindView(R.id.navigation_main)
    FrameLayout mNavigationMain;
    private ConfirmDialogFragment confirmDialogFragment;

    @Override
    public int getLayoutId() {
        return R.layout.activity_navigation_layout;
    }

    @Override
    protected void onViewCreated() {
        super.onViewCreated();
        StatusBarCompat.translucentStatusBarForImage(this, true, true);
    }

    @Override
    public void initView() {


        ArrayList<View> views = new ArrayList<View>();
        mViewPager.setBackgroundColor(0xff000000);

        View vlast = getLayoutInflater().inflate(R.layout.activity_navigation_item_viewlast, null);
        TextView cb_checkall = vlast.findViewById(R.id.cb_checkall);
        TextView tv_delete = vlast.findViewById(R.id.tv_delete);
        TextView tv_qx = vlast.findViewById(R.id.tv_qx);
        tv_qx.setOnClickListener(v -> {
            jumpXieyiActivity(BuildConfig.H5_BASE_URL + "/agree.html");
            StatisticsUtils.trackClick("Service_agreement_click", "服务协议", "mine_page", "about_page");
        });
        initLastClick(cb_checkall, tv_delete);
        views.add(vlast);

        NavigationAdapter navigationAdapter = new NavigationAdapter(views);
        mViewPager.setAdapter(navigationAdapter);
        mViewPager.setBackgroundColor(Color.TRANSPARENT);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int scroll_state) {
            }
        });

    }

    private void showConfirmDialog() {

        String html = "欢迎使用悟空清理！我们依据最新的法律要求，更新了隐私政策，" +
                "特此向您说明。作为互联网安全公司，" +
                "我们在为用户提供隐私保护的同时，对自身的安全产品提出了更高级别的标准。" +
                "在使用悟空清理前，请务必仔细阅读并了解<font color='#06C581'><a href=\"https://www.baidu.com\">《隐私政策》</a></font>和" +
                "<font color='#06C581'><a href=\"https://www.baidu.com\">《用户协议》</a></font>" +
                "全部条款，如您同意并接收全部条款，请点击同意开始使用我们的产品和服务。";


        confirmDialogFragment = ConfirmDialogFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putString("title", getString(R.string.reminder));
//        bundle.putString("content", getString(R.string.html));
        bundle.putString("content", html);
        confirmDialogFragment.setArguments(bundle);
        confirmDialogFragment.show(getFragmentManager(), "");

        confirmDialogFragment.setOnClickListener(new ConfirmDialogFragment.OnClickListener() {
            @Override
            public void onConfirm() {
                SPUtil.setBoolean(NavigationActivity.this, "consentAgreement", true);
                PreferenceUtil.saveFirstOpenApp();
                SPUtil.setFirstIn(NavigationActivity.this, "isfirst", false);
                startActivity(MainActivity.class);
                finish();
            }

            @Override
            public void onCancel() {
                confirmDialogFragment.dismiss();
                showMessageDialog();
            }
        });
    }

    private void showMessageDialog() {
        MessageDialogFragment messageDialogFragment = MessageDialogFragment.newInstance();
        messageDialogFragment.show(getFragmentManager(), "");
        messageDialogFragment.setOnClickListener(new MessageDialogFragment.OnClickListener() {
            @Override
            public void onConfirm() {
                confirmDialogFragment.show(getFragmentManager(), "");
            }

            @Override
            public void onCancel() {

            }
        });
    }


    private class NavigationAdapter extends PagerAdapter {
        private ArrayList<View> views;

        public NavigationAdapter(ArrayList<View> views) {
            this.views = views;
        }

        @Override
        public int getCount() {
            return views == null ? 0 : views.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(views.get(position), 0);
            return views.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(views.get(position));
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    public void initLastClick(TextView cb_checkall, TextView tv_delete) {
        cb_checkall.setSelected(true);
        cb_checkall.setOnClickListener(v -> {
            cb_checkall.setSelected(!cb_checkall.isSelected());
            cb_checkall.setBackgroundResource(cb_checkall.isSelected() ? R.drawable.icon_select : R.drawable.icon_unselect);
            tv_delete.setBackgroundResource(cb_checkall.isSelected() ? R.drawable.delete_select_bg : R.drawable.delete_unselect_bg);

        });
        tv_delete.setOnClickListener(v -> {
            if (cb_checkall.isSelected()) {
                boolean consentAgreement = SPUtil.getBoolean(this, "consentAgreement", false);
                if (!consentAgreement) {
                    showConfirmDialog();
                }
            }
        });
    }

    public void jumpXieyiActivity(String url) {
        Bundle bundle = new Bundle();
        bundle.putString(Constant.URL, url);
        bundle.putString(Constant.Title, "服务协议");
        bundle.putBoolean(Constant.NoTitle, false);
        startActivity(UserLoadH5Activity.class, bundle);
    }
}
