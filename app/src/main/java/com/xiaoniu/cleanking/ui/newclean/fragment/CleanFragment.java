package com.xiaoniu.cleanking.ui.newclean.fragment;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.Constant;
import com.xiaoniu.cleanking.app.injector.component.FragmentComponent;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.BaseFragment;
import com.xiaoniu.cleanking.ui.main.adapter.DockingExpandableListViewAdapter;
import com.xiaoniu.cleanking.ui.main.bean.CountEntity;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.bean.JunkGroup;
import com.xiaoniu.cleanking.ui.newclean.activity.NewCleanFinishActivity;
import com.xiaoniu.cleanking.ui.newclean.activity.NowCleanActivity;
import com.xiaoniu.cleanking.ui.newclean.presenter.CleanPresenter;
import com.xiaoniu.cleanking.ui.newclean.view.NewCleanAnimView;
import com.xiaoniu.cleanking.utils.CleanUtil;
import com.xiaoniu.cleanking.utils.FileQueryUtils;
import com.xiaoniu.cleanking.utils.NiuDataAPIUtil;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.statistic.NiuDataAPI;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 清理操作页面
 */
public class CleanFragment extends BaseFragment<CleanPresenter> {

    @BindView(R.id.junk_list)
    ExpandableListView mExpandableListView;
    @BindView(R.id.view_clean_anim)
    NewCleanAnimView mCleanAnimView;
    View mHeadView;

    private DockingExpandableListViewAdapter mAdapter;
    private CountEntity mCountEntity;
    private HashMap<Integer, JunkGroup> mJunkGroups;

    public static CleanFragment newInstance() {
        return new CleanFragment();
    }

    @Override
    protected void inject(FragmentComponent fragmentComponent) {
        fragmentComponent.inject(this);
    }

    @Override
    public void netError() {
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_clean;
    }

    @Override
    protected void initView() {
        ((NowCleanActivity) getActivity()).getToolBar().setBackgroundColor(getResources().getColor(R.color.color_FD6F46));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarCompat.setStatusBarColor(getActivity(), getResources().getColor(R.color.color_FD6F46), true);
        } else {
            StatusBarCompat.setStatusBarColor(getActivity(), getResources().getColor(R.color.color_FD6F46), false);
        }
        mHeadView = getLayoutInflater().inflate(R.layout.layout_head_now_clean, null);

        TextView tvSize = mHeadView.findViewById(R.id.tv_size);
        TextView tvUnit = mHeadView.findViewById(R.id.tv_clear_finish_gb_title);

        mCountEntity = ((NowCleanActivity) getActivity()).getCountEntity();
        if (mCountEntity != null){
            tvSize.setText(mCountEntity.getTotalSize());
            tvUnit.setText(mCountEntity.getUnit());
        }
        mJunkGroups = ((NowCleanActivity) getActivity()).getJunkGroups();

        mExpandableListView.setGroupIndicator(null);
        mExpandableListView.setChildIndicator(null);
        mExpandableListView.setDividerHeight(0);

        mExpandableListView.addHeaderView(mHeadView);

        mExpandableListView.setOnGroupClickListener((parent, v, groupPosition, id) -> {
            JunkGroup junkGroup = mJunkGroups.get(groupPosition);
            if (junkGroup != null) {
                junkGroup.isExpand = !junkGroup.isExpand();
                mAdapter.notifyDataSetChanged();
            }
            return false;
        });

        mAdapter = new DockingExpandableListViewAdapter(getActivity(), mExpandableListView);

        mAdapter.setOnItemSelectListener(() -> {
            long totalSize = getTotalSize();
            mCountEntity = CleanUtil.formatShortFileSize(totalSize);
        });

        mExpandableListView.setAdapter(mAdapter);

        mAdapter.setData(mJunkGroups);
        for (int i = 0; i < mJunkGroups.size(); i++) {
            mExpandableListView.expandGroup(i);

        }
        mCleanAnimView.setAnimationEnd(() -> cleanFinish());

        mCleanAnimView.setOnColorChangeListener(animation -> showBarColor(animation));

        mCleanAnimView.setCleanOverListener(() -> {
            if (getActivity() !=null){
                ((NowCleanActivity) getActivity()).setClean(false);
            }
        });
    }

    /**
     * 状态栏颜色变化
     *
     * @param animatedValue
     */
    public void showBarColor(int animatedValue) {
        if (((NowCleanActivity) getActivity()).getToolBar() == null)
            return;
        ((NowCleanActivity) getActivity()).getToolBar().setBackgroundColor(animatedValue);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarCompat.setStatusBarColor(getActivity(), animatedValue, true);
        } else {
            StatusBarCompat.setStatusBarColor(getActivity(), animatedValue, false);
        }
    }

    @OnClick(R.id.layout_junk_clean)
    public void starClean() {
        //扫描中弹框_确认按钮
        StatisticsUtils.trackClick( "cleaning_button_click", "用户在扫描结果页点击【清理】按钮", "clean_up_scan_page", "scanning_result_page");
        startClean();
    }

    /**
     * 清理完成
     */
    public void cleanFinish() {
        AppHolder.getInstance().setCleanFinishSourcePageId("scanning_result_page");
        if (PreferenceUtil.getNowCleanTime() || TextUtils.isEmpty(Constant.APP_IS_LIVE)) {
            PreferenceUtil.saveNowCleanTime();
        }
        Bundle bundle = new Bundle();
        bundle.putString("title", getString(R.string.tool_suggest_clean));
        bundle.putString("num", mCountEntity.getTotalSize());
        bundle.putString("unit", mCountEntity.getUnit());
        startActivity(NewCleanFinishActivity.class, bundle);
        getActivity().finish();
    }

    private void startClean() {
        mCleanAnimView.setStopClean(false);
        mCleanAnimView.setVisibility(View.VISIBLE);
        mCleanAnimView.setData(mCountEntity);
        //清理动画
        mCleanAnimView.startCleanAnim(false);
        clearAll();
    }

    @SuppressLint("CheckResult")
    private void clearAll() {
        if (mJunkGroups == null || mJunkGroups.size() < 1) {
            cleanFinish();
            return;
        }
        Observable.create(e -> {
            long total = 0;
            for (Map.Entry<Integer, JunkGroup> entry : mJunkGroups.entrySet()) {
                JunkGroup value = entry.getValue();
                if (value.mChildren != null && value.mChildren.size() > 0) {
                    if ("TYPE_PROCESS".equals(value.mChildren.get(0).getGarbageType())) {
                        for (FirstJunkInfo info : value.mChildren) {
                            if (info.isAllchecked()) {
                                total += info.getTotalSize();
                                CleanUtil.killAppProcesses(info.getAppPackageName(), info.getPid());
                            }
                        }
                    } else if ("TYPE_CACHE".equals(value.mChildren.get(0).getGarbageType())) {
                        long l = CleanUtil.freeJunkInfos(value.mChildren);
                        total += l;
                    } else if ("TYPE_APK".equals(value.mChildren.get(0).getGarbageType())) {
                        long l1 = CleanUtil.freeJunkInfos(value.mChildren);

                        total += l1;
                    }
                }
            }

            e.onNext(total);
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(o -> {
        });

    }

    private long getTotalSize() {
        long size = 0L;
        for (JunkGroup group : mJunkGroups.values()) {
            for (FirstJunkInfo firstJunkInfo : group.mChildren) {
                if (firstJunkInfo.isAllchecked()) {
                    size += firstJunkInfo.getTotalSize();
                }
            }
        }
        return size;
    }

    /**
     * 停止清理
     */
    public void stopClean() {
        mCleanAnimView.stopClean();
    }


    @Override
    public void onResume() {
        super.onResume();
        NiuDataAPI.onPageStart("scanning_result_page_view_page", "用户在扫描结果页浏览");

    }

    @Override
    public void onPause() {
        super.onPause();
        NiuDataAPIUtil.onPageEnd("clean_up_scan_page","scanning_result_page","scanning_result_page_view_page", "用户在扫描结果页浏览");
    }
}
