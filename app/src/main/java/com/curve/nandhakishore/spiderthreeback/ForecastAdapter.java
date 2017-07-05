package com.curve.nandhakishore.spiderthreeback;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.bumptech.glide.Glide;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.cardHolder> {

    static Context myContext;
    int state;
    ArrayList<com.curve.nandhakishore.spiderthreeback.Models.Forecast.List> weather;

    public ForecastAdapter(Context con, ArrayList<com.curve.nandhakishore.spiderthreeback.Models.Forecast.List> list, int s) {
        myContext = con;
        weather = list;
        state = s;
    }

    public void setWeather(ArrayList<com.curve.nandhakishore.spiderthreeback.Models.Forecast.List> weather) {
        this.weather = weather;
    }

    @Override
    public cardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext()).inflate(R.layout.forecast_card, parent, false);
        return new cardHolder(inflatedView);
    }


    @Override
    public void onBindViewHolder(final cardHolder holder, final int position) {

            holder.date.setText(unixToTime(weather.get(position).getDt()));
            holder.main.setText(weather.get(position).getWeather().get(0).getMain());
            holder.desc.setText(weather.get(position).getWeather().get(0).getDescription());
            holder.temp.setText("Temp: " + String.format("%.1f", weather.get(position).getMain().getTemp() - 273.15) + " °C");
            holder.min.setText("Min: " + String.format("%.1f", weather.get(position).getMain().getTempMin() - 273.15) + " °C");
            holder.max.setText("Max: " + String.format("%.1f", weather.get(position).getMain().getTempMax() - 273.15) + " °C");
            holder.pressure.setText("Pressure: " + String.valueOf(weather.get(position).getMain().getPressure()) + " hPa");
            holder.humidity.setText("Humidity: " + String.valueOf(weather.get(position).getMain().getHumidity()) + "%");
            holder.wind.setText("Wind Speed: " + String.valueOf(weather.get(position).getWind().getSpeed()) + " m/s");
            holder.cloud.setText("Cloudiness: " + String.valueOf(weather.get(position).getClouds().getAll()) + "%");

            URL imgUrl;
            try {
                imgUrl = new URL("http://openweathermap.org/img/w/" + weather.get(position).getWeather().get(0).getIcon() + ".png");
                Glide.with(myContext).load(imgUrl).into(holder.icon);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    @Override
    public int getItemCount() {
        return weather.size();
    }

    public static class cardHolder extends RecyclerView.ViewHolder {

        private ImageView icon;
        private customTextView date, main, desc, temp, min, max, pressure, humidity, wind, cloud;

        public cardHolder(View v) {
            super(v);
            this.date = (customTextView) v.findViewById(R.id.date);
            this.icon = (ImageView) v.findViewById(R.id.icon);
            this.main = (customTextView) v.findViewById(R.id.weather_main);
            this.desc = (customTextView) v.findViewById(R.id.description);
            this.temp = (customTextView) v.findViewById(R.id.temp);
            this.min = (customTextView) v.findViewById(R.id.min_temp);
            this.max = (customTextView) v.findViewById(R.id.max_temp);
            this.pressure = (customTextView) v.findViewById(R.id.pressure);
            this.humidity = (customTextView) v.findViewById(R.id.humidity);
            this.wind = (customTextView) v.findViewById(R.id.wind_speed);
            this.cloud = (customTextView) v.findViewById(R.id.cloudiness);
        }
    }

    private String unixToTime(long unix){
        Date date = new Date(unix*1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.format(date);
    }

}
