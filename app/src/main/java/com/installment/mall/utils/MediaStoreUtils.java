package com.installment.mall.utils;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.installment.mall.app.AppApplication;

import java.io.File;
import java.io.FileOutputStream;

/**
 * 图片存储工具类
 */
public class MediaStoreUtils {

    public static String getCapturePathFromCamera(Context context, Intent data) {
        if (context == null || data == null) {
            return null;
        }
        Uri uri = data.getData();
        String filePath = null;

        filePath = getCapturePathFromPicture(context, uri);
        if (TextUtils.isEmpty(filePath)) {
            filePath = bgCameraStatusForRootPath(uri);
        }
        if (TextUtils.isEmpty(filePath)) {
            filePath = bgCameraStatusForSDPath(uri);
        }
        if (TextUtils.isEmpty(filePath)) {
            filePath = bgCameraStatus(data);
        }
        return filePath;
    }

    private static String bgCameraStatusForRootPath(Uri uri) {
        String filePath = null;
        if (uri != null) {
            filePath = uri.toString();
            if (!TextUtils.isEmpty(filePath)) {
                filePath = filePath.replaceAll("file://", "");
            }
        }
        return filePath;
    }

    private static String bgCameraStatusForSDPath(Uri uri) {
        String filePath = null;
        if (uri != null) {
            filePath = uri.toString();
            if (!TextUtils.isEmpty(filePath)) {
                filePath = filePath.replaceAll("file://", "/mnt");
            }
        }
        return filePath;
    }

    private static String bgCameraStatus(Intent data) {
        Bundle bundle = data.getExtras();
        if (bundle != null) {
            try {
                Bitmap photo = (Bitmap) bundle.get("data");
                String filePath = getSDcardPath() + "AndroidForum/camera/" + System.currentTimeMillis() + ".jpg";
                if (photo != null) {
                    saveBitmap(photo, filePath);
                    photo.recycle();
                    return filePath;
                }
            } catch (Exception e) {
            }
        }
        return null;
    }

    public static void saveBitmap(Bitmap bmp, String path) {
        File file = new File(path);
        try {
            file.mkdirs();
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            FileOutputStream fOut = null;
            fOut = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getSDcardPath() {
        File sdCardDir = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // 获取跟目录
            sdCardDir = Environment.getExternalStorageDirectory();
        }
        String path = ((sdCardDir == null) ? AppApplication.getInstance().getFilesDir().getAbsolutePath() : sdCardDir.toString());
        return path + File.separator;
    }

    public static String getCapturePathFromPicture(final Context context, final Uri uri) {
        if (uri == null) {
            return null;
        }
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider5
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri)) {
                return uri.getLastPathSegment();
            }

            return getDataColumn(uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for MediaStore Uris, and other file-based ContentProviders.
     *
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor =   AppApplication.getInstance().getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}
