package com.xiaoniu.cleanking.ui.main.presenter;

import com.google.gson.Gson;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.xiaoniu.cleanking.api.UserApiService;
import com.xiaoniu.cleanking.base.BaseEntity;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.ui.main.activity.QuestionReportActivity;
import com.xiaoniu.cleanking.ui.main.bean.FileUploadInfoBean;
import com.xiaoniu.cleanking.ui.main.model.QuestionReportMode;
import com.xiaoniu.cleanking.utils.net.Common4Subscriber;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by lang.chen on 2019/7/6
 */
public class QuestionReportPresenter extends RxPresenter<QuestionReportActivity, QuestionReportMode> {

    private RxAppCompatActivity mActivity;
    @Inject
    UserApiService mService;

    @Inject
    public QuestionReportPresenter(RxAppCompatActivity activity) {
        this.mActivity = activity;
    }


    //提交意见反馈
    public void submitData(String uid, String feedbackContent, String contactType, String feedbackPic
            , Common4Subscriber<BaseEntity> commonSubscriber) {
        Gson gson = new Gson();
        Map<String, Object> map = new HashMap<>();
        map.put("uid", uid);
        map.put("feedbackContent", feedbackContent);
        map.put("contactType", contactType);
        map.put("feedbackPic", feedbackPic);
        String json = gson.toJson(map);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        mModel.submitData(uid, feedbackContent, contactType, feedbackPic, commonSubscriber);
    }


    public void uploadFile(File file, Common4Subscriber<FileUploadInfoBean> commonSubscriber) {
        mModel.uploadFile(file, commonSubscriber);

    }

}
