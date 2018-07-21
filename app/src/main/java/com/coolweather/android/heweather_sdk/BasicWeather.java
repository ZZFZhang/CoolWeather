package com.coolweather.android.heweather_sdk;

public class BasicWeather {
    public String location;
    public String weatherId;
    public String updateTime;

    public void setLocation(String location) {
        this.location = location;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
