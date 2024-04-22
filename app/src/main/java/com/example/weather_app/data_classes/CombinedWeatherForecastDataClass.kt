package com.example.weather_app.data_classes

data class CombinedLocationData(
    val weatherData: WeatherData,
    val forecastData: WeatherForecastResponse
)