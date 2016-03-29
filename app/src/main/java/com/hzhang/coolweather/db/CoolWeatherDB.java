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
            db.insert("city", null, values);
        }
    }

    /**
     * 取出所有cities
     * @return
     */
    public List<City> getCities()
    {
        List<City> list = new ArrayList<City>();
        Cursor cursor = db.query("city", null, null, null, null, null, null, null);
        if(cursor.moveToFirst())
        {
            do
            {
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));

                list.add(city);
            }while(cursor.moveToNext());
        }
        return list;
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

            db.insert("county", null, values);
        }
    }

    /**
     * 取出表中所有counties
     * @return
     */
    public List<County> getCounties()
    {
        List<County> list = new ArrayList<County>();
        Cursor cursor = db.query("county", null, null, null, null, null, null);
        if(cursor.moveToFirst())
        {
            do
            {
                County county = new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
                county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));

                list.add(county);
            }while(cursor.moveToNext());
        }

        return list;
    }
}
