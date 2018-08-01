package com.coolweather.android.database;

import org.litepal.crud.LitePalSupport;

public class Weather extends LitePalSupport {
    private String weatherId;
    private String weatherText;

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public void setWeatherText(String weatherText) {
        this.weatherText = weatherText;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public String getWeatherText() {
        return weatherText;
    }
}
