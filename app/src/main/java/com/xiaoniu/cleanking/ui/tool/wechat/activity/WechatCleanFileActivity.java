package com.xiaoniu.cleanking.ui.tool.wechat.activity;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.event.WxQqCleanEvent;
import com.xiaoniu.cleanking.ui.main.presenter.ImageListPresenter;
import com.xiaoniu.cleanking.ui.tool.wechat.adapter.WechatCleanFileAdapter;
import com.xiaoniu.cleanking.ui.tool.wechat.bean.CleanWxChildInfo;
import com.xiaoniu.cleanking.ui.tool.wechat.bean.CleanWxEasyInfo;
import com.xiaoniu.cleanking.ui.tool.wechat.bean.CleanWxFourItemInfo;
import com.xiaoniu.cleanking.ui.tool.wechat.bean.CleanWxGroupInfo;
import com.xiaoniu.cleanking.ui.tool.wechat.bean.CleanWxItemInfo;
import com.xiaoniu.cleanking.ui.tool.wechat.presenter.WechatCleanFilePresenter;
import com.xiaoniu.cleanking.ui.tool.wechat.util.TimeUtil;
import com.xiaoniu.cleanking.ui.tool.wechat.util.WxQqUtil;
import com.xiaoniu.cleanking.utils.CleanAllFileScanUtil;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.widget.xrecyclerview.MultiItemInfo;
import com.xiaoniu.common.widget.xrecyclerview.XRecyclerView;
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
    @BindView(R.id.cb_checkall)
    TextView cb_checkall;
    @BindView(R.id.tv_delete)
    TextView tv_delete;
    @BindView(R.id.layout_not_net)
    LinearLayout layoutNotNet;
    @BindView(R.id.recyclerView)
    XRecyclerView recyclerView;

    CleanWxEasyInfo cleanWxEasyInfoFile;
    ArrayList<CleanWxItemInfo> listDataToday = new ArrayList<>();
    ArrayList<CleanWxItemInfo> listDataYestoday = new ArrayList<>();
    ArrayList<CleanWxItemInfo> listDataInMonth = new ArrayList<>();
    ArrayList<CleanWxItemInfo> listDataInHalfYear = new ArrayList<>();
    private WechatCleanFileAdapter cleanFileAdapter;
    private ArrayList<MultiItemInfo> mAllDatas;
    private ArrayList<MultiItemInfo> mItemSelectList;

    @Override
    public int getLayoutId() {
        return R.layout.activity_wxclean_file;
    }

    @Override
    public void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }


    @OnClick({R.id.iv_back, R.id.tv_delete})
    public void onClickView(View view) {
        int ids = view.getId();
        if (ids == R.id.iv_back) {
            finish();
            StatisticsUtils.trackClick("receive_files_return_click", "接收文件返回点击", "wechat_cleaning_page", "wechat_receive_files_cleaning_page");
        } else if (ids == R.id.tv_delete) {
            if (!tv_delete.isSelected())
                return;
            StatisticsUtils.trackClick("wechat_receive_files_cleaning_delete_click", "删除按钮点击", "wechat_cleaning_page", "wechat_receive_files_cleaning_page");
            int fileCount = cleanFileAdapter.getSelectedData().size();

            mPresenter.alertBanLiveDialog(WechatCleanFileActivity.this, fileCount, new ImageListPresenter.ClickListener() {
                @Override
                public void clickOKBtn() {
                    //删除本地文件
                    mPresenter.delFile(cleanFileAdapter.getSelectedData());
                    //数据库删除选中的文件
                }

                @Override
                public void cancelBtn() {

                }
            });
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
            recyclerView.setVisibility(View.GONE);
            return;
        } else {
            layoutNotNet.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
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

        /*以下是封装分组数据，必须继承MultiItemInfo类*/
        mAllDatas = new ArrayList<>();
        /*隐藏数据为0的分组标题*/
        if (listDataToday.size() > 0) {
            CleanWxGroupInfo groupInfo1 = new CleanWxGroupInfo();
            groupInfo1.title = "今天";
            groupInfo1.isExpanded = true;
            for (int i = 0; i < listDataToday.size(); i++) {
                CleanWxItemInfo itemInfo = listDataToday.get(i);
                CleanWxChildInfo childInfo = new CleanWxChildInfo();
                childInfo.canLoadPic = itemInfo.isCanLoadPic();
                childInfo.Days = itemInfo.getDays();
                childInfo.file = itemInfo.getFile();
                childInfo.fileType = itemInfo.getFileType();
                childInfo.totalSize = itemInfo.getFileSize();
                childInfo.stringDay = itemInfo.getStringDay();
                groupInfo1.addItemInfo(childInfo);
            }
            mAllDatas.add(groupInfo1);
        }
        if (listDataYestoday.size() > 0) {
            CleanWxGroupInfo groupInfo2 = new CleanWxGroupInfo();
            groupInfo2.title = "昨天";
            groupInfo2.isExpanded = true;
            for (int i = 0; i < listDataYestoday.size(); i++) {
                CleanWxItemInfo itemInfo = listDataYestoday.get(i);
                CleanWxChildInfo childInfo = new CleanWxChildInfo();
                childInfo.canLoadPic = itemInfo.isCanLoadPic();
                childInfo.Days = itemInfo.getDays();
                childInfo.file = itemInfo.getFile();
                childInfo.fileType = itemInfo.getFileType();
                childInfo.totalSize = itemInfo.getFileSize();
                childInfo.stringDay = itemInfo.getStringDay();
                groupInfo2.addItemInfo(childInfo);
            }
            mAllDatas.add(groupInfo2);
        }
        if (listDataInMonth.size() > 0) {
            CleanWxGroupInfo groupInfo3 = new CleanWxGroupInfo();
            groupInfo3.title = "一月内";
            groupInfo3.isExpanded = true;
            for (int i = 0; i < listDataInMonth.size(); i++) {
                CleanWxItemInfo itemInfo = listDataInMonth.get(i);
                CleanWxChildInfo childInfo = new CleanWxChildInfo();
                childInfo.canLoadPic = itemInfo.isCanLoadPic();
                childInfo.Days = itemInfo.getDays();
                childInfo.file = itemInfo.getFile();
                childInfo.fileType = itemInfo.getFileType();
                childInfo.totalSize = itemInfo.getFileSize();
                childInfo.stringDay = itemInfo.getStringDay();
                groupInfo3.addItemInfo(childInfo);
            }
            mAllDatas.add(groupInfo3);
        }
        if (listDataInHalfYear.size() > 0) {
            CleanWxGroupInfo groupInfo4 = new CleanWxGroupInfo();
            groupInfo4.title = "更早";
            groupInfo4.isExpanded = true;
            for (int i = 0; i < listDataInHalfYear.size(); i++) {
                CleanWxItemInfo itemInfo = listDataInHalfYear.get(i);
                CleanWxChildInfo childInfo = new CleanWxChildInfo();
                childInfo.canLoadPic = itemInfo.isCanLoadPic();
                childInfo.Days = itemInfo.getDays();
                childInfo.file = itemInfo.getFile();
                childInfo.fileType = itemInfo.getFileType();
                childInfo.totalSize = itemInfo.getFileSize();
                childInfo.stringDay = itemInfo.getStringDay();
                groupInfo4.addItemInfo(childInfo);
            }
            mAllDatas.add(groupInfo4);
        }
        cleanFileAdapter = new WechatCleanFileAdapter(WechatCleanFileActivity.this);
        cleanFileAdapter.setData(mAllDatas);
        recyclerView.setAdapter(cleanFileAdapter);
        DividerItemDecoration decor = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        decor.setDrawable(ContextCompat.getDrawable(this, R.drawable.bg_divider_shape)); //这里在就是
        recyclerView.addItemDecoration(decor);

        cleanFileAdapter.setmOnCheckListener((info) -> {
            long allSize = cleanFileAdapter.getSelectedSize();
//            cb_checkall.setBackgroundResource(isAllSelect ? R.drawable.icon_select : R.drawable.icon_unselect);
            tv_delete.setBackgroundResource(allSize == 0 ? R.drawable.delete_unselect_bg : R.drawable.delete_select_bg);
            tv_delete.setSelected(allSize == 0 ? false : true);
            tv_delete.setText(allSize == 0 ? "删除" : "删除 " + CleanAllFileScanUtil.byte2FitSizeOne(allSize));
        });

        tv_delete.setSelected(false);
        cb_checkall.setSelected(false);
        cb_checkall.setOnClickListener(v -> {
            if (mAllDatas.size() == 0) return;
            StatisticsUtils.trackClick("wechat_receive_files_cleaning_all_election_click", "全选按钮点击", "wechat_cleaning_page", "wechat_receive_files_cleaning_page");
            cb_checkall.setSelected(!cb_checkall.isSelected());
            tv_delete.setSelected(cb_checkall.isSelected());

            cb_checkall.setBackgroundResource(cb_checkall.isSelected() ? R.drawable.icon_select : R.drawable.icon_unselect);
            tv_delete.setBackgroundResource(cb_checkall.isSelected() ? R.drawable.delete_select_bg : R.drawable.delete_unselect_bg);
            tv_delete.setSelected(cb_checkall.isSelected() ? true : false);

            cleanFileAdapter.selectAll(cb_checkall.isSelected());
            long allSize = cleanFileAdapter.getSelectedSize();
            tv_delete.setText(allSize == 0 ? "删除" : "删除 " + CleanAllFileScanUtil.byte2FitSizeOne(allSize));
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

    //删除成功
    public void deleteSuccess() {
        cleanFileAdapter.removeSelectedData();
        tv_delete.setSelected(false);
        tv_delete.setText("删除");
        tv_delete.setBackgroundResource(R.drawable.delete_unselect_bg);
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
        if (mAllDatas == null)
            return 0;
        long fileSize = 0;
        for (int i = 0; i < mAllDatas.size(); i++) {
            CleanWxGroupInfo titleInfo = (CleanWxGroupInfo) mAllDatas.get(i);
            List<CleanWxChildInfo> listItem = titleInfo.getChildList();
            if (listItem != null) {
                for (int j = 0; j < listItem.size(); j++) {
                    CleanWxChildInfo itemInfo = listItem.get(j);
                    fileSize += itemInfo.totalSize;
                }
            }
        }
        return fileSize;
    }
}
