package com.example.weather_app

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weather_app.data_classes.NewWeatherResponse
import com.example.weather_app.data_classes.Location
import com.example.weather_app.data_classes.SavedLocation

class MainActivityViewModel: ViewModel() {

    var text = MutableLiveData<String>()
    var location = MutableLiveData<Location>()
    private val _locations = MutableLiveData<List<SavedLocation>>()
    val savedLocations: LiveData<List<SavedLocation>> get() = _locations

    fun addLocation(location: NewWeatherResponse) {
        val savedLocation = SavedLocation(location.name, location.coord.lat, location.coord.lat, false, location.main.temp, location.weather[0].description)
        val locations = _locations.value?.toMutableList() ?: mutableListOf()
        locations.add(savedLocation)
        _locations.value = locations
    }
}