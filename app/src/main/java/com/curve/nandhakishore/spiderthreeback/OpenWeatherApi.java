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
    Call<CurrentWeather> getWeather(@Query("q") String cityName, @Query("APPID") String apiKey);

    @GET("forecast")
    Call<ForecastWeather> getForecast(@Query("q") String cityName, @Query("APPID") String apiKey);
}
