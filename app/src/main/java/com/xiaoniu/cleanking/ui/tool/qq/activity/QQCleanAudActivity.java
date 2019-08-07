package com.xiaoniu.cleanking.ui.tool.qq.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.tool.qq.adapter.QQCleanAudAdapter;
import com.xiaoniu.cleanking.ui.tool.qq.bean.CleanWxClearInfo;
import com.xiaoniu.cleanking.ui.tool.qq.presenter.QQCleanAudPresenter;
import com.xiaoniu.cleanking.ui.tool.qq.util.QQUtil;
import com.xiaoniu.cleanking.utils.CleanAllFileScanUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * QQ清理语音
 */
public class QQCleanAudActivity extends BaseActivity<QQCleanAudPresenter> {
    @BindView(R.id.recycle_view)
    RecyclerView recycle_view;
    @BindView(R.id.cb_checkall)
    TextView cb_checkall;
    @BindView(R.id.tv_delete)
    TextView tv_delete;
    List<CleanWxClearInfo> listData = new ArrayList<>();
    QQCleanAudAdapter audAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_qqclean_aud;
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
            finish();
        }
    }

    @Override
    public void initView() {

        listData = QQUtil.audioList;

        audAdapter = new QQCleanAudAdapter(QQCleanAudActivity.this, listData);
        recycle_view.setLayoutManager(new LinearLayoutManager(QQCleanAudActivity.this));
        recycle_view.setAdapter(audAdapter);
        audAdapter.setmOnCheckListener(new QQCleanAudAdapter.onCheckListener() {
            @Override
            public void onCheck(List<CleanWxClearInfo> listFile, int pos) {
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
                if (!recycle_view.isComputingLayout()) {
                    cb_checkall.setSelected(!cb_checkall.isSelected());
                    tv_delete.setSelected(cb_checkall.isSelected());
                    audAdapter.setIsCheckAll(cb_checkall.isSelected() ? true : false);
                    cb_checkall.setBackgroundResource(cb_checkall.isSelected() ? R.drawable.icon_select : R.drawable.icon_unselect);
                    tv_delete.setBackgroundResource(cb_checkall.isSelected() ? R.drawable.delete_select_bg : R.drawable.delete_unselect_bg);
                    compulateDeleteSize();
                }
            }
        });
        compulateDeleteSize();

    }

    //计算删除文件大小
    public void compulateDeleteSize() {
        List<CleanWxClearInfo> listF = new ArrayList<>();
        List<CleanWxClearInfo> listData = audAdapter.getListImage();
        for (int i = 0; i < listData.size(); i++) {
            if (listData.get(i).getIsSelect())
                listF.add(listData.get(i));
        }
        long deleteSize = 0;
        for (int i = 0; i < listF.size(); i++) {
            deleteSize += listF.get(i).getSize();
        }
        tv_delete.setText(deleteSize == 0 ? "未选中" : "确认选中 " + CleanAllFileScanUtil.byte2FitSizeOne(deleteSize));
        tv_delete.setBackgroundResource(deleteSize != 0 ? R.drawable.delete_select_bg : R.drawable.delete_unselect_bg);
        cb_checkall.setBackgroundResource(listData.size() == listF.size() ? R.drawable.icon_select : R.drawable.icon_unselect);
    }

    //删除成功
    public void deleteSuccess(List<CleanWxClearInfo> listF) {
        tv_delete.setSelected(false);
        tv_delete.setText("未选中");
        audAdapter.deleteData(listF);
//        line_none.setVisibility(imageAdapter.getListImage().size() == 0 ? View.VISIBLE : View.GONE);
//        recycle_view.setVisibility(imageAdapter.getListImage().size() == 0 ? View.GONE : View.VISIBLE);
    }

    @Override
    public void netError() {

    }
}
