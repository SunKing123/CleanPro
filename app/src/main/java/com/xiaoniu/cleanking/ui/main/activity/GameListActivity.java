package com.xiaoniu.cleanking.ui.main.activity;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaredrummler.android.processes.models.AndroidAppProcess;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.scheme.SchemeProxy;
import com.xiaoniu.cleanking.ui.main.adapter.GameListAdapter;
import com.xiaoniu.cleanking.ui.main.bean.AnimationItem;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.bean.HomeRecommendEntity;
import com.xiaoniu.cleanking.ui.main.bean.HomeRecommendListEntity;
import com.xiaoniu.cleanking.ui.main.presenter.GameListPresenter;
import com.xiaoniu.cleanking.ui.tool.notify.event.SelectGameEvent;
import com.xiaoniu.cleanking.utils.ExtraConstant;
import com.xiaoniu.cleanking.utils.GlideUtils;
import com.xiaoniu.cleanking.utils.NiuDataAPIUtil;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.statistic.NiuDataAPI;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static android.view.View.VISIBLE;
import static com.jaredrummler.android.processes.AndroidProcesses.getRunningAppProcesses;

/**
 * @author XiLei
 * @date 2019/10/18.
 * description：游戏加速应用列表
 */
public class GameListActivity extends BaseActivity<GameListPresenter> implements View.OnClickListener {

    @BindView(R.id.recycle_view)
    RecyclerView recycle_view;
    @BindView(R.id.iv_back)
    ImageView iv_back;
    @BindView(R.id.viewt)
    View viewt;
    @BindView(R.id.line_title)
    View line_title;
    @BindView(R.id.v_banner)
    View mBannerView;
    @BindView(R.id.iv_icon)
    ImageView mIconIv;
    @BindView(R.id.tv_name)
    TextView mNameTv;
    @BindView(R.id.tv_content)
    TextView mContentTv;
    @BindView(R.id.tv_button)
    TextView mBtnTv;

    private int mNotSelectCount;
    private ArrayList<String> mSelectNameList;
    private List<FirstJunkInfo> mAllList; //所有应用列表
    private ArrayList<FirstJunkInfo> mSelectList; //选择的应用列表
    private GameListAdapter mGameListAdapter;
    private List<HomeRecommendListEntity> mBannerList;
    private final String CURRENT_PAGE = "gameboost_add_list_page";

    @Override
    public int getLayoutId() {
        return R.layout.activity_game_list;
    }

    @Override
    public void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    public void initView() {
        NiuDataAPI.onPageStart("gameboost_add_list_page_view_page", "游戏加速添加列表页浏览");
        NiuDataAPIUtil.onPageEnd("gameboost_add_page", CURRENT_PAGE, "gameboost_add_list_page_view_page", "游戏加速添加列表页浏览");
        mSelectNameList = new ArrayList<>();
        mAllList = new ArrayList<>();
        mSelectList = new ArrayList<>();
        mPresenter.getRecommendList();
        if (null != getIntent() && null != getIntent().getSerializableExtra(ExtraConstant.SELECT_GAME_LIST)) {
            mSelectNameList = (ArrayList<String>) getIntent().getSerializableExtra(ExtraConstant.SELECT_GAME_LIST);
        }
        iv_back.setOnClickListener(this);
        mBannerView.setOnClickListener(this);
        allApp();
    }

    /**
     * 获取手机安装的所有应用列表
     */
    private void allApp() {
       /* PackageManager pm = AppApplication.getInstance().getPackageManager();
        Intent launcherIntent = new Intent(Intent.ACTION_MAIN, null);
        launcherIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> list = pm.queryIntentActivities(launcherIntent, 0);
        if (null == list || list.isEmpty()) return;
        ArrayList<FirstJunkInfo> aboveListInfo = new ArrayList<>();
        for (ResolveInfo mResolveInfo : list) {
            Drawable icon = mResolveInfo.loadIcon(pm);
            String name = mResolveInfo.loadLabel(pm).toString();
            FirstJunkInfo mInfo = new FirstJunkInfo();
            mInfo.setGarbageIcon(icon);
            mInfo.setAppName(name);
            aboveListInfo.add(mInfo);
        }*/

        ArrayList<FirstJunkInfo> aboveListInfo = new ArrayList<>();
        List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            //判断是否系统应用
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) { //非系统应用
                FirstJunkInfo tmpInfo = new FirstJunkInfo();
                tmpInfo.setAppName(packageInfo.applicationInfo.loadLabel(getPackageManager()).toString());
                tmpInfo.setGarbageIcon(packageInfo.applicationInfo.loadIcon(getPackageManager()));
                tmpInfo.setAppPackageName(packageInfo.applicationInfo.packageName);
                tmpInfo.setAppProcessName(packageInfo.applicationInfo.processName);
                aboveListInfo.add(tmpInfo);
            }
        }
        setAdapter(aboveListInfo);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                StatisticsUtils.trackClick("return_click", "游戏加速添加列表页返回", "gameboost_add_page", CURRENT_PAGE);
                EventBus.getDefault().post(new SelectGameEvent(mAllList, mSelectList, (mNotSelectCount == mAllList.size()) ? true : false));
                GameListActivity.this.finish();
                break;
            case R.id.v_banner:
                if (null == mBannerList || mBannerList.size() <= 0) return;
                if (mBannerList.get(0).getLinkType().equals("1")) {
                    SchemeProxy.openScheme(this, mBannerList.get(0).getLinkUrl());
                } else if (mBannerList.get(0).getLinkType().equals("2")) {
                    startActivity(new Intent(this, AgentWebViewActivity.class).putExtra(ExtraConstant.WEB_URL, mBannerList.get(0).getLinkUrl()));
                } else if (mBannerList.get(0).getLinkType().equals("3")) {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse(mBannerList.get(0).getLinkUrl());
                    intent.setData(content_url);
                    startActivity(intent);
                }
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            StatisticsUtils.trackClick("system_return_click", "游戏加速添加列表页返回", "gameboost_add_page", CURRENT_PAGE);
            EventBus.getDefault().post(new SelectGameEvent(mAllList, mSelectList, (mNotSelectCount == mAllList.size()) ? true : false));
            GameListActivity.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void setAdapter(ArrayList<FirstJunkInfo> listInfos) {
        if (null == recycle_view)
            return;
        mAllList = listInfos;
        mGameListAdapter = new GameListAdapter(GameListActivity.this, listInfos, mSelectNameList);
        recycle_view.setLayoutManager(new LinearLayoutManager(GameListActivity.this));
        recycle_view.setAdapter(mGameListAdapter);
        mGameListAdapter.setmOnCheckListener((listFile, pos) -> {
            StatisticsUtils.trackClick("gameboost_choice_click", "游戏加速添加列表页选择框点击", "gameboost_add_page", CURRENT_PAGE);
            mSelectList.clear();
            mNotSelectCount = 0;
            for (int i = 0; i < listFile.size(); i++) {
                if (listFile.get(i).isSelect()) {
                    mSelectList.add(listFile.get(i));
                } else {
                    mNotSelectCount++;
                }
            }
        });

        AnimationItem animationItem = new AnimationItem("Slide from bottom", R.anim.layout_animation_from_bottom);
        mPresenter.runLayoutAnimation(recycle_view, animationItem);
    }

    /**
     * 获取推荐列表成功
     *
     * @param entity
     */
    public void getRecommendListSuccess(HomeRecommendEntity entity) {
        if (null == entity || null == entity.getData() || entity.getData().size() <= 0)
            return;
        mBannerList = entity.getData();
        mBannerView.setVisibility(VISIBLE);
        GlideUtils.loadImage(this, entity.getData().get(0).getIconUrl(), mIconIv);
        mNameTv.setText(entity.getData().get(0).getName());
        mContentTv.setText(entity.getData().get(0).getContent());
        mBtnTv.setText(entity.getData().get(0).getButtonName());
    }

    @Override
    public void netError() {

    }

}

