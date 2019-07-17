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
import com.xiaoniu.cleanking.ui.main.bean.CountEntity;
import com.xiaoniu.cleanking.ui.main.bean.FirstLevelEntity;
import com.xiaoniu.cleanking.ui.main.bean.SecondLevelEntity;
import com.xiaoniu.cleanking.ui.main.bean.ThirdLevelEntity;
import com.xiaoniu.cleanking.ui.main.model.CleanBigFileModel;
import com.xiaoniu.cleanking.utils.CleanUtil;
import com.xiaoniu.cleanking.utils.net.RxUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;

public class CleanBigFilePresenter extends RxPresenter<CleanBigFileActivity, CleanBigFileModel> {

    private SecondLevelEntity mSecondLevelEntity;
    private SecondLevelEntity mMusicList;
    private SecondLevelEntity mImageList;
    private SecondLevelEntity mZipList;
    private SecondLevelEntity mTextList;
    private SecondLevelEntity mApkList;
    private List<SecondLevelEntity> mLists = new ArrayList<>();
    private long mTotal;

    @Inject
    public CleanBigFilePresenter() {
    }

    /**
     * 大文件扫描
     */
    @SuppressLint("CheckResult")
    public void scanBigFile() {

        initList();

        Observable.create(e -> {
            try {
                Cursor query = AppApplication.getInstance().getContentResolver().query(Uri.parse("content://media/external/file"), new String[]{"mime_type", "_data", "_size"}, "_size>=?", new String[]{"10485760"}, "_size");
                long total = 0;
                if (query != null) {
                    int columnIndex = query.getColumnIndex("_data");
                    query.getColumnIndex("_size");
                    int mimeTypeColumn = query.getColumnIndex("mime_type");
                    if (query.moveToFirst()) {
                        while (true) {
                            File file = new File(query.getString(columnIndex));
                            String mimeType = query.getString(mimeTypeColumn);
                            System.out.println(file.getName() + "===================:" + mimeType);
                            if (file.exists()) {
                                long length = file.length();
                                if (length < 10485760) {
                                    break;
                                }
                                ThirdLevelEntity cleanFileManagerInfo = new ThirdLevelEntity();
                                cleanFileManagerInfo.setFile(file);
                                if (!TextUtils.isEmpty(mimeType)){
                                    if (mimeType.contains("video")) {
                                        //视频
                                        cleanFileManagerInfo.setContent("视频");
                                        mSecondLevelEntity.addSubItem(cleanFileManagerInfo);
                                        mSecondLevelEntity.addSize(length);
                                        total += length;
                                    } else if (mimeType.contains("audio")) {
                                        //音乐
                                        cleanFileManagerInfo.setContent("音乐");
                                        mMusicList.addSubItem(cleanFileManagerInfo);
                                        mMusicList.addSize(length);
                                        total += length;
                                    } else if (mimeType.contains("application/rar") || mimeType.contains("application/zip") || mimeType.contains("application/x-tar")) {
                                        //压缩包
                                        cleanFileManagerInfo.setContent("压缩包");
                                        mZipList.addSubItem(cleanFileManagerInfo);
                                        mZipList.addSize(length);
                                        total += length;
                                    } else if (mimeType.contains("image")) {
                                        //图片
                                        cleanFileManagerInfo.setContent("图片");
                                        mImageList.addSubItem(cleanFileManagerInfo);
                                        mImageList.addSize(length);
                                        total += length;
                                    } else if (mimeType.contains("text") || mimeType.contains("application/msword") || mimeType.contains("application/vnd.ms-powerpoint") || mimeType.contains("application/pdf")) {
                                        //文档
                                        cleanFileManagerInfo.setContent("文档");
                                        mTextList.addSubItem(cleanFileManagerInfo);
                                        mTextList.addSize(length);
                                        total += length;
                                    }
                                }
                            }
                            if (!query.moveToNext()) {
                                break;
                            }
                        }
                    }
                    query.close();

//                    List<FilePathInfoClean> filePathInfoCleans = CleanDBManager.queryAllFilePathInfo();
//                    if (filePathInfoCleans == null) {
//                        e.onNext(total);
//                        e.onNext(list);
//                        return;
//                    }
//                    for (FilePathInfoClean filePathInfoClean : filePathInfoCleans) {
//                        if (!TextUtils.isEmpty(filePathInfoClean.getRootPath())) {
//                            filePathInfoClean.setRootPath(FileQueryUtils.getFilePath(filePathInfoClean.getRootPath()));
//                        }
//                    }
//                    if (list.size() > 0) {
//                        for (ThirdLevelEntity cleanFileManagerInfo : list) {
//                            Iterator it = filePathInfoCleans.iterator();
//                            while (true) {
//                                if (!it.hasNext()) {
//                                    break;
//                                }
//                                FilePathInfoClean filePathInfoClean2 = (FilePathInfoClean) it.next();
//                                if (!TextUtils.isEmpty(filePathInfoClean2.getRootPath()) && cleanFileManagerInfo.getFile().getAbsolutePath().contains(filePathInfoClean2.getRootPath())) {
//                                    cleanFileManagerInfo.setContent("来自" + filePathInfoClean2.getAppName());
//                                    break;
//                                }
//                            }
//                            if (TextUtils.isEmpty(cleanFileManagerInfo.getContent())) {
//                                cleanFileManagerInfo.setContent("来自/" + cleanFileManagerInfo.getFile().getParentFile().getName());
//                            }
//                        }
//                    }

                    List<SecondLevelEntity> realData = new ArrayList<>();
                    for (SecondLevelEntity entity : mLists) {
                        if (entity.getSubItems() != null && entity.getSubItems().size() > 0) {
                            realData.add(entity);
                        }
                    }
                    e.onNext(total);
                    e.onNext(realData);
                }

            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }).compose(RxUtil.rxObservableSchedulerHelper(mView))
                .subscribe(o -> {
                    FirstLevelEntity firstLevelEntity = new FirstLevelEntity();
                    if (o instanceof List) {
                        firstLevelEntity.setSubItems((List<SecondLevelEntity>) o);
                        firstLevelEntity.setTotal(mTotal);
                        mView.showList(firstLevelEntity);
                    } else {
                        mTotal = (long) o;
                        mView.showTotal(mTotal);
                    }
                });
    }


    private void initList() {
        mSecondLevelEntity = new SecondLevelEntity();
        mSecondLevelEntity.setName("下载的视频");
        mSecondLevelEntity.setType(SecondLevelEntity.TYPE_VIDEO);

        //音乐
        mMusicList = new SecondLevelEntity();
        mMusicList.setName("下载的音乐");
        mMusicList.setType(SecondLevelEntity.TYPE_MUSIC);

        //压缩包
        mZipList = new SecondLevelEntity();
        mZipList.setName("下载的压缩包");
        mZipList.setType(SecondLevelEntity.TYPE_ZIP);
        //图片
        mImageList = new SecondLevelEntity();
        mImageList.setName("下载的图片");
        mImageList.setType(SecondLevelEntity.TYPE_IMAGE);
        //文档
        mTextList = new SecondLevelEntity();
        mTextList.setName("下载的办公文件");
        mTextList.setType(SecondLevelEntity.TYPE_WORD);
        //APK
        mApkList = new SecondLevelEntity();
        mApkList.setName("下载的安装包");
        mApkList.setType(SecondLevelEntity.TYPE_APK);

        mLists.add(mSecondLevelEntity);
        mLists.add(mMusicList);
        mLists.add(mZipList);
        mLists.add(mImageList);
        mLists.add(mTextList);
        mLists.add(mApkList);
    }

    /**
     * 执行清理操作
     *
     * @param data
     */
    @SuppressLint("CheckResult")
    public void deleteFiles(List<ThirdLevelEntity> data, long total) {
        Flowable.create(e -> {
            for (ThirdLevelEntity entity : data) {
                if (entity.isChecked()) {
                    entity.getFile().delete();
                }
            }
            e.onNext("finish");
        }, BackpressureStrategy.BUFFER).compose(RxUtil.rxSchedulerHelper(mView)).subscribe(o -> {
            mView.cleanFinish(total);
        });
    }

    /**
     * 确认删除弹窗
     *
     * @param data
     */
    public void showDeleteDialog(List<ThirdLevelEntity> data) {

        long total = 0;
        int count = 0;
        for (ThirdLevelEntity entity : data) {
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

        CountEntity countEntity = CleanUtil.formatShortFileSize(total);
        //大小
        mTextTtitle.setText("将清理" +countEntity.getResultSize()  + "文件");
        //路径
        mTextTips.setText("此次清理包含" + count + "个文件，清谨慎清理");

        mTextCancel.setOnClickListener(v -> dialog.dismiss());
        long finalTotal = total;
        mTextConfirm.setOnClickListener(v -> {
            dialog.dismiss();
            mView.startCleanAnim(countEntity);
            //设置titlebar颜色
            mView.showBarColor(mView.getResources().getColor(R.color.color_FD6F46));
            deleteFiles(data, finalTotal);
        });
        dialog.setContentView(view);
        if (dialog.isShowing()) {
            return;
        }
        dialog.show();
    }
}
