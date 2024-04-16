import android.content.Context
import com.google.gson.Gson
import com.example.weather_app.data_classes.SavedLocation
import com.google.gson.reflect.TypeToken

class SharedPreferences(context: Context) {
    private val sharedPref = context.getSharedPreferences("weather_app", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val locationsKey = "locations"

    fun saveLocations(location: List<SavedLocation>) {
        val locationJson = gson.toJson(location)
        sharedPref.edit().putString(locationsKey, locationJson).apply()
    }

    fun updateLocations(location: SavedLocation) {
        val locationJson = gson.toJson(location)
        sharedPref.edit().putString(locationsKey, locationJson).apply()
    }

    fun getLocations(): List<SavedLocation>? {
        val locationJson = sharedPref.getString(locationsKey, null)
        return if (locationJson != null) {
            val type = object : TypeToken<List<SavedLocation>>() {}.type
            gson.fromJson(locationJson, type)
        } else {
            null
        }
    }
}