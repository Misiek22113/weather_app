package com.example.weather_app.data.api

import com.example.weather_app.data_classes.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface ApiWeatherService {
    @GET("data/2.5/forecast")
    fun getWeatherData(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String
    ): Call<WeatherResponse>
}

object RetrofitWeatherClient {
    private const val BASE_URL = "https://api.openweathermap.org/"

    fun create(): ApiWeatherService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ApiWeatherService::class.java)
    }
}