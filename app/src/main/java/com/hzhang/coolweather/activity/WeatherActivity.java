package com.hzhang.coolweather.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hzhang.coolweather.R;
import com.hzhang.coolweather.util.HttpUtil;
import com.hzhang.coolweather.util.Utility;

public class WeatherActivity extends AppCompatActivity implements View.OnClickListener
{

    private LinearLayout weatherInfoLayout;
    /**
     * 显示城市名
     */
    private TextView cityNameText;

    /**
     * 用于显示发布时间
     */
    private TextView publicText;

    /**
     * 用于显示天气描述信息
     */
    private TextView weatherDespText;

    /**
     * 用于显示气温1
     */
    private TextView temp1Text;

    /**
     * 用于显示气温2
     */
    private TextView temp2Text;

    /**
     * 用于显示当前时间
     */
    private TextView currentDateText;

    /**
     *  切换城市按钮
     */
    private Button switchCity;

    /**
     * 更新天气按钮
     */
    private Button refreshWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);

        //初始化各个控件
        weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
        cityNameText = (TextView) findViewById(R.id.city_name);
        publicText = (TextView) findViewById(R.id.publish_text);
        weatherDespText = (TextView) findViewById(R.id.weather_desp);
        temp1Text = (TextView) findViewById(R.id.temp1);
        temp2Text = (TextView) findViewById(R.id.temp2);
        currentDateText = (TextView) findViewById(R.id.current_date);

        switchCity = (Button) findViewById(R.id.switch_city);
        refreshWeather = (Button) findViewById(R.id.refresh_weather);

        String countyCode = getIntent().getStringExtra("county_code");
        if(!TextUtils.isEmpty(countyCode))
        {
            //有县级代号时就去查询天气
            publicText.setText("同步中...");
            //隐藏天气详细信息
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityNameText.setVisibility(View.INVISIBLE);
            queryWeatherCode(countyCode);
        } else {
            //没有县级代号
            showWeather();
        }

        switchCity.setOnClickListener(this);
        refreshWeather.setOnClickListener(this);
    }

    private void queryWeatherCode(String countyCode)
    {
        String address = "http://www.weather.com.cn/data/list3/city" +
                countyCode + ".xml";
        queryFromServer(address, "countyCode");
    }

    private void queryWeatherInfo(String weatherCode)
    {
        String address = "http://www.weather.com.cn/data/cityinfo/" +
                weatherCode + ".html";
        queryFromServer(address, "weatherCode");
    }

    private void queryFromServer(final String address, final String type)
    {
        HttpUtil.sendHttpRequest(address, new HttpUtil.HttpCallbackListener()
        {
            @Override
            public void onFinish(String response)
            {
                Log.d("TAG", response);
                if ("countyCode".equals(type))
                {
                    if (!TextUtils.isEmpty(response))
                    {
                        String[] arr = response.split("\\|");
                        if (arr != null && arr.length == 2)
                        {
                            String weatherCode = arr[1];
                            queryWeatherInfo(weatherCode);
                        }
                    }
                } else if ("weatherCode".equals(type))
                {
                    //处理服务器返回的天气信息
                    Utility.handleWeatherResponse(WeatherActivity.this, response);

                    //显示天气信息
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e)
            {

            }
        });
    }

    /**
     * 从shared文件中读取天气信息，显示到界面上
     */
    public void showWeather()
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        cityNameText.setText(prefs.getString("city_name", ""));
        temp1Text.setText(prefs.getString("temp1", ""));
        temp2Text.setText(prefs.getString("temp2", ""));
        weatherDespText.setText(prefs.getString("weather_desp", ""));
        publicText.setText("今天" + prefs.getString("publish_time", "") + "发布");
        currentDateText.setText(prefs.getString("current_date", ""));

        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);
    }


    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.switch_city:
                Intent intent = new Intent(this, ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity", true);
                startActivity(intent);
                finish();
                break;
            case R.id.refresh_weather:
                publicText.setText("同步中...");
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                String weatherCode = prefs.getString("weather_code", "");
                if(!TextUtils.isEmpty(weatherCode))
                {
                    queryWeatherInfo(weatherCode);
                }
                break;

            default:
                break;
        }
    }
}
