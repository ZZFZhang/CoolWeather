package com.coolweather.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.coolweather.android.database.Weather;
import com.coolweather.android.service.AutoUpdateService;

import org.litepal.LitePal;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //启动自动更新服务
        Intent intentService=new Intent(this, AutoUpdateService.class);
        startService(intentService);

        /*SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
        if (preferences.getString("heweather",null)!=null){
            Intent intent =new Intent(MainActivity.this,WeatherActivity.class);
            startActivity(intent);
            finish();
        }*/

        List<Weather> weatherList= LitePal.findAll(Weather.class);
        if (weatherList.size()!=0){
            Intent intent =new Intent(MainActivity.this,WeatherActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
