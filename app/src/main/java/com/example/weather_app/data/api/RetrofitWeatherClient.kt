package com.example.weather_app.data.api

import com.example.weather_app.data_classes.Location
import com.example.weather_app.data_classes.WeatherResponse
import com.example.weather_app.data_classes.WeatherForecastResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface ApiWeatherService {
    @GET("data/2.5/forecast")
    suspend fun getForecastWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
    ): Response<WeatherForecastResponse>

    @GET("geo/1.0/direct")
    suspend fun getLocations(
        @Query("q") query: String,
        @Query("limit") limit: Int,
        @Query("appid") apiKey: String
    ): Response<List<Location>>

    @GET("data/2.5/weather")
    suspend fun getLocationWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String
    ): Response<WeatherResponse>

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