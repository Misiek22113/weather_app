package com.example.weather_app.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

data class Location(
    val name: String,
    val lat: Double,
    val lon: Double,
    val country: String
)

interface ApiLocationService {
    @GET("geo/1.0/direct")
    fun getLocation(
        @Query("q") query: String,
        @Query("limit") limit: Int,
        @Query("appid") apiKey: String
    ): Call<List<Location>>
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