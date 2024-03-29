package com.example.weatherapp

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterFace {
    @GET("weather")
    fun getWeatherData(
        @Query("q") city: String,  // Specify the city name for the weather data
        @Query("appid") appid: String,  // Specify the API key
        @Query("units") units: String  // Specify the units for temperature (e.g., metric, imperial)
    ):Call<WeatherApp>
}
