package com.example.weather_app.data_classes

data class WeatherForecastResponse(
    val cod: String,
    val message: Int,
    val cnt: Int,
    val list: List<Forecast>,
    val city: City
)

data class City(
    val id: Int,
    val name: String,
    val country: String,
    val timezone: Int,
    val sunrise: Long,
    val sunset: Long
)
data class Forecast(
    val dt: Long,
    val main: Main,
    val weather: List<Weather>,
    val clouds: Clouds,
    val wind: Wind,
    val visibility: Int,
    val pop: Double,
    val sys: Sys,
)