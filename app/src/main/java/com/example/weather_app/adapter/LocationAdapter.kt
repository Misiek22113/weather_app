package com.example.weather_app.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weather_app.MainActivityViewModel
import com.example.weather_app.R
import com.example.weather_app.data_classes.CombinedLocationData
import com.example.weather_app.data_classes.WeatherData
import com.example.weather_app.ui.fragments.LocationFragment

interface LocationCardClickListener {
    fun onLocationCardClick(location: CombinedLocationData)
}

class LocationAdapter(
    private var locations: List<CombinedLocationData>,
    private val viewModel: MainActivityViewModel,
    private val listener: LocationFragment
) : RecyclerView.Adapter<LocationAdapter.LocationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.location_card, parent, false)
        return LocationViewHolder(view)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val location = locations[position]
        holder.cityTextView.text = location.weatherData.name
        holder.temperatureTextView.text =
            viewModel.getTemperature(location.weatherData.main.temp).toString()
                .plus("°" + viewModel.getTemperatureUnit().slice(0..0).uppercase())
        holder.weatherImageView.setImageResource(
            viewModel.getWeatherIcon(
                location.weatherData.weather[0].main,
                location.weatherData.weather[0].description
            )
        )
    }

    override fun getItemCount(): Int {
        return locations.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateLocations(newLocations: List<CombinedLocationData>) {
        Log.i("Logcat", newLocations.toString())
        this.locations = newLocations
        notifyDataSetChanged()
    }

    inner class LocationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cityTextView: TextView = view.findViewById(R.id.locationNameText)
        val temperatureTextView: TextView = view.findViewById(R.id.temperatureText)
        val weatherImageView: ImageView = view.findViewById(R.id.weatherIcon)
        val deleteButton: ImageButton = view.findViewById(R.id.deleteButton)

        init {
            deleteButton.setOnClickListener {
                val location = locations[absoluteAdapterPosition]
                viewModel.deleteLocation(location)
            }

            view.setOnClickListener {
                val position = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val location = locations[absoluteAdapterPosition]
                    listener.onLocationCardClick(location)
                }
            }
        }
    }
}