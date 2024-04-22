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
import java.text.DecimalFormat


class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    val apiKey = BuildConfig.API_KEY
    var retrofit = RetrofitWeatherClient.create()
    var text = MutableLiveData<String>()
    var currentLocation = MutableLiveData<CombinedLocationData>()
    val savedLocations: LiveData<List<CombinedLocationData>> get() = locations
    private val locations = MutableLiveData<List<CombinedLocationData>>()
    private val sharedPreferences = SharedPreferences(application)
    private val selectedLocationData = MutableLiveData<WeatherData?>()
    val selectedLocationWeather: MutableLiveData<WeatherData?> get() = selectedLocationData
    private val internetConnectionManager: ConnectivityManager =
        application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

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

//    private fun fetchLocationData(lat: Double, lon: Double) {
//        CoroutineScope(Dispatchers.IO).launch {
//            val response = retrofit.getLocationWeather(lat, lon, apiKey)
//            if (response.isSuccessful) {
//                val locationResponse = response.body()
//                withContext(Dispatchers.Main) {
//                    addLocation(locationResponse!!, lat, lon)
//                }
//            } else {
//                response.errorBody()?.let {
//                    val errorBodyString = it.string()
//                    Log.i("Logcat", errorBodyString)
//                }
//            }
//        }
//    }

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


    //    fun fetchCurrentWeather(lat: Double, lon: Double, apiKey: String) {
//        CoroutineScope(Dispatchers.IO).launch {
//            val response = retrofit.getLocationWeather(lat, lon, apiKey)
//            if (response.isSuccessful) {
//                val locationResponse = response.body()
//                withContext(Dispatchers.Main) {
//                    selectedLocationData.value = locationResponse
//                    Log.i("Logcat", locationResponse.toString())
//                }
//            } else {
//                response.errorBody()?.let {
//                    val errorBodyString = it.string()
//                    Log.i("Logcat", errorBodyString)
//                }
//            }
//        }
//    }
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

//    fun fetchForecastWeather(lat: Double, lon: Double, apiKey: String) {
//        CoroutineScope(Dispatchers.IO).launch {
//            val response = retrofit.getForecastWeather(lat, lon, apiKey)
//            if (response.isSuccessful) {
//                val locationResponse = response.body()
//                withContext(Dispatchers.Main) {
//                    selectedLocationForecastData.value = locationResponse
//                    sharedPreferences.setLocationForecast(locationResponse!!)
//                    Log.i("Logcat", locationResponse.toString())
//                }
//            } else {
//                response.errorBody()?.let {
//                    val errorBodyString = it.string()
//                    Log.i("Logcat", errorBodyString)
//                }
//            }
//        }
//    }

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
                    updatedLocationForecastData!!
                )
                locations[index] = combinedLocationData
                sharedPreferences.saveLocations(locations)
            }
        }
    }

    private fun addLocation(locationData: WeatherData, locationForecast: WeatherForecastResponse) {
        val combinedLocationData = CombinedLocationData(
            locationData,
            locationForecast
        )

        val locations = locations.value?.toMutableList() ?: mutableListOf()
        locations.add(combinedLocationData)

        this.locations.value = locations
        sharedPreferences.saveLocations(locations)

        Log.i("Logcat", ("Wybrane Miasto: $combinedLocationData").toString())
    }

    private suspend fun fetchUpdateLocationData(
        lat: Double,
        lon: Double,
        apiKey: String
    ): WeatherData? {
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

//    fun updateSavedLocations() {
//        CoroutineScope(Dispatchers.IO).launch {
//            val locations = locations.value?.toMutableList() ?: mutableListOf()
//            locations.forEachIndexed { index, savedLocation ->
//                val updatedLocation = fetchUpdateLocationData(
//                    savedLocation.coord.lat,
//                    savedLocation.coord.lon,
//                    apiKey
//                )
//                updatedLocation?.let {
//                    val newSavedLocation = it
//                    locations[index] = newSavedLocation
//                }
//            }
//            withContext(Dispatchers.Main) {
//                this@MainActivityViewModel.locations.value = locations
//                sharedPreferences.saveLocations(locations)
//            }
//        }
//    }

//    fun updateSelectedLocationWeather() {
//        CoroutineScope(Dispatchers.IO).launch {
//            Log.i("Logcat", ("Api_key: $apiKey").toString())
//            val currentLocation = currentLocation.value
//            currentLocation?.let {
//                val updatedLocation = fetchUpdateLocationData(
//                    it.coord.lat,
//                    it.coord.lon,
//                    apiKey
//                )
//                updatedLocation?.let {
//                    withContext(Dispatchers.Main) {
//                        selectedLocationData.value = updatedLocation
//                    }
//                }
//            }
//        }
//    }

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
        this.currentLocation.value = location
        sharedPreferences.setCurrentWeatherLocation(location)
    }

    fun getCurrentLocation() {
        this.currentLocation.value = sharedPreferences.getCurrentWeatherLocation()
    }

    fun isLocationSaved(location: Location): Boolean {
        return locations.value?.any {
            it.weatherData.coord.lat == location.lat && it.weatherData.coord.lon == location.lon
        }
            ?: false
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