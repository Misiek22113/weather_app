package com.example.weather_app

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weather_app.data_classes.Location
import com.example.weather_app.data_classes.SavedLocation

class MainActivityViewModel: ViewModel() {

    var text = MutableLiveData<String>()
    var location = MutableLiveData<Location>()
    private val _locations = MutableLiveData<List<SavedLocation>>()
    val savedLocations: LiveData<List<SavedLocation>> get() = _locations

    fun addLocation(location: Location) {
        val savedLocation = SavedLocation(location.name, location.lat, location.lon, "UK", false, 0.0, "")
        val locations = _locations.value?.toMutableList() ?: mutableListOf()
        locations.add(savedLocation)
        _locations.value = locations
    }
}