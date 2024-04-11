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
import com.example.weather_app.ui.search.SearchFragment

interface LocationCardClickListener {
    fun onLocationCardClick(location: Location)
}

class SearchLocationAdapter(private var locations: List<Location>, private val listener: SearchFragment) : RecyclerView.Adapter<SearchLocationAdapter.LocationViewHolder>() {

    inner class LocationViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val cityTextView: TextView = view.findViewById(R.id.cityName)
        val locationImage: ImageView = view.findViewById(R.id.locationImage)
        val countryTextView: TextView = view.findViewById(R.id.countryName)

        init {
            view.setOnClickListener{
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val location = locations[position]
                    listener.onLocationCardClick(location)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_location_card, parent, false)
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