package com.hzhang.coolweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.hzhang.coolweather.db.CoolWeatherDB;
import com.hzhang.coolweather.model.City;
import com.hzhang.coolweather.model.County;
import com.hzhang.coolweather.model.Province;
import com.hzhang.coolweather.model.WeatherInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

    /**
     * 处理服务器返回的json字符串
     * @param context
     * @param response
     */
    public static void handleWeatherResponse(Context context, String response)
    {
        try
        {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject weatherinfo = jsonObject.getJSONObject("weatherinfo");

            WeatherInfo info = new WeatherInfo();
            info.setCityName(weatherinfo.getString("city"));
            info.setWeatherCode(weatherinfo.getString("cityid"));
            info.setTemp1(weatherinfo.getString("temp1"));
            info.setTemp2(weatherinfo.getString("temp2"));
            info.setWeatherDesp(weatherinfo.getString("weather"));
            info.setPublishTime( weatherinfo.getString("ptime"));

            //将天气信息存入shared文件
            saveWeatherInfo(context, info);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 将服务器返回的天气信息，存入shared文件
     */
    public static void saveWeatherInfo(Context context, WeatherInfo weatherInfo)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();

        //是否选择了city
        editor.putBoolean("city_selected", true);   //标志位，判断是否已有地区天气信息
        editor.putString("city_name", weatherInfo.getCityName());
        editor.putString("weather_code", weatherInfo.getWeatherCode());
        editor.putString("temp1", weatherInfo.getTemp1());
        editor.putString("temp2", weatherInfo.getTemp2());
        editor.putString("weather_desp", weatherInfo.getWeatherDesp());
        editor.putString("publish_time", weatherInfo.getPublishTime());
        //存入当前时间
        editor.putString("current_date", sdf.format(new Date()));

        editor.commit();
    }

}
