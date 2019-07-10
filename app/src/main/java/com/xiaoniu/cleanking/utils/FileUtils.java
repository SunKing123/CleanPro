package com.xiaoniu.cleanking.utils;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppApplication;

import java.io.File;

public class FileUtils {

    public static boolean isAppInstalled(String str) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = AppApplication.getInstance().getPackageManager().getPackageInfo(str, 0);
        } catch (PackageManager.NameNotFoundException e) {
        }
        if (packageInfo != null) {
            return true;
        }else {
            return false;
        }
    }

    public static boolean haveSDCard() {
        try {
            return Environment.getExternalStorageState().equals("mounted");
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isSystemApK(String str) {
        try {
            if ((AppApplication.getInstance().getPackageManager().getPackageInfo(str, 0).applicationInfo.flags & 1) > 0) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    public static void deleteFileAndFolder(File file) {
        if (file != null && file.exists()) {
            if (file.isDirectory()) {
                File[] listFiles = file.listFiles();
                if (listFiles != null && listFiles.length > 0) {
                    for (File file2 : listFiles) {
                        if (file2 != null) {
                            if (file2.isDirectory()) {
                                deleteFileAndFolder(file2);
                            } else {
                                forceDelete(file2);
                            }
                        }
                    }
                }
            }
            forceDelete(file);
        }
    }

    public static void forceDelete(File file) {
        if (file.exists()) {
            file.delete();
        }
    }

    public static void showIconByFile(ImageView imageView, File file) {
        if (imageView != null && file != null) {
            if (!file.isFile()) {
                imageView.setTag(null);
                imageView.setImageResource(R.drawable.clean_icon_folder);
            } else if (file.getAbsolutePath().endsWith(".apk") || file.getAbsolutePath().endsWith(".apk.1")) {
                DisplayUtils.getInstance().displayImage(file.getAbsolutePath(), imageView);
            } else if (file.getAbsolutePath().toLowerCase().endsWith(".mp3") || file.getAbsolutePath().toLowerCase().endsWith(".ape") || file.getAbsolutePath().toLowerCase().endsWith(".flac") || file.getAbsolutePath().toLowerCase().endsWith(".wav") || file.getAbsolutePath().toLowerCase().endsWith(".wma") || file.getAbsolutePath().toLowerCase().endsWith(".amr") || file.getAbsolutePath().toLowerCase().endsWith(".rm") || file.getAbsolutePath().toLowerCase().endsWith(".mwv") || file.getAbsolutePath().toLowerCase().endsWith(".amv")) {
                imageView.setImageResource(R.drawable.clean_icon_music);
            } else if (file.getAbsolutePath().toLowerCase().endsWith(".doc") || file.getAbsolutePath().toLowerCase().endsWith(".docx")) {
                imageView.setImageResource(R.drawable.clean_icon_doc);
            } else if (file.getAbsolutePath().toLowerCase().endsWith(".pdf")) {
                imageView.setImageResource(R.drawable.clean_icon_pdf);
            } else if (file.getAbsolutePath().toLowerCase().endsWith(".ppt") || file.getAbsolutePath().toLowerCase().endsWith(".pptx")) {
                imageView.setImageResource(R.drawable.clean_icon_ppt);
            } else if (file.getAbsolutePath().toLowerCase().endsWith(".xls") || file.getAbsolutePath().toLowerCase().endsWith(".xlsx")) {
                imageView.setImageResource(R.drawable.clean_icon_xls);
            } else if (file.getAbsolutePath().toLowerCase().endsWith(".txt") || file.getAbsolutePath().toLowerCase().endsWith(".text")) {
                imageView.setImageResource(R.drawable.clean_icon_txt);
            } else if (file.getAbsolutePath().toLowerCase().endsWith(".zip") || file.getAbsolutePath().toLowerCase().endsWith(".rar") || file.getAbsolutePath().toLowerCase().endsWith(".7z") || file.getAbsolutePath().toLowerCase().endsWith(".iso")) {
                imageView.setImageResource(R.drawable.clean_icon_zip);
            } else if (file.getAbsolutePath().toLowerCase().endsWith(".png") || file.getAbsolutePath().toLowerCase().endsWith(".jpg") || file.getAbsolutePath().toLowerCase().endsWith(".jpeg") || file.getAbsolutePath().toLowerCase().endsWith(".gif") || file.getAbsolutePath().toLowerCase().endsWith(".svg") || file.getAbsolutePath().toLowerCase().endsWith(".psd") || file.getAbsolutePath().toLowerCase().endsWith(".raw") || file.getAbsolutePath().toLowerCase().endsWith(".webp") || file.getAbsolutePath().toLowerCase().endsWith(".bmp") || file.getAbsolutePath().toLowerCase().endsWith(".tiff") || file.getAbsolutePath().toLowerCase().endsWith(".tga") || file.getAbsolutePath().toLowerCase().endsWith(".wmf")) {
                imageView.setTag(null);
                Glide.with(AppApplication.getInstance()).load("file://" + file.getAbsolutePath()).apply(new RequestOptions().dontAnimate().placeholder(R.drawable.clean_icon_pic).error(R.drawable.clean_icon_pic)).into(imageView);

            } else {
                imageView.setTag(null);
                Glide.with(AppApplication.getInstance()).load("file://" + file.getAbsolutePath()).apply(new RequestOptions().dontAnimate().placeholder(R.drawable.clean_icon_others).error(R.drawable.clean_icon_others)).into(imageView);
            }
        }
    }

}
