package com.coolweather.android.heweather_sdk;

import java.io.Serializable;

public class NowWeather implements Serializable{
    private String temperature;
    private String cond_txt;

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public void setCond_txt(String cond_txt) {
        this.cond_txt = cond_txt;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getCond_txt() {
        return cond_txt;
    }
}
