package com.curve.nandhakishore.spiderthreeback;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.curve.nandhakishore.spiderthreeback.Models.Today.CurrentWeather;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherHome extends AppCompatActivity {

    static final String BASE_URL = "http://api.openweathermap.org/data/2.5/";
    static final String API_KEY = "b2a4634d73179061c182c1850b050d99";
    Gson gson;
    URL imgUrl;
    Retrofit retroFit;
    CurrentWeather result;
    EditText searchBar;
    Button searchButton, more, refresh;
    customTextView name, main, desc, temp, min, max, pressure, humidity, wind, clouds, errorMessage, time, country;
    ImageView icon;
    RelativeLayout rl, error;
    CardView card;
    ProgressBar pBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_home);

        initCard();
        initRetrofit();
        searchBar = (EditText) findViewById(R.id.search_bar);
        searchButton = (Button) findViewById(R.id.search_button);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(searchBar.getText())) {
                    if (isNetworkAvailable()) {
                        card.setVisibility(View.VISIBLE);
                        error.setVisibility(View.GONE);
                        rl.setVisibility(View.GONE);
                        pBar.setVisibility(View.VISIBLE);
                        getWeather(searchBar.getText().toString());
                    } else {
                        card.setVisibility(View.VISIBLE);
                        rl.setVisibility(View.GONE);
                        pBar.setVisibility(View.GONE);
                        error.setVisibility(View.VISIBLE);
                    }
                    hideSoftKeyboard();
                }
                else
                    Toast.makeText(getApplicationContext(), "The text field is blank", Toast.LENGTH_SHORT).show();
            }
        });

        searchBar.setOnEditorActionListener(new EditText.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH){
                    if (!TextUtils.isEmpty(searchBar.getText())) {
                        if (isNetworkAvailable()) {
                            card.setVisibility(View.VISIBLE);
                            error.setVisibility(View.GONE);
                            rl.setVisibility(View.GONE);
                            pBar.setVisibility(View.VISIBLE);
                            getWeather(searchBar.getText().toString());
                        } else {
                            card.setVisibility(View.VISIBLE);
                            rl.setVisibility(View.GONE);
                            pBar.setVisibility(View.GONE);
                            error.setVisibility(View.VISIBLE);
                        }
                        hideSoftKeyboard();
                    }
                    else
                        Toast.makeText(getApplicationContext(), "The text field is blank", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNetworkAvailable()) {
                    card.setVisibility(View.VISIBLE);
                    error.setVisibility(View.GONE);
                    rl.setVisibility(View.GONE);
                    pBar.setVisibility(View.VISIBLE);
                    getWeather(name.getText().toString());
                }
                else {
                    card.setVisibility(View.VISIBLE);
                    rl.setVisibility(View.GONE);
                    pBar.setVisibility(View.GONE);
                    error.setVisibility(View.VISIBLE);
                }
            }
        });

        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent forecast = new Intent(getApplicationContext(), CityForecast.class);
                forecast.putExtra("city", name.getText().toString());
                startActivity(forecast);
            }
        });

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

    private void getWeather(String city) {
        OpenWeatherApi api = retroFit.create(OpenWeatherApi.class);
        Call<CurrentWeather> call = api.getWeather(city, API_KEY);
        call.enqueue(new Callback<CurrentWeather>() {
            @Override
            public void onResponse(Call<CurrentWeather> call, Response<CurrentWeather> response) {
                if(response.isSuccessful()) {
                    result = response.body();
                    pBar.setVisibility(View.GONE);
                    rl.setVisibility(View.VISIBLE);
                    displayWeather(result);
                }
                else {
                    card.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Unable to fetch data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CurrentWeather> call, Throwable t) {
                card.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Unable to fetch data", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    private void initCard(){
        name = (customTextView) findViewById(R.id.date);
        main = (customTextView) findViewById(R.id.weather_main);
        desc = (customTextView) findViewById(R.id.description);
        temp = (customTextView) findViewById(R.id.temp);
        min = (customTextView) findViewById(R.id.min_temp);
        max = (customTextView) findViewById(R.id.max_temp);
        pressure = (customTextView) findViewById(R.id.pressure);
        humidity = (customTextView) findViewById(R.id.humidity);
        wind = (customTextView) findViewById(R.id.wind_speed);
        clouds = (customTextView) findViewById(R.id.cloudiness);
        icon = (ImageView) findViewById(R.id.icon);
        pBar = (ProgressBar) findViewById(R.id.p_bar);
        rl = (RelativeLayout) findViewById(R.id.card);
        card = (CardView) findViewById(R.id.cv);
        error = (RelativeLayout) findViewById(R.id.no_net);
        errorMessage = (customTextView) findViewById(R.id.e_msg);
        more = (Button) findViewById(R.id.forecast);
        refresh = (Button) findViewById(R.id.refresh);
        time = (customTextView) findViewById(R.id.time);
        country = (customTextView) findViewById(R.id.country);
    }

    private void displayWeather(CurrentWeather w) {
        name.setText(w.getName());
        country.setText(w.getSys().getCountry());
        main.setText(w.getWeather().get(0).getMain());
        desc.setText(w.getWeather().get(0).getDescription());
        temp.setText("Temp: " + String.format("%.1f", w.getMain().getTemp() - 273.15f) + " °C");
        min.setText("Min: " + String.format("%.1f", w.getMain().getTempMin() - 273.15f) + " °C");
        max.setText("Max: " + String.format("%.1f", w.getMain().getTempMax() - 273.15f) + " °C");
        pressure.setText("Pressure: " + String.valueOf(w.getMain().getPressure()) + " hPa");
        humidity.setText("Humidity: " + String.valueOf(w.getMain().getHumidity()) + "%");
        wind.setText("Wind Speed: " + String.valueOf(w.getWind().getSpeed()) + " m/s");
        clouds.setText("Cloudiness: " + String.valueOf(w.getClouds().getAll()) + "%");
        time.setText("Last updated at " + unixToTime(w.getDt()));

        try {
            imgUrl = new URL("http://openweathermap.org/img/w/" + w.getWeather().get(0).getIcon() + ".png");
        }catch (Exception e){
            e.printStackTrace();
        }
        Glide.with(getApplicationContext()).load(imgUrl).fitCenter().into(icon);
    }

    private String unixToTime(long unix){

        Date date = new Date(unix*1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss z");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.format(date);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}
