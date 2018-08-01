package com.coolweather.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.coolweather.android.database.Weather;
import com.coolweather.android.heweather_sdk.AQIWeather;
import com.coolweather.android.heweather_sdk.BasicWeather;
import com.coolweather.android.heweather_sdk.ForecastWeather;
import com.coolweather.android.heweather_sdk.HandleData;
import com.coolweather.android.heweather_sdk.HeWeather6;
import com.coolweather.android.heweather_sdk.LifeStyleWeather;
import com.coolweather.android.heweather_sdk.NowWeather;
import com.coolweather.android.util.Listener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class AddCityActivity extends AppCompatActivity {
    public DrawerLayout drawerLayout;
    private List<Weather> weatherList=new ArrayList<>();
    public HandleData handleData;
    private HeWeather6 heWeather6;
    private NowWeather nowWeather;
    private BasicWeather basicWeather;
    private AQIWeather aqiWeather;
    private List<ForecastWeather> forecastWeatherList;
    private List<LifeStyleWeather> lifeStyleWeatherList;
    private String weatherId;

    private RecyclerView recyclerView;
    private AddCityAdapter adapter;

    private static final String TAG = "AddCityActivity";
    public Listener listener=new Listener() {
        @Override
        public void updateSuccess(int i) {
            if (i==4){
                generateHeWeather6();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weatherList=LitePal.findAll(Weather.class);
                        Log.d(TAG, "run: "+new Gson().toJson(weatherList));
                        adapter=new AddCityAdapter(weatherList);
                        adapter.notifyDataSetChanged();
                        recyclerView.setAdapter(adapter);
                    }
                });
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_city);
        drawerLayout=(DrawerLayout) findViewById(R.id.drawer_layout);
        Button add=(Button) findViewById(R.id.add);
        Button confirm=(Button) findViewById(R.id.confirm);

        handleData=new HandleData(listener);
        heWeather6=new HeWeather6();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AddCityActivity.this,WeatherActivity.class);
                startActivity(intent);
                finish();
            }
        });
        weatherList=LitePal.findAll(Weather.class);
        recyclerView=(RecyclerView) findViewById(R.id.recycle_view);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter=new AddCityAdapter(weatherList);
        recyclerView.setAdapter(adapter);
    }

    public void addCity(){
        handleData.UseSDK(weatherId);
    }

    private void generateHeWeather6(){
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String basicWeatherText=preferences.getString("basic_weather",null);
        String nowWeatherText=preferences.getString("now_weather",null);
        String forecastWeahterText=preferences.getString("forecast_weather",null);
        String aqiWeatherText=preferences.getString("aqi_weather",null);
        String lifeStyleWeatherText=preferences.getString("lifestyle_weather",null);
        String heWeather6Text;

        basicWeather=new BasicWeather();
        nowWeather=new NowWeather();
        forecastWeatherList=new ArrayList<>();
        aqiWeather=new AQIWeather();
        lifeStyleWeatherList=new ArrayList<>();

        basicWeather=new Gson().fromJson(basicWeatherText,BasicWeather.class);
        nowWeather=new Gson().fromJson(nowWeatherText,NowWeather.class);
        forecastWeatherList=new Gson().fromJson(forecastWeahterText,new TypeToken<List<ForecastWeather>>(){}.getType());
        aqiWeather=new Gson().fromJson(aqiWeatherText,AQIWeather.class);
        lifeStyleWeatherList=new Gson().fromJson(lifeStyleWeatherText,new TypeToken<List<LifeStyleWeather>>(){}.getType());

        heWeather6.setBasicWeather(basicWeather);
        heWeather6.setNowWeather(nowWeather);
        heWeather6.setForecastList(forecastWeatherList);
        heWeather6.setAqiWeather(aqiWeather);
        heWeather6.setLifeStyleWeatherList(lifeStyleWeatherList);
        heWeather6.setStatus("ok");
        heWeather6Text=new Gson().toJson(heWeather6);

        Weather weather=new Weather();
        weather.setWeatherId(heWeather6.getBasicWeather().weatherId);
        weather.setWeatherText(heWeather6Text);
        weather.save();
    }
}
