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
import com.xiaoniu.cleanking.callback.OnCleanListSelectListener;
import com.xiaoniu.cleanking.ui.main.adapter.DockingExpandableListViewAdapter;
import com.xiaoniu.cleanking.ui.main.bean.CountEntity;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.bean.JunkGroup;
import com.xiaoniu.cleanking.ui.newclean.activity.NewCleanFinishActivity;
import com.xiaoniu.cleanking.ui.newclean.activity.NowCleanActivity;
import com.xiaoniu.cleanking.ui.newclean.presenter.CleanPresenter;
import com.xiaoniu.cleanking.ui.newclean.view.NewCleanAnimView;
import com.xiaoniu.cleanking.utils.CleanUtil;
import com.xiaoniu.cleanking.utils.NiuDataAPIUtil;
import com.xiaoniu.cleanking.utils.prefs.NoClearSPHelper;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.statistic.NiuDataAPI;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

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
    @BindView(R.id.do_junk_clean)
    TextView doJunkClean;
    TextView tvCheckedSize;
    View mHeadView;

    private DockingExpandableListViewAdapter mAdapter;
    private CountEntity totalCountEntity;
    private CountEntity checkCountEntity;
    private long checkedSize = 0;
    private long totalSize = 0;
    private HashMap<Integer, JunkGroup> mJunkGroups;

    @Inject
    NoClearSPHelper mSPHelper;

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
        tvCheckedSize = mHeadView.findViewById(R.id.tv_checked_size);
        mJunkGroups = ((NowCleanActivity) getActivity()).getJunkGroups();

        totalSize = checkedSize = CleanUtil.getTotalSize(mJunkGroups);
        checkCountEntity = totalCountEntity = CleanUtil.formatShortFileSize(totalSize);

        if (totalCountEntity != null) {
            tvSize.setText(totalCountEntity.getTotalSize());
            tvUnit.setText(totalCountEntity.getUnit());
            tvCheckedSize.setText(mContext.getString(R.string.select_already) + checkCountEntity.getTotalSize() + checkCountEntity.getUnit());
            doJunkClean.setText(mContext.getString(R.string.text_clean)+checkCountEntity.getTotalSize() + checkCountEntity.getUnit());
        }

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
        mAdapter.setOnCleanListSelectListener(new OnCleanListSelectListener() {
            @Override
            public void onGroupSelected(int groupPosition, boolean isChecked) {
                JunkGroup junkGroup = mJunkGroups.get(groupPosition);
                junkGroup.isChecked = isChecked;
                mJunkGroups.put(groupPosition,junkGroup);
                checkedSize = CleanUtil.getTotalSize(mJunkGroups);
                checkCountEntity = CleanUtil.formatShortFileSize(checkedSize);
                if (checkCountEntity != null) {
                    tvCheckedSize.setText(mContext.getString(R.string.select_already) + checkCountEntity.getTotalSize() + checkCountEntity.getUnit());
                    doJunkClean.setText(mContext.getString(R.string.text_clean)+checkCountEntity.getTotalSize() + checkCountEntity.getUnit());
                }
            }

            @Override
            public void onFistChilSelected(int groupPosition, int childPosition, boolean isChecked) {
                JunkGroup junkGroup = mJunkGroups.get(groupPosition);
                junkGroup.mChildren.get(childPosition).setAllchecked(isChecked);
                mJunkGroups.put(groupPosition,junkGroup);
                checkedSize = CleanUtil.getTotalSize(mJunkGroups);
                checkCountEntity = totalCountEntity = CleanUtil.formatShortFileSize(checkedSize);
                if (totalCountEntity != null) {
                    tvCheckedSize.setText(mContext.getString(R.string.select_already) + checkCountEntity.getTotalSize() + checkCountEntity.getUnit());
                    doJunkClean.setText(mContext.getString(R.string.text_clean)+checkCountEntity.getTotalSize() + checkCountEntity.getUnit());

                }

            }
        });
        mAdapter.setOnItemSelectListener(() -> {
            long totalSize = CleanUtil.getTotalSize(mJunkGroups);
            totalCountEntity = CleanUtil.formatShortFileSize(totalSize);
        });

        mExpandableListView.setAdapter(mAdapter);

        mAdapter.setData(mJunkGroups);
        for (int i = 0; i < mJunkGroups.size(); i++) {
            mExpandableListView.expandGroup(i);

        }
        mCleanAnimView.setAnimationEnd(() -> cleanFinish());

        mCleanAnimView.setOnColorChangeListener(animation -> showBarColor(animation));

        mCleanAnimView.setCleanOverListener(() -> {
            if (getActivity() != null) {
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
        StatisticsUtils.trackClick("cleaning_button_click", "用户在扫描结果页点击【清理】按钮", "clean_up_scan_page", "scanning_result_page");
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
        bundle.putString("num", totalCountEntity.getTotalSize());
        bundle.putString("unit", totalCountEntity.getUnit());
        startActivity(NewCleanFinishActivity.class, bundle);
        getActivity().finish();
    }

    private void startClean() {
        mCleanAnimView.setStopClean(false);
        mCleanAnimView.setVisibility(View.VISIBLE);
        mCleanAnimView.setData(totalCountEntity);
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
            double memoryShow = NoClearSPHelper.getMemoryShow();
            if (memoryShow == 1) {
                //清理完成，存储时间点
                mSPHelper.saveCleanTime(System.currentTimeMillis());
            }
        });

    }

/*    private long getTotalSize() {
        long size = 0L;
        for (JunkGroup group : mJunkGroups.values()) {
            for (FirstJunkInfo firstJunkInfo : group.mChildren) {
                if (firstJunkInfo.isAllchecked()) {
                    size += firstJunkInfo.getTotalSize();
                }
            }
        }
        return size;
    }*/

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
        NiuDataAPIUtil.onPageEnd("clean_up_scan_page", "scanning_result_page", "scanning_result_page_view_page", "用户在扫描结果页浏览");
    }
}
