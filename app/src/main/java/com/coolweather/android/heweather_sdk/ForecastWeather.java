package com.coolweather.android.heweather_sdk;

public class ForecastWeather {
    public String date;
    public String tmp_max;
    public String tmp_min;
    public String cond_txt_d;

    public void setDate(String date) {
        this.date = date;
    }

    public void setTmp_max(String tmp_max) {
        this.tmp_max = tmp_max;
    }

    public void setTmp_min(String tmp_min) {
        this.tmp_min = tmp_min;
    }

    public void setCond_txt_d(String cond_txt_d) {
        this.cond_txt_d = cond_txt_d;
    }
}
