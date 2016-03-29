package com.hzhang.coolweather.model;

/**
 * Created by hzh on 2016/3/29.
 */
public class County
{
    private int id;
    private String countyName;
    private String countyCode;
    private City city;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getCountyName()
    {
        return countyName;
    }

    public void setCountyName(String countyName)
    {
        this.countyName = countyName;
    }

    public String getCountyCode()
    {
        return countyCode;
    }

    public void setCountyCode(String countyCode)
    {
        this.countyCode = countyCode;
    }

    public City getCity()
    {
        return city;
    }

    public void setCity(City city)
    {
        this.city = city;
    }
}
