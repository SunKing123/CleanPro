package com.xiaoniu.cleanking.ui.tool.qq.activity;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.tool.qq.adapter.QQCleanFileAdapter;
import com.xiaoniu.cleanking.ui.tool.qq.bean.CleanWxClearInfo;
import com.xiaoniu.cleanking.ui.tool.qq.presenter.QQCleanFilePresenter;
import com.xiaoniu.cleanking.ui.tool.qq.util.QQUtil;
import com.xiaoniu.cleanking.ui.tool.wechat.bean.CleanWxEasyInfo;
import com.xiaoniu.cleanking.ui.tool.wechat.util.TimeUtil;
import com.xiaoniu.cleanking.ui.tool.wechat.util.WxQqUtil;
import com.xiaoniu.cleanking.utils.CleanAllFileScanUtil;
import com.xiaoniu.cleanking.utils.StatisticsUtils;
import com.xiaoniu.statistic.NiuDataAPI;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * QQ理文件
 */
public class QQCleanFileActivity extends BaseActivity<QQCleanFilePresenter> {
    @BindView(R.id.recycle_view_today)
    RecyclerView recycleViewToday;
    @BindView(R.id.recycle_view_yestoday)
    RecyclerView recycleViewYestoday;
    @BindView(R.id.recycle_view_month)
    RecyclerView recycleViewMonth;
    @BindView(R.id.recycle_view_halfyear)
    RecyclerView recycleViewHalfyear;
    @BindView(R.id.cb_checkall)
    TextView cb_checkall;
    @BindView(R.id.tv_delete)
    TextView tv_delete;
    @BindView(R.id.tv_select_today)
    TextView tvSelectToday;
    @BindView(R.id.tv_size_today)
    TextView tvSizeToday;
    @BindView(R.id.tv_size_yestoday)
    TextView tvSizeYestoday;
    @BindView(R.id.tv_size_month)
    TextView tvSizeMonth;
    @BindView(R.id.tv_size_halfyear)
    TextView tvSizeHalfyear;
    @BindView(R.id.tv_select_yestoday)
    TextView tvSelectYestoday;
    @BindView(R.id.tv_select_month)
    TextView tvSelectMonth;
    @BindView(R.id.tv_select_halfyear)
    TextView tvSelectHalfyear;
    @BindView(R.id.iv_arrow_today)
    ImageView ivArrowToday;
    @BindView(R.id.iv_arrow_halfyear)
    ImageView ivArrowHalfyear;
    @BindView(R.id.iv_arrow_yestoday)
    ImageView ivArrowYestoday;
    @BindView(R.id.iv_arrow_month)
    ImageView ivArrowMonth;
    @BindView(R.id.cons_today)
    ConstraintLayout consToday;
    @BindView(R.id.cons_yestoday)
    ConstraintLayout consYestoday;
    @BindView(R.id.cons_month)
    ConstraintLayout consMonth;
    @BindView(R.id.cons_halfyear)
    ConstraintLayout consHalfyear;


    @BindView(R.id.layout_not_net)
    LinearLayout layoutNotNet;
    @BindView(R.id.scroll_view)
    ScrollView scrollView;

    CleanWxEasyInfo cleanWxEasyInfoFile;
    ArrayList<CleanWxClearInfo> listDataToday = new ArrayList<>();
    ArrayList<CleanWxClearInfo> listDataYestoday = new ArrayList<>();
    ArrayList<CleanWxClearInfo> listDataInMonth = new ArrayList<>();
    ArrayList<CleanWxClearInfo> listDataInHalfYear = new ArrayList<>();
    QQCleanFileAdapter fileAdapterToday;
    QQCleanFileAdapter fileAdapterYestoday;
    QQCleanFileAdapter fileAdapterInMonth;
    QQCleanFileAdapter fileAdapterInHalfYear;

    @Override
    public int getLayoutId() {
        return R.layout.activity_wxclean_file;
    }

    @Override
    public void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        NiuDataAPI.onPageStart("qq_file_cleaning_view_page", "文件清理页面浏览");
    }

    @Override
    protected void onPause() {
        super.onPause();
        NiuDataAPI.onPageEnd("qq_file_cleaning_view_page", "文件清理页面浏览");
    }

    @OnClick({R.id.tv_select_today, R.id.tv_select_yestoday, R.id.tv_select_month, R.id.tv_select_halfyear, R.id.cons_halfyear, R.id.cons_month, R.id.cons_yestoday, R.id.cons_today, R.id.iv_back, R.id.tv_delete})
    public void onClickView(View view) {
        int ids = view.getId();
        if (ids == R.id.iv_back) {
            finish();
            StatisticsUtils.trackClick("receive_files_return_click", "接收文件返回点击", "qq_cleaning_page", "qq_file_cleaning_page");
        } else if (ids == R.id.tv_delete) {
            if (!tv_delete.isSelected())
                return;
            finish();
            StatisticsUtils.trackClick("qq_file_confirm_the_selection_click", "\"确认选中\"点击", "qq_cleaning_page", "qq_file_cleaning_page");

        } else if (ids == R.id.cons_today) {
            recycleViewToday.setVisibility(recycleViewToday.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            ivArrowToday.setImageResource(recycleViewToday.getVisibility() == View.VISIBLE ? R.mipmap.arrow_up : R.mipmap.arrow_down);
        } else if (ids == R.id.cons_yestoday) {
            recycleViewYestoday.setVisibility(recycleViewYestoday.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            ivArrowYestoday.setImageResource(recycleViewYestoday.getVisibility() == View.VISIBLE ? R.mipmap.arrow_up : R.mipmap.arrow_down);
        } else if (ids == R.id.cons_month) {
            recycleViewMonth.setVisibility(recycleViewMonth.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            ivArrowMonth.setImageResource(recycleViewMonth.getVisibility() == View.VISIBLE ? R.mipmap.arrow_up : R.mipmap.arrow_down);
        } else if (ids == R.id.cons_halfyear) {
            recycleViewHalfyear.setVisibility(recycleViewHalfyear.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            ivArrowHalfyear.setImageResource(recycleViewHalfyear.getVisibility() == View.VISIBLE ? R.mipmap.arrow_up : R.mipmap.arrow_down);
        } else if (ids == R.id.tv_select_today) {
            tvSelectToday.setSelected(!tvSelectToday.isSelected());
            fileAdapterToday.setIsCheckAll(tvSelectToday.isSelected() ? true : false);
            tvSelectToday.setBackgroundResource(tvSelectToday.isSelected() ? R.drawable.icon_select : R.drawable.icon_unselect);
            isSelectAllData();
            compulateDeleteSize();
        } else if (ids == R.id.tv_select_yestoday) {
            tvSelectYestoday.setSelected(!tvSelectYestoday.isSelected());
            fileAdapterYestoday.setIsCheckAll(tvSelectYestoday.isSelected() ? true : false);
            tvSelectYestoday.setBackgroundResource(tvSelectYestoday.isSelected() ? R.drawable.icon_select : R.drawable.icon_unselect);
            isSelectAllData();
            compulateDeleteSize();
        } else if (ids == R.id.tv_select_month) {
            tvSelectMonth.setSelected(!tvSelectMonth.isSelected());
            fileAdapterInMonth.setIsCheckAll(tvSelectMonth.isSelected() ? true : false);
            tvSelectMonth.setBackgroundResource(tvSelectMonth.isSelected() ? R.drawable.icon_select : R.drawable.icon_unselect);
            isSelectAllData();
            compulateDeleteSize();
        } else if (ids == R.id.tv_select_halfyear) {
            tvSelectHalfyear.setSelected(!tvSelectHalfyear.isSelected());
            fileAdapterInHalfYear.setIsCheckAll(tvSelectHalfyear.isSelected() ? true : false);
            tvSelectHalfyear.setBackgroundResource(tvSelectHalfyear.isSelected() ? R.drawable.icon_select : R.drawable.icon_unselect);
            isSelectAllData();
            compulateDeleteSize();
        }
    }

    List<CleanWxClearInfo> listData;

    @Override
    public void initView() {
        cleanWxEasyInfoFile = WxQqUtil.n;

        listData = QQUtil.fileList;

        if (listData.size() == 0) {
            layoutNotNet.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);
            return;
        } else {
            layoutNotNet.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
        }
        for (int j = 0; j < listData.size(); j++) {
            try {

                if (TimeUtil.IsToday(com.xiaoniu.cleanking.utils.TimeUtil.getTimesByLong(listData.get(j).getTime()))) {
                    listDataToday.add(listData.get(j));
                } else if (TimeUtil.IsYesterday(com.xiaoniu.cleanking.utils.TimeUtil.getTimesByLong(listData.get(j).getTime()))) {
                    listDataYestoday.add(listData.get(j));
                } else if (TimeUtil.IsInMonth(com.xiaoniu.cleanking.utils.TimeUtil.getTimesByLong(listData.get(j).getTime()))) {
                    listDataInMonth.add(listData.get(j));
                } else {
                    listDataInHalfYear.add(listData.get(j));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        /*隐藏数据为0的分组标题*/
        if (listDataToday.size() <= 0) {
            consToday.setVisibility(View.GONE);
        }
        if (listDataYestoday.size() <= 0) {
            consYestoday.setVisibility(View.GONE);
        }
        if (listDataInMonth.size() <= 0) {
            consMonth.setVisibility(View.GONE);
        }
        if (listDataInHalfYear.size() <= 0) {
            consHalfyear.setVisibility(View.GONE);
        }

        fileAdapterToday = new QQCleanFileAdapter(QQCleanFileActivity.this, listDataToday);
        fileAdapterYestoday = new QQCleanFileAdapter(QQCleanFileActivity.this, listDataYestoday);
        fileAdapterInMonth = new QQCleanFileAdapter(QQCleanFileActivity.this, listDataInMonth);
        fileAdapterInHalfYear = new QQCleanFileAdapter(QQCleanFileActivity.this, listDataInHalfYear);
        recycleViewToday.setLayoutManager(new LinearLayoutManager(QQCleanFileActivity.this));
        recycleViewYestoday.setLayoutManager(new LinearLayoutManager(QQCleanFileActivity.this));
        recycleViewMonth.setLayoutManager(new LinearLayoutManager(QQCleanFileActivity.this));
        recycleViewHalfyear.setLayoutManager(new LinearLayoutManager(QQCleanFileActivity.this));
        recycleViewToday.setAdapter(fileAdapterToday);
        recycleViewYestoday.setAdapter(fileAdapterYestoday);
        recycleViewMonth.setAdapter(fileAdapterInMonth);
        recycleViewHalfyear.setAdapter(fileAdapterInHalfYear);
        fileAdapterToday.setmOnCheckListener(new QQCleanFileAdapter.onCheckListener() {
            @Override
            public void onCheck(List<CleanWxClearInfo> listFile, int pos) {
                isSelectAllData();
                compulateDeleteSize();
            }
        });
        fileAdapterYestoday.setmOnCheckListener(new QQCleanFileAdapter.onCheckListener() {
            @Override
            public void onCheck(List<CleanWxClearInfo> listFile, int pos) {
                isSelectAllData();
                compulateDeleteSize();
            }
        });
        fileAdapterInMonth.setmOnCheckListener(new QQCleanFileAdapter.onCheckListener() {
            @Override
            public void onCheck(List<CleanWxClearInfo> listFile, int pos) {
                isSelectAllData();
                compulateDeleteSize();
            }
        });
        fileAdapterInHalfYear.setmOnCheckListener(new QQCleanFileAdapter.onCheckListener() {
            @Override
            public void onCheck(List<CleanWxClearInfo> listFile, int pos) {
                isSelectAllData();
                compulateDeleteSize();
            }
        });
        compulateDeleteSize();
        isSelectAllData();

        tv_delete.setSelected(false);
        cb_checkall.setSelected(false);
        tvSelectToday.setSelected(false);
        tvSelectYestoday.setSelected(false);
        tvSelectMonth.setSelected(false);
        tvSelectHalfyear.setSelected(false);
        cb_checkall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listData.size() == 0) return;
                if (!recycleViewToday.isComputingLayout()) {
                    cb_checkall.setSelected(!cb_checkall.isSelected());
                    tv_delete.setSelected(cb_checkall.isSelected());
                    fileAdapterToday.setIsCheckAll(cb_checkall.isSelected() ? true : false);
                    fileAdapterYestoday.setIsCheckAll(cb_checkall.isSelected() ? true : false);
                    fileAdapterInMonth.setIsCheckAll(cb_checkall.isSelected() ? true : false);
                    fileAdapterInHalfYear.setIsCheckAll(cb_checkall.isSelected() ? true : false);
                    cb_checkall.setBackgroundResource(cb_checkall.isSelected() ? R.drawable.icon_select : R.drawable.icon_unselect);
                    tv_delete.setBackgroundResource(cb_checkall.isSelected() ? R.drawable.delete_select_bg : R.drawable.delete_unselect_bg);
                    compulateDeleteSize();
                    StatisticsUtils.trackClick("qq_file_cleaning_all_election_click", "全选按钮点击", "qq_cleaning_page", "qq_file_cleaning_page");
                }
            }
        });
    }

    public void isSelectAllData() {
        List<CleanWxClearInfo> listAll = new ArrayList<>();
        List<CleanWxClearInfo> listF = new ArrayList<>();
        List<CleanWxClearInfo> listData = fileAdapterToday.getListImage();
        List<CleanWxClearInfo> listData2 = fileAdapterYestoday.getListImage();
        List<CleanWxClearInfo> listData3 = fileAdapterInMonth.getListImage();
        List<CleanWxClearInfo> listData4 = fileAdapterInHalfYear.getListImage();
        listAll.addAll(listData);
        listAll.addAll(listData2);
        listAll.addAll(listData3);
        listAll.addAll(listData4);
        for (int i = 0; i < listAll.size(); i++) {
            if (listAll.get(i).getIsSelect())
                listF.add(listAll.get(i));
        }
        cb_checkall.setBackgroundResource(listF.size() == fileAdapterToday.getListImage().size() + fileAdapterYestoday.getListImage().size() + fileAdapterInMonth.getListImage().size() + fileAdapterInHalfYear.getListImage().size() ? R.drawable.icon_select : R.drawable.icon_unselect);
        tv_delete.setBackgroundResource(listF.size() == 0 ? R.drawable.delete_unselect_bg : R.drawable.delete_select_bg);
        tv_delete.setSelected(listF.size() == 0 ? false : true);

    }

    //计算删除文件大小
    public void compulateDeleteSize() {
        List<CleanWxClearInfo> listAll = new ArrayList<>();
        List<CleanWxClearInfo> listF = new ArrayList<>();
        List<CleanWxClearInfo> listData = fileAdapterToday.getListImage();
        List<CleanWxClearInfo> listData2 = fileAdapterYestoday.getListImage();
        List<CleanWxClearInfo> listData3 = fileAdapterInMonth.getListImage();
        List<CleanWxClearInfo> listData4 = fileAdapterInHalfYear.getListImage();
        listAll.addAll(listData);
        listAll.addAll(listData2);
        listAll.addAll(listData3);
        listAll.addAll(listData4);
        for (int i = 0; i < listAll.size(); i++) {
            if (listAll.get(i).getIsSelect())
                listF.add(listAll.get(i));
        }
        long deleteSize = 0;
        for (int i = 0; i < listF.size(); i++) {
            deleteSize += listF.get(i).getSize();
        }
        setSelectSize(listData, tvSizeToday, tvSelectToday);
        setSelectSize(listData2, tvSizeYestoday, tvSelectYestoday);
        setSelectSize(listData3, tvSizeMonth, tvSelectMonth);
        setSelectSize(listData4, tvSizeHalfyear, tvSelectHalfyear);
        tv_delete.setText(deleteSize == 0 ? "未选中" : "选中 " + CleanAllFileScanUtil.byte2FitSizeOne(deleteSize));
    }

    //今天、明天、一月内选中对应的recyclerview是否全选
    public void setSelectSize(List<CleanWxClearInfo> listData, TextView tvSize, TextView tvSelect) {
        List<CleanWxClearInfo> listAll = new ArrayList<>();
        for (int i = 0; i < listData.size(); i++) {
            if (listData.get(i).getIsSelect())
                listAll.add(listData.get(i));
        }
        long deleteSize = 0;
        for (int i = 0; i < listAll.size(); i++) {
            deleteSize += listAll.get(i).getSize();
        }
        tvSelect.setBackgroundResource(listAll.size() != 0 ? R.drawable.icon_select : R.drawable.icon_unselect);
        tvSize.setText(CleanAllFileScanUtil.byte2FitSizeOne(deleteSize));
    }

    //删除成功
    public void deleteSuccess(List<CleanWxClearInfo> list1, List<CleanWxClearInfo> list2, List<CleanWxClearInfo> list3, List<CleanWxClearInfo> list4) {
        tv_delete.setSelected(false);
        tv_delete.setText("删除");
        fileAdapterToday.deleteData(list1);
        fileAdapterYestoday.deleteData(list2);
        fileAdapterInMonth.deleteData(list3);
        fileAdapterInHalfYear.deleteData(list4);
//        line_none.setVisibility(imageAdapter.getListImage().size() == 0 ? View.VISIBLE : View.GONE);
//        recycle_view.setVisibility(imageAdapter.getListImage().size() == 0 ? View.GONE : View.VISIBLE);
    }

    @Override
    public void netError() {

    }
}
