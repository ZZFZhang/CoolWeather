package com.coolweather.android.heweather_sdk;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.coolweather.android.MyApplication;
import com.coolweather.android.util.Listener;
import com.coolweather.android.util.Utility;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import interfaces.heweather.com.interfacesmodule.bean.Lang;
import interfaces.heweather.com.interfacesmodule.bean.Unit;
import interfaces.heweather.com.interfacesmodule.bean.air.now.AirNow;
import interfaces.heweather.com.interfacesmodule.bean.weather.forecast.Forecast;
import interfaces.heweather.com.interfacesmodule.bean.weather.lifestyle.Lifestyle;
import interfaces.heweather.com.interfacesmodule.bean.weather.lifestyle.LifestyleBase;
import interfaces.heweather.com.interfacesmodule.bean.weather.now.Now;
import interfaces.heweather.com.interfacesmodule.view.HeConfig;
import interfaces.heweather.com.interfacesmodule.view.HeWeather;

public class HandleData {
    private NowWeather nowWeather;
    private BasicWeather basicWeather;
    private AQIWeather aqiWeather;
    private List<ForecastWeather> forecastWeatherList;
    private ForecastWeather forecastWeather;
    private List<LifeStyleWeather> lifeStyleWeatherList;
    private LifeStyleWeather lifeStyleWeather;

    private int i;
    private Listener listener;
    public HandleData(Listener listener){this.listener=listener;}

    private static final String TAG = "HandleData";

    public void UseSDK(String cid){
        HeConfig.init("HE1807171519171119","5eac2ba871ae40ed868fd7ed9ff86a7c");
        HeConfig.switchToFreeServerNode();
        i=0;

        final SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).edit();
        HeWeather.getWeatherNow(MyApplication.getContext(), cid, Lang.CHINESE_SIMPLIFIED, Unit.METRIC,new HeWeather.OnResultWeatherNowBeanListener() {
            @Override
            public void onError(Throwable throwable) {
                Log.d(TAG, "onError: getWeatherNow"+throwable);
            }

            @Override
            public void onSuccess(List<Now> list) {
                nowWeather=new NowWeather();
                basicWeather=new BasicWeather();
                nowWeather.setTemperature(list.get(0).getNow().getTmp());
                nowWeather.setCond_txt(list.get(0).getNow().getCond_txt());
                basicWeather.setLocation(list.get(0).getBasic().getLocation());
                basicWeather.setWeatherId(list.get(0).getBasic().getCid());
                basicWeather.setUpdateTime(list.get(0).getUpdate().getLoc());

                editor.putString("now_weather",new Gson().toJson(nowWeather));
                editor.apply();

                editor.putString("basic_weather",new Gson().toJson(basicWeather));
                editor.apply();

                listener.updateSuccess(++i);

                Log.d(TAG, "onSuccess: "+new Gson().toJson(nowWeather));
                Log.d(TAG, "onSuccess: "+new Gson().toJson(basicWeather));
            }
        });

        HeWeather.getAirNow(MyApplication.getContext(), cid, new HeWeather.OnResultAirNowBeansListener() {
            @Override
            public void onError(Throwable throwable) {
                aqiWeather=new AQIWeather();
                aqiWeather.setAqi("0");
                aqiWeather.setPm25("0");
                editor.putString("aqi_weather",new Gson().toJson(aqiWeather));
                editor.apply();
                listener.updateSuccess(++i);
                Log.d(TAG, "onError: getAirNow"+throwable);
            }

            @Override
            public void onSuccess(List<AirNow> list) {
                aqiWeather=new AQIWeather();
                aqiWeather.setAqi(list.get(0).getAir_now_city().getAqi());
                aqiWeather.setPm25(list.get(0).getAir_now_city().getPm25());
                editor.putString("aqi_weather",new Gson().toJson(aqiWeather));
                editor.apply();
                listener.updateSuccess(++i);
                Log.d(TAG, "onSuccess: "+new Gson().toJson(aqiWeather));
            }
        });

        HeWeather.getWeatherForecast(MyApplication.getContext(), cid, new HeWeather.OnResultWeatherForecastBeanListener() {
            @Override
            public void onError(Throwable throwable) {
                Log.d(TAG, "onError: getWeatherForecast"+throwable);
            }

            @Override
            public void onSuccess(List<Forecast> list) {
                forecastWeatherList=new ArrayList<>(3);
                forecastWeatherList.clear();
                for (int i=0;i<3;i++){
                    forecastWeather=new ForecastWeather();
                    forecastWeather.setDate(list.get(0).getDaily_forecast().get(i).getDate());
                    forecastWeather.setCond_txt_d(list.get(0).getDaily_forecast().get(i).getCond_txt_d());
                    forecastWeather.setTmp_max(list.get(0).getDaily_forecast().get(i).getTmp_max());
                    forecastWeather.setTmp_min(list.get(0).getDaily_forecast().get(i).getTmp_min());
                    forecastWeatherList.add(forecastWeather);
                }
                editor.putString("forecast_weather",new Gson().toJson(forecastWeatherList));
                editor.apply();
                listener.updateSuccess(++i);
                Log.d(TAG, "onSuccess: "+new Gson().toJson(forecastWeatherList));
            }
        });

        HeWeather.getWeatherLifeStyle(MyApplication.getContext(), cid, new HeWeather.OnResultWeatherLifeStyleBeanListener() {
            @Override
            public void onError(Throwable throwable) {
                Log.d(TAG, "onError: getWeatherLifeStyle"+throwable);
            }

            @Override
            public void onSuccess(List<Lifestyle> list) {
                lifeStyleWeatherList=new ArrayList<>();
                lifeStyleWeatherList.clear();
                for (LifestyleBase lifestyleBase:list.get(0).getLifestyle()){
                    lifeStyleWeather=new LifeStyleWeather();
                    if (lifestyleBase.getType().equals("comf")||lifestyleBase.getType().equals("cw")||lifestyleBase.getType().equals("sport")){
                        lifeStyleWeather.setBrf(lifestyleBase.getBrf());
                        lifeStyleWeather.setTxt(lifestyleBase.getTxt());
                        lifeStyleWeather.setType(lifestyleBase.getType());
                        lifeStyleWeatherList.add(lifeStyleWeather);
                    }
                }

                editor.putString("lifestyle_weather",new Gson().toJson(lifeStyleWeatherList));
                editor.apply();
                listener.updateSuccess(++i);
                Log.d(TAG, "onSuccess: "+new Gson().toJson(lifeStyleWeatherList));
            }
        });
    }
}
