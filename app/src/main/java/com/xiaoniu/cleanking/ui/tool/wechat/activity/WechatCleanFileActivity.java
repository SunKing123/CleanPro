package com.xiaoniu.cleanking.ui.tool.wechat.activity;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.event.FileCleanSizeEvent;
import com.xiaoniu.cleanking.ui.main.event.WxQqCleanEvent;
import com.xiaoniu.cleanking.ui.main.presenter.ImageListPresenter;
import com.xiaoniu.cleanking.ui.tool.wechat.adapter.WechatCleanFileAdapter;
import com.xiaoniu.cleanking.ui.tool.wechat.bean.CleanWxEasyInfo;
import com.xiaoniu.cleanking.ui.tool.wechat.bean.CleanWxFourItemInfo;
import com.xiaoniu.cleanking.ui.tool.wechat.bean.CleanWxItemInfo;
import com.xiaoniu.cleanking.ui.tool.wechat.presenter.WechatCleanFilePresenter;
import com.xiaoniu.cleanking.ui.tool.wechat.util.TimeUtil;
import com.xiaoniu.cleanking.ui.tool.wechat.util.WxQqUtil;
import com.xiaoniu.cleanking.utils.CleanAllFileScanUtil;
import com.xiaoniu.cleanking.utils.StatisticsUtils;
import com.xiaoniu.statistic.NiuDataAPI;

import org.greenrobot.eventbus.EventBus;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 微信清理文件
 */
public class WechatCleanFileActivity extends BaseActivity<WechatCleanFilePresenter> {
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
    ArrayList<CleanWxItemInfo> listDataToday = new ArrayList<>();
    ArrayList<CleanWxItemInfo> listDataYestoday = new ArrayList<>();
    ArrayList<CleanWxItemInfo> listDataInMonth = new ArrayList<>();
    ArrayList<CleanWxItemInfo> listDataInHalfYear = new ArrayList<>();
    WechatCleanFileAdapter fileAdapterToday;
    WechatCleanFileAdapter fileAdapterYestoday;
    WechatCleanFileAdapter fileAdapterInMonth;
    WechatCleanFileAdapter fileAdapterInHalfYear;

    @Override
    public int getLayoutId() {
        return R.layout.activity_wxclean_file;
    }

    @Override
    public void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }


    @OnClick({R.id.tv_select_today, R.id.tv_select_yestoday, R.id.tv_select_month, R.id.tv_select_halfyear, R.id.cons_halfyear, R.id.cons_month, R.id.cons_yestoday, R.id.cons_today, R.id.iv_back, R.id.tv_delete})
    public void onClickView(View view) {
        int ids = view.getId();
        if (ids == R.id.iv_back) {
            finish();
            StatisticsUtils.trackClick("receive_files_return_click", "接收文件返回点击", "wechat_cleaning_page", "wechat_receive_files_cleaning_page");
        } else if (ids == R.id.tv_delete) {
            if (!tv_delete.isSelected())
                return;
            List<CleanWxItemInfo> listFAll = new ArrayList<>();
            List<CleanWxItemInfo> listF = new ArrayList<>();
            List<CleanWxItemInfo> listData = fileAdapterToday.getListImage();
            for (int i = 0; i < listData.size(); i++) {
                if (listData.get(i).getIsSelect())
                    listF.add(fileAdapterToday.getListImage().get(i));
            }

            List<CleanWxItemInfo> listF2 = new ArrayList<>();
            List<CleanWxItemInfo> listData2 = fileAdapterYestoday.getListImage();
            for (int i = 0; i < listData2.size(); i++) {
                if (listData2.get(i).getIsSelect())
                    listF2.add(fileAdapterYestoday.getListImage().get(i));
            }

            List<CleanWxItemInfo> listF3 = new ArrayList<>();
            List<CleanWxItemInfo> listData3 = fileAdapterInMonth.getListImage();
            for (int i = 0; i < listData3.size(); i++) {
                if (listData3.get(i).getIsSelect())
                    listF3.add(fileAdapterInMonth.getListImage().get(i));
            }

            List<CleanWxItemInfo> listF4 = new ArrayList<>();
            List<CleanWxItemInfo> listData4 = fileAdapterInHalfYear.getListImage();
            for (int i = 0; i < listData4.size(); i++) {
                if (listData4.get(i).getIsSelect())
                    listF4.add(fileAdapterInHalfYear.getListImage().get(i));
            }
            listFAll.addAll(listF);
            listFAll.addAll(listF2);
            listFAll.addAll(listF3);
            listFAll.addAll(listF4);

            StatisticsUtils.trackClick("wechat_receive_files_cleaning_delete_click", "删除按钮点击", "wechat_cleaning_page", "wechat_receive_files_cleaning_page");
            mPresenter.alertBanLiveDialog(WechatCleanFileActivity.this, listFAll.size(), new ImageListPresenter.ClickListener() {
                @Override
                public void clickOKBtn() {
                    //删除本地文件
                    mPresenter.delFile(listFAll, listF, listF2, listF3, listF4);
                    //数据库删除选中的文件
                }

                @Override
                public void cancelBtn() {

                }
            });


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

    List<CleanWxItemInfo> listDataTemp;

    @Override
    public void initView() {
        cleanWxEasyInfoFile = WxQqUtil.n;

        List<CleanWxFourItemInfo> listFour = new ArrayList<>();
        listDataTemp = new ArrayList<>();
        for (int i = 0; i < cleanWxEasyInfoFile.getList().size(); i++) {
            if (cleanWxEasyInfoFile.getList().get(i) instanceof CleanWxFourItemInfo) {
                CleanWxFourItemInfo cleanWxHeadInfo = (CleanWxFourItemInfo) cleanWxEasyInfoFile.getList().get(i);
                listFour.add(cleanWxHeadInfo);
            }
        }

        for (int j = 0; j < listFour.size(); j++) {
            listDataTemp.addAll(listFour.get(j).getFourItem());
        }


        if (listDataTemp.size() == 0) {
            layoutNotNet.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);
            return;
        } else {
            layoutNotNet.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
        }

        for (int j = 0; j < listDataTemp.size(); j++) {
            try {
                listDataTemp.get(j).setIsSelect(false);
                if (TimeUtil.IsToday(listDataTemp.get(j).getStringDay())) {
                    listDataToday.add(listDataTemp.get(j));
                } else if (TimeUtil.IsYesterday(listDataTemp.get(j).getStringDay())) {
                    listDataYestoday.add(listDataTemp.get(j));
                } else if (TimeUtil.IsInMonth(listDataTemp.get(j).getStringDay())) {
                    listDataInMonth.add(listDataTemp.get(j));
                } else {
                    listDataInHalfYear.add(listDataTemp.get(j));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }


        fileAdapterToday = new WechatCleanFileAdapter(WechatCleanFileActivity.this, listDataToday);
        fileAdapterYestoday = new WechatCleanFileAdapter(WechatCleanFileActivity.this, listDataYestoday);
        fileAdapterInMonth = new WechatCleanFileAdapter(WechatCleanFileActivity.this, listDataInMonth);
        fileAdapterInHalfYear = new WechatCleanFileAdapter(WechatCleanFileActivity.this, listDataInHalfYear);
        recycleViewToday.setLayoutManager(new LinearLayoutManager(WechatCleanFileActivity.this));
        recycleViewYestoday.setLayoutManager(new LinearLayoutManager(WechatCleanFileActivity.this));
        recycleViewMonth.setLayoutManager(new LinearLayoutManager(WechatCleanFileActivity.this));
        recycleViewHalfyear.setLayoutManager(new LinearLayoutManager(WechatCleanFileActivity.this));
        recycleViewToday.setAdapter(fileAdapterToday);
        recycleViewYestoday.setAdapter(fileAdapterYestoday);
        recycleViewMonth.setAdapter(fileAdapterInMonth);
        recycleViewHalfyear.setAdapter(fileAdapterInHalfYear);
        fileAdapterToday.setmOnCheckListener(new WechatCleanFileAdapter.onCheckListener() {
            @Override
            public void onCheck(List<CleanWxItemInfo> listFile, int pos) {
                isSelectAllData();
                compulateDeleteSize();
            }
        });
        fileAdapterYestoday.setmOnCheckListener(new WechatCleanFileAdapter.onCheckListener() {
            @Override
            public void onCheck(List<CleanWxItemInfo> listFile, int pos) {
                isSelectAllData();
                compulateDeleteSize();
            }
        });
        fileAdapterInMonth.setmOnCheckListener(new WechatCleanFileAdapter.onCheckListener() {
            @Override
            public void onCheck(List<CleanWxItemInfo> listFile, int pos) {
                isSelectAllData();
                compulateDeleteSize();
            }
        });
        fileAdapterInHalfYear.setmOnCheckListener(new WechatCleanFileAdapter.onCheckListener() {
            @Override
            public void onCheck(List<CleanWxItemInfo> listFile, int pos) {
                isSelectAllData();
                compulateDeleteSize();
            }
        });

        tv_delete.setSelected(false);
        cb_checkall.setSelected(false);
        tvSelectToday.setSelected(false);
        tvSelectYestoday.setSelected(false);
        tvSelectMonth.setSelected(false);
        tvSelectHalfyear.setSelected(false);
        cb_checkall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listDataTemp.size() == 0) return;
                if (!recycleViewToday.isComputingLayout()) {
                    StatisticsUtils.trackClick("wechat_receive_files_cleaning_all_election_click", "全选按钮点击", "wechat_cleaning_page", "wechat_receive_files_cleaning_page");
                    cb_checkall.setSelected(!cb_checkall.isSelected());
                    tv_delete.setSelected(cb_checkall.isSelected());
                    fileAdapterToday.setIsCheckAll(cb_checkall.isSelected() ? true : false);
                    fileAdapterYestoday.setIsCheckAll(cb_checkall.isSelected() ? true : false);
                    fileAdapterInMonth.setIsCheckAll(cb_checkall.isSelected() ? true : false);
                    fileAdapterInHalfYear.setIsCheckAll(cb_checkall.isSelected() ? true : false);
                    cb_checkall.setBackgroundResource(cb_checkall.isSelected() ? R.drawable.icon_select : R.drawable.icon_unselect);
                    tv_delete.setBackgroundResource(cb_checkall.isSelected() ? R.drawable.delete_select_bg : R.drawable.delete_unselect_bg);
                    compulateDeleteSize();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        NiuDataAPI.onPageStart("wechat_receive_files_cleaning_view_page", "文件清理页面浏览");
    }

    @Override
    protected void onPause() {
        super.onPause();
        NiuDataAPI.onPageEnd("wechat_receive_files_cleaning_view_page", "文件清理页面浏览");
    }

    public void isSelectAllData() {
        List<CleanWxItemInfo> listAll = new ArrayList<>();
        List<CleanWxItemInfo> listF = new ArrayList<>();
        List<CleanWxItemInfo> listData = fileAdapterToday.getListImage();
        List<CleanWxItemInfo> listData2 = fileAdapterYestoday.getListImage();
        List<CleanWxItemInfo> listData3 = fileAdapterInMonth.getListImage();
        List<CleanWxItemInfo> listData4 = fileAdapterInHalfYear.getListImage();
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
        List<CleanWxItemInfo> listAll = new ArrayList<>();
        List<CleanWxItemInfo> listF = new ArrayList<>();
        List<CleanWxItemInfo> listData = fileAdapterToday.getListImage();
        List<CleanWxItemInfo> listData2 = fileAdapterYestoday.getListImage();
        List<CleanWxItemInfo> listData3 = fileAdapterInMonth.getListImage();
        List<CleanWxItemInfo> listData4 = fileAdapterInHalfYear.getListImage();
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
            deleteSize += listF.get(i).getFileSize();
        }
        setSelectSize(listData, tvSizeToday, tvSelectToday);
        setSelectSize(listData2, tvSizeYestoday, tvSelectYestoday);
        setSelectSize(listData3, tvSizeMonth, tvSelectMonth);
        setSelectSize(listData4, tvSizeHalfyear, tvSelectHalfyear);
        tv_delete.setText(deleteSize == 0 ? "删除" : "删除 " + CleanAllFileScanUtil.byte2FitSizeOne(deleteSize));
    }

    //今天、明天、一月内选中对应的recyclerview是否全选
    public void setSelectSize(List<CleanWxItemInfo> listData, TextView tvSize, TextView tvSelect) {
        List<CleanWxItemInfo> listAll = new ArrayList<>();
        for (int i = 0; i < listData.size(); i++) {
            if (listData.get(i).getIsSelect())
                listAll.add(listData.get(i));
        }
        long deleteSize = 0;
        for (int i = 0; i < listAll.size(); i++) {
            deleteSize += listAll.get(i).getFileSize();
        }
        tvSelect.setBackgroundResource(listAll.size() != 0 ? R.drawable.icon_select : R.drawable.icon_unselect);
        tvSize.setText(CleanAllFileScanUtil.byte2FitSizeOne(deleteSize));
    }

    //删除成功
    public void deleteSuccess(List<CleanWxItemInfo> list1, List<CleanWxItemInfo> list2, List<CleanWxItemInfo> list3, List<CleanWxItemInfo> list4) {
        tv_delete.setSelected(false);
        tv_delete.setText("删除");
        tv_delete.setBackgroundResource(R.drawable.delete_unselect_bg);
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

    @Override
    public void finish() {
        EventBus.getDefault().post(new WxQqCleanEvent(WxQqCleanEvent.WX_CLEAN_FILE, getAllFileSize()));
        super.finish();
    }

    //获取当前文件大小
    public long getAllFileSize() {
        if (fileAdapterToday == null)
            return 0;
        long fileSize = 0;
        List<CleanWxItemInfo> listAll = new ArrayList<>();
        List<CleanWxItemInfo> listData = fileAdapterToday.getListImage();
        List<CleanWxItemInfo> listData2 = fileAdapterYestoday.getListImage();
        List<CleanWxItemInfo> listData3 = fileAdapterInMonth.getListImage();
        List<CleanWxItemInfo> listData4 = fileAdapterInHalfYear.getListImage();
        listAll.addAll(listData);
        listAll.addAll(listData2);
        listAll.addAll(listData3);
        listAll.addAll(listData4);
        for (int i = 0; i < listAll.size(); i++) {
            fileSize += listAll.get(i).getFileSize();
        }
        return fileSize;
    }
}
