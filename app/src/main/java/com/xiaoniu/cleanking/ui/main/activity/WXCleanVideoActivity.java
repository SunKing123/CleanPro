package com.xiaoniu.cleanking.ui.main.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.Constant;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.adapter.CommonFragmentPageAdapter;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.event.EmptyEvent;
import com.xiaoniu.cleanking.ui.main.fragment.WXVideoCameraFragment;
import com.xiaoniu.cleanking.ui.main.fragment.WXVideoChatFragment;
import com.xiaoniu.cleanking.ui.main.fragment.WXVideoSaveListFragment;
import com.xiaoniu.cleanking.utils.StatisticsUtils;
import com.xiaoniu.statistic.NiuDataAPI;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 微信视频清理
 * Created by lang.chen on 2019/8/1
 * <p>
 * <p>
 * 1.扫描微信目录
 * <p>
 * <p>
 * 2.图片分类
 * <p>
 * a)聊天图片
 * -| 缩略图
 * -|  日期 今日，昨天,一月内,半年内
 * b)手机拍摄的图片
 * c)保存下载的图片
 * <p>
 * <p>
 * 3.数据组装和渲染
 */
public class WXCleanVideoActivity extends BaseActivity{

    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    @BindView(R.id.txt_img_chat)
    TextView mTxtImgChat;
    @BindView(R.id.view_line_chat)
    View mViewLineChat;
    @BindView(R.id.txt_img_camera)
    TextView mTxtImgCamera;
    @BindView(R.id.view_line_img_camera)
    View mViewLineImgCamera;
    @BindView(R.id.txt_img_download)
    TextView mTxtImgDownload;
    @BindView(R.id.view_line_img_download)
    View mViewLineImgDownload;
    @BindView(R.id.ll_img_save_list)
    LinearLayout llImgSaveList;


    //
    private List<Fragment> mFragments = new ArrayList<>();
    private CommonFragmentPageAdapter mAdapter;


    @Override
    public void inject(ActivityComponent activityComponent) {
    }


    @Override
    public void netError() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.wx_clean_video_main;
    }

    @Override
    protected void initView() {
        SharedPreferences sharedPreferences =getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putLong(Constant.WX_CACHE_SIZE_VIDEO,0);

        mFragments.add(WXVideoChatFragment.newInstance());
        mFragments.add(WXVideoCameraFragment.newInstance());
        mFragments.add(WXVideoSaveListFragment.newInstance());
        mAdapter = new CommonFragmentPageAdapter(getSupportFragmentManager());
        mAdapter.modifyList(mFragments);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(3);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                setSelcteStatusView(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setSelcteStatusView(0);


    }


    private void setSelcteStatusView(int position) {
        switch (position) {

            case 0:

                mTxtImgChat.setTextColor(getResources().getColor(R.color.white));
                mTxtImgCamera.setTextColor(getResources().getColor(R.color.color_b2ffffff));
                mTxtImgDownload.setTextColor(getResources().getColor(R.color.color_b2ffffff));
                mViewLineChat.setVisibility(View.VISIBLE);
                mViewLineImgCamera.setVisibility(View.INVISIBLE);
                mViewLineImgDownload.setVisibility(View.INVISIBLE);
                break;
            case 1:
                mTxtImgChat.setTextColor(getResources().getColor(R.color.color_b2ffffff));
                mTxtImgCamera.setTextColor(getResources().getColor(R.color.white));
                mTxtImgDownload.setTextColor(getResources().getColor(R.color.color_b2ffffff));
                mViewLineChat.setVisibility(View.INVISIBLE);
                mViewLineImgCamera.setVisibility(View.VISIBLE);
                mViewLineImgDownload.setVisibility(View.INVISIBLE);
                break;

            case 2:
                mTxtImgChat.setTextColor(getResources().getColor(R.color.color_b2ffffff));
                mTxtImgCamera.setTextColor(getResources().getColor(R.color.color_b2ffffff));
                mTxtImgDownload.setTextColor(getResources().getColor(R.color.white));
                mViewLineChat.setVisibility(View.INVISIBLE);
                mViewLineImgCamera.setVisibility(View.INVISIBLE);
                mViewLineImgDownload.setVisibility(View.VISIBLE);

                break;
        }
    }


    @OnClick({R.id.img_back,
            R.id.ll_img_chat,
            R.id.ll_img_camera, R.id.ll_img_save_list})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                StatisticsUtils.trackClick("wechat_video_return_click","\"聊天视频返回\"点击"
                        ,"wechat_cleaning_page","wechat_video_cleaning_page");
                finish();
                break;
            case R.id.ll_img_chat:
                mViewPager.setCurrentItem(0);
                setSelcteStatusView(0);
                break;
            case R.id.ll_img_camera:
                mViewPager.setCurrentItem(1);
                setSelcteStatusView(1);
                break;
            case R.id.ll_img_save_list:
                mViewPager.setCurrentItem(2);
                setSelcteStatusView(2);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        StatisticsUtils.trackClick("wechat_video_return_click","\"聊天视频返回\"点击"
                ,"wechat_cleaning_page","wechat_video_cleaning_page");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void emptyEvent(EmptyEvent emptyEvent) {

    }


    @Override
    protected void onResume() {
        super.onResume();
        NiuDataAPI.onPageStart("wechat_video_cleaning_view_page","视频清理页面浏览");
    }

    @Override
    protected void onPause() {
        super.onPause();
        NiuDataAPI.onPageEnd("wechat_video_cleaning_view_page","视频清理页面浏览");
    }
}
