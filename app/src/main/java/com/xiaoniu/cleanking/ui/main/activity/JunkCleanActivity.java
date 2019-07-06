package com.xiaoniu.cleanking.ui.main.activity;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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
import com.xiaoniu.cleanking.utils.StatisticsUtils;
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

        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                JunkGroup junkGroup = mJunkGroups.get(groupPosition);
                if (junkGroup != null) {
                    junkGroup.isExpand = !junkGroup.isExpand();
                    mAdapter.notifyDataSetChanged();
                }
                return false;
            }
        });

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

        for (int i = 0; i < mJunkGroups.size(); i++) {
            listView.expandGroup(i);
        }
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
        //添加银行卡
        textApk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                startActivity(JunkCleanActivity.class);
                StatisticsUtils.burying(StatisticsUtils.BuryEvent.BANK_MANAGE_ADD_BANK_WINDOW);
            }
        });
        //设置还款顺序
        textMemory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                startActivity(JunkCleanActivity.class);
                StatisticsUtils.burying(StatisticsUtils.BuryEvent.BANK_MANAGE_SET_ORDER_WINDOW);
            }
        });
        popupWindow.setTouchable(true);
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });
        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bug
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        // 设置好参数之后再show
        popupWindow.showAsDropDown(statusView);
    }
}
