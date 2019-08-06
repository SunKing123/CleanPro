package com.xiaoniu.cleanking.ui.tool.qq.presenter;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.ui.main.model.MainModel;
import com.xiaoniu.cleanking.ui.main.presenter.ImageListPresenter;
import com.xiaoniu.cleanking.ui.tool.qq.activity.QQCleanFileActivity;
import com.xiaoniu.cleanking.ui.tool.qq.bean.CleanWxClearInfo;
import com.xiaoniu.cleanking.utils.prefs.NoClearSPHelper;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by z on 2017/5/15.
 */
public class QQCleanFilePresenter extends RxPresenter<QQCleanFileActivity, MainModel> {

    private final RxAppCompatActivity mActivity;
    @Inject
    NoClearSPHelper mSPHelper;


    @Inject
    public QQCleanFilePresenter(RxAppCompatActivity activity) {
        mActivity = activity;
    }

    public AlertDialog alertBanLiveDialog(Context context, int deleteNum, final ImageListPresenter.ClickListener okListener) {
        final AlertDialog dlg = new AlertDialog.Builder(context).create();
        if (((Activity) context).isFinishing()) {
            return dlg;
        }
        dlg.show();
        Window window = dlg.getWindow();
        window.setContentView(R.layout.alite_redp_send_dialog);
        WindowManager.LayoutParams lp = dlg.getWindow().getAttributes();
        //这里设置居中
        lp.gravity = Gravity.CENTER;
        window.setAttributes(lp);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView btnOk = (TextView) window.findViewById(R.id.btnOk);

        TextView btnCancle = (TextView) window.findViewById(R.id.btnCancle);
        TextView tipTxt = (TextView) window.findViewById(R.id.tipTxt);
        TextView content = (TextView) window.findViewById(R.id.content);
        content.setText("永久从设备中删除这些文件，删除后将不可恢复。");
        tipTxt.setText("确定删除这" + deleteNum + "个文件？");
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
                okListener.clickOKBtn();
            }
        });
        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
                okListener.cancelBtn();
            }
        });
        return dlg;
    }

    //删除本地文件
    public void delFile(List<CleanWxClearInfo> list, List<CleanWxClearInfo> list1, List<CleanWxClearInfo> list2, List<CleanWxClearInfo> list3, List<CleanWxClearInfo> list4) {
        mView.showLoadingDialog();
        List<CleanWxClearInfo> files = list;
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {

                for (CleanWxClearInfo appInfoBean : files) {
                    File file = new File(appInfoBean.getFilePath());
                    if (null != file) {
                        file.delete();
                    }
                }
                emitter.onNext("");
                emitter.onComplete();
            }

        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(String value) {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                        mView.cancelLoadingDialog();
                        mView.deleteSuccess(list1, list2, list3, list4);
                    }
                });

    }
}