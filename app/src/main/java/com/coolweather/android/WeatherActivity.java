package com.coolweather.android;

import android.arch.lifecycle.Lifecycle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.coolweather.android.database.Weather;
import com.coolweather.android.heweather_sdk.AQIWeather;
import com.coolweather.android.heweather_sdk.BasicWeather;
import com.coolweather.android.heweather_sdk.ForecastWeather;
import com.coolweather.android.heweather_sdk.HandleData;
import com.coolweather.android.heweather_sdk.HeWeather6;
import com.coolweather.android.heweather_sdk.LifeStyleWeather;
import com.coolweather.android.heweather_sdk.NowWeather;
import com.coolweather.android.service.AutoUpdateService;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Listener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private ImageView bingPicImg;
    public DrawerLayout drawerLayout;
    public ViewPager viewPager;
    private FloatingActionButton floatingActionButton;

    public static HandleData handleData;
    private HeWeather6 heWeather6;
    private NowWeather nowWeather;
    private BasicWeather basicWeather;
    private AQIWeather aqiWeather;
    private List<ForecastWeather> forecastWeatherList;
    private List<LifeStyleWeather> lifeStyleWeatherList;
    private String weatherId;

    private List<Weather> weatherList;
    private int pageId;

    public Listener listener=new Listener() {
        @Override
        public void updateSuccess(int i) {
            if (i==4){
                generateHeWeather6();
                Intent intent=new Intent(WeatherActivity.this,WeatherActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);//取消跳转动画
                finish();
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
        drawerLayout=(DrawerLayout) findViewById(R.id.drawer_layout);
        viewPager=(ViewPager) findViewById(R.id.view_pager);
        floatingActionButton=(FloatingActionButton) findViewById(R.id.fab);

        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
        String bingPicUrl=preferences.getString("bing_pic",null);
        Log.d(TAG, "onCreate: "+bingPicUrl);
        if (bingPicUrl!=null){//已获取每日一图
            Glide.with(this).load(bingPicUrl).into(bingPicImg);
        }else{
            loadBingPic();
        }

        initViewPager();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(WeatherActivity.this,AddCityActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private static final String TAG = "WeatherActivity";

    public void loadBingPic(){
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

    private void initViewPager(){
        handleData=new HandleData(listener);
        heWeather6=new HeWeather6();
        List<Fragment> fragmentList=new ArrayList<>();
        weatherList=LitePal.findAll(Weather.class);
        if(weatherList.size()==0){
            weatherId=getIntent().getStringExtra("weather_id");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    handleData.UseSDK(weatherId);
                }
            }).start();
        }else {
            for (Weather weather:weatherList){
                WeatherFragment weatherFragment=new WeatherFragment();
                Bundle bundle=new Bundle();
                bundle.putString("weather_text",weather.getWeatherText());
                Log.d(TAG, "initViewPager: "+weather.getWeatherText());
                weatherFragment.setArguments(bundle);
                fragmentList.add(weatherFragment);
            }
            viewPager.setOffscreenPageLimit(weatherList.size());
        }
        MyFragmentPagerAdapter myFragmentPagerAdapter=new MyFragmentPagerAdapter(getSupportFragmentManager(),fragmentList);
        myFragmentPagerAdapter.notifyDataSetChanged();
        viewPager.setAdapter(myFragmentPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                pageId=position;
                SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
                editor.putInt("page_id",pageId);
                editor.apply();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        viewPager.setCurrentItem(preferences.getInt("page_id",0));
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

        if(weatherList.size()==0){
            Weather weather=new Weather();
            weather.setWeatherId(heWeather6.getBasicWeather().weatherId);
            weather.setWeatherText(heWeather6Text);
            weather.save();
        }else {
            Weather weather=new Weather();
            weather.setWeatherId(heWeather6.getBasicWeather().weatherId);
            weather.setWeatherText(heWeather6Text);
            weather.updateAll("weatherId=?",weatherList.get(pageId).getWeatherId());
        }
    }
}
