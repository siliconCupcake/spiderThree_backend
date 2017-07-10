package com.curve.nandhakishore.spiderthreeback;

import com.curve.nandhakishore.spiderthreeback.Models.Details.PlaceDetails;
import com.curve.nandhakishore.spiderthreeback.Models.Places.PlacePredictions;
import com.curve.nandhakishore.spiderthreeback.Models.Today.CurrentWeather;
import com.google.android.gms.location.places.Places;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PlacesApi {

    @GET("autocomplete/json")
    Call<PlacePredictions> getPlaces(@Query("input") String text, @Query("types") String types, @Query("key") String apiKey);

    @GET("details/json")
    Call<PlaceDetails> getDetails(@Query("placeid") String id, @Query("key") String apiKey);
}
