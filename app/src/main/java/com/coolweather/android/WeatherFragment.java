package com.coolweather.android;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.coolweather.android.database.Weather;
import com.coolweather.android.heweather_sdk.ForecastWeather;
import com.coolweather.android.heweather_sdk.HeWeather6;
import com.coolweather.android.heweather_sdk.LifeStyleWeather;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class WeatherFragment extends Fragment {
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
    public static SwipeRefreshLayout swipeRefresh;
    private Button navButton;
    private String weatherText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.weather_fragment,container,false);
        scrollView=(ScrollView) view.findViewById(R.id.weather_layout);
        titleCity=(TextView) view.findViewById(R.id.title_city_name);
        titleUpdateTime=(TextView) view.findViewById(R.id.title_update_time);
        degreeText=(TextView) view.findViewById(R.id.degree_text);
        weatherInfoText=(TextView) view.findViewById(R.id.weather_info_text);
        forecastLayout=(LinearLayout) view.findViewById(R.id.forecast_item);
        aqiText=(TextView) view.findViewById(R.id.aqi_text);
        pm25Text=(TextView) view.findViewById(R.id.pm25_text);
        comfortText=(TextView) view.findViewById(R.id.comfort_text);
        carWashText=(TextView) view.findViewById(R.id.car_wash_text);
        sportText=(TextView) view.findViewById(R.id.sport_text);
        swipeRefresh=(SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        navButton=(Button) view.findViewById(R.id.nav_button);
        return view;
    }

    private static final String TAG = "WeatherFragment";

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle=getArguments();
        weatherText=bundle.getString("weather_text");
        Log.d(TAG, "onActivityCreated: "+weatherText);
        final HeWeather6 heWeather6=new Gson().fromJson(weatherText,HeWeather6.class);
        showWeatherInfo(heWeather6);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final WeatherActivity activity=(WeatherActivity) getActivity();
                        activity.handleData.UseSDK(heWeather6.getBasicWeather().weatherId);
                        activity.loadBingPic();
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                swipeRefresh.setRefreshing(false);
                            }
                        });
                    }
                }).start();
            }
        });

        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WeatherActivity activity=(WeatherActivity) getActivity();
                activity.drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    public void showWeatherInfo(HeWeather6 heWeather6){
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
            View view= LayoutInflater.from(getContext()).inflate(R.layout.forecast_item,forecastLayout,false);
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
        swipeRefresh.setRefreshing(false);
    }
}
