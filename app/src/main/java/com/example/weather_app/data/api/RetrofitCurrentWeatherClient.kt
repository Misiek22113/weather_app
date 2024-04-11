package com.example.weather_app.data.api

import com.example.weather_app.data_classes.NewWeatherResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrentWeatherService {
    @GET("data/2.5/weather")
    suspend fun getLocation(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String
    ): Response<NewWeatherResponse>
}

object RetrofitCurrentWeatherClient {
    private const val BASE_URL = "https://api.openweathermap.org/"

    fun create(): CurrentWeatherService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(CurrentWeatherService::class.java)
    }
}