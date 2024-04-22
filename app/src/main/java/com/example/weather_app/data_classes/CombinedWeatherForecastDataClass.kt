package com.example.weather_app.data_classes

data class CombinedData(
    val weatherData: WeatherData,
    val forecastData: WeatherForecastResponse
)