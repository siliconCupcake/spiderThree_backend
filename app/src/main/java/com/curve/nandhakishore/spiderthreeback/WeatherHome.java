package com.curve.nandhakishore.spiderthreeback;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.curve.nandhakishore.spiderthreeback.Models.Details.PlaceDetails;
import com.curve.nandhakishore.spiderthreeback.Models.Places.PlacePredictions;
import com.curve.nandhakishore.spiderthreeback.Models.Today.CurrentWeather;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherHome extends AppCompatActivity {

    static final String BASE_URL = "http://api.openweathermap.org/data/2.5/";
    static final String PLACE_URL = "https://maps.googleapis.com/maps/api/place/";
    static final String API_KEY = "b2a4634d73179061c182c1850b050d99";
    static final String PLACE_KEY = "AIzaSyBPAc0ObhV5BGj6dAavkqcqUxdsR44uURQ";
    static final String PLACE_TYPE = "(cities)";
    static final String TAG = "WeatherHome";
    Gson gson;
    URL imgUrl;
    Retrofit wRetroFit, pRetroFit;
    Double lat, lon;
    PlacePredictions predictions;
    String id;
    PlaceDetails details;
    SimpleAdapter adapter;
    List<HashMap<String , String>> predictionList = new ArrayList<>();
    CurrentWeather result;
    databaseManage dbData = new databaseManage(this);
    AutoCompletePlaces searchBar;
    Button searchButton, more, refresh;
    customTextView name, main, desc, temp, min, max, pressure, humidity, wind, clouds, errorMessage, time, country;
    ImageView icon;
    RelativeLayout rl, error;
    CardView card;
    ProgressBar pBar;
    int apiCode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_home);

        initCard();
        initWeatherRetrofit();
        initPlacesRetrofit();
        searchBar = (AutoCompletePlaces) findViewById(R.id.search_bar);
        searchButton = (Button) findViewById(R.id.search_button);
        dbData.open();

        searchBar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                apiCode = 1;
                id = predictionList.get(i).get("id");
                Log.e(TAG, "Item Id: " + predictionList.get(i).get("id"));
            }
        });

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
                        searchBar.setText("");
                    } else {
                        result = dbData.getWeather(searchBar.getText().toString());
                        if (result == null) {
                            card.setVisibility(View.VISIBLE);
                            rl.setVisibility(View.GONE);
                            pBar.setVisibility(View.GONE);
                            error.setVisibility(View.VISIBLE);
                        } else {
                            card.setVisibility(View.VISIBLE);
                            error.setVisibility(View.GONE);
                            pBar.setVisibility(View.GONE);
                            rl.setVisibility(View.VISIBLE);
                            displayWeather(result);
                        }
                    }
                    hideSoftKeyboard();
                }
                else
                    Toast.makeText(getApplicationContext(), "The text field is blank", Toast.LENGTH_SHORT).show();
            }
        });

        searchBar.setThreshold(3);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                apiCode = 0;
                getPlaces(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

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
                            searchBar.setText("");
                        } else {
                            result = dbData.getWeather(searchBar.getText().toString());
                            if (result == null) {
                                card.setVisibility(View.VISIBLE);
                                rl.setVisibility(View.GONE);
                                pBar.setVisibility(View.GONE);
                                error.setVisibility(View.VISIBLE);
                            } else {
                                card.setVisibility(View.VISIBLE);
                                error.setVisibility(View.GONE);
                                pBar.setVisibility(View.GONE);
                                rl.setVisibility(View.VISIBLE);
                                displayWeather(result);
                            }
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
                if (isNetworkAvailable()) {
                    card.setVisibility(View.VISIBLE);
                    error.setVisibility(View.GONE);
                    rl.setVisibility(View.GONE);
                    pBar.setVisibility(View.VISIBLE);
                    getWeather(searchBar.getText().toString());
                } else {
                    result = dbData.getWeather(searchBar.getText().toString());
                    if (result == null) {
                        card.setVisibility(View.VISIBLE);
                        rl.setVisibility(View.GONE);
                        pBar.setVisibility(View.GONE);
                        error.setVisibility(View.VISIBLE);
                    } else {
                        card.setVisibility(View.VISIBLE);
                        error.setVisibility(View.GONE);
                        pBar.setVisibility(View.GONE);
                        rl.setVisibility(View.VISIBLE);
                        displayWeather(result);
                    }
                }
            }
        });

        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent forecast = new Intent(getApplicationContext(), CityForecast.class);
                forecast.putExtra("city", name.getText().toString());
                forecast.putExtra("id", result.getId());
                startActivity(forecast);
            }
        });

    }

    private void initPlacesRetrofit() {
        gson = new GsonBuilder()
                .setLenient()
                .create();

        pRetroFit = new Retrofit.Builder()
                .baseUrl(PLACE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    private void getPlaces(String search) {
        PlacesApi api = pRetroFit.create(PlacesApi.class);
        Call<PlacePredictions> call = api.getPlaces(search, PLACE_TYPE, PLACE_KEY);
        call.enqueue(new Callback<PlacePredictions>() {
            @Override
            public void onResponse(Call<PlacePredictions> call, Response<PlacePredictions> response) {
                if(response.isSuccessful()){
                    predictions = response.body();
                    Log.e(TAG, "Count: " + String.valueOf(predictions.getPredictions().size()));
                    predictionList.clear();
                    for(int i = 0; i < predictions.getPredictions().size(); i++) {
                        HashMap<String, String> place = new HashMap<String, String>();
                        place.put("name", predictions.getPredictions().get(i).getStructuredFormatting().getMainText());
                        place.put("description", predictions.getPredictions().get(i).getStructuredFormatting().getSecondaryText());
                        place.put("id", predictions.getPredictions().get(i).getPlaceId());
                        predictionList.add(place);
                    }
                    int[] to = new int[] {R.id.name_field, R.id.desc_field};
                    String[] from = new String[] {"name", "description"};
                    adapter = new SimpleAdapter(getBaseContext(), predictionList, R.layout.autocomplete_list_item, from, to);
                    searchBar.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
                else
                    Log.e(TAG, "Response failed");
            }

            @Override
            public void onFailure(Call<PlacePredictions> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void getDetails(String id) {
        PlacesApi api = pRetroFit.create(PlacesApi.class);
        Call<PlaceDetails> call = api.getDetails(id, PLACE_KEY);
        call.enqueue(new Callback<PlaceDetails>() {
            @Override
            public void onResponse(Call<PlaceDetails> call, Response<PlaceDetails> response) {
                if(response.isSuccessful()){
                    details = response.body();
                    lat = details.getResult().getGeometry().getLocation().getLat();
                    lon = details.getResult().getGeometry().getLocation().getLng();
                    getWeatherByCoord();
                }
                else
                    Log.e(TAG, "Response failed");
            }

            @Override
            public void onFailure(Call<PlaceDetails> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }

    private void initWeatherRetrofit() {
        gson = new GsonBuilder()
                .setLenient()
                .create();

        wRetroFit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    private void getWeather(String city) {
        if (apiCode == 0) {
            getWeatherByName(city);
        }
        else {
            getDetails(id);
        }
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

    private void getWeatherByCoord (){
        OpenWeatherApi api = wRetroFit.create(OpenWeatherApi.class);
        Call<CurrentWeather> call;
        call = api.getWeatherByCoord(lat, lon, API_KEY);
        call.enqueue(new Callback<CurrentWeather>() {
            @Override
            public void onResponse(Call<CurrentWeather> call, Response<CurrentWeather> response) {
                if(response.isSuccessful()) {
                    result = response.body();
                    try{
                        dbData.removeWeather(result);
                    }
                    catch (Exception e){
                        Log.e("Database del", e.getMessage());
                    }
                    dbData.addWeather(result);
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

    private void getWeatherByName(String city){
        OpenWeatherApi api = wRetroFit.create(OpenWeatherApi.class);
        Call<CurrentWeather> call;
        call = api.getWeatherByName(city, API_KEY);
        call.enqueue(new Callback<CurrentWeather>() {
            @Override
            public void onResponse(Call<CurrentWeather> call, Response<CurrentWeather> response) {
                if(response.isSuccessful()) {
                    result = response.body();
                    try{
                        dbData.removeWeather(result);
                    }
                    catch (Exception e){
                        Log.e("Database del", e.getMessage());
                    }
                    dbData.addWeather(result);
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

    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    protected void onDestroy() {
        dbData.close();
        super.onDestroy();
    }
}
