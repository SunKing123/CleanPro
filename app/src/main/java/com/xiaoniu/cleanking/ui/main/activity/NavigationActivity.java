//package com.xiaoniu.cleanking.ui.main.activity;
//
//import android.graphics.Color;
//import android.os.Bundle;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.FrameLayout;
//import android.widget.TextView;
//
//import androidx.viewpager.widget.PagerAdapter;
//import androidx.viewpager.widget.ViewPager;
//
//import com.xiaoniu.cleanking.AppConstants;
//import com.xiaoniu.cleanking.R;
//import com.xiaoniu.cleanking.constant.Constant;
//import com.xiaoniu.cleanking.base.SimpleActivity;
//import com.xiaoniu.cleanking.ui.main.widget.SPUtil;
//import com.xiaoniu.cleanking.ui.usercenter.activity.UserLoadH5Activity;
//import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;
//import com.xiaoniu.common.utils.StatisticsUtils;
//
//import java.util.ArrayList;
//
//import butterknife.BindView;
//
//
//public class NavigationActivity extends SimpleActivity {
//
//    @BindView(R.id.activity_navigation_viewpager)
//    ViewPager mViewPager;
//    @BindView(R.id.navigation_main)
//    FrameLayout mNavigationMain;
//
//    @Override
//    public int getLayoutId() {
//        return R.layout.activity_navigation_layout;
//    }
//
//    @Override
//    protected void onViewCreated() {
//        super.onViewCreated();
//        StatusBarCompat.translucentStatusBarForImage(this, true, true);
//    }
//
//    @Override
//    public void initView() {
//        ArrayList<View> views = new ArrayList<View>();
//        mViewPager.setBackgroundColor(0xff000000);
//
//        View vlast = getLayoutInflater().inflate(R.layout.activity_navigation_item_viewlast, null);
//        TextView cb_checkall = vlast.findViewById(R.id.cb_checkall);
//        TextView tv_delete = vlast.findViewById(R.id.tv_delete);
//        TextView tv_qx = vlast.findViewById(R.id.tv_qx);
//        tv_qx.setOnClickListener(v -> {
//            jumpXieyiActivity(AppConstants.Base_H5_Host + "/userAgreement.html");
//            StatisticsUtils.trackClick("Service_agreement_click", "服务协议", "mine_page", "about_page");
//        });
//        initLastClick(cb_checkall, tv_delete);
//        views.add(vlast);
//
//        NavigationAdapter navigationAdapter = new NavigationAdapter(views);
//        mViewPager.setAdapter(navigationAdapter);
//        mViewPager.setBackgroundColor(Color.TRANSPARENT);
//        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageSelected(int position) {
//            }
//
//            @Override
//            public void onPageScrolled(int arg0, float arg1, int arg2) {
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int scroll_state) {
//            }
//        });
//
//    }
//
//    private class NavigationAdapter extends PagerAdapter {
//        private ArrayList<View> views;
//
//        public NavigationAdapter(ArrayList<View> views) {
//            this.views = views;
//        }
//
//        @Override
//        public int getCount() {
//            return views == null ? 0 : views.size();
//        }
//
//        @Override
//        public Object instantiateItem(ViewGroup container, int position) {
//            container.addView(views.get(position), 0);
//            return views.get(position);
//        }
//
//        @Override
//        public void destroyItem(ViewGroup container, int position, Object object) {
//            container.removeView(views.get(position));
//        }
//
//        @Override
//        public boolean isViewFromObject(View view, Object object) {
//            return view == object;
//        }
//    }
//
//    public void initLastClick(TextView cb_checkall, TextView tv_delete) {
//        cb_checkall.setSelected(true);
//        cb_checkall.setOnClickListener(v -> {
//            cb_checkall.setSelected(!cb_checkall.isSelected());
//            cb_checkall.setBackgroundResource(cb_checkall.isSelected() ? R.drawable.icon_select : R.drawable.icon_unselect);
//            tv_delete.setBackgroundResource(cb_checkall.isSelected() ? R.drawable.delete_select_bg : R.drawable.delete_unselect_bg);
//
//        });
//        tv_delete.setOnClickListener(v -> {
//            if (cb_checkall.isSelected()) {
//                SPUtil.setFirstIn(NavigationActivity.this, "isfirst", false);
//                startActivity(MainActivity.class);
//                finish();
//            }
//        });
//    }
//
//    public void jumpXieyiActivity(String url) {
//        Bundle bundle = new Bundle();
//        bundle.putString(Constant.URL, url);
//        bundle.putString(Constant.Title, "服务协议");
//        bundle.putBoolean(Constant.NoTitle, false);
//        startActivity(UserLoadH5Activity.class, bundle);
//    }
//}
