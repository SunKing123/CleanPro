package com.xiaoniu.cleanking.ui.main.model;

import com.google.gson.Gson;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.umeng.socialize.media.Base;
import com.xiaoniu.cleanking.api.UserApiService;
import com.xiaoniu.cleanking.base.BaseEntity;
import com.xiaoniu.cleanking.base.BaseModel;
import com.xiaoniu.cleanking.ui.main.bean.FileUploadInfoBean;
import com.xiaoniu.cleanking.utils.net.Common4Subscriber;
import com.xiaoniu.cleanking.utils.net.RxUtil;

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
public class QuestionReportMode extends BaseModel {


    private final RxAppCompatActivity mActivity;

    @Inject
    UserApiService mService;

    @Inject
    public QuestionReportMode(RxAppCompatActivity activity) {
        mActivity = activity;
    }

    //提交意见反馈
    public void submitData(String uid, String feedbackContent, String contactType, String feedbackPic
    ,Common4Subscriber<BaseEntity> commonSubscriber) {
        Gson gson = new Gson();
        Map<String, Object> map = new HashMap<>();
        map.put("uid", uid);
        map.put("feedbackContent", feedbackContent);
        map.put("contactType", contactType);
        map.put("feedbackPic", feedbackPic);
        String json = gson.toJson(map);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);


        mService.submitQuestionReport(body).compose(RxUtil.<BaseEntity>rxSchedulerHelper(mActivity))
                .subscribeWith(commonSubscriber);
    }


    public void uploadFile(File file, Common4Subscriber<FileUploadInfoBean> commonSubscriber){
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData(
                "file", file.getName(), requestFile);

        mService.uploadFile(body).compose(RxUtil.<FileUploadInfoBean>rxSchedulerHelper(mActivity))
                .subscribeWith(commonSubscriber);

    }

}
