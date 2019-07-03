package com.xiaoniu.cleanking.ui.main.presenter;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.ui.main.activity.ImageActivity;
import com.xiaoniu.cleanking.ui.main.adapter.ImageShowAdapter;
import com.xiaoniu.cleanking.ui.main.bean.FileEntity;
import com.xiaoniu.cleanking.ui.main.model.MainModel;
import com.xiaoniu.cleanking.utils.ToastUtils;
import com.xiaoniu.cleanking.utils.db.FileTableManager;
import com.xiaoniu.cleanking.utils.prefs.NoClearSPHelper;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by tie on 2017/5/15.
 */
public class ImageListPresenter extends RxPresenter<ImageActivity, MainModel> {

    private final RxAppCompatActivity mActivity;
    @Inject
    NoClearSPHelper mSPHelper;


    @Inject
    public ImageListPresenter(RxAppCompatActivity activity) {
        mActivity = activity;
    }

    public void getSdcardFiles() {
        Observable.create(new ObservableOnSubscribe<List<Map<String, String>>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Map<String, String>>> e) throws Exception {
                e.onNext(FileTableManager.queryAllFiles(AppApplication.getInstance()));
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Map<String, String>>>() {
                    @Override
                    public void accept(List<Map<String, String>> strings) throws Exception {
                        mView.scanSdcardResult(strings);
                    }
                });
    }

    public interface ClickListener {
        public void clickOKBtn();

        public void cancelBtn();
    }

    public AlertDialog alertBanLiveDialog(Context context, int deleteNum,final ClickListener okListener) {
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
        Button btnOk = (Button) window.findViewById(R.id.btnOk);

        Button btnCancle = (Button) window.findViewById(R.id.btnCancle);
        TextView tipTxt = (TextView) window.findViewById(R.id.tipTxt);
        tipTxt.setText("确定删除这"+deleteNum+"张图片？");
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

    //删除数据库中的相应图片
    public void deleteFromDatabase(List<FileEntity> listF, ImageShowAdapter imageAdapter) {
        String[] strPaths = new String[listF.size()];
        for (int i = 0; i < listF.size(); i++) {
            strPaths[i] = listF.get(i).getPath();
        }
        mView.showLoadingDialog();
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                //删除数据库
                FileTableManager.deleteByPath(AppApplication.getInstance(), strPaths);
                e.onNext("");
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String strings) throws Exception {
                        mView.cancelLoadingDialog();
                        mView.deleteSuccess(listF);
                        ToastUtils.show("删除成功");
                    }
                });
    }
}
