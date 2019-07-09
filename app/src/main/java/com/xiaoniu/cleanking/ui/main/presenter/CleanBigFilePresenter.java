package com.xiaoniu.cleanking.ui.main.presenter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.ui.main.activity.CleanBigFileActivity;
import com.xiaoniu.cleanking.ui.main.bean.BigFileInfoEntity;
import com.xiaoniu.cleanking.ui.main.bean.FilePathInfoClean;
import com.xiaoniu.cleanking.ui.main.model.CleanBigFileModel;
import com.xiaoniu.cleanking.utils.CleanUtil;
import com.xiaoniu.cleanking.utils.FileQueryUtils;
import com.xiaoniu.cleanking.utils.db.CleanDBManager;
import com.xiaoniu.cleanking.utils.net.RxUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CleanBigFilePresenter extends RxPresenter<CleanBigFileActivity, CleanBigFileModel> {

    @Inject
    public CleanBigFilePresenter() {
    }

    /**
     * 大文件扫描
     */
    @SuppressLint("CheckResult")
    public void scanBigFile() {
        Observable.create(e -> {
            try {
                Cursor query = AppApplication.getInstance().getContentResolver().query(Uri.parse("content://media/external/file"), new String[]{"_data", "_size"}, "_size>=?", new String[]{"10485760"}, "_size");
                long total = 0;
                List<BigFileInfoEntity> list = new ArrayList<>();
                if (query != null) {
                    int columnIndex = query.getColumnIndex("_data");
                    query.getColumnIndex("_size");
                    if (query.moveToFirst()) {
                        while (true) {
                            File file = new File(query.getString(columnIndex));
                            if (file.exists()) {
                                long length = file.length();
                                if (length < 10485760) {
                                    break;
                                }
                                total += length;
                                BigFileInfoEntity cleanFileManagerInfo = new BigFileInfoEntity();
                                cleanFileManagerInfo.setFile(file);
                                list.add(cleanFileManagerInfo);
                            }
                            if (!query.moveToNext()) {
                                break;
                            }
                        }
                    }
                    query.close();

                    List<FilePathInfoClean> filePathInfoCleans = CleanDBManager.queryAllFilePathInfo();
                    if (filePathInfoCleans == null) {
                        e.onNext(total);
                        e.onNext(list);
                        return;
                    }
                    for (FilePathInfoClean filePathInfoClean : filePathInfoCleans) {
                        if (!TextUtils.isEmpty(filePathInfoClean.getRootPath())) {
                            filePathInfoClean.setRootPath(FileQueryUtils.getFilePath(filePathInfoClean.getRootPath()));
                        }
                    }
                    if (list.size() > 0) {
                        for (BigFileInfoEntity cleanFileManagerInfo : list) {
                            Iterator it = filePathInfoCleans.iterator();
                            while (true) {
                                if (!it.hasNext()) {
                                    break;
                                }
                                FilePathInfoClean filePathInfoClean2 = (FilePathInfoClean) it.next();
                                if (!TextUtils.isEmpty(filePathInfoClean2.getRootPath()) && cleanFileManagerInfo.getFile().getAbsolutePath().contains(filePathInfoClean2.getRootPath())) {
                                    cleanFileManagerInfo.setContent("来自" + filePathInfoClean2.getAppName());
                                    break;
                                }
                            }
                            if (TextUtils.isEmpty(cleanFileManagerInfo.getContent())) {
                                cleanFileManagerInfo.setContent("来自/" + cleanFileManagerInfo.getFile().getParentFile().getName());
                            }
                        }
                    }
                }

                e.onNext(total);
                e.onNext(list);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    if (o instanceof List) {
                        mView.showList((List<BigFileInfoEntity>) o);
                    } else {
                        mView.showTotal((long) o);
                    }
                });
    }

    /**
     * 执行清理操作
     *
     * @param data
     */
    @SuppressLint("CheckResult")
    public void deleteFiles(List<BigFileInfoEntity> data, long total) {
        Flowable.create(e -> {
            for (BigFileInfoEntity entity : data) {
                if (entity.isChecked()) {
                    entity.getFile().delete();
                }
            }
            e.onNext("finish");
        }, BackpressureStrategy.BUFFER).compose(RxUtil.rxSchedulerHelper()).subscribe(o -> {
            mView.cleanFinish(total);
        });
    }

    /**
     * 确认删除弹窗
     *
     * @param data
     */
    public void showDeleteDialog(List<BigFileInfoEntity> data) {

        long total = 0;
        int count = 0;
        for (BigFileInfoEntity entity : data) {
            if (entity.isChecked()) {
                total += entity.getFile().length();
                count++;
            }
        }
        //提示对话框
        final Dialog dialog = new Dialog(mView, R.style.custom_dialog);
        View view = LayoutInflater.from(mView).inflate(R.layout.dialog_delete_confirm, null);

        TextView mTextTips = view.findViewById(R.id.text_tips);
        TextView mTextTtitle = view.findViewById(R.id.text_title);
        TextView mTextConfirm = view.findViewById(R.id.text_confirm);
        TextView mTextCancel = view.findViewById(R.id.text_cancel);

        //大小
        mTextTtitle.setText("将清理" + CleanUtil.formatShortFileSize(mView, total) + "文件");
        //路径
        mTextTips.setText("此次清理包含" + count + "个文件，清谨慎清理");

        mTextCancel.setOnClickListener(v -> dialog.dismiss());
        long finalTotal = total;
        mTextConfirm.setOnClickListener(v -> {
            dialog.dismiss();
            deleteFiles(data, finalTotal);
        });
        dialog.setContentView(view);
        if (dialog.isShowing()) {
            return;
        }
        dialog.show();
    }
}
