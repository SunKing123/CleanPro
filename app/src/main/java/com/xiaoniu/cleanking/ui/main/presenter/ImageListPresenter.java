package com.xiaoniu.cleanking.ui.main.presenter;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.ui.main.activity.ImageActivity;
import com.xiaoniu.cleanking.ui.main.adapter.ImageShowAdapter;
import com.xiaoniu.cleanking.ui.main.bean.FileEntity;
import com.xiaoniu.cleanking.ui.main.model.MainModel;
import com.xiaoniu.cleanking.utils.prefs.NoClearSPHelper;
import com.xiaoniu.common.utils.ToastUtils;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
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
//        Observable.create(new ObservableOnSubscribe<List<Map<String, String>>>() {
//            @Override
//            public void subscribe(ObservableEmitter<List<Map<String, String>>> e) throws Exception {
//                e.onNext(FileTableManager.queryAllFiles(AppApplication.getInstance()));
//            }
//        }).subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<List<Map<String, String>>>() {
//                    @Override
//                    public void accept(List<Map<String, String>> strings) throws Exception {
//                        mView.scanSdcardResult(strings);
//                    }
//                });
    }

    public interface ClickListener {
        void clickOKBtn();

        void cancelBtn();
    }

    public AlertDialog alertBanLiveDialog(Context context, int deleteNum, final ClickListener okListener) {
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
        tipTxt.setText("确定删除这" + deleteNum + "张图片？");
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
                e.onNext("");
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String strings) throws Exception {
                        mView.cancelLoadingDialog();
                        mView.deleteSuccess(listF);
                        ToastUtils.showShort("删除成功");
                    }
                });
    }

    //删除本地文件
    public void delFile(List<FileEntity> list) {
        mView.showLoadingDialog();
        List<FileEntity> files = list;
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {

                for (FileEntity appInfoBean : files) {
                    AppApplication.getInstance().getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,MediaStore.Audio.Media.DATA+"= \""+appInfoBean.getPath()+"\"",null);
                    File file = new File(appInfoBean.path);
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
                    }
                });

    }
}
