package com.xiaoniu.cleanking.ui.main.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.app.RouteConstants;
import com.xiaoniu.cleanking.base.SimpleActivity;
import com.xiaoniu.cleanking.ui.main.adapter.DockingExpandableListViewAdapter;
import com.xiaoniu.cleanking.ui.main.bean.CountEntity;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.bean.JunkGroup;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.fragment.CleanMainFragment;
import com.xiaoniu.cleanking.ui.main.widget.CleanAnimView;
import com.xiaoniu.cleanking.utils.CleanUtil;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Administrator
 */
@Route(path = RouteConstants.JUNK_CLEAN_ACTIVITY)
public class JunkCleanActivity extends SimpleActivity {

    private DockingExpandableListViewAdapter mAdapter;

    @BindView(R.id.tv_title)
    TextView mTextTitle;
    @BindView(R.id.icon_more)
    ImageView mIconMore;
    @BindView(R.id.do_junk_clean)
    TextView mTextClean;

    @BindView(R.id.layout_show_list)
    RelativeLayout mLayoutList;
    @BindView(R.id.layout_clean_finish)
    RelativeLayout mLayoutFinish;
    @BindView(R.id.text_clean_number)
    TextView mTextCleanNumber;
    @BindView(R.id.view_clean_anim)
    CleanAnimView mCleanAnimView;

    private CountEntity countEntity;

    private HashMap<Integer, JunkGroup> mJunkGroups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color_4690FD), true);
        } else {
            StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color_4690FD), false);
        }
        mTextTitle.setText("垃圾详情");

        mJunkGroups = CleanMainFragment.mJunkGroups;

        //立即清理点击
        mTextClean.setOnClickListener(v -> {
            mCleanAnimView.setData(countEntity);
            mCleanAnimView.setVisibility(View.VISIBLE);
            //清理动画
            mCleanAnimView.startTopAnim(false);
            //title bar
            showBarColor(getResources().getColor(R.color.color_FD6F46));
            clearAll();
        });

        ExpandableListView listView = findViewById(R.id.junk_list);
        listView.setGroupIndicator(null);
        listView.setChildIndicator(null);
        listView.setDividerHeight(0);

        listView.setOnGroupClickListener((parent, v, groupPosition, id) -> {
            JunkGroup junkGroup = mJunkGroups.get(groupPosition);
            if (junkGroup != null) {
                junkGroup.isExpand = !junkGroup.isExpand();
                mAdapter.notifyDataSetChanged();
            }
            return false;
        });

        mAdapter = new DockingExpandableListViewAdapter(this, listView);
        mAdapter.setOnItemSelectListener(() -> {
            long totalSize = getTotalSize();
            if (totalSize <= 0) {
                mTextClean.setEnabled(false);
                mTextClean.setText("清理");
            } else {
                mTextClean.setEnabled(true);
                countEntity = CleanUtil.formatShortFileSize(totalSize);
                mTextClean.setText("清理" + countEntity.getResultSize());
            }
        });

        listView.setAdapter(mAdapter);

        mAdapter.setData(mJunkGroups);

        for (int i = 0; i < mJunkGroups.size(); i++) {
            listView.expandGroup(i);
        }
        countEntity = CleanUtil.formatShortFileSize(getTotalSize());
        mTextClean.setText("清理" + countEntity.getResultSize());

        mCleanAnimView.setOnColorChangeListener(this::showBarColor);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_junk_clean;
    }

    @Override
    protected void initView() {

    }

    @SuppressLint("CheckResult")
    private void clearAll() {
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
            EventBus.getDefault().post("clean_finish");
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

    @OnClick({R.id.img_back})
    public void onBackPress(View view) {
        finish();
    }

    @OnClick({R.id.icon_more})
    public void onMoreClick(View view) {
        showPopupWindow(mIconMore);
    }

    /**
     * 显示可绑定银行列表
     *
     * @param statusView
     */
    public void showPopupWindow(View statusView) {
        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(this).inflate(
                R.layout.layout_clean_more_info, null);
        View textApk = contentView.findViewById(R.id.text_apk);
        View textMemory = contentView.findViewById(R.id.text_memory);
        final PopupWindow popupWindow = new PopupWindow(contentView,
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        //APK白名单
        textApk.setOnClickListener(v -> {
            popupWindow.dismiss();
            startActivity(WhiteListInstallPackgeManageActivity.class);
        });
        //内存加速白名单
        textMemory.setOnClickListener(v -> {
            popupWindow.dismiss();
            startActivity(WhiteListSpeedAddActivity.class);
        });
        popupWindow.setTouchable(true);
        popupWindow.setTouchInterceptor((v, event) -> {
            return false;
            // 这里如果返回true的话，touch事件将被拦截
            // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
        });
        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bug
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        // 设置好参数之后再show
        popupWindow.showAsDropDown(statusView);
    }


    @Override
    protected void onResume() {
        super.onResume();

        getWhiteApp();
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 状态栏颜色变化
     *
     * @param animatedValue
     */
    public void showBarColor(int animatedValue) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarCompat.setStatusBarColor(this, animatedValue, true);
        } else {
            StatusBarCompat.setStatusBarColor(this, animatedValue, false);
        }
    }

    public void getWhiteApp() {
        ArrayList<FirstJunkInfo> processList = new ArrayList<>();
        for (Map.Entry<Integer, JunkGroup> entry : mJunkGroups.entrySet()) {
            JunkGroup value = entry.getValue();
            if ("进程清理".equals(value.mName)) {
                for (FirstJunkInfo info : value.mChildren) {
                    //进程
                    if (!isCacheWhite(info.getAppPackageName())) {
                        processList.add(info);
                    }
                }
                value.mChildren = processList;
            }
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
}
