package com.hzhang.coolweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hzhang.coolweather.model.City;
import com.hzhang.coolweather.model.County;
import com.hzhang.coolweather.model.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hzh on 2016/3/29.
 */
public class CoolWeatherDB
{
    /**
     * 数据库名
     */
    public static final String DB_NAME = "cool_weather";

    /**
     * 版本
     */
    public static final int version = 1;

    private SQLiteDatabase db;

    private static CoolWeatherDB coolWeatherDB;

    private CoolWeatherDB(Context context)
    {
        CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(context, DB_NAME, null, version);
        db = dbHelper.getWritableDatabase();
    }

    /**
     * 单例
     * @param context
     * @return
     */
    public static CoolWeatherDB getInstance(Context context)
    {
        if(coolWeatherDB == null)
        {
            synchronized (CoolWeatherDB.class)
            {
                if(coolWeatherDB == null)
                {
                    coolWeatherDB = new CoolWeatherDB(context);
                }
            }
        }
        return  coolWeatherDB;
    }

    /**
     * 将province实例存储到数据库
     * @param province
     */
    public void saveProvince(Province province)
    {
        if(province != null)
        {
            ContentValues values = new ContentValues();
            //表中列名和要插入的内容
            values.put("province_name", province.getProvinceName());
            values.put("province_code", province.getProvinceCode());
            db.insert("province", null, values);
        }
    }

    /**
     * 取出所有provinces
     * @return
     */
    public List<Province> getProvinces()
    {
        List<Province> list = new ArrayList<Province>();
        Cursor cursor = db.query("province", null, null, null, null, null, null);
        if(cursor.moveToFirst())
        {
            do
            {
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));

                list.add(province);
            }while(cursor.moveToNext());
        }

        return list;
    }

    public Province getProvinceById(int id)
    {
        Province province = null;

        Cursor cursor = db.query("province", null, "id=?", new String[]{id+""}, //
                null, null, null);

        if(cursor.moveToFirst())
        {
            province = new Province();
            province.setId(cursor.getInt(cursor.getColumnIndex("id")));
            province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
            province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
        }

        return province;
    }

    /**
     * 将City实例存储到数据库
     * @param city
     */
    public void saveCity(City city)
    {
        if(city != null)
        {
            ContentValues values = new ContentValues();
            values.put("city_name", city.getCityName());
            values.put("city_code", city.getCityCode());
            values.put("province_id", city.getProvince().getId());

            db.insert("city", null, values);
        }
    }

    /**
     * 取出所有cities
     * @return
     */
    public List<City> getCities(int provinceId)
    {
        List<City> list = new ArrayList<City>();
        Cursor cursor = db.query("city", null, "province_id=?", //
                new String[]{String.valueOf(provinceId)}, null, null, null);
        if(cursor.moveToFirst())
        {
            do
            {
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setProvince(getProvinceById(provinceId));

                list.add(city);
            }while(cursor.moveToNext());
        }
        return list;
    }

    public City getCityById(int id)
    {
        City city = null;

        Cursor cursor = db.query("city", null, "id=?", new String[]{id+""},//
                null, null, null);
        if(cursor.moveToFirst())
        {
            city = new City();
            city.setId(cursor.getInt(cursor.getColumnIndex("id")));
            city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
            city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
            city.setProvince(getProvinceById(cursor.getInt(cursor.getColumnIndex("province_id"))));
        }

        return city;
    }

    /**
     * 将County实例存储到数据库
     * @param county
     */
    public void saveCounty(County county)
    {
        if(county != null)
        {
            ContentValues values = new ContentValues();
            values.put("county_name", county.getCountyName());
            values.put("county_code", county.getCountyCode());
            values.put("city_id", county.getCity().getId());

            db.insert("county", null, values);
        }
    }

    /**
     * 取出表中所有counties
     * @return
     */
    public List<County> getCounties(int cityId)
    {
        List<County> list = new ArrayList<County>();
        Cursor cursor = db.query("county", null, "city_id=?",//
                new String[]{String.valueOf(cityId)}, null, null, null);
        if(cursor.moveToFirst())
        {
            do
            {
                County county = new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
                county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
                county.setCity(getCityById(cityId));

                list.add(county);
            }while(cursor.moveToNext());
        }

        return list;
    }

    public County getCountyById(int id)
    {
        County county = null;

        Cursor cursor = db.query("county", null, "id=?", new String[]{id+""}, //
                null, null, null);

        if(cursor.moveToFirst())
        {
            county = new County();
            county.setId(id);
            county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
            county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
            county.setCity(getCityById(cursor.getInt(cursor.getColumnIndex("city_id"))));
        }

        return county;
    }
}
