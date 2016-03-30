package com.hzhang.coolweather.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hzhang.coolweather.R;
import com.hzhang.coolweather.db.CoolWeatherDB;
import com.hzhang.coolweather.model.City;
import com.hzhang.coolweather.model.County;
import com.hzhang.coolweather.model.Province;
import com.hzhang.coolweather.util.HttpUtil;
import com.hzhang.coolweather.util.Utility;

import java.util.ArrayList;
import java.util.List;

public class ChooseAreaActivity extends AppCompatActivity
{
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private int currentLevel = LEVEL_PROVINCE;
    private ProgressDialog progressDialog;
    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private CoolWeatherDB coolWeatherDB;
    private List<String> dataList = new ArrayList<String>();

    /**
     * 省列表
     */
    private List<Province> provinceList;
    /**
     * 市列表
     */
    private List<City> cityList;
    /**
     * 县列表
     */
    private List<County> countyList;
    /**
     * 选中的省份
     */
    private Province selectedProvince;
    /**
     * 选中的城市
     */
    private City selectedCity;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(prefs.getBoolean("city_selected", false))
        {
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        listView = (ListView) findViewById(R.id.area_lv);
        titleText = (TextView) findViewById(R.id.tile_tv);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        coolWeatherDB = CoolWeatherDB.getInstance(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if(currentLevel == LEVEL_PROVINCE)
                {
                    selectedProvince = provinceList.get(position);
                    queryCities();
                } else if(currentLevel == LEVEL_CITY)
                {
                    selectedCity = cityList.get(position);
                    queryCounties();
                } else if(currentLevel == LEVEL_COUNTY)
                {
                    Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
                    intent.putExtra("county_code", countyList.get(position).getCountyCode());
                    startActivity(intent);
                    finish();
                }
            }
        });

        //加载省级数据
        queryProvinces();
    }

    /**
     * 查询全国所有省份数据，优先从数据库中查，查不到就从服务器中查
     */
    private void queryProvinces()
    {
        provinceList = coolWeatherDB.getProvinces();
        //先从数据库中查，查不到就从服务器上查
        if(provinceList != null && provinceList.size() > 0)
        {
            dataList.clear();
            for(Province province : provinceList)
            {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("中国");

            currentLevel = LEVEL_PROVINCE;
        }else{
            queryFromServer(null, "province");
        }
    }

    /**
     * 查询省内所有市的数据，优先从数据库中查，查不到就从服务器中查
     */
    private void queryCities()
    {
        cityList = coolWeatherDB.getCities(selectedProvince.getId());
        if(cityList != null && cityList.size() > 0)
        {
            dataList.clear();
            for(City city : cityList)
            {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getProvinceName());

            currentLevel = LEVEL_CITY;
        } else {
            queryFromServer(selectedProvince.getProvinceCode(), "city");
        }
    }

    /**
     * 查询市内所有县的数据，优先从数据库中查，查不到就从服务器中查
     */
    private void queryCounties()
    {
        countyList = coolWeatherDB.getCounties(selectedCity.getId());
        if(countyList != null && countyList.size() > 0)
        {
            dataList.clear();
            for(County county : countyList)
            {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedCity.getCityName());

            currentLevel = LEVEL_COUNTY;
        } else
        {
            queryFromServer(selectedCity.getCityCode(), "county");
        }
    }

    /**
     * 从服务器查询省市县数据
     */
    private void queryFromServer(final String code, final String type)
    {
        String address;
        if(!TextUtils.isEmpty(code))
        {
            address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
        }else{
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }

        showProgressDialog();

        HttpUtil.sendHttpRequest(address, new HttpUtil.HttpCallbackListener()
        {
            boolean result = false;
            @Override
            public void onFinish(String response)
            {
                Log.d("TAG", response);
                if("province".equals(type))
                {
                    result = Utility.handleProvincesResponse(response, coolWeatherDB);
                } else if("city".equals(type))
                {
                    result = Utility.handleCitiesResponse(response, coolWeatherDB, selectedProvince);
                } else if("county".equals(type))
                {
                    result = Utility.handleCountiesResponse(response, coolWeatherDB, selectedCity);
                }

                if(result)
                {
                    //通过runOnUiThread方法回到主线程
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            closeProgressDialog();
                            if("province".equals(type))
                            {
                                queryProvinces();
                            } else if ("city".equals(type))
                            {
                                queryCities();
                            } else if("county".equals(type))
                            {
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e)
            {
                //通过runOnUiThread方法回到主线程
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this, "加载失败...", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog()
    {
        if(progressDialog == null)
        {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog()
    {
        if(progressDialog != null)
        {
            progressDialog.dismiss();
        }
    }

    /**
     * 捕获back按键，根据当前级别来判断应该返回市级列表还是省级列表，还是退出
     */
    @Override
    public void onBackPressed()
    {
        if(currentLevel == LEVEL_COUNTY)
        {
            queryCities();
        } else if(currentLevel == LEVEL_CITY)
        {
            queryProvinces();
        } else {
            finish();
        }
    }
}
