package com.xiaoniu.cleanking.ui.main.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.base.BaseEntity;
import com.xiaoniu.cleanking.ui.main.adapter.QuestionReportImgAdapter;
import com.xiaoniu.cleanking.ui.main.bean.FileUploadInfoBean;
import com.xiaoniu.cleanking.ui.main.bean.ImgBean;
import com.xiaoniu.cleanking.ui.main.fragment.dialog.QuestionReportLoadingDialogFragment;
import com.xiaoniu.cleanking.ui.main.presenter.QuestionReportPresenter;
import com.xiaoniu.cleanking.utils.StatisticsUtils;
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

    private QuestionReportLoadingDialogFragment mLoading;

    //是否已经提交过，用来区分loading
    private boolean mIsSubmit;


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
        mTxtContact.addTextChangedListener(textWatcherContact);
        //
        ImgBean imgBean = new ImgBean();
        imgBean.itemType = 1;
        List<ImgBean> lists = new ArrayList<>();
        lists.add(imgBean);
        mAdapter.modifyData(lists);
        mAdapter.setOnItemImgClickListener(this);

        //
        mLoading = QuestionReportLoadingDialogFragment.newInstance();
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
            String content = mTxtContent.getText().toString();
            String contact = mTxtContact.getText().toString();
            if (null != mTxtContent && null != mTxtLength) {
                int length = mTxtContent.getText().toString().length();

                    if (length > 200) {
                        mTxtContent.removeTextChangedListener(textWatcherContent);
                        SpannableString spannableString = new SpannableString(content);
                        //设置字体背景色
                        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#FF3838")), 200
                                , mTxtContent.length() , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        mTxtContent.getText().clear();
                        mTxtContent.append(spannableString);
                        mTxtContent.setSelection(content.length());

                        mTxtContent.addTextChangedListener(textWatcherContent);
                    }

                mTxtLength.setText(String.format("%s/200", String.valueOf(length)));

                if (!TextUtils.isEmpty(contact) && !TextUtils.isEmpty(content) && content.length() <= 200) {
                    mBtnSumbit.setSelected(true);
                    mBtnSumbit.setEnabled(true);
                } else {
                    mBtnSumbit.setSelected(false);
                    mBtnSumbit.setEnabled(false);
                }

            }

        }
    };



    TextWatcher textWatcherContact = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            String content = mTxtContent.getText().toString();
            String contact = mTxtContact.getText().toString();
            if (!TextUtils.isEmpty(contact) && !TextUtils.isEmpty(content) && content.length() <= 200) {
                mBtnSumbit.setSelected(true);
                mBtnSumbit.setEnabled(true);
            } else {
                mBtnSumbit.setSelected(false);
                mBtnSumbit.setEnabled(false);
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
            lists.add(0, imgBean);


            //如果图片大于3，移除第一张， +1是头部{
            if (mAdapter.getLists().size() > IMG_MAX_SIZE + 1) {
                lists.remove(IMG_MAX_SIZE - 1);
            }
            mAdapter.notifyDataSetChanged();
            mTxtImgSize.setText(String.format("%s/3", mAdapter.getLists().size() - 1));
        }
    }

    private  Toast mToastContactHint;
    @OnClick({R.id.img_back, R.id.btn_submit})
    public void onClickView(View view) {
        int ids = view.getId();
        if (ids == R.id.img_back) {
            finish();
        } else if (ids == R.id.btn_submit) {// 提交反馈
            String content = mTxtContent.getText().toString();
            String contact = mTxtContact.getText().toString();
            if(TextUtils.isEmpty(contact) || contact.length()<5 || contact.length()>15){
                if(mToastContactHint==null){
                    mToastContactHint=Toast.makeText(mContext,"填写的联系方式有误",Toast.LENGTH_SHORT);
                    mToastContactHint.setGravity(Gravity.CENTER,0,0);
                }else {
                    mToastContactHint.show();
                }
                return;
            }
            StatisticsUtils.trackClick("Submission_click","\"提交\"点击","personal_center_page","question_feedback_page");

            mLoading.show(getSupportFragmentManager(), "");

            if (mIsSubmit) {
                mLoading.setReportSuccess(0);
            }
            List<ImgBean> datas = mAdapter.getLists();
            //过滤头部
            List<String> paths = new ArrayList<>();
            for (ImgBean imgBean : datas) {
                if (!TextUtils.isEmpty(imgBean.path)) {
                    paths.add(imgBean.path);
                }
            }
            if (paths.size() > 0) {
                for (String path : paths) {
                    uploadFile(path);
                }
            } else {

                mPresenter.submitData("", content, contact, "", new Common4Subscriber<BaseEntity>() {
                    @Override
                    public void showExtraOp(String code, String message) {

                    }

                    @Override
                    public void getData(BaseEntity baseEntity) {
                        mLoading.setReportSuccess(1);
                        mBtnSumbit.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 1000);


                    }

                    @Override
                    public void showExtraOp(String message) {
                        mLoading.dismissAllowingStateLoss();
                    }

                    @Override
                    public void netConnectError() {
                        mLoading.dismissAllowingStateLoss();
                        Toast toast = Toast.makeText(mContext, "网络异常，请稍后重试", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();

                    }
                });
            }

            mIsSubmit = true;
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
                FileUploadInfoBean.ImgUrl imgUrl = fileUploadInfoBean.data;
                mUploadUrls.add(imgUrl.url);
                // -1  去除尾部
                if (mUploadUrls.size() == mAdapter.getLists().size() - 1) {

                    String content = mTxtContent.getText().toString();
                    String contact = mTxtContact.getText().toString();
                    StringBuilder stringPaths = new StringBuilder();
                    for (int i = 0; i < mUploadUrls.size(); i++) {
                        String path = mUploadUrls.get(i);
                        stringPaths.append(path);
                        //不是最后一个添加分隔符;
                        if (mUploadUrls.size() != 1 && i != mUploadUrls.size() - 1) {
                            stringPaths.append(";");
                        }
                    }
                    mPresenter.submitData("", content, contact, stringPaths.toString(), new Common4Subscriber<BaseEntity>() {
                        @Override
                        public void showExtraOp(String code, String message) {

                        }

                        @Override
                        public void getData(BaseEntity baseEntity) {
                            mLoading.setReportSuccess(1);
                            mBtnSumbit.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finish();
                                }
                            }, 1000);


                        }

                        @Override
                        public void showExtraOp(String message) {
                            mLoading.dismissAllowingStateLoss();
                        }

                        @Override
                        public void netConnectError() {
                            mLoading.dismissAllowingStateLoss();
                            Toast toast = Toast.makeText(mContext, "网络异常，请稍后重试", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();

                        }
                    });
                }
            }


            @Override
            public void showExtraOp(String message) {

            }

            @Override
            public void netConnectError() {
                mLoading.dismissAllowingStateLoss();
                Toast toast = Toast.makeText(mContext, "网络异常，请稍后重试", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
    }



/*    Common4Subscriber<BaseEntity> common4Subscriber = new Common4Subscriber<BaseEntity>() {
        @Override
        public void showExtraOp(String code, String message) {

        }

        @Override
        public void getData(BaseEntity baseEntity) {
            mLoading.setReportSuccess(1);
            mBtnSumbit.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 1000);


        }

        @Override
        public void showExtraOp(String message) {
            mLoading.dismissAllowingStateLoss();
        }

        @Override
        public void netConnectError() {
            mLoading.dismissAllowingStateLoss();
            Toast toast = Toast.makeText(mContext, "网络异常，请稍后重试", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();

        }
    };*/

    @Override
    protected void onResume() {
        super.onResume();
        StatisticsUtils.trackClick("question_feedback_view_page","\"问题反馈\"浏览","personal_center_page","question_feedback_page");
    }

    @Override
    public void onSelectImg() {
        StatisticsUtils.trackClick("Upload_photos_click","\"上传照片\"点击","personal_center_page","question_feedback_page");

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
        mTxtImgSize.setText(String.format("%s/3", mAdapter.getLists().size() - 1));
    }
}
