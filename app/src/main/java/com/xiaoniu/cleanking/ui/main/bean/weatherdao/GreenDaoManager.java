package com.xiaoniu.cleanking.ui.main.bean.weatherdao;

import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import com.xiaoniu.cleanking.constant.Constant;
import com.xiaoniu.cleanking.bean.AppPackageNameListDB;
import com.xiaoniu.cleanking.bean.weatherdao.AppPackageNameListDBDao;
import com.xiaoniu.cleanking.bean.weatherdao.DaoMaster;
import com.xiaoniu.cleanking.bean.weatherdao.DaoSession;
import com.xiaoniu.cleanking.bean.weatherdao.WeatherCityDao;
import com.xiaoniu.cleanking.utils.CollectionUtils;
import com.xiaoniu.common.utils.ContextUtils;

import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author xiangzhenbiao
 * @since 2019/4/11 20:13
 */
public class GreenDaoManager {
    private static final String TAG = "GreenDaoManager";

    //外部数据库saa.db
    private DaoMaster mWeatherCityDaoMaster;
    private DaoSession mWeatherCityDaoSession;
    private static GreenDaoManager mInstance;

    //获取单例
    public static GreenDaoManager getInstance() {
        if (mInstance == null) {
            synchronized (GreenDaoManager.class) {
                if (mInstance == null) {
                    mInstance = new GreenDaoManager();
                }
            }
        }
        return mInstance;
    }

    private GreenDaoManager() {
        if (mInstance == null) {

            //数据库升级
            DaoMaster.OpenHelper openHelper = new UpgradeSQLiteOpenHelper(ContextUtils.getApplication(), Constant.WEATHER_DB_NAME, null);
            mWeatherCityDaoMaster = new DaoMaster(openHelper.getWritableDatabase());
            mWeatherCityDaoSession = mWeatherCityDaoMaster.newSession();

//            attentionCityWeatherDatabase = MainApp.getContext().openOrCreateDatabase(GlobalConstant.attentionCityWeather_db_name, Context.MODE_PRIVATE, null);
//            DaoMaster.DevOpenHelper attentionCityWeatherHelper = new DaoMaster.DevOpenHelper(MainApp.getContext(),GlobalConstant.attentionCityWeather_db_name , null);
//            mAttentionCityWeatherDaoMaster = new DaoMaster(attentionCityWeatherHelper.getWritableDatabase());
//            mAttentionCityWeatherDaoMaster = new DaoMaster(openHelper.getWritableDatabase());
//            mAttentionCityWeatherDaoSession = mAttentionCityWeatherDaoMaster.newSession();
//            mAttentionCityWeatherEntityDao = mAttentionCityWeatherDaoSession.getAttentionCityWeatherEntityDao();
        }
    }



    /********************************* WeatherCity数据库查询************************************/
    /**
     * 重置定位状态
     *
     * @param isPositioning
     */
    public void resetSelectState(int isPositioning) {
        final String SQL_Update_SelectState = "UPDATE " + Constant.CLEAN_TABLE_NAME
                + " SET " + WeatherCityDao.Properties.IsPositioning.columnName + "= ?";
        try {
            mWeatherCityDaoSession.getDatabase().execSQL(SQL_Update_SelectState, new Object[isPositioning]);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    /**
     * 更新定位城市，查询失败返回null
     *
     * @param gaodeLocationCity
     * @return
     */
    public WeatherCity updateTableLocation(LocationCityInfo gaodeLocationCity) {
        WeatherCity locationCity = null;
        if (gaodeLocationCity != null && !TextUtils.isEmpty(gaodeLocationCity.getProvince()) && !TextUtils.isEmpty(gaodeLocationCity.getDistrict())) {
//            WeatherCity locationCity = queryLocationCity(locationCityInfo);
            try {
                List<WeatherCity> weatherCityList = mWeatherCityDaoSession.getWeatherCityDao().loadAll();
                if (weatherCityList != null && !weatherCityList.isEmpty()) {
                    for (int i = 0; i < weatherCityList.size(); i++) {
                        WeatherCity weatherCity = weatherCityList.get(i);
                        //数据库表里面数据是简写 Province:"湖北"，City:"武汉"，District:"武昌",而高德定位返回的是全称 Province:“湖北省”,City:“武汉市”，District:“武昌区”
                        //数据库表里面数据是简写 Province:"上海"，City:"浦东"，District:"浦东",而高德定位返回的是全称 Province:“上海市”,City:“上海市”，District:“浦东新区”
                        if (weatherCity != null) {
                            //                        Log.d(TAG, "updateTableLocation->weatherCity: " + weatherCity.toString());
                            //所以这里只根据Province和District做模糊搜索
                            if (gaodeLocationCity.getProvince().contains(weatherCity.getProvince())
                                    && gaodeLocationCity.getDistrict().contains(weatherCity.getDistrict())) {
                                Log.d(TAG, "updateTableLocation定位更新了: " + weatherCity.getProvince() + "," + weatherCity.getDistrict());
                                weatherCity.setIsPositioning(1);
                                //TODO 定位后更新地理位置 经纬度
                                //                            G.look("gaodeLocationCity.getLatitude()="+gaodeLocationCity.getLatitude());
                                //                            weatherCity.setLatitude(gaodeLocationCity.getLatitude());
                                //                            weatherCity.setLongitude(gaodeLocationCity.getLongitude());

                                locationCity = weatherCity;
                            } else {
                                //                            weatherCity.setIsPositioning(0);
                            }
                        }
                    }
                    //                LogUtils.d(TAG, "updateTableLocation()->currentThread1:" + Thread.currentThread().getName());
                    if (locationCity != null) {
                        Log.d(TAG, "updateTableLocation定位locationCity: " + locationCity.toString());
                        /*WeatherCity oldPositionCity = getPositionCity();
                        if(oldPositionCity != null){
                            //如果存在旧的定位城市，重置
                            if(oldPositionCity == locationCity){
                                //新定位的城市跟旧的是同一个，不需要更新
                                return locationCity;
                            }else{
                                oldPositionCity.setIsPositioning(0);
                                mWeatherCityDaoSession.getWeatherCityDao().update(oldPositionCity);
                            }
                        }*/
                        resetSelectState(0);//将其它城市旧的状态重置为0
                        mWeatherCityDaoSession.getWeatherCityDao().update(locationCity);
                    } else {
                        Log.d(TAG, "updateTableLocation定位locationCity空: ");
                    }
                } else {

                }
            } catch (Exception e) {

                e.printStackTrace();
            }
        }
        return locationCity;
    }


    /**
     * 根据包名查询全部记录数据
     */
    public List<AppPackageNameListDB> queryAppList(String packageName) {
        AppPackageNameListDBDao dao = mWeatherCityDaoSession.getAppPackageNameListDBDao();
        QueryBuilder queryBuilder = dao.queryBuilder();
        queryBuilder.where(AppPackageNameListDBDao.Properties.PackageName.eq(packageName));
        List<AppPackageNameListDB> categoryListDBS = queryBuilder.list();
        return categoryListDBS;
    }


    /**
     * 数据库是否为空
     */
    public boolean isAppListNull() {
        AppPackageNameListDBDao dao = mWeatherCityDaoSession.getAppPackageNameListDBDao();
        if (dao.loadAll() != null && dao.loadAll().size() > 0) {
            return false;
        }
        return true;
    }


    /**
     * 新增（更新）的记录数据表
     */
    public void updateAppList(AppPackageNameListDB appPackageNameList) {
        if (!TextUtils.isEmpty(appPackageNameList.getPackageName()) && !TextUtils.isEmpty(appPackageNameList.getName())) {
            AppPackageNameListDBDao dao = mWeatherCityDaoSession.getAppPackageNameListDBDao();
            dao.insertOrReplaceInTx(appPackageNameList);
        }
    }

}
