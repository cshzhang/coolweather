package com.hzhang.coolweather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hzhang.coolweather.service.AutoUpdateService;

/**
 * Created by hzh on 2016/3/30.
 */
public class AutoUpdateReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Intent i = new Intent(context, AutoUpdateService.class);
        context.startService(i);
    }
}
