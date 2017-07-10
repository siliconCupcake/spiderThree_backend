package com.curve.nandhakishore.spiderthreeback;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.curve.nandhakishore.spiderthreeback.Models.Forecast.ForecastWeather;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CityForecast extends AppCompatActivity {

    static final String BASE_URL = "http://api.openweathermap.org/data/2.5/";
    static final String API_KEY = "b2a4634d73179061c182c1850b050d99";
    Retrofit retroFit;
    ProgressBar pBar;
    Gson gson;
    ArrayList<com.curve.nandhakishore.spiderthreeback.Models.Forecast.List> forecast = new ArrayList<>();
    ForecastWeather result;
    RecyclerView rView;
    String city;
    RecyclerView.LayoutManager layoutManager;
    databaseManage dbData = new databaseManage(this);
    ForecastAdapter adapter;
    int state, id;
    RelativeLayout error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.city_forecast);

        dbData.open();
        city = getIntent().getStringExtra("city");
        id = getIntent().getIntExtra("id", 0);
        setTitle(city);
        rView = (RecyclerView) findViewById(R.id.r_view);
        layoutManager = new LinearLayoutManager(this);
        rView.setLayoutManager(layoutManager);
        pBar = (ProgressBar) findViewById(R.id.loading);
        error = (RelativeLayout) findViewById(R.id.no_net);

        initRetrofit();
        state = 1;
        if (!isNetworkAvailable()) {
            state = 0;
            forecast = dbData.getForecast(id);
            if (forecast == null) {
                pBar.setVisibility(View.GONE);
                error.setVisibility(View.VISIBLE);
            } else {
                error.setVisibility(View.GONE);
                pBar.setVisibility(View.GONE);
                rView.setVisibility(View.VISIBLE);
            }
        }

        adapter = new ForecastAdapter(this, forecast, state);
        rView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        if (state == 1){
            getForecast();
        }


    }

    private void initRetrofit() {
        gson = new GsonBuilder()
                .setLenient()
                .create();

        retroFit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    private void getForecast() {
        OpenWeatherApi api = retroFit.create(OpenWeatherApi.class);
        Call<ForecastWeather> call = api.getForecast(id, API_KEY);
        call.enqueue(new Callback<ForecastWeather>() {
            @Override
            public void onResponse(Call<ForecastWeather> call, Response<ForecastWeather> response) {
                if(response.isSuccessful()) {
                    result = response.body();
                    try{
                        dbData.removeForecast(result);
                    }catch (Exception e){
                        Log.e("Database del", e.getMessage());
                    }
                    for (int i = 0; i < result.getList().size(); i+=8) {
                        forecast.add(result.getList().get(i));
                        dbData.addForecast(result.getList().get(i), result.getCity().getName(), result.getCity().getId());
                    }
                    adapter.setWeather(forecast);
                    pBar.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Unable to fetch data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ForecastWeather> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Unable to fetch data", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    @Override
    protected void onDestroy() {
        dbData.close();
        super.onDestroy();
    }
}
