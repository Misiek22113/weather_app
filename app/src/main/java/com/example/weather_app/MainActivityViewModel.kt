package com.example.weather_app

import SharedPreferences
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.weather_app.adapter.SearchLocationAdapter
import com.example.weather_app.data.api.RetrofitWeatherClient
import com.example.weather_app.data_classes.Location
import com.example.weather_app.data_classes.NewWeatherResponse
import com.example.weather_app.data_classes.SavedLocation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    var retrofit = RetrofitWeatherClient.create()
    var text = MutableLiveData<String>()
    var currentLocation = MutableLiveData<SavedLocation>()
    val savedLocations: LiveData<List<SavedLocation>> get() = locations
    private val locations = MutableLiveData<List<SavedLocation>>()
    private val sharedPreferences = SharedPreferences(application)
    private val selectedLocationData = MutableLiveData<NewWeatherResponse?>()
    val selectedLocationWeather: MutableLiveData<NewWeatherResponse?> get() = selectedLocationData

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

    fun fetchCurrentWeather(lat: Double, lon: Double, apiKey: String){
        CoroutineScope(Dispatchers.IO).launch {
            val response = retrofit.getLocationWeather(lat, lon, apiKey)
            if (response.isSuccessful) {
                val locationResponse = response.body()
                withContext(Dispatchers.Main) {
//                    textView.text = weatherResponse?.list?.get(0).toString()
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

    private fun addLocation(location: NewWeatherResponse, lat: Double, lon: Double) {
        val savedLocation = SavedLocation(
            location.name,
            location.coord.lat,
            location.coord.lon,
            false,
            location.main.temp,
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

    fun getCurrentLocation(){
        this.currentLocation.value = sharedPreferences.getWeatherLocation()
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
        val unit = if (isChecked) "m/s" else "mph"
        sharedPreferences.setSpeedUnit(unit)
    }

    fun getSpeed(speed: Int): Int {
        val unit = sharedPreferences.getSpeedUnit()
        if (unit == "m/s") {
            return speed
        } else {
            return speed * 2
        }
    }

    fun getWeatherIcon(description: String): Int {
        return when (description) {
            "clear sky" -> R.drawable.sun
            "few clouds" -> R.drawable.few_clouds
            "scattered clouds" -> R.drawable.broken_clouds
            "broken clouds" -> R.drawable.broken_clouds
            "shower rain" -> R.drawable.rain
            "rain" -> R.drawable.rain
            "thunderstorm" -> R.drawable.storm
            "snow" -> R.drawable.snow
            "mist" -> R.drawable.mist
            else -> R.drawable.sun
        }
    }
}