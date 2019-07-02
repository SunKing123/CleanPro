package com.xiaoniu.cleanking.utils.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.xiaoniu.cleanking.utils.CleanAllFileScanUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileTableManager {

    private static long time1 = 0;

    public static boolean insertBySql(Context context, List<File> list) {
        time1 = System.currentTimeMillis();
        deleteTable(context);
        if (null == list || list.size() <= 0) {
            return false;
        }
        try {
            String sql = "insert into " + FileTabSQL.FILE_MANAGER_TABLE_NAME + "("
                    + "type,"// 包名
                    + "size,"// 账号
                    + "time,"// 传输状态
                    + "path) " + "values(?,?,?,?)";
            SQLiteStatement stat = FileDBManager.getDBconnection(context).compileStatement(sql);
            FileDBManager.getDBconnection(context).beginTransaction();
            for (File remoteAppInfo : list) {
                stat.bindString(1, getFileType(remoteAppInfo.getAbsolutePath()));
                stat.bindString(2, remoteAppInfo.length() + "");
                stat.bindString(3, remoteAppInfo.lastModified() + "");
                stat.bindString(4, remoteAppInfo.getAbsolutePath());
                long result = stat.executeInsert();
                if (result < 0) {
                    return false;
                }
            }
            FileDBManager.getDBconnection(context).setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (null != FileDBManager.getDBconnection(context)) {
                    FileDBManager.getDBconnection(context).endTransaction();
                    FileDBManager.dbClose();
                }
                long time2 = System.currentTimeMillis() - time1;
                Log.e("ewq", "插入耗时：" + time2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 判断文件类型
     *
     * @param filePath
     * @return
     */
    public static String getFileType(String filePath) {
        String type = FileDBManager.TYPE_IMAGE;
        if (Arrays.asList(CleanAllFileScanUtil.videoFormat).contains(filePath.substring(filePath.lastIndexOf('.'), filePath.length()))) {
            type = FileDBManager.TYPE_VIDEO;
        } else if (Arrays.asList(CleanAllFileScanUtil.apkFormat).contains(filePath.substring(filePath.lastIndexOf('.'), filePath.length()))) {
            type = FileDBManager.TYPE_APK;
        } else if (Arrays.asList(CleanAllFileScanUtil.musicFormat).contains(filePath.substring(filePath.lastIndexOf('.'), filePath.length()))) {
            type = FileDBManager.TYPE_MUSIC;
        } else {
            type = FileDBManager.TYPE_IMAGE;
        }
        return type;
    }

    // 添加扫描出来的文件入库
    public static void addFile(Context context, String type, String size, String time, String path) {
        ContentValues values = new ContentValues();
        values.put("type", type);
        values.put("size", size);
        values.put("time", time);
        values.put("path", path);
        FileDBManager.getDBconnection(context).insert(FileTabSQL.FILE_MANAGER_TABLE_NAME, null, values);
        FileDBManager.dbClose();
    }


    /**
     * @param context
     * @param path    根据文件路径删除
     */
    public static void deleteByBaseNo(Context context, String path) {
        FileDBManager.getDBconnection(context).delete(FileTabSQL.FILE_MANAGER_TABLE_NAME, "path=?", new String[]{path});
        FileDBManager.dbClose();
    }

    /**
     * @param context 删除表所有内容
     */
    public static void deleteTable(Context context) {
        FileDBManager.getDBconnection(context).delete(FileTabSQL.FILE_MANAGER_TABLE_NAME, null, null);
        FileDBManager.dbClose();
    }

    // 查询消息封装成list
    public static List<Map<String, String>> queryAllFiles(Context context) {
        List<Map<String, String>> fileList = new ArrayList<Map<String, String>>();
        Cursor cursor = getAllFilesCursor(context);
        if (cursor == null) {
            return fileList;
        } else {
            if (cursor.moveToFirst() == true) {
                for (int index = 0; index < cursor.getCount(); index++) {

                    Map<String, String> map = new HashMap<String, String>();
                    String type = cursor.getString(cursor
                            .getColumnIndex("type"));
                    String size = cursor.getString(cursor
                            .getColumnIndex("size"));
                    String time = cursor.getString(cursor
                            .getColumnIndex("time"));
                    String path = cursor.getString(cursor
                            .getColumnIndex("path"));
                    map.put("type", type);
                    map.put("size", size);
                    map.put("time", time);
                    map.put("path", path);
                    fileList.add(map);
                    cursor.moveToNext();
                }
                cursor.close();
            }
            return fileList;
        }
    }


    // 查询所有消息
    public static Cursor getAllFilesCursor(Context context) {
        try {
            return FileDBManager.getDBconnection(context).query(FileTabSQL.FILE_MANAGER_TABLE_NAME,
                    new String[]{"type", "size", "time", "path"}, null,
                    null, null, null, null);
        } catch (Exception e) {

            return null;
        }
    }
}

