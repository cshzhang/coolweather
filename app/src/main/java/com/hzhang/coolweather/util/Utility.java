package com.hzhang.coolweather.util;

import android.text.TextUtils;

import com.hzhang.coolweather.db.CoolWeatherDB;
import com.hzhang.coolweather.model.City;
import com.hzhang.coolweather.model.County;
import com.hzhang.coolweather.model.Province;

/**
 * Created by hzh on 2016/3/30.
 */
public class Utility 
{
    public synchronized static boolean handleProvincesResponse(String response, CoolWeatherDB coolWeatherDB)
    {
        if(!TextUtils.isEmpty(response))
        {
            String[] provinces = response.split(",");
            if(provinces != null && provinces.length > 0)
            {
                for(String p : provinces)
                {
                    String[] arr = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(arr[0]);
                    province.setProvinceName(arr[1]);

                    //存入数据库
                    coolWeatherDB.saveProvince(province);
                }
            }
            return true;
        }

        return false;
    }

    public synchronized static boolean handleCitiesResponse(String response, CoolWeatherDB coolWeatherDB,
                                                            Province province)
    {
        if(!TextUtils.isEmpty(response))
        {
            String[] cities = response.split(",");
            if(cities != null && cities.length > 0)
            {
                for(String c : cities)
                {
                    String[] arr = c.split("\\|");
                    City city = new City();
                    city.setCityCode(arr[0]);
                    city.setCityName(arr[1]);
                    city.setProvince(province);

                    //存入数据库
                    coolWeatherDB.saveCity(city);
                }
            }
            return true;
        }

        return false;
    }

    public synchronized static boolean handleCountiesResponse(String response, CoolWeatherDB coolWeatherDB,
                                                            City city)
    {
        if(!TextUtils.isEmpty(response))
        {
            String[] counties = response.split(",");
            if(counties != null && counties.length > 0)
            {
                for(String c : counties)
                {
                    String[] arr = c.split("\\|");
                    County county = new County();
                    county.setCountyCode(arr[0]);
                    county.setCountyName(arr[1]);
                    county.setCity(city);

                    //存入数据库
                    coolWeatherDB.saveCounty(county);
                }
            }
            return true;
        }

        return false;
    }

}
