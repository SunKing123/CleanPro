package com.xiaoniu.cleanking.ui.tool.wechat.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.presenter.ImageListPresenter;
import com.xiaoniu.cleanking.ui.tool.wechat.adapter.WechatCleanFileAdapter;
import com.xiaoniu.cleanking.ui.tool.wechat.bean.CleanWxEasyInfo;
import com.xiaoniu.cleanking.ui.tool.wechat.bean.CleanWxFourItemInfo;
import com.xiaoniu.cleanking.ui.tool.wechat.bean.CleanWxItemInfo;
import com.xiaoniu.cleanking.ui.tool.wechat.presenter.WechatCleanFilePresenter;
import com.xiaoniu.cleanking.ui.tool.wechat.util.WxQqUtil;
import com.xiaoniu.cleanking.utils.CleanAllFileScanUtil;

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
    @BindView(R.id.cb_checkall)
    TextView cb_checkall;
    @BindView(R.id.tv_delete)
    TextView tv_delete;
    CleanWxEasyInfo cleanWxEasyInfoFile;
    ArrayList<CleanWxItemInfo> listData = new ArrayList<>();
    WechatCleanFileAdapter fileAdapter;

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
        } else if (ids == R.id.tv_delete) {
            if (!tv_delete.isSelected())
                return;
            List<CleanWxItemInfo> listF = new ArrayList<>();
            List<CleanWxItemInfo> listData = fileAdapter.getListImage();
            for (int i = 0; i < listData.size(); i++) {
                if (listData.get(i).getIsSelect())
                    listF.add(fileAdapter.getListImage().get(i));
            }


            mPresenter.alertBanLiveDialog(WechatCleanFileActivity.this, listF.size(), new ImageListPresenter.ClickListener() {
                @Override
                public void clickOKBtn() {
                    //删除本地文件
                    mPresenter.delFile(listF);
                    //数据库删除选中的文件
                }

                @Override
                public void cancelBtn() {

                }
            });


        }
    }

    @Override
    public void initView() {

        cleanWxEasyInfoFile = WxQqUtil.n;

        List<CleanWxFourItemInfo> listFour = new ArrayList<>();
        List<CleanWxItemInfo> listDataTemp = new ArrayList<>();
        for (int i = 0; i < cleanWxEasyInfoFile.getList().size(); i++) {
            if (cleanWxEasyInfoFile.getList().get(i) instanceof CleanWxFourItemInfo) {
                CleanWxFourItemInfo cleanWxHeadInfo = (CleanWxFourItemInfo) cleanWxEasyInfoFile.getList().get(i);
                listFour.add(cleanWxHeadInfo);
            }
        }

        for (int j = 0; j < listFour.size(); j++) {
            listDataTemp.addAll(listFour.get(j).getFourItem());
        }

        for (int j = 0; j < listDataTemp.size(); j++) {
            listData.add(listDataTemp.get(j));
        }

        Log.e("qweewq", "" + listData.size());

        fileAdapter = new WechatCleanFileAdapter(WechatCleanFileActivity.this, listData);
        recycleViewToday.setLayoutManager(new LinearLayoutManager(WechatCleanFileActivity.this));
        recycleViewToday.setAdapter(fileAdapter);
        fileAdapter.setmOnCheckListener(new WechatCleanFileAdapter.onCheckListener() {
            @Override
            public void onCheck(List<CleanWxItemInfo> listFile, int pos) {
                int selectCount = 0;
                for (int i = 0; i < listFile.size(); i++) {
                    if (listFile.get(i).getIsSelect()) {
                        selectCount++;
                    }
                }
                cb_checkall.setBackgroundResource(selectCount == listFile.size() ? R.drawable.icon_select : R.drawable.icon_unselect);
                tv_delete.setBackgroundResource(selectCount == 0 ? R.drawable.delete_unselect_bg : R.drawable.delete_select_bg);
                tv_delete.setSelected(selectCount == 0 ? false : true);
                compulateDeleteSize();
            }
        });

        tv_delete.setSelected(false);
        cb_checkall.setSelected(false);
        cb_checkall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!recycleViewToday.isComputingLayout()) {
                    cb_checkall.setSelected(!cb_checkall.isSelected());
                    tv_delete.setSelected(cb_checkall.isSelected());
                    fileAdapter.setIsCheckAll(cb_checkall.isSelected() ? true : false);
                    cb_checkall.setBackgroundResource(cb_checkall.isSelected() ? R.drawable.icon_select : R.drawable.icon_unselect);
                    tv_delete.setBackgroundResource(cb_checkall.isSelected() ? R.drawable.delete_select_bg : R.drawable.delete_unselect_bg);
                    compulateDeleteSize();
                }
            }
        });
    }

    //计算删除文件大小
    public void compulateDeleteSize() {
        List<CleanWxItemInfo> listF = new ArrayList<>();
        List<CleanWxItemInfo> listData = fileAdapter.getListImage();
        for (int i = 0; i < listData.size(); i++) {
            if (listData.get(i).getIsSelect())
                listF.add(listData.get(i));
        }
        long deleteSize = 0;
        for (int i = 0; i < listF.size(); i++) {
            deleteSize += listF.get(i).getFileSize();
        }
        tv_delete.setText(deleteSize == 0 ? "删除" : "删除 " + CleanAllFileScanUtil.byte2FitSizeOne(deleteSize));
    }

    //删除成功
    public void deleteSuccess(List<CleanWxItemInfo> listF) {
        tv_delete.setSelected(false);
        tv_delete.setText("删除");
        fileAdapter.deleteData(listF);
//        line_none.setVisibility(imageAdapter.getListImage().size() == 0 ? View.VISIBLE : View.GONE);
//        recycle_view.setVisibility(imageAdapter.getListImage().size() == 0 ? View.GONE : View.VISIBLE);
    }

    @Override
    public void netError() {

    }
}
