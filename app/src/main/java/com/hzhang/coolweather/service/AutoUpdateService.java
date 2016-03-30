package com.hzhang.coolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.hzhang.coolweather.receiver.AutoUpdateReceiver;
import com.hzhang.coolweather.util.HttpUtil;
import com.hzhang.coolweather.util.Utility;

public class AutoUpdateService extends Service
{
    public AutoUpdateService()
    {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                updateWeather();
            }
        }).start();

        /**
         * 定时服务
         * 定时发送一个广播，广播中又启动服务
         */
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int eightHour = 8 * 60 * 60 * 1000; //8小时的毫秒数
        long triggerTime = SystemClock.elapsedRealtime() + eightHour;
        Intent i = new Intent(this, AutoUpdateReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP ,triggerTime, pi);

        return super.onStartCommand(intent, flags, startId);
    }

    private void updateWeather()
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherCode = prefs.getString("weather_code", "");
        String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";

        HttpUtil.sendHttpRequest(address, new HttpUtil.HttpCallbackListener()
        {
            @Override
            public void onFinish(String response)
            {
                Utility.handleWeatherResponse(AutoUpdateService.this, response);
            }

            @Override
            public void onError(Exception e)
            {

            }
        });
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
}
