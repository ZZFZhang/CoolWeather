package com.coolweather.android;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.coolweather.android.heweather_sdk.AQIWeather;
import com.coolweather.android.heweather_sdk.BasicWeather;
import com.coolweather.android.heweather_sdk.ForecastWeather;
import com.coolweather.android.heweather_sdk.HandleData;
import com.coolweather.android.heweather_sdk.HeWeather6;
import com.coolweather.android.heweather_sdk.LifeStyleWeather;
import com.coolweather.android.heweather_sdk.NowWeather;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Listener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import interfaces.heweather.com.interfacesmodule.bean.weather.now.Now;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private ImageView bingPicImg;
    private ScrollView scrollView;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;

    private HandleData handleData;
    private HeWeather6 heWeather6;
    private NowWeather nowWeather;
    private BasicWeather basicWeather;
    private AQIWeather aqiWeather;
    private List<ForecastWeather> forecastWeatherList;
    private List<LifeStyleWeather> lifeStyleWeatherList;
    private String weatherText;
    private String weatherId;

    private Listener listener=new Listener() {
        @Override
        public void updateSuccess(int i) {
            if (i==4){
                generateHeWeather6();
                SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
                weatherText=preferences.getString("heweather",null);
                heWeather6=new Gson().fromJson(weatherText,HeWeather6.class);
                showWeatherInfo(heWeather6);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置状态栏透明
        if (Build.VERSION.SDK_INT>=21){
            View decorView=getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);//透明状态栏，布局在与状态栏重叠
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);

        bingPicImg=(ImageView) findViewById(R.id.bing_pic_img);
        scrollView=(ScrollView) findViewById(R.id.weather_layout);
        titleCity=(TextView) findViewById(R.id.title_city_name);
        titleUpdateTime=(TextView) findViewById(R.id.title_update_time);
        degreeText=(TextView) findViewById(R.id.degree_text);
        weatherInfoText=(TextView) findViewById(R.id.weather_info_text);
        forecastLayout=(LinearLayout) findViewById(R.id.forecast_item);
        aqiText=(TextView) findViewById(R.id.aqi_text);
        pm25Text=(TextView) findViewById(R.id.pm25_text);
        comfortText=(TextView) findViewById(R.id.comfort_text);
        carWashText=(TextView) findViewById(R.id.car_wash_text);
        sportText=(TextView) findViewById(R.id.sport_text);

        handleData=new HandleData(listener);
        heWeather6=new HeWeather6();
        weatherId=getIntent().getStringExtra("weather_id");

        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
        String bingPicUrl=preferences.getString("bing_pic",null);
        Log.d(TAG, "onCreate: "+bingPicUrl);
        if (bingPicUrl!=null){
            Glide.with(this).load(bingPicUrl).into(bingPicImg);
        }else{
            loadBingPic();
        }
        weatherText=preferences.getString("heweather",null);
        Log.d(TAG, "onCreate: "+weatherText);
        if (weatherText!=null){
            heWeather6=new Gson().fromJson(weatherText,HeWeather6.class);
            showWeatherInfo(heWeather6);
        }else{
            scrollView.setVisibility(View.INVISIBLE);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    handleData.UseSDK(weatherId);
                }
            }).start();
        }
    }

    private static final String TAG = "WeatherActivity";


    private void loadBingPic(){
        String requestBingPic="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic=response.body().string();
                SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
        });
    }
    private void showWeatherInfo(HeWeather6 heWeather6){
        String cityName=heWeather6.getBasicWeather().location;
        String updateTime=heWeather6.getBasicWeather().updateTime;
        String degree=heWeather6.getNowWeather().getTemperature()+"℃";
        String condText=heWeather6.getNowWeather().getCond_txt();

        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(condText);

        forecastLayout.removeAllViews();
        for (ForecastWeather forecastWeather:heWeather6.getForecastList()){
            View view= LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
            TextView dateText=(TextView) view.findViewById(R.id.date_text);
            TextView infoText=(TextView) view.findViewById(R.id.info);
            TextView maxText=(TextView) view.findViewById(R.id.max_tmp);
            TextView minText=(TextView) view.findViewById(R.id.min_tmp);

            String date=forecastWeather.date;
            String cond_txt_d=forecastWeather.cond_txt_d;
            String tmp_max=forecastWeather.tmp_max;
            String tmp_min=forecastWeather.tmp_min;

            dateText.setText(date);
            infoText.setText(cond_txt_d);
            maxText.setText(tmp_max);
            minText.setText(tmp_min);

            forecastLayout.addView(view);
        }

        if (heWeather6.getAqiWeather()!=null){
            aqiText.setText(heWeather6.getAqiWeather().aqi);
            pm25Text.setText(heWeather6.getAqiWeather().pm25);
        }

        for (LifeStyleWeather lifeStyleWeather:heWeather6.getLifeStyleWeatherList()){
            if (lifeStyleWeather.type.equals("comf")){
                String comfort="舒适度："+lifeStyleWeather.brf+"\n"+lifeStyleWeather.txt;
                comfortText.setText(comfort);
            }
            if (lifeStyleWeather.type.equals("cw")){
                String carWash="洗车指数："+lifeStyleWeather.brf+"\n"+lifeStyleWeather.txt;
                carWashText.setText(carWash);
            }
            if (lifeStyleWeather.type.equals("sport")){
                String sport="运动指数："+lifeStyleWeather.brf+"\n"+lifeStyleWeather.txt;
                sportText.setText(sport);
            }
        }

        scrollView.setVisibility(View.VISIBLE);

    }

    private void generateHeWeather6(){
        SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(getBaseContext());
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

        SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
        editor.putString("heweather",heWeather6Text);
        editor.apply();
    }
}
