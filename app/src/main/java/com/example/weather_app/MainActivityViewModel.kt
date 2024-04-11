package com.example.weather_app

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weather_app.data.api.RetrofitWeatherClient
import com.example.weather_app.data_classes.NewWeatherResponse
import com.example.weather_app.data_classes.SavedLocation

class MainActivityViewModel : ViewModel() {

    //TODO move data to sheared preferences!!!
    //TODO look for serializable or parcelable to save data as data class

    var retrofit = RetrofitWeatherClient.create()
    var text = MutableLiveData<String>()
    private val locations = MutableLiveData<List<SavedLocation>>()
    val savedLocations: LiveData<List<SavedLocation>> get() = locations

    fun addLocation(location: NewWeatherResponse) {
        val savedLocation = SavedLocation(
            location.name,
            location.coord.lat,
            location.coord.lat,
            false,
            location.main.temp,
            location.weather[0].description
        )
        val locations = locations.value?.toMutableList() ?: mutableListOf()
        locations.add(savedLocation)
        this.locations.value = locations
    }
}