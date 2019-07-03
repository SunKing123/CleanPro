package com.xiaoniu.cleanking.utils.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FileDBHelper extends SQLiteOpenHelper {

    public FileDBHelper(Context context) {
        super(context, "xn_db", null, 1);
    }

    //	//创建表
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(FileTabSQL.FILE_MANAGER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }


}
