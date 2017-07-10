package com.curve.nandhakishore.spiderthreeback;

import com.curve.nandhakishore.spiderthreeback.Models.Forecast.ForecastWeather;
import com.curve.nandhakishore.spiderthreeback.Models.Today.CurrentWeather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Nandha Kishore on 04-07-2017.
 */

public interface OpenWeatherApi {

    @GET("weather")
    Call<CurrentWeather> getWeatherByName(@Query("q") String cityName, @Query("APPID") String apiKey);

    @GET("weather")
    Call<CurrentWeather> getWeatherByCoord(@Query("lat") Double lat, @Query("lon") Double lon, @Query("APPID") String apiKey);

    @GET("forecast")
    Call<ForecastWeather> getForecast(@Query("id") Integer cityId, @Query("APPID") String apiKey);
}
