package com.example.weather_app.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weather_app.MainActivityViewModel
import com.example.weather_app.R
import com.example.weather_app.data_classes.Location
import com.example.weather_app.ui.fragments.SearchFragment

interface SearchLocationCardClickListener {
    fun onSearchLocationCardClick(location: Location)
}

class SearchLocationAdapter(
    private var locations: List<Location>,
    private val listener: SearchFragment,
    private val viewModel: MainActivityViewModel
) : RecyclerView.Adapter<SearchLocationAdapter.LocationViewHolder>() {

    inner class LocationViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val cityTextView: TextView = view.findViewById(R.id.cityName)
        val locationImage: ImageView = view.findViewById(R.id.locationImage)
        val countryTextView: TextView = view.findViewById(R.id.countryName)
        val checkmark: ImageView = view.findViewById(R.id.checkmarkIcon)

        init {
            view.setOnClickListener {
                val position = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val location = locations[position]
                    listener.onSearchLocationCardClick(location)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.search_location_card, parent, false)
        return LocationViewHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateLocations(newLocations: List<Location>) {
        this.locations = newLocations
        Log.i("Logcat", ("wyszukane: $newLocations").toString())
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val location = locations[position]
        holder.cityTextView.text = location.name
        holder.countryTextView.text = location.country

        if(viewModel.isLocationSaved(location)) {
            holder.checkmark.visibility = View.VISIBLE
        } else {
            holder.checkmark.visibility = View.GONE
        }
    }

    override fun getItemCount() = locations.size
}