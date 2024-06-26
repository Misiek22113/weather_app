package com.example.weather_app

import SharedPreferences
import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.weather_app.adapter.SearchLocationAdapter
import com.example.weather_app.data.api.RetrofitWeatherClient
import com.example.weather_app.data_classes.Location
import com.example.weather_app.data_classes.WeatherResponse
import com.example.weather_app.data_classes.SavedLocation
import com.example.weather_app.data_classes.WeatherForecastResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat


class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    val apiKey = BuildConfig.API_KEY
    var retrofit = RetrofitWeatherClient.create()
    var text = MutableLiveData<String>()
    var currentLocation = MutableLiveData<SavedLocation>()
    val savedLocations: LiveData<List<SavedLocation>> get() = locations
    private val locations = MutableLiveData<List<SavedLocation>>()
    private val sharedPreferences = SharedPreferences(application)
    private val selectedLocationData = MutableLiveData<WeatherResponse?>()
    private val selectedLocationForecastData = MutableLiveData<WeatherForecastResponse?>()
    val selectedLocationWeather: MutableLiveData<WeatherResponse?> get() = selectedLocationData
    val selectedLocationForecast: MutableLiveData<WeatherForecastResponse?> get() = selectedLocationForecastData

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

    fun fetchLocationData(lat: Double, lon: Double, apiKey: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = retrofit.getLocationWeather(lat, lon, apiKey)
            if (response.isSuccessful) {
                val locationResponse = response.body()
                withContext(Dispatchers.Main) {
                    addLocation(locationResponse!!, lat, lon)
                }
            } else {
                response.errorBody()?.let {
                    val errorBodyString = it.string()
                    Log.i("Logcat", errorBodyString)
                }
            }
        }
    }

    fun fetchCurrentWeather(lat: Double, lon: Double, apiKey: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = retrofit.getLocationWeather(lat, lon, apiKey)
            if (response.isSuccessful) {
                val locationResponse = response.body()
                withContext(Dispatchers.Main) {
                    selectedLocationData.value = locationResponse
                    Log.i("Logcat", locationResponse.toString())
                }
            } else {
                response.errorBody()?.let {
                    val errorBodyString = it.string()
                    Log.i("Logcat", errorBodyString)
                }
            }
        }
    }

    fun fetchForecastWeather(lat: Double, lon: Double, apiKey: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = retrofit.getForecastWeather(lat, lon, apiKey)
            if (response.isSuccessful) {
                val locationResponse = response.body()
                withContext(Dispatchers.Main) {
                    selectedLocationForecastData.value = locationResponse
                    sharedPreferences.setLocationForecast(locationResponse!!)
                    Log.i("Logcat", locationResponse.toString())
                }
            } else {
                response.errorBody()?.let {
                    val errorBodyString = it.string()
                    Log.i("Logcat", errorBodyString)
                }
            }
        }
    }

    private fun addLocation(location: WeatherResponse, lat: Double, lon: Double) {
        val savedLocation = SavedLocation(
            location.name,
            location.coord.lat,
            location.coord.lon,
            false,
            location.main.temp,
            location.weather[0].main,
            location.weather[0].description
        )
        savedLocation.lat = lat
        savedLocation.lon = lon
        val locations = locations.value?.toMutableList() ?: mutableListOf()
        locations.add(savedLocation)
        this.locations.value = locations
        sharedPreferences.saveLocations(locations)
        Log.i("Logcat", ("Wybrane Miasto: $savedLocation").toString())
    }

    private suspend fun fetchUpdateLocationData(
        lat: Double,
        lon: Double,
        apiKey: String
    ): WeatherResponse? {
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

    fun updateSavedLocations() {
        CoroutineScope(Dispatchers.IO).launch {
            val locations = locations.value?.toMutableList() ?: mutableListOf()
            locations.forEachIndexed { index, savedLocation ->
                val updatedLocation = fetchUpdateLocationData(
                    savedLocation.lat,
                    savedLocation.lon,
                    apiKey
                )
                updatedLocation?.let {
                    val newSavedLocation = SavedLocation(
                        it.name,
                        it.coord.lat,
                        it.coord.lon,
                        false,
                        it.main.temp,
                        it.weather[0].main,
                        it.weather[0].description
                    )
                    locations[index] = newSavedLocation
                }
            }
            withContext(Dispatchers.Main) {
                this@MainActivityViewModel.locations.value = locations
                sharedPreferences.saveLocations(locations)
            }
        }
    }

    fun updateSelectedLocationWeather() {
        CoroutineScope(Dispatchers.IO).launch {
            Log.i("Logcat", ("Api_key: $apiKey").toString())
            val currentLocation = currentLocation.value
            currentLocation?.let {
                val updatedLocation = fetchUpdateLocationData(
                    it.lat,
                    it.lon,
                    apiKey
                )
                updatedLocation?.let {
                    withContext(Dispatchers.Main) {
                        selectedLocationData.value = updatedLocation
                    }
                }
            }
        }
    }

    fun deleteLocation(location: SavedLocation) {
        val locations = locations.value?.toMutableList() ?: mutableListOf()
        locations.remove(location)
        this.locations.value = locations
        sharedPreferences.saveLocations(locations)
    }

    fun setCurrentLocation(location: SavedLocation) {
        this.currentLocation.value = location
        sharedPreferences.setWeatherLocation(location)
    }

    fun getCurrentLocation() {
        this.currentLocation.value = sharedPreferences.getWeatherLocation()
    }

    fun getCurrentLocationForecast() {
        this.selectedLocationForecastData.value = sharedPreferences.getLocationForecast()
    }

    fun isLocationSaved(location: Location): Boolean {
        return locations.value?.any {
            it.lat == location.lat && it.lon == location.lon
        }
            ?: false
    }

    fun setLocations() {
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
        if (unit == "m/s") {
            return df.format(speed).toString()
        } else {
            return df.format(speed * 2.23).toString()
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