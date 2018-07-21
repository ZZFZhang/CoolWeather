package com.coolweather.android.heweather_sdk;

import com.coolweather.android.gson.Forecast;

import java.util.List;

public class HeWeather6 {
    private String status;
    private BasicWeather basicWeather;
    private NowWeather nowWeather;
    private AQIWeather aqiWeather;
    private List<ForecastWeather> forecastList;
    private List<LifeStyleWeather> lifeStyleWeatherList;

    public HeWeather6(){
    }

    public String getStatus() {
        return status;
    }

    public BasicWeather getBasicWeather() {
        return basicWeather;
    }

    public NowWeather getNowWeather() {
        return nowWeather;
    }

    public AQIWeather getAqiWeather() {
        return aqiWeather;
    }

    public List<ForecastWeather> getForecastList() {
        return forecastList;
    }

    public List<LifeStyleWeather> getLifeStyleWeatherList() {
        return lifeStyleWeatherList;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setBasicWeather(BasicWeather basicWeather) {
        this.basicWeather = basicWeather;
    }

    public void setNowWeather(NowWeather nowWeather) {
        this.nowWeather = nowWeather;
    }

    public void setAqiWeather(AQIWeather aqiWeather) {
        this.aqiWeather = aqiWeather;
    }

    public void setForecastList(List<ForecastWeather> forecastList) {
        this.forecastList = forecastList;
    }

    public void setLifeStyleWeatherList(List<LifeStyleWeather> lifeStyleWeatherList) {
        this.lifeStyleWeatherList = lifeStyleWeatherList;
    }
}
