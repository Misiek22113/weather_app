package com.example.weather_app

import SharedPreferences
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.weather_app.data.api.RetrofitWeatherClient
import com.example.weather_app.data_classes.Location
import com.example.weather_app.data_classes.NewWeatherResponse
import com.example.weather_app.data_classes.SavedLocation

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    var retrofit = RetrofitWeatherClient.create()
    var text = MutableLiveData<String>()
    private val locations = MutableLiveData<List<SavedLocation>>()
    val savedLocations: LiveData<List<SavedLocation>> get() = locations
    private val sharedPreferences = SharedPreferences(application)

    fun addLocation(location: NewWeatherResponse, lat: Double, lon: Double){
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
    }

    fun deleteLocation(location: SavedLocation) {
        val locations = locations.value?.toMutableList() ?: mutableListOf()
        locations.remove(location)
        this.locations.value = locations
        sharedPreferences.saveLocations(locations)
    }

    fun isLocationSaved(location: Location): Boolean {
        return locations.value?.any {
            it.name == location.name &&
                    it.lat == location.lat && it.lon == location.lon
        }
            ?: false
    }

    fun setLocations() {
        locations.value = sharedPreferences.getLocations()
    }

    fun setTemperatureUnit(isChecked: Boolean) {
        val unit = if (isChecked) "celsius" else "fahrenheit"
        sharedPreferences.setTemperatureUnit(unit)
    }

    fun getTemperatureUnit(): String {
        return sharedPreferences.getTemperatureUnit()
    }

    fun getTemperature(temp: Double): Int {
        val unit = sharedPreferences.getTemperatureUnit()
        if(unit == "celsius") {
            return (temp - 273.15).toInt()
        }
        else {
            return temp.toInt()
        }
    }

    fun setSpeedUnit(isChecked: Boolean) {
        val unit = if (isChecked) "m/s" else "mph"
        sharedPreferences.setSpeedUnit(unit)
    }

    fun getSpeed(speed: Int): Int {
        val unit = sharedPreferences.getSpeedUnit()
        if(unit == "m/s") {
            return speed
        }
        else {
            return speed * 2
        }
    }
}