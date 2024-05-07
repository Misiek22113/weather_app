import android.content.Context
import android.util.Log
import com.example.weather_app.data_classes.CombinedLocationData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPreferences(context: Context) {
    private val sharedPref = context.getSharedPreferences("weather_app", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val locationsKey = "locations"
    private val weatherLocationKey = "weather_location"
    private val temperatureUnitKey = "temperature_unit"
    private val speedUnitKey = "speed_unit"

    fun saveLocations(location: List<CombinedLocationData>) {
        val locationJson = gson.toJson(location)
        sharedPref.edit().putString(locationsKey, locationJson).apply()
    }

    fun getLocations(): List<CombinedLocationData>? {
        val locationJson = sharedPref.getString(locationsKey, null)
        return if (locationJson != null) {
            val type = object : TypeToken<List<CombinedLocationData>>() {}.type
            gson.fromJson(locationJson, type)
        } else {
            null
        }
    }

    fun setCurrentWeatherLocation(location: CombinedLocationData) {
        val locationJson = gson.toJson(location)
        sharedPref.edit().putString(weatherLocationKey, locationJson).apply()
    }

    fun getCurrentWeatherLocation(): CombinedLocationData? {
        val locationJson = sharedPref.getString(weatherLocationKey, null)
        return if (locationJson != null) {
            gson.fromJson(locationJson, CombinedLocationData::class.java)
        } else {
            null
        }
    }

    fun setTemperatureUnit(unit: String) {
        sharedPref.edit().putString(temperatureUnitKey, unit).apply()
    }

    fun getTemperatureUnit(): String {
        return sharedPref.getString(temperatureUnitKey, "celsius")!!
    }

    fun setSpeedUnit(unit: String) {
        sharedPref.edit().putString(speedUnitKey, unit).apply()
    }

    fun getSpeedUnit(): String {
        return sharedPref.getString(speedUnitKey, "m/s")!!
    }

}