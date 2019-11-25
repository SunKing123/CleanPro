package com.xiaoniu.cleanking.ui.main.bean.weatherdao;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;


import org.greenrobot.greendao.database.Database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;



public class UpgradeSQLiteOpenHelper extends DaoMaster.OpenHelper{
    private static final String TAG = "UpgradeSQLiteOpenHelper";
    private Context mContext;

    //add a constructor
    public UpgradeSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
        mContext = context;
    }

    //implement onUpgrade method
    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
//        LogUtils.w("dkk", "===>>>> onUpgrade newVersion = " + newVersion + " oldVersion = " + oldVersion);
        if(newVersion > oldVersion){
            switch (oldVersion){
                case 1:

                    Observable.create(new ObservableOnSubscribe<Boolean>() {
                        @Override
                        public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                            InputStream inputStream = null;
                            InputStreamReader inputStreamReader = null;
                            BufferedReader reader = null;
                            try {
                                db.beginTransaction();

                                //1、插入增加的数据 开始
                                inputStream = mContext.getAssets().open("WeatherUpgradeVersion2UpdateSQL.txt");
                                inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                                reader = new BufferedReader(inputStreamReader);
                                String lineInsertSql;

                                while ((lineInsertSql = reader.readLine()) != null) {
                                    if(!TextUtils.isEmpty(lineInsertSql)){
                                        db.execSQL(lineInsertSql);
                                    }
                                }
                                //1、插入增加的数据 结束

                                //2、更新数据 结束

                                db.setTransactionSuccessful();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                db.endTransaction();

                                try {
                                    if(inputStream != null){
                                        inputStream.close();
                                    }
                                    if(inputStreamReader != null){
                                        inputStreamReader.close();
                                    }
                                    if(reader != null){
                                        reader.close();
                                    }
                                } catch (IOException e) {

                                    e.printStackTrace();
                                }

                            }
                        }
                    }).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) throws Exception {

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {

                        }
                    });

//                    break; //不要break，做连续升级
                case 2: // 新增加两个字段，日出日落 2019.9.5 by dkk
                    db.beginTransaction();
                    String sunRiseTime = "ALTER TABLE ATTENTION_CITY_WEATHER_ENTITY ADD COLUMN sunRiseTime TEXT";
                    String sunSetTime = "ALTER TABLE ATTENTION_CITY_WEATHER_ENTITY ADD COLUMN sunSetTime TEXT";
                    db.execSQL(sunRiseTime);
                    db.execSQL(sunSetTime);
                    db.setTransactionSuccessful();
                    db.endTransaction();
                    break;
            }
        }
        /*
        MigrationHelper.migrate(db, new MigrationHelper.ReCreateAllTableListener() {

            @Override
            public void onCreateAllTables(Database db, boolean ifNotExists) {
                DaoMaster.createAllTables(db, ifNotExists);
            }

            @Override
            public void onDropAllTables(Database db, boolean ifExists) {
                DaoMaster.dropAllTables(db, ifExists);
            }
        }, AttentionCityWeatherEntityDao.class, WeatherCityDao.class); //目前只升级WeatherCity表
        */
    }

    private void insertUpgradeVersion2Data(){

    }

}
