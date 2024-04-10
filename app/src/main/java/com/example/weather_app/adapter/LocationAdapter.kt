package com.example.weather_app.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weather_app.R
import com.example.weather_app.data.api.Location

class LocationAdapter(private var locations: List<Location>) : RecyclerView.Adapter<LocationAdapter.LocationViewHolder>() {

    class LocationViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val cityTextView: TextView = view.findViewById(R.id.cityName)
        val locationImage: ImageView = view.findViewById(R.id.locationImage)
        val countryTextView: TextView = view.findViewById(R.id.countryName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.location_card, parent, false)
        return LocationViewHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateLocations(newLocations: List<Location>) {
        Log.i("LocationAdapter", newLocations.toString())
       this.locations = newLocations
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val location = locations[position]
        holder.cityTextView.text = location.name
        holder.countryTextView.text = location.country
    }

    override fun getItemCount() = locations.size
}