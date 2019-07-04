package com.xiaoniu.cleanking.ui.main.activity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.RouteConstants;
import com.xiaoniu.cleanking.base.SimpleActivity;
import com.xiaoniu.cleanking.ui.main.DockingExpandableListViewAdapter;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.bean.JunkGroup;
import com.xiaoniu.cleanking.ui.main.fragment.CleanMainFragment;
import com.xiaoniu.cleanking.utils.CleanUtil;
import com.xiaoniu.cleanking.utils.ToastUtils;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;

import java.util.HashMap;

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

    public static final int MSG_SYS_CACHE_BEGIN = 0x1001;
    public static final int MSG_SYS_CACHE_POS = 0x1002;
    public static final int MSG_SYS_CACHE_FINISH = 0x1003;

    public static final int MSG_PROCESS_BEGIN = 0x1011;
    public static final int MSG_PROCESS_POS = 0x1012;
    public static final int MSG_PROCESS_FINISH = 0x1013;

    public static final int MSG_OVERALL_BEGIN = 0x1021;
    public static final int MSG_OVERALL_POS = 0x1022;
    public static final int MSG_OVERALL_FINISH = 0x1023;

    public static final int MSG_SYS_CACHE_CLEAN_FINISH = 0x1100;
    public static final int MSG_PROCESS_CLEAN_FINISH = 0x1101;
    public static final int MSG_OVERALL_CLEAN_FINISH = 0x1102;

    public static final String HANG_FLAG = "hanged";

    private DockingExpandableListViewAdapter mAdapter;

    @BindView(R.id.tv_title)
    TextView mTextTitle;
    @BindView(R.id.do_junk_clean)
    TextView mTextClean;

    @BindView(R.id.layout_show_list)
    RelativeLayout mLayoutList;
    @BindView(R.id.layout_clean_finish)
    RelativeLayout mLayoutFinish;
    @BindView(R.id.text_clean_number)
    TextView mTextCleanNumber;

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

        mTextClean.setOnClickListener(v -> {
            clearAll();
        });



        ExpandableListView listView = findViewById(R.id.junk_list);
        listView.setGroupIndicator(null);
        listView.setChildIndicator(null);
        listView.setDividerHeight(0);

        mAdapter = new DockingExpandableListViewAdapter(this,listView);
        mAdapter.setOnItemSelectListener(() -> {
            long totalSize = getTotalSize();
            if (totalSize <= 0) {
                mTextClean.setEnabled(false);
                mTextClean.setText("清理");
            }else {
                mTextClean.setEnabled(true);
                mTextClean.setText("清理" + CleanUtil.formatShortFileSize(JunkCleanActivity.this, totalSize));
            }
        });

        listView.setAdapter(mAdapter);

        mAdapter.setData(mJunkGroups);
        String s = CleanUtil.formatShortFileSize(JunkCleanActivity.this, getTotalSize());
        mTextClean.setText("清理"+s);
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
            JunkGroup processGroup = mJunkGroups.get(JunkGroup.GROUP_PROCESS);
            long total = 0;
            for (FirstJunkInfo info : processGroup.mChildren) {
                if (info.isAllchecked()) {
                    total += info.getTotalSize();
                    CleanUtil.killAppProcesses(info.getAppPackageName(),info.getPid());
                }
            }

            JunkGroup group = mJunkGroups.get(JunkGroup.GROUP_CACHE);

            long l = CleanUtil.freeJunkInfos(group.mChildren);

            total += l;

            JunkGroup junkGroup = mJunkGroups.get(JunkGroup.GROUP_APK);

            long l1 = CleanUtil.freeJunkInfos(junkGroup.mChildren);

            total += l1;

            e.onNext(total);
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(o -> {
            //TODO 清理完成
            String s = CleanUtil.formatShortFileSize(JunkCleanActivity.this, (Long) o);
            ToastUtils.show("清理了" + s + "垃圾");
            mTextCleanNumber.setText("清理了 " + s + " 垃圾");
            mLayoutList.setVisibility(View.GONE);
            mLayoutFinish.setVisibility(View.VISIBLE);
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
}
