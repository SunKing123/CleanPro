package com.xiaoniu.cleanking.ui.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.adapter.QuestionReportImgAdapter;
import com.xiaoniu.cleanking.ui.main.bean.ImgBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 问题反馈
 * Created by lang.chen on 2019/7/5
 */
public class QuestionReportActivity extends BaseActivity implements QuestionReportImgAdapter.OnItemImgClickListener {


    //照片选择
    public static final int CODE_IMG_SELECT = 0X2100;
    //图片上传大小
    public static final int IMG_MAX_SIZE=3;
    @BindView(R.id.recycle_view_img)
    RecyclerView mRecyclerView;

    @BindView(R.id.txt_content)
    EditText mTxtContent;
    @BindView(R.id.txt_length)
    TextView mTxtLength;

    @BindView(R.id.btn_submit)
    Button mBtnSumbit;

    private QuestionReportImgAdapter mAdapter;

    @Override
    public void inject(ActivityComponent activityComponent) {

    }

    @Override
    public void netError() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_question_report;
    }

    @Override
    protected void initView() {

        mAdapter = new QuestionReportImgAdapter(getBaseContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        //调整RecyclerView的排列方向
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mTxtContent.addTextChangedListener(textWatcherContent);

        //
        ImgBean imgBean = new ImgBean();
        imgBean.itemType = 1;
        List<ImgBean> lists = new ArrayList<>();
        lists.add(imgBean);
        mAdapter.modifyData(lists);
        mAdapter.setOnItemImgClickListener(this);
    }

    TextWatcher textWatcherContent = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (null != mTxtContent && null != mTxtLength) {
                int length = mTxtContent.getText().toString().length();
                mTxtLength.setText(String.format("%s/200", String.valueOf(length)));

                if (length > 0) {
                    mBtnSumbit.setSelected(true);
                } else {
                    mBtnSumbit.setSelected(false);
                }
            }

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_IMG_SELECT && data != null) {
            Bundle bundle = data.getExtras();

            //更新照片信息
            String path = bundle.getString("img_path","");
            List<ImgBean> lists =mAdapter.getLists();
            ImgBean imgBean = new ImgBean();
            imgBean.path=path;
            lists.add(imgBean);


            //如果图片大于3，移除第一张， +1是头部
            if(mAdapter.getLists().size()>(IMG_MAX_SIZE+1)){
                lists.remove(1);
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    @OnClick({R.id.img_back})
    public void onClickView(View view) {
        int ids = view.getId();
        if (ids == R.id.img_back) {
            finish();
        }
    }

    @Override
    public void onSelectImg() {
        //跳转到照片选择页面
        startActivityForResult(new Intent(mContext, SimplePhotoActivity.class), CODE_IMG_SELECT);
    }

    @Override
    public void onDelImg(int position) {
        List<ImgBean> lists = mAdapter.getLists();
        lists.remove(position);
        mAdapter.notifyDataSetChanged();
    }
}
