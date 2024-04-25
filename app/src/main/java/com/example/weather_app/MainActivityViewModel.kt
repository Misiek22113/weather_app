package com.example.weather_app

import SharedPreferences
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.example.weather_app.adapter.SearchLocationAdapter
import com.example.weather_app.data.api.RetrofitWeatherClient
import com.example.weather_app.data_classes.CombinedLocationData
import com.example.weather_app.data_classes.Location
import com.example.weather_app.data_classes.WeatherData
import com.example.weather_app.data_classes.WeatherForecastResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Time
import java.text.DecimalFormat


class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val apiKey = BuildConfig.API_KEY
    private var retrofit = RetrofitWeatherClient.create()
    private val locations = MutableLiveData<List<CombinedLocationData>>()
    private val sharedPreferences = SharedPreferences(application)
    private val internetConnectionManager: ConnectivityManager =
        application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    var text = MutableLiveData<String>()
    var currentLocation = MutableLiveData<CombinedLocationData>()
    val savedLocations: LiveData<List<CombinedLocationData>> get() = locations

    fun isInternetConnectionEstablished(): Boolean {
        Log.i("Logcat", ("isInternetConnectionEstablished").toString())
        val activeNetwork = internetConnectionManager.activeNetwork
        return activeNetwork != null
    }

    fun fetchLocation(
        query: String,
        limit: Int,
        apiKey: String,
        searchLocationsAdapter: SearchLocationAdapter
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = retrofit.getLocations(query, limit, apiKey)
            if (response.isSuccessful) {
                val locationResponse = response.body()
                withContext(Dispatchers.Main) {
                    searchLocationsAdapter.updateLocations(locationResponse!!)
                }
            } else {
                response.errorBody()?.let {
                    val errorBodyString = it.string()
                    Log.i("Logcat", errorBodyString)
                }
            }
        }
    }

    private suspend fun fetchLocationData(lat: Double, lon: Double): WeatherData? {
        val response = retrofit.getLocationWeather(lat, lon, apiKey)
        return if (response.isSuccessful) {
            response.body()
        } else {
            response.errorBody()?.let {
                val errorBodyString = it.string()
                Log.i("Logcat", errorBodyString)
            }
            null
        }
    }

    private suspend fun fetchForecastWeather(lat: Double, lon: Double): WeatherForecastResponse? {
        val response = retrofit.getForecastWeather(lat, lon, apiKey)
        return if (response.isSuccessful) {
            response.body()
        } else {
            response.errorBody()?.let {
                val errorBodyString = it.string()
                Log.i("Logcat", errorBodyString)
            }
            null
        }
    }

    fun createNewCombinedLocationData(lat: Double, lon: Double) {
        CoroutineScope(Dispatchers.IO).launch {
            val locationData = fetchLocationData(lat, lon)
            val locationForecast = fetchForecastWeather(lat, lon)
            addLocation(locationData!!, locationForecast!!)
        }
    }

    fun updateSavedLocationsData() {
        CoroutineScope(Dispatchers.IO).launch {
            val locations = locations.value?.toMutableList() ?: mutableListOf()
            locations.forEachIndexed { index, savedLocation ->
                val updatedLocationWeatherData = fetchLocationData(
                    savedLocation.weatherData.coord.lat,
                    savedLocation.weatherData.coord.lon
                )
                val updatedLocationForecastData = fetchForecastWeather(
                    savedLocation.weatherData.coord.lat,
                    savedLocation.weatherData.coord.lon
                )
                val combinedLocationData = CombinedLocationData(
                    updatedLocationWeatherData!!,
                    updatedLocationForecastData!!,
                    Time(System.currentTimeMillis()).time
                )
                locations[index] = combinedLocationData
                sharedPreferences.saveLocations(locations)
            }
        }
    }

    fun updateCurrentLocationData() {
        CoroutineScope(Dispatchers.IO).launch {
            val currentLocation = currentLocation.value
            if (currentLocation != null) {
                val updatedLocationWeatherData = fetchLocationData(
                    currentLocation.weatherData.coord.lat,
                    currentLocation.weatherData.coord.lon
                )
                val updatedLocationForecastData = fetchForecastWeather(
                    currentLocation.weatherData.coord.lat,
                    currentLocation.weatherData.coord.lon
                )
                val combinedLocationData = CombinedLocationData(
                    updatedLocationWeatherData!!,
                    updatedLocationForecastData!!,
                    Time(System.currentTimeMillis()).time
                )
//                updateSingleSavedLocation(combinedLocationData)
                setCurrentLocation(combinedLocationData)
            }
        }
    }

//    private fun updateSingleSavedLocation(data: CombinedLocationData) {
//        val locations = locations.value?.toMutableList() ?: mutableListOf()
//        val index = locations.indexOf(currentLocation.value)
//        locations[index] = data
//    }

    private suspend fun addLocation(
        locationData: WeatherData,
        locationForecast: WeatherForecastResponse
    ) {
        val combinedLocationData = CombinedLocationData(
            locationData,
            locationForecast,
            Time(System.currentTimeMillis()).time
        )

        withContext(Dispatchers.Main) {
            val locations = locations.value?.toMutableList() ?: mutableListOf()
            locations.add(combinedLocationData)

            this@MainActivityViewModel.locations.value = locations
            sharedPreferences.saveLocations(locations)
        }

        Log.i("Logcat", ("Wybrane Miasto: $combinedLocationData").toString())
    }

    fun deleteLocation(location: CombinedLocationData) {
        val locations = locations.value?.toMutableList() ?: mutableListOf()
        val locationToDelete = locations.find {
            it.weatherData.coord.lat == location.weatherData.coord.lat && it.weatherData.coord.lon == location.weatherData.coord.lon
        }
        locations.remove(locationToDelete)
        this.locations.value = locations
        sharedPreferences.saveLocations(locations)
    }

    fun setCurrentLocation(location: CombinedLocationData) {
        CoroutineScope(Dispatchers.Main).launch {
            this@MainActivityViewModel.currentLocation.value = location
            sharedPreferences.setCurrentWeatherLocation(location)
        }
    }

    fun getCurrentLocation() {
        val location = sharedPreferences.getCurrentWeatherLocation()
        if (location != null) {
            this.currentLocation.value = location
        }
    }

    fun isLocationSaved(location: Location): Boolean {
        return locations.value?.any {
            it.weatherData.coord.lat == location.lat && it.weatherData.coord.lon == location.lon
        }
            ?: false
    }

    fun getUpdateTime(time: Long): Long {
        return (System.currentTimeMillis() - time) / 1000 / 60
    }

    fun getLocationsFromStorage() {
        locations.value = sharedPreferences.getLocations()
    }

    fun setTemperatureUnit(isChecked: Boolean) {
        val unit = if (isChecked) "celsius" else "kelvin"
        sharedPreferences.setTemperatureUnit(unit)
    }

    fun getTemperatureUnit(): String {
        return sharedPreferences.getTemperatureUnit()
    }

    fun getTemperature(temp: Double): Int {
        val unit = sharedPreferences.getTemperatureUnit()
        if (unit == "celsius") {
            return (temp - 273.15).toInt()
        } else {
            return temp.toInt()
        }
    }

    fun setSpeedUnit(isChecked: Boolean) {
        Log.i("Logcat", ("setSpeedUnit ViewModel: $isChecked").toString())
        val unit = if (isChecked) "mph" else "m/s"
        sharedPreferences.setSpeedUnit(unit)
    }

    fun getSpeedUnit(): String {
        return sharedPreferences.getSpeedUnit()
    }

    fun getSpeed(speed: Double): String {
        val unit = sharedPreferences.getSpeedUnit()
        val df = DecimalFormat("#.##")
        return if (unit == "m/s") {
            df.format(speed).toString()
        } else {
            df.format(speed * 2.23).toString()
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun getSunriseSunset(time: Long): String {
        val date = java.util.Date(time * 1000)
        val formatter = java.text.SimpleDateFormat("HH:mm")
        return formatter.format(date)
    }

    fun getWeatherIcon(weather: String, weatherDescription: String): Int {
        if (weather == "Clouds") {
            return when (weatherDescription) {
                "few clouds" -> R.drawable.few_clouds
                "scattered clouds" -> R.drawable.few_clouds
                "broken clouds" -> R.drawable.broken_clouds
                "overcast clouds" -> R.drawable.broken_clouds
                else -> R.drawable.broken_clouds
            }
        }

        return when (weather) {
            "Clear" -> R.drawable.sun
            "Rain" -> R.drawable.rain
            "Thunderstorm" -> R.drawable.storm
            "Snow" -> R.drawable.snow
            "Mist" -> R.drawable.mist
            "Drizzle" -> R.drawable.drizzle
            else -> R.drawable.sun
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun getHour(timestamp: Long): String {
        val date = java.util.Date(timestamp * 1000)
        val formatter = java.text.SimpleDateFormat("HH:mm")
        return formatter.format(date)
    }

}