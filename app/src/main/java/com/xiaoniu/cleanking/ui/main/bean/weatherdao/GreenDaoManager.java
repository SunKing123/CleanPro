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
//    private SQLiteDatabase weatherCityDatabase;

//    private DaoMaster mAttentionCityWeatherDaoMaster;
//    private AttentionCityWeatherEntityDao mAttentionCityWeatherEntityDao;
//    private DaoSession mAttentionCityWeatherDaoSession;
//    private SQLiteDatabase attentionCityWeatherDatabase;

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

    /********************************* AttentionCityWeather数据库查询************************************/


    /********************************* WeatherCity数据库查询************************************/
    /**
     * 根据cityType查询省份,很多重复的数据
     *
     * @param cityType 0:国内，1：国外
     * @return
     */
    public List<WeatherCity> getWeatherCountryByCityType(int cityType) {
        return mWeatherCityDaoSession.getWeatherCityDao().queryBuilder().where(WeatherCityDao.Properties.CityType.eq(cityType)).list();
    }

    /**
     * 查询定位城市
     *
     * @return
     */
    public WeatherCity queryPositionWeatherCity() {
        WeatherCity locationWeatherCity = null;
        List<WeatherCity> attentionCityWeatherEntities = mWeatherCityDaoSession.getWeatherCityDao().queryBuilder().where(
                WeatherCityDao.Properties.IsPositioning.eq(1)).list();
        if (attentionCityWeatherEntities != null && !attentionCityWeatherEntities.isEmpty()) {
            locationWeatherCity = attentionCityWeatherEntities.get(0);
        }
        return locationWeatherCity;
    }

    /**
     * 按照国家条件查询，通过
     *
     * @param countryName
     * @return
     */
    public List<WeatherCity> selectWeatherCityByCountryName(String countryName) {
//        countryName = "中国";
        return mWeatherCityDaoSession.getWeatherCityDao().queryBuilder().where(WeatherCityDao.Properties.Country.eq(countryName)).list();
    }

    /**
     * 按照国家模糊查询
     *
     * @return
     */
    public List<WeatherCity> queryWeatherCityByCountryName(String countryName) {
        return mWeatherCityDaoSession.getWeatherCityDao().queryBuilder().where(WeatherCityDao.Properties.Country.like("%" + countryName + "%")).list();
    }


    /**
     * 获取国内所有不同的省份信息
     * cityType 0:国内
     *
     * @return
     */
    public List<WeatherCity> getHomeProvinceNameByCityType(int cityType) {
        List<WeatherCity> result = new ArrayList<WeatherCity>();
        final String SQL_SELECT_DISTINCT_Province = "SELECT DISTINCT " + WeatherCityDao.Properties.Province.columnName
                + " FROM " + Constant.CLEAN_TABLE_NAME
                + " WHERE " + WeatherCityDao.Properties.CityType.columnName + "=0";
//        Cursor c = session.getDatabase().rawQuery(SqlManager.SQL_SELECT_DISTINCT_Province, null);
        Cursor c = mWeatherCityDaoSession.getDatabase().rawQuery(SQL_SELECT_DISTINCT_Province, null);
        try {
            if (c.moveToFirst()) {
                do {
                    // TODO: 2019/4/16 支持选择过的城市标记
                    WeatherCity weatherCity = new WeatherCity();
                    weatherCity.setProvince(c.getString(c.getColumnIndex(WeatherCityDao.Properties.Province.columnName)));

                    result.add(weatherCity);
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return result;
    }

    /**
     * @param cityType 1:国外
     * @return
     */
    public List<WeatherCity> getGlobalCityNameByCityType(int cityType) {
        List<WeatherCity> result = new ArrayList<WeatherCity>();
        final String SQL_SELECT_DISTINCT_Province = "SELECT DISTINCT " + WeatherCityDao.Properties.City.columnName
                + " FROM " + Constant.CLEAN_TABLE_NAME
                + " WHERE " + WeatherCityDao.Properties.CityType.columnName + "=1";
//        Cursor c = session.getDatabase().rawQuery(SqlManager.SQL_SELECT_DISTINCT_Province, null);
        Cursor cursor = mWeatherCityDaoSession.getDatabase().rawQuery(SQL_SELECT_DISTINCT_Province, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    WeatherCity weatherCity = new WeatherCity();
                    weatherCity.setCity(cursor.getString(cursor.getColumnIndex(WeatherCityDao.Properties.City.columnName)));

                    result.add(weatherCity);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }

        }
        return result;
    }


    /**
     * 按照省份条件查询，通过
     *
     * @param provinceName
     * @return
     */
    public List<WeatherCity> selectWeatherCityByProvinceName(String provinceName) {
//        provinceName = "中国";
        return mWeatherCityDaoSession.getWeatherCityDao().queryBuilder().where(WeatherCityDao.Properties.Province.eq(provinceName)).list();
    }

    /**
     * 根据城市编码查询，正常来说，只有一条数据
     *
     * @param areaCode
     * @return
     */
    public List<WeatherCity> selectWeatherCityByAreaCode(String areaCode) {
        return mWeatherCityDaoSession.getWeatherCityDao().queryBuilder().where(WeatherCityDao.Properties.AreaCode.eq(areaCode)).list();
    }

    /**
     * 根据城市编码查询，正常来说，只有一条数据
     *
     * @param areaCode
     * @return
     */
    public WeatherCity selectOneWeatherCityByAreaCode(String areaCode) {
        List<WeatherCity> weatherCityList = mWeatherCityDaoSession.getWeatherCityDao().queryBuilder().where(WeatherCityDao.Properties.AreaCode.eq(areaCode)).list();
        WeatherCity weatherCity = null;
        if (weatherCityList != null && !weatherCityList.isEmpty()) {
            weatherCity = weatherCityList.get(0);
        }
        return weatherCity;
    }

    /**
     * 根据城市编码更新关注状态
     *
     * @param areaCode
     */
    public void updateWeatherCitySelectStateByAreaCode(String areaCode, int isSelected) {
        List<WeatherCity> weatherCityList = mWeatherCityDaoSession.getWeatherCityDao().queryBuilder().where(WeatherCityDao.Properties.AreaCode.eq(areaCode)).list();
        WeatherCity weatherCity = null;
        if (weatherCityList != null && !weatherCityList.isEmpty()) {
            weatherCity = weatherCityList.get(0);
        }
        if (weatherCity != null) {
            weatherCity.setIsSelected(isSelected);
            updateDistrictSelectState(weatherCity);
        }
    }


    /**
     * 按照省份模糊查询
     *
     * @return
     */
    public List<WeatherCity> queryWeatherCityByProvinceName(String provinceName) {
        return mWeatherCityDaoSession.getWeatherCityDao().queryBuilder().where(WeatherCityDao.Properties.Province.like("%" + provinceName + "%")).list();
    }

    /**
     * 按照市条件查询
     *
     * @param cityName
     * @return
     */
    public List<WeatherCity> selectWeatherCityByCityName(String cityName) {
//        cityName = "房山区";
        return mWeatherCityDaoSession.getWeatherCityDao().queryBuilder().where(WeatherCityDao.Properties.City.eq(cityName)).list();
    }

    /**
     * 按照市模糊查询
     *
     * @return
     */
    public List<WeatherCity> queryWeatherCityByCityName(String cityName) {
//        cityName = "武汉";
        return mWeatherCityDaoSession.getWeatherCityDao().queryBuilder().where(WeatherCityDao.Properties.City.like("%" + cityName + "%")).list();
    }

    /**
     * 按照区、县条件查询
     *
     * @param districtName
     * @return
     */
    public List<WeatherCity> selectWeatherCityByDistrictName(String districtName) {
//        districtName = "利川市";
        return mWeatherCityDaoSession.getWeatherCityDao().queryBuilder().where(WeatherCityDao.Properties.District.eq(districtName)).list();
    }

    /**
     * 模糊查询需要对传入的值前后加"%"
     * 按照区、县模糊查询
     *
     * @return
     */
    public List<WeatherCity> queryWeatherCityByDistrictName(String districtName) {
        return mWeatherCityDaoSession.getWeatherCityDao().queryBuilder().where(WeatherCityDao.Properties.District.like("%" + districtName + "%")).list();
    }


    /**
     * 单条插入
     *
     * @param weatherCity
     */
    public void insertWeatherCity(WeatherCity weatherCity) {
        try {
            mWeatherCityDaoSession.getWeatherCityDao().insert(weatherCity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

/*
    public List<WeatherCity> getWeatherCityEntity(final String searchText) {
        QueryBuilder queryBuilder = mWeatherCityDaoSession.getWeatherCityDao().queryBuilder();

//        queryBuilder.where(WeatherCityDao.Properties.City.like("%" + searchText + "%"));
        queryBuilder.where(WeatherCityDao.Properties.Pinyin_district.eq("ziyang"));
        List<WeatherCity> weatherCityEntities = queryBuilder.list();

        //map查询后合并?
        return weatherCityEntities;
    }*/

    /**
     * test 测试通过，可以查询数据了
     */
    public List<WeatherCity> selectAll() {
      /*  List<WeatherCity> weatherCityEntities =
        if (weatherCityEntities == null) {
            Log.d(TAG, "searchMatchCity->onNext:无记录 ");
        }
        for (int i = 0; i < weatherCityEntities.size(); i++) {
            Log.d(TAG, "searchMatchCity->onNext: " + weatherCityEntities.get(i).toString());
        }*/
        return mWeatherCityDaoSession.getWeatherCityDao().loadAll();
    }

    public void getSearchCity(Observer<List<WeatherCity>> observer, final String searchText) {

        Observable.create(new ObservableOnSubscribe<List<WeatherCity>>() {
            @Override
            public void subscribe(ObservableEmitter<List<WeatherCity>> emitter) throws Exception {
                List<WeatherCity> list = new ArrayList<>();
//                list.addAll(selectWeatherCityByCityName(searchText));
                list.addAll(queryWeatherCityByCityName(searchText));
                emitter.onNext(list);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    /**
     * 根据定位信息匹配城市
     */
    public WeatherCity getCity(String province, String city, String district) {
        QueryBuilder queryBuilder = mWeatherCityDaoSession.getWeatherCityDao().queryBuilder();
        queryBuilder.where(WeatherCityDao.Properties.District.eq(district), WeatherCityDao.Properties.Province.eq(province), WeatherCityDao.Properties.City.like(city));
        List<WeatherCity> list = queryBuilder.list();
        if (!CollectionUtils.isEmpty(list)) {
            WeatherCity entity = list.get(0);
            return entity;
        }
        return null;
    }

    /**
     * 搜索区、县
     *
     * @param district
     * @return
     */
    public List<WeatherCity> searchDistrict(String district) {
        QueryBuilder queryBuilder = mWeatherCityDaoSession.getWeatherCityDao().queryBuilder();
        queryBuilder.where(WeatherCityDao.Properties.District.like("%" + district + "%"));
        List<WeatherCity> list = queryBuilder.list();

        return list;
    }

    /**
     * 搜索城市
     *
     * @param city
     * @return
     */
    public List<WeatherCity> searchCity(String city) {
        QueryBuilder queryBuilder = mWeatherCityDaoSession.getWeatherCityDao().queryBuilder();
        queryBuilder.where(WeatherCityDao.Properties.City.like("%" + city + "%"));
        List<WeatherCity> list = queryBuilder.list();
        return list;
    }

    /**
     * 搜索省
     *
     * @param province
     * @return
     */
    public List<WeatherCity> searchProvince(String province) {
        QueryBuilder queryBuilder = mWeatherCityDaoSession.getWeatherCityDao().queryBuilder();
        queryBuilder.where(WeatherCityDao.Properties.Province.like("%" + province + "%"));
        List<WeatherCity> list = queryBuilder.list();
        return list;
    }

    /**
     * 搜索国家
     *
     * @param country
     * @return
     */
    public List<WeatherCity> searchCountry(String country) {
        QueryBuilder queryBuilder = mWeatherCityDaoSession.getWeatherCityDao().queryBuilder();
        queryBuilder.where(WeatherCityDao.Properties.Country.like("%" + country + "%"));
        List<WeatherCity> list = queryBuilder.list();
        return list;
    }

    /**
     * 根据拼音搜索
     *
     * @param pinyin
     * @return
     */
    public List<WeatherCity> searchByPinyin(String pinyin) {
        QueryBuilder queryBuilder = mWeatherCityDaoSession.getWeatherCityDao().queryBuilder();
        queryBuilder.where(WeatherCityDao.Properties.PinyinDistrict.like("%" + pinyin + "%"));
        List<WeatherCity> list = queryBuilder.list();
        return list;
    }

    /**
     * 获取热门城市
     *
     * @return
     */
    public List<WeatherCity> getRecommendCity() {
        return mWeatherCityDaoSession.getWeatherCityDao().queryBuilder().where(WeatherCityDao.Properties.IsRecommend.eq(1)).list();
    }

    /**
     * 这是日历的热门城市逻辑
     *  获取热门城市
     * @return 城市信息
     */
    /*public List<WeatherCity> getRecommendCity(){
        List<WeatherCity> hotCityList = new ArrayList<>();
        WeatherCity positionCity = GreenDaoManager.getInstance().getPositionCity();
        if (positionCity == null || positionCity.getIsRecommend()==0 ){
            QueryBuilder queryBuilder =  mWeatherCityDaoSession.getWeatherCityDao().queryBuilder();
            queryBuilder.where(WeatherCityDao.Properties.IsRecommend.eq(1),WeatherCityDao.Properties.IsSelected.eq(1));
            hotCityList.addAll(queryBuilder.list());

            queryBuilder =  mWeatherCityDaoSession.getWeatherCityDao().queryBuilder();
            queryBuilder.where(WeatherCityDao.Properties.IsRecommend.eq(1),WeatherCityDao.Properties.IsSelected.eq(0));
            hotCityList.addAll(queryBuilder.list());
        }else if (positionCity.getIsSelected()==1 ){
            hotCityList.add(positionCity);
            QueryBuilder queryBuilder =  mWeatherCityDaoSession.getWeatherCityDao().queryBuilder();
            queryBuilder.where(WeatherCityDao.Properties.IsRecommend.eq(1),WeatherCityDao.Properties.IsSelected.eq(1),WeatherCityDao.Properties.IsPositioning.eq(0));
            hotCityList.addAll(queryBuilder.list());

            queryBuilder =  mWeatherCityDaoSession.getWeatherCityDao().queryBuilder();
            queryBuilder.where(WeatherCityDao.Properties.IsRecommend.eq(1),WeatherCityDao.Properties.IsSelected.eq(0));
            hotCityList.addAll(queryBuilder.list());

        }else {
            QueryBuilder queryBuilder =  mWeatherCityDaoSession.getWeatherCityDao().queryBuilder();
            queryBuilder.where(WeatherCityDao.Properties.IsRecommend.eq(1),WeatherCityDao.Properties.IsSelected.eq(1));
            hotCityList.addAll(queryBuilder.list());

            hotCityList.add(positionCity);

            queryBuilder =  mWeatherCityDaoSession.getWeatherCityDao().queryBuilder();
            queryBuilder.where(WeatherCityDao.Properties.IsRecommend.eq(1),WeatherCityDao.Properties.IsSelected.eq(0),WeatherCityDao.Properties.IsPositioning.eq(0));
            hotCityList.addAll(queryBuilder.list());

        }

        return hotCityList;
    }*/


    /**
     * 获取已选择的城市
     *
     * @return 城市信息
     */
    public List<WeatherCity> getSelectedCity() {

        QueryBuilder queryBuilder = mWeatherCityDaoSession.getWeatherCityDao().queryBuilder();
        queryBuilder.where(WeatherCityDao.Properties.IsSelected.eq(1));
        List<WeatherCity> list = queryBuilder.list();
        if (!CollectionUtils.isEmpty(list)) {
            // WeatherCity UserEntity = list.get(0);
            return list;
        }
        return null;
    }

    /**
     * 获取定位城市
     *
     * @return 城市信息
     */
    public WeatherCity getPositionCity() {
//        LogUtils.d(TAG, "getPositionCity()->:" + Thread.currentThread().getName());
        QueryBuilder queryBuilder = mWeatherCityDaoSession.getWeatherCityDao().queryBuilder();

        queryBuilder.where(WeatherCityDao.Properties.IsPositioning.eq(1));
        if (queryBuilder != null) {
            List<WeatherCity> list = queryBuilder.list();
            if (!CollectionUtils.isEmpty(list)) {
                WeatherCity entity = list.get(0);
                return entity;
            }
        }

        return null;
    }


    /**
     * 获取默认城市
     *
     * @return 城市信息
     */
    public WeatherCity getDefalutCity() {
        QueryBuilder queryBuilder = mWeatherCityDaoSession.getWeatherCityDao().queryBuilder();
        queryBuilder.where(WeatherCityDao.Properties.IsDefalut.eq(1));
        List<WeatherCity> list = queryBuilder.list();
        if (!CollectionUtils.isEmpty(list)) {
            WeatherCity entity = list.get(0);
            return entity;
        }
        return null;
    }


    /**
     * 设置定位城市
     */
    public void setPositionCity(String province, String city, String district) {
        WeatherCity weatherCity = getCity(province, city, district);
        if (weatherCity.getIsPositioning() == 0) {
            weatherCity.setIsPositioning(1);
            WeatherCity positionCity = getPositionCity();
            positionCity.setIsPositioning(0);
            WeatherCityDao weatherCityDao = mWeatherCityDaoSession.getWeatherCityDao();
            weatherCityDao.updateInTx(positionCity, weatherCity);
        }
    }

    /**
     * 设置默认城市
     */
    public void setDefalutCity(WeatherCity weatherCity) {
        WeatherCity defalutCity = getDefalutCity();
        if (defalutCity.getLongitude().equals(weatherCity.getLongitude()) && defalutCity.getLatitude().equals(weatherCity.getLatitude())) {
            defalutCity.setIsDefalut(0);
            WeatherCityDao weatherCityDao = mWeatherCityDaoSession.getWeatherCityDao();
            weatherCityDao.updateInTx(defalutCity, weatherCity);
        }
    }

    /**
     * 设置选择城市
     */
    public void setSelectCity(List weatherCity) {
        WeatherCityDao weatherCityDao = mWeatherCityDaoSession.getWeatherCityDao();
        weatherCityDao.insertOrReplaceInTx(weatherCity);
    }

    /**
     * 删除选择城市
     */
    public void removeSelectCity(WeatherCity weatherCity) {
        WeatherCityDao weatherCityDao = mWeatherCityDaoSession.getWeatherCityDao();
        weatherCityDao.insertOrReplaceInTx(weatherCity);
    }

    /**
     * 第一次定位使用
     */

    public void firstPosition(String province, String city, String district) {
        WeatherCity first = getCity(province, city, district);
        first.setIsSelected(1);
        first.setIsDefalut(1);
        first.setIsPositioning(1);

        WeatherCity city1 = getCity("北京", "北京", "北京");
        city1.setIsSelected(0);
        city1.setIsDefalut(0);
        city1.setIsPositioning(0);

        WeatherCityDao weatherCityDao = mWeatherCityDaoSession.getWeatherCityDao();
        // weatherCityDao.updateInTx(first,city1);
        weatherCityDao.insertOrReplaceInTx(first, city1);
    }

    /**
     * 更新区、县的选中状态，选中则表示添加了关注,貌似该方法删除时，更新有问题
     */
    public void updateDistrictSelectState(WeatherCity weatherCity) {
        WeatherCityDao weatherCityDao = mWeatherCityDaoSession.getWeatherCityDao();
        weatherCityDao.update(weatherCity);
    }

    /**
     * 请求增加关注城市,先对个数做判断
     * @param areaCode
     * @param isSelected
     * @return
     * @throws Exception
     */
    /*public WeatherCity requestIncreaseAttentionDistrict(String areaCode,int isSelected) throws Exception{
        if(queryHasAttentionCity() >= GlobalConstant.MAX_ATTENTION_CITYS){
            throw new IllegalStateException("最多只能添加" + GlobalConstant.MAX_ATTENTION_CITYS + "个关注城市");
        }else{
            return updateSelectStateByAreaCode(areaCode, isSelected);
        }
    }*/

    // TODO: 2019/4/21  下面的updateSelectStateByAreaCode方法貌似有问题

    /**
     * @param weatherCity
     * @param isSelected
     * @return 失败返回null
     */
    public WeatherCity updateSelectStateByAreaCode(WeatherCity weatherCity, int isSelected) {
        if (weatherCity != null) {
            final String SQL_UPDATE = "UPDATE " + Constant.CLEAN_TABLE_NAME
                    + " SET " + WeatherCityDao.Properties.IsSelected.columnName + "=?"
                    + " WHERE " + WeatherCityDao.Properties.AreaCode.columnName + "=?";
            try {
                mWeatherCityDaoSession.getDatabase().execSQL(SQL_UPDATE, new Object[]{isSelected, weatherCity.getAreaCode()});
            } catch (Exception e) {
                e.printStackTrace();

            }

        }
        return weatherCity;
    }

    /**
     * 根据省、市、区查找
     *
     * @param locationCityInfo
     * @return
     */
    public WeatherCity queryLocationCity(LocationCityInfo locationCityInfo) {
        QueryBuilder queryBuilder = mWeatherCityDaoSession.getWeatherCityDao().queryBuilder();
//        queryBuilder.where(WeatherCityDao.Properties.IsDefalut.eq(1));
        WhereCondition provinceCondition = WeatherCityDao.Properties.Province.like("%" + locationCityInfo.getProvince() + "%");
        WhereCondition cityCondition = WeatherCityDao.Properties.City.like("%" + locationCityInfo.getCity() + "%");
        WhereCondition districtCondition = WeatherCityDao.Properties.District.like("%" + locationCityInfo.getDistrict() + "%");
        List<WeatherCity> list = queryBuilder.where(provinceCondition, cityCondition, districtCondition).list();
        if (!CollectionUtils.isEmpty(list)) {
            WeatherCity entity = list.get(0);
            return entity;
        }
        return null;
    }

    /**
     * 查询已经关注(包含定位)城市的个数
     *
     * @return
     */
    public long queryHasAttentionCity() {
        long hasAttentionCitys = mWeatherCityDaoSession.getWeatherCityDao().queryBuilder()
                .where(WeatherCityDao.Properties.IsSelected.eq("1")).buildCount().count();
        long hasPositioningCitys = mWeatherCityDaoSession.getWeatherCityDao().queryBuilder()
                .where(WeatherCityDao.Properties.IsPositioning.eq("1")).buildCount().count();
        return hasAttentionCitys + hasPositioningCitys;
    }

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
