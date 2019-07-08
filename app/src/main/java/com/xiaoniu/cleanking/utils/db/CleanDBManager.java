package com.xiaoniu.cleanking.utils.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.ui.main.bean.AppInfoClean;
import com.xiaoniu.cleanking.ui.main.bean.FilePathInfoClean;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据存储操作管理类
 *
 */
public class CleanDBManager {
	
	public static SQLiteDatabase db = null;

	private static final String TABLE_CLEAN_APP_INFO = "com_shyz_clean_entity_AppInfoClean";
	private static final String TABLE_CLEAN_INFO_PATH = "com_shyz_clean_entity_FilePathInfoClean";

	/**
	 * 得到一个可写的数据库实例
	 * @return
	 */
	public static SQLiteDatabase getDBconnection(){
		if(db == null){
			CleanSqlHelper dbHelper = new CleanSqlHelper(AppApplication.getInstance());
			db = dbHelper.getWritableDatabase();
		}
		return db;
	}

	// 查询消息封装成list
	public static List<AppInfoClean> queryInfoList() {
		List<AppInfoClean> fileList = new ArrayList<>();
		Cursor cursor = null;
		try {
			cursor =  CleanDBManager.getDBconnection().query(TABLE_CLEAN_APP_INFO,
					new String[]{"id", "appName", "packageName"}, null,
					null, null, null, null);
		} catch (Exception e) {

			return null;
		}
		try {
			if (cursor == null) {
				return fileList;
			} else {
				if (cursor.moveToFirst()) {
					for (int index = 0; index < cursor.getCount(); index++) {
						AppInfoClean appInfoClean = new AppInfoClean();
						appInfoClean.setId(cursor.getLong(cursor
								.getColumnIndex("id")));
						appInfoClean.setAppName(cursor.getString(cursor
								.getColumnIndex("appName")));
						appInfoClean.setPackageName(cursor.getString(cursor
								.getColumnIndex("packageName")));
						fileList.add(appInfoClean);
						cursor.moveToNext();
					}
					cursor.close();
				}
				return fileList;
			}
		} catch (Exception e) {
			return fileList;
		}finally {
			CleanDBManager.dbClose();
		}
	}

	public static List<FilePathInfoClean> queryFilePathInfo(String path) {
		List<FilePathInfoClean> fileList = new ArrayList<>();
		Cursor cursor = null;
		try {
			cursor =  CleanDBManager.getDBconnection().query(TABLE_CLEAN_INFO_PATH,
					new String[]{"id", "appName", "filePath","garbageName","garbagetype","packageName","rootPath"}, "packageName=?",
					new String[]{path}, null, null, null);
		} catch (Exception e) {

			return null;
		}
		try {
			if (cursor == null) {
				return fileList;
			} else {
				if (cursor.moveToFirst()) {
					for (int index = 0; index < cursor.getCount(); index++) {
						FilePathInfoClean appInfoClean = new FilePathInfoClean();
						appInfoClean.setId(cursor.getLong(cursor
								.getColumnIndex("id")));
						appInfoClean.setAppName(cursor.getString(cursor
								.getColumnIndex("appName")));
						appInfoClean.setPackageName(cursor.getString(cursor
								.getColumnIndex("packageName")));
						appInfoClean.setFilePath(cursor.getString(cursor
								.getColumnIndex("filePath")));
						appInfoClean.setGarbageName(cursor.getString(cursor
								.getColumnIndex("garbageName")));
						appInfoClean.setGarbagetype(cursor.getString(cursor
								.getColumnIndex("garbagetype")));
						appInfoClean.setRootPath(cursor.getString(cursor
								.getColumnIndex("rootPath")));
						fileList.add(appInfoClean);
						cursor.moveToNext();
					}
					cursor.close();
				}
				return fileList;
			}
		} catch (Exception e) {
			return fileList;
		}finally {
			CleanDBManager.dbClose();
		}
	}

	public static List<FilePathInfoClean> queryAllFilePathInfo() {
		List<FilePathInfoClean> fileList = new ArrayList<>();
		Cursor cursor = null;
		try {
			cursor =  CleanDBManager.getDBconnection().query(TABLE_CLEAN_INFO_PATH,
					new String[]{"id", "appName", "filePath","garbageName","garbagetype","packageName","rootPath"}, null,
					null, null, null, null);
		} catch (Exception e) {

			return null;
		}
		try {
			if (cursor == null) {
				return fileList;
			} else {
				if (cursor.moveToFirst()) {
					for (int index = 0; index < cursor.getCount(); index++) {
						FilePathInfoClean appInfoClean = new FilePathInfoClean();
						appInfoClean.setId(cursor.getLong(cursor
								.getColumnIndex("id")));
						appInfoClean.setAppName(cursor.getString(cursor
								.getColumnIndex("appName")));
						appInfoClean.setPackageName(cursor.getString(cursor
								.getColumnIndex("packageName")));
						appInfoClean.setFilePath(cursor.getString(cursor
								.getColumnIndex("filePath")));
						appInfoClean.setGarbageName(cursor.getString(cursor
								.getColumnIndex("garbageName")));
						appInfoClean.setGarbagetype(cursor.getString(cursor
								.getColumnIndex("garbagetype")));
						appInfoClean.setRootPath(cursor.getString(cursor
								.getColumnIndex("rootPath")));
						fileList.add(appInfoClean);
						cursor.moveToNext();
					}
					cursor.close();
				}
				return fileList;
			}
		} catch (Exception e) {
			return fileList;
		}finally {
			CleanDBManager.dbClose();
		}
	}
	
	/***
	 * 关闭数据库
	 */
	public static void dbClose(){
		if(db != null && db.isOpen()){
			db.close();
			db = null;
		}
	}
	
}
