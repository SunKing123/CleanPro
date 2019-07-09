package com.xiaoniu.cleanking.ui.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.base.BaseEntity;
import com.xiaoniu.cleanking.ui.main.adapter.QuestionReportImgAdapter;
import com.xiaoniu.cleanking.ui.main.bean.FileUploadInfoBean;
import com.xiaoniu.cleanking.ui.main.bean.ImgBean;
import com.xiaoniu.cleanking.ui.main.presenter.QuestionReportPresenter;
import com.xiaoniu.cleanking.utils.net.Common4Subscriber;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 问题反馈
 * Created by lang.chen on 2019/7/5
 */
public class QuestionReportActivity extends BaseActivity<QuestionReportPresenter> implements QuestionReportImgAdapter.OnItemImgClickListener {


    private static final String TAG = "QuestionReportActivity";
    //照片选择
    public static final int CODE_IMG_SELECT = 0X2100;
    //图片上传大小
    public static final int IMG_MAX_SIZE = 3;
    @BindView(R.id.recycle_view_img)
    RecyclerView mRecyclerView;

    @BindView(R.id.txt_content)
    EditText mTxtContent;
    @BindView(R.id.txt_length)
    TextView mTxtLength;

    @BindView(R.id.btn_submit)
    Button mBtnSumbit;
    //图片大小
    @BindView(R.id.txt_img_lenth)
    TextView mTxtImgSize;
    @BindView(R.id.txt_contact)
    EditText mTxtContact;


    private QuestionReportImgAdapter mAdapter;

    @Override
    public void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
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
            String path = bundle.getString("img_path", "");
            List<ImgBean> lists = mAdapter.getLists();
            ImgBean imgBean = new ImgBean();
            imgBean.path = path;
            lists.add(imgBean);


            //如果图片大于3，移除第一张， +1是头部{
            //                lists.remove(1);
            //            }
            if (mAdapter.getLists().size() > (IMG_MAX_SIZE + 1))
            mAdapter.notifyDataSetChanged();
            mTxtImgSize.setText(String.format("%s/3", mAdapter.getLists().size() - 1));
        }
    }

    @OnClick({R.id.img_back, R.id.btn_submit})
    public void onClickView(View view) {
        int ids = view.getId();
        if (ids == R.id.img_back) {
            finish();
        } else if (ids == R.id.btn_submit) {// 提交反馈


            List<ImgBean> datas = mAdapter.getLists();
            //过滤头部
            List<String> paths=new ArrayList<>();
            for(ImgBean imgBean : datas){
                if(!TextUtils.isEmpty(imgBean.path)){
                    paths.add(imgBean.path);
                }
            }
            if (paths.size() > 0) {
                for (String path : paths) {
                    uploadFile(path);
                }
            } else {
                String content = mTxtContent.getText().toString();
                String contact = mTxtContact.getText().toString();
                mPresenter.submitData("", content, contact, "", common4Subscriber);
            }
        }
    }

    private List<String> mUploadUrls = new ArrayList<>();

    private void uploadFile(String path) {
        mPresenter.uploadFile(new File(path), new Common4Subscriber<FileUploadInfoBean>() {
            @Override
            public void showExtraOp(String code, String message) {

            }

            @Override
            public void getData(FileUploadInfoBean fileUploadInfoBean) {
                FileUploadInfoBean.ImgUrl imgUrl=fileUploadInfoBean.data;
                mUploadUrls.add(imgUrl.url);
                Log.i(TAG, "successful");
                // -1 移除头部
                if (mUploadUrls.size() == mAdapter.getLists().size() - 1) {

                    String content = mTxtContent.getText().toString();
                    String contact = mTxtContact.getText().toString();
                    StringBuilder stringPaths=new StringBuilder();
                    for(int i=0;i<mUploadUrls.size();i++){
                        String path=mUploadUrls.get(i);
                        stringPaths.append(path);
                        //不是最后一个添加分隔符;
                        if(mUploadUrls.size()!=1 &&  i!=mUploadUrls.size()-1){
                            stringPaths.append(";");
                        }
                    }
                    mPresenter.submitData("", content, contact, stringPaths.toString(), common4Subscriber);
                }
            }


            @Override
            public void showExtraOp(String message) {

            }

            @Override
            public void netConnectError() {
                Toast.makeText(mContext, "网络异常，请稍后重试", Toast.LENGTH_SHORT).show();
            }
        });
    }

    Common4Subscriber<BaseEntity> common4Subscriber = new Common4Subscriber<BaseEntity>() {
        @Override
        public void showExtraOp(String code, String message) {

        }

        @Override
        public void getData(BaseEntity baseEntity) {
            Toast.makeText(mContext, "上传成功", Toast.LENGTH_SHORT).show();
            finish();
        }

        @Override
        public void showExtraOp(String message) {
            Toast.makeText(mContext, "上传失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void netConnectError() {
            Toast.makeText(mContext, "网络异常，请稍后重试", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onSelectImg() {
        //跳转到照片选择页面
        startActivityForResult(new Intent(mContext, SimplePhotoActivity.class), CODE_IMG_SELECT);
    }

    @Override
    public void onDelImg(int position) {
        List<ImgBean> lists = mAdapter.getLists();
        if (lists.size() > 0 && position <= lists.size() - 1) {
            lists.remove(position);
            mAdapter.notifyDataSetChanged();
        }
    }
}
