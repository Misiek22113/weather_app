package com.example.weather_app.data_classes

import com.google.gson.annotations.SerializedName

data class NewWeatherResponse(
    val coord: NewCoord,
    val weather: List<NewWeather>,
    val base: String,
    val main: NewMain,
    val visibility: Int,
    val wind: NewWind,
    val rain: NewRain,
    val clouds: NewClouds,
    val dt: Long,
    val sys: NewSys,
    val timezone: Int,
    val id: Int,
    val name: String,
    val cod: Int
)

data class NewCoord(
    var lon: Double,
    var lat: Double
)

data class NewWeather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

data class NewMain(
    val temp: Double,
    @SerializedName("feels_like") val feelsLike: Double,
    @SerializedName("temp_min") val tempMin: Double,
    @SerializedName("temp_max") val tempMax: Double,
    val pressure: Int,
    val humidity: Int,
    @SerializedName("sea_level") val seaLevel: Int,
    @SerializedName("grnd_level") val groundLevel: Int
)

data class NewWind(
    val speed: Double,
    val deg: Int,
    val gust: Double
)

data class NewRain(
    @SerializedName("1h") val oneHour: Double
)

data class NewClouds(
    val all: Int
)

data class NewSys(
    val type: Int,
    val id: Int,
    val country: String,
    val sunrise: Long,
    val sunset: Long
)