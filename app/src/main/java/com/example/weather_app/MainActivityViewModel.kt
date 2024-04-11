package com.example.weather_app

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weather_app.data_classes.Location

class MainActivityViewModel: ViewModel() {

    var text = MutableLiveData<String>()
    var location = MutableLiveData<Location>()
    var savedLocations = MutableLiveData<List<Location>>()

    fun addLocation(location: Location) {
        this.savedLocations.value?.toMutableList()?.add(location)
    }

    fun getLocation() : MutableLiveData<Location> {
        return this.savedLocations.value?.let { MutableLiveData(it[0]) } ?: MutableLiveData()
    }
}