package com.xiaoniu.cleanking.utils.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * 数据存储操作管理类
 *
 */
public class FileDBManager {
	
	public static SQLiteDatabase db = null;

	public static String TYPE_IMAGE = "TYPE_IMAGE";
	public static String TYPE_VIDEO = "TYPE_VIDEO";
	public static String TYPE_APK = "TYPE_APK";
	public static String TYPE_MUSIC = "TYPE_MUSIC";

	
	/**
	 * 得到一个可写的数据库实例
	 * @param context
	 * @return
	 */
	public static SQLiteDatabase getDBconnection(Context context){
		if(db == null){
			 
			FileDBHelper dbHelper = new FileDBHelper(context);
			db = dbHelper.getWritableDatabase();
			 
		}
		return db;
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
