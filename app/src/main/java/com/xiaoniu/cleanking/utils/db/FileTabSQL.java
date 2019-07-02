package com.xiaoniu.cleanking.utils.db;

/**
 * 创建表SQL类
 *
 * @author Administrator
 */
public class FileTabSQL {
    //文件管理，扫描出的文件入库
    public static final String FILE_MANAGER_TABLE_NAME = "file_manager_table";
    /**
     * 扫描出的文件入库
     */
    public static final String FILE_MANAGER_TABLE = "create table " + FILE_MANAGER_TABLE_NAME + "("
            + "_id INTEGER PRIMARY KEY AUTOINCREMENT," // 主键
            + "type TEXT  NOT NULL," // 文件类型
            + "size TEXT  NOT NULL," // 文件大小
            + "time TEXT  NOT NULL," // 文件修改时间
            + "path TEXT  NOT NULL" // 文件路径
            + ")"; // 时间
}
