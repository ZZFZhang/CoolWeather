package com.coolweather.android;

import android.util.Log;

import java.util.List;

import interfaces.heweather.com.interfacesmodule.bean.air.Air;
import interfaces.heweather.com.interfacesmodule.bean.weather.now.Now;
import interfaces.heweather.com.interfacesmodule.view.HeConfig;
import interfaces.heweather.com.interfacesmodule.view.HeWeather;

public class Test extends HeWeather{
    private static String test;
    public static void log(){
        HeConfig.init("HE1807171519171119","5eac2ba871ae40ed868fd7ed9ff86a7c");
        HeConfig.switchToFreeServerNode();

        HeWeather.getAir(MyApplication.getContext(), "北京", new HeWeather.OnResultAirBeanListener() {
            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onSuccess(List<Air> list) {
                test="aaa";
                Log.d(TAG, "onSuccess: "+test);
            }
        });
        HeWeather.getWeatherNow(MyApplication.getContext(), "北京", new HeWeather.OnResultWeatherNowBeanListener() {
            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onSuccess(List<Now> list) {
                Log.d(TAG, "onSuccess: "+test);
            }
        });
        Log.d(TAG, "log: "+test);
    }

    private static final String TAG = "Test";
}
