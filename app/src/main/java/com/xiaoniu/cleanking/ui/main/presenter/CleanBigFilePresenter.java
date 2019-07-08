package com.xiaoniu.cleanking.ui.main.presenter;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.ui.main.activity.CleanBigFileActivity;
import com.xiaoniu.cleanking.ui.main.bean.BigFileInfoEntity;
import com.xiaoniu.cleanking.ui.main.bean.FilePathInfoClean;
import com.xiaoniu.cleanking.ui.main.model.CleanBigFileModel;
import com.xiaoniu.cleanking.utils.FileQueryUtils;
import com.xiaoniu.cleanking.utils.db.CleanDBManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

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
                mView.showList((List<BigFileInfoEntity>)o);
            }else {
                mView.showTotal((long) o);
            }
        });
    }
}
