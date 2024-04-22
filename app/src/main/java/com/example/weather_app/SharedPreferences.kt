import android.content.Context
import android.util.Log
import com.example.weather_app.data_classes.Forecast
import com.google.gson.Gson
import com.example.weather_app.data_classes.SavedLocation
import com.example.weather_app.data_classes.WeatherForecastResponse
import com.example.weather_app.data_classes.WeatherResponse
import com.google.gson.reflect.TypeToken

class SharedPreferences(context: Context) {
    private val sharedPref = context.getSharedPreferences("weather_app", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val locationsKey = "locations"
    private val weatherLocationKey = "weather_location"
    private val temperatureUnitKey = "temperature_unit"
    private val speedUnitKey = "speed_unit"
    private val forecastKey = "forecast"

    fun saveLocations(location: List<WeatherResponse>) {
        val locationJson = gson.toJson(location)
        sharedPref.edit().putString(locationsKey, locationJson).apply()
    }

    fun getLocations(): List<WeatherResponse>? {
        val locationJson = sharedPref.getString(locationsKey, null)
        return if (locationJson != null) {
            val type = object : TypeToken<List<SavedLocation>>() {}.type
            gson.fromJson(locationJson, type)
        } else {
            null
        }
    }

    fun setWeatherLocation(location: WeatherResponse) {
        val locationJson = gson.toJson(location)
        sharedPref.edit().putString(weatherLocationKey, locationJson).apply()
    }

    fun getWeatherLocation(): WeatherResponse? {
        val locationJson = sharedPref.getString(weatherLocationKey, null)
        return if (locationJson != null) {
            gson.fromJson(locationJson, WeatherResponse::class.java)
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
        Log.i("Logcat", ("setSpeedUni: $unit").toString())
        sharedPref.edit().putString(speedUnitKey, unit).apply()
    }

    fun getSpeedUnit(): String {
        Log.i("Logcat", ("getSpeedUnit: ${sharedPref.getString(speedUnitKey, "m/s")}").toString())
        return sharedPref.getString(speedUnitKey, "m/s")!!
    }

    fun setLocationForecast(forecast: WeatherForecastResponse) {
        val locationForecastJson = gson.toJson(forecast)
        sharedPref.edit().putString(forecastKey, locationForecastJson).apply()
    }

    fun getLocationForecast(): WeatherForecastResponse? {
        val locationForecastJson = sharedPref.getString(forecastKey, null)
        return if (locationForecastJson != null) {
            gson.fromJson(locationForecastJson, WeatherForecastResponse::class.java)
        } else {
            null
        }
    }

    fun clearSharedPreferences() {
        sharedPref.edit().clear().apply()
    }
}