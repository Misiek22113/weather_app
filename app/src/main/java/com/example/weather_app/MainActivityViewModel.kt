package com.example.weather_app

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weather_app.data.api.Location

class MainActivityViewModel: ViewModel() {

    var text = MutableLiveData<String>()
    var location = MutableLiveData<Location>()

    fun addLocation(location: Location) {
        this.location.value = location
    }

    fun getLocation() : MutableLiveData<Location> {
        return location
    }
}