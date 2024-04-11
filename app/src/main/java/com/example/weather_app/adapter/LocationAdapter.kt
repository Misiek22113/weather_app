package com.example.weather_app.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weather_app.R
import com.example.weather_app.data_classes.Location
import com.example.weather_app.data_classes.SavedLocation

class LocationAdapter(private var locations: List<SavedLocation>) : RecyclerView.Adapter<LocationAdapter.LocationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val view = View.inflate(parent.context, R.layout.location_card, null)
        return LocationViewHolder(view)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val location = locations[position]
        holder.cityTextView.text = location.name
        holder.temperatureTextView.text = location.temperature.toString()
    }

    override fun getItemCount(): Int {
        return locations.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateLocations(newLocations: List<SavedLocation>) {
        Log.i("LocationAdapter", newLocations.toString())
        this.locations = newLocations
        notifyDataSetChanged()
    }

    inner class LocationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cityTextView: TextView = view.findViewById(R.id.cityName)
        val temperatureTextView: TextView = view.findViewById(R.id.temperatureText)
        val weatherImageView: ImageView = view.findViewById(R.id.weatherIcon)
        val likeImageView: ImageView = view.findViewById(R.id.likeIcon)
    }
}