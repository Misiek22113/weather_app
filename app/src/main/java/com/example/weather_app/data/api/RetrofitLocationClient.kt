package com.example.weather_app.data.api

import com.example.weather_app.data_classes.Location
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiLocationService {
    @GET("geo/1.0/direct")
    suspend fun getLocation(
        @Query("q") query: String,
        @Query("limit") limit: Int,
        @Query("appid") apiKey: String
    ): Response<List<Location>>
}

object RetrofitLocationClient {
    private const val BASE_URL = "https://api.openweathermap.org/"

    fun create(): ApiLocationService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ApiLocationService::class.java)
    }
}