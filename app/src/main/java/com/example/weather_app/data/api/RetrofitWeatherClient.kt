package com.example.weather_app.data.api

import com.example.weather_app.data_classes.Location
import com.example.weather_app.data_classes.NewWeatherResponse
import com.example.weather_app.data_classes.WeatherResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//TODO move to coroutine (change return Response not Call)
interface ApiWeatherService {
    @GET("data/2.5/forecast")
    suspend fun getWeatherData(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String
    ): Response<WeatherResponse>

    @GET("geo/1.0/direct")
    suspend fun getCurrentWeather(
        @Query("q") query: String,
        @Query("limit") limit: Int,
        @Query("appid") apiKey: String
    ): Response<List<Location>>

    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String
    ): Response<NewWeatherResponse>

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