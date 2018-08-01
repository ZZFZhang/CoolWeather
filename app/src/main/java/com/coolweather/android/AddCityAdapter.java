package com.coolweather.android;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.coolweather.android.database.Weather;
import com.coolweather.android.heweather_sdk.HeWeather6;
import com.google.gson.Gson;

import org.litepal.LitePal;
import org.w3c.dom.Text;

import java.util.List;

public class AddCityAdapter extends RecyclerView.Adapter<AddCityAdapter.ViewHolder> {
    private Context context;
    private List<Weather> weatherList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView cityName;
        TextView degreeText;
        TextView weatherInfo;
        TextView rangeTemperature;

        public ViewHolder(View view){
            super(view);
            cardView=(CardView) view;
            cityName=(TextView) view.findViewById(R.id.city_name);
            degreeText=(TextView) view.findViewById(R.id.degree_text);
            weatherInfo=(TextView) view.findViewById(R.id.weather_info);
            rangeTemperature=(TextView) view.findViewById(R.id.max_min_tmp);
        }
    }

    public AddCityAdapter(List<Weather> weathers){this.weatherList=weathers;}
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (context==null){
            context=parent.getContext();
        }
        View view= LayoutInflater.from(context).inflate(R.layout.add_city_item,parent,false);
        final ViewHolder holder=new ViewHolder(view);
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showPopupMenu(v,holder.getAdapterPosition());
                return true;
            }
        });
        return holder;
    }

    private static final String TAG = "AddCityAdapter";

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Weather weather=weatherList.get(position);
        HeWeather6 heWeather6=new Gson().fromJson(weather.getWeatherText(),HeWeather6.class);
        holder.cityName.setText(heWeather6.getBasicWeather().location);
        holder.degreeText.setText(heWeather6.getNowWeather().getTemperature());
        holder.weatherInfo.setText(heWeather6.getNowWeather().getCond_txt());
        holder.rangeTemperature.setText(heWeather6.getForecastList().get(0).tmp_min+"/"+heWeather6.getForecastList().get(0).tmp_max+"℃");
    }

    @Override
    public int getItemCount() {
        return weatherList.size();
    }

    private void showPopupMenu(View view, final int position){
        PopupMenu menu=new PopupMenu(context,view);
        menu.getMenuInflater().inflate(R.menu.delete_city,menu.getMenu());
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()){
                    case R.id.delete_city:
                        String weatherId=weatherList.get(position).getWeatherId();
                        LitePal.deleteAll(Weather.class,"weatherId=?",weatherId);
                        removeData(position);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        menu.show();
    }

    private void removeData(int position){
        weatherList.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }
}
