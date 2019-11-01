package com.xiaoniu.cleanking.ui.main.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.adapter.GameListAdapter;
import com.xiaoniu.cleanking.ui.main.bean.AnimationItem;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.presenter.GameListPresenter;
import com.xiaoniu.cleanking.ui.tool.notify.event.SelectGameEvent;
import com.xiaoniu.cleanking.utils.ExtraConstant;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;

/**
 * @author XiLei
 * @date 2019/10/18.
 * description：游戏加速应用列表
 */
public class GameListActivity extends BaseActivity<GameListPresenter> {

    @BindView(R.id.recycle_view)
    RecyclerView recycle_view;
    @BindView(R.id.iv_back)
    ImageView iv_back;
    @BindView(R.id.viewt)
    View viewt;
    @BindView(R.id.line_title)
    View line_title;

    private int mNotSelectCount;
    private ArrayList<String> mSelectNameList;
    private List<FirstJunkInfo> mAllList; //所有应用列表
    private ArrayList<FirstJunkInfo> mSelectList; //选择的应用列表
    private GameListAdapter mGameListAdapter;
    private ArrayList<FirstJunkInfo> mListInfoData = new ArrayList<>();

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
        mSelectNameList = new ArrayList<>();
        mAllList = new ArrayList<>();
        mSelectList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= 26) {
            mPresenter.getAccessAbove22();
        } else {
            mPresenter.getAccessListBelow();
        }
        iv_back.setOnClickListener(v -> {
            EventBus.getDefault().post(new SelectGameEvent(mAllList, mSelectList, (mNotSelectCount == mListInfoData.size()) ? true : false));
            GameListActivity.this.finish();
        });
        if (null != getIntent() && null != getIntent().getSerializableExtra(ExtraConstant.SELECT_GAME_LIST)) {
            mSelectNameList = (ArrayList<String>) getIntent().getSerializableExtra(ExtraConstant.SELECT_GAME_LIST);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Log.d("XiLei", "mNotSelectCount=" + mNotSelectCount);
            Log.d("XiLei", "mListInfoData.size()=" + mListInfoData.size());
            EventBus.getDefault().post(new SelectGameEvent(mAllList, mSelectList, (mNotSelectCount == mListInfoData.size()) ? true : false));
            GameListActivity.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //低于Android O
    public void getAccessListBelow(ArrayList<FirstJunkInfo> listInfo) {
        if (listInfo == null) return;

        //悟空清理app加入默认白名单
        for (FirstJunkInfo firstJunkInfo : listInfo) {
            if (SpCacheConfig.APP_ID.equals(firstJunkInfo.getAppPackageName())) {
                listInfo.remove(firstJunkInfo);
            }
        }
        if (listInfo.size() != 0) {
            setAdapter(listInfo);
        }
    }

    //Android O以上的
    PackageManager packageManager = AppApplication.getInstance().getPackageManager();

    public void getAccessListAbove22(List<ActivityManager.RunningAppProcessInfo> listInfo) {
        if (listInfo.size() == 0) {

        } else {
            ArrayList<FirstJunkInfo> aboveListInfo = new ArrayList<>();
            if (listInfo.size() < 15) {
                for (ActivityManager.RunningAppProcessInfo info : listInfo) {
                    //悟空清理app加入默认白名单
                    if (!SpCacheConfig.APP_ID.equals(info.processName)) {
                        FirstJunkInfo mInfo = new FirstJunkInfo();
                        mInfo.setAppPackageName(info.processName);
                        mInfo.setAppName(info.processName);
                        aboveListInfo.add(mInfo);
                    }
                }
            } else {
                for (int i = 0; i < 15; i++) {
                    //悟空清理app加入默认白名单
                    if (!SpCacheConfig.APP_ID.equals(listInfo.get(i).processName)) {
                        FirstJunkInfo mInfo = new FirstJunkInfo();
                        mInfo.setAppPackageName(listInfo.get(i).processName);
                        mInfo.setAppName(listInfo.get(i).processName);
                        aboveListInfo.add(mInfo);
                    }
                }
            }
            setAdapter(aboveListInfo);
        }
    }

    /**
     * 获取缓存白名单
     */
    public boolean isCacheWhite(String packageName) {
        SharedPreferences sp = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_NAME_WHITE_LIST_INSTALL_PACKE, Context.MODE_PRIVATE);
        Set<String> sets = sp.getStringSet(SpCacheConfig.WHITE_LIST_KEY_INSTALL_PACKE_NAME, new HashSet<>());
        return sets.contains(packageName);
    }

    public void setAdapter(ArrayList<FirstJunkInfo> listInfos) {
        if (null == recycle_view)
            return;
        mAllList = listInfos;
        for (FirstJunkInfo firstJunkInfo : listInfos) {
            if (!isCacheWhite(firstJunkInfo.getAppPackageName()))
                mListInfoData.add(firstJunkInfo);
        }
        mGameListAdapter = new GameListAdapter(GameListActivity.this, mListInfoData, mSelectNameList);
        recycle_view.setLayoutManager(new LinearLayoutManager(GameListActivity.this));
        recycle_view.setAdapter(mGameListAdapter);
        mGameListAdapter.setmOnCheckListener((listFile, pos) -> {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void netError() {

    }


}

