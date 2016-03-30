package com.hzhang.coolweather.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by hzh on 2016/3/29.
 */
public class HttpUtil 
{
    public static void sendHttpRequest(final String address,
                                       final HttpCallbackListener listener)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                HttpURLConnection connection = null;
                try
                {
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8 * 1000); //8s
                    connection.setReadTimeout(8 * 1000);

                    InputStream in = connection.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    String line;
                    StringBuilder sb = new StringBuilder();
                    while((line = br.readLine()) != null)
                    {
                        sb.append(line);
                    }

                    if(listener != null)
                    {
                        listener.onFinish(sb.toString());
                    }
                } catch (Exception e)
                {
                    if(listener != null)
                    {
                        listener.onError(e);
                    }
                } finally
                {
                    if(connection != null)
                    {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }


    public interface HttpCallbackListener
    {
        void onFinish(String response);
        void onError(Exception e);
    }
}
