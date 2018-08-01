package com.coolweather.android.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.app.AlarmManager;
import android.os.SystemClock;
import android.app.PendingIntent;
import android.preference.PreferenceManager;
import android.util.Log;

import com.coolweather.android.MyApplication;
import com.coolweather.android.WeatherActivity;
import com.coolweather.android.database.Weather;
import com.coolweather.android.heweather_sdk.HandleData;
import com.coolweather.android.heweather_sdk.HeWeather6;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Listener;
import com.google.gson.Gson;

import org.litepal.LitePal;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {
    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        /*updateWeather();
        updateBingPic();*/
        AlarmManager manager=(AlarmManager) getSystemService(ALARM_SERVICE);
        long triggerAtTime= SystemClock.elapsedRealtime()+8*60*60*1000;//定时8小时
        Intent i=new Intent(this,AutoUpdateService.class);
        PendingIntent pi=PendingIntent.getService(this,0,i,0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent, flags, startId);
    }

    private static final String TAG = "AutoUpdateService";

    private void updateWeather(){
        try{
            Weather weather= LitePal.find(Weather.class,1);
            String weatherText=weather.getWeatherText();
            HeWeather6 heWeather6=new HeWeather6();
            heWeather6=new Gson().fromJson(weatherText,HeWeather6.class);
            String weatherId=heWeather6.getBasicWeather().weatherId;
            WeatherActivity.handleData.UseSDK(weatherId);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void updateBingPic(){
        String requestBingPic="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String bingPicUrl=response.body().string();
                SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).edit();
                editor.putString("bing_pic",bingPicUrl);
                editor.apply();
            }
        });
    }
}
