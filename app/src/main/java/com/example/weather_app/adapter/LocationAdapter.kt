package com.example.weather_app.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.weather_app.data.api.Location

class LocationAdapter(private var locations: List<Location>){

    class LocationViewHolder(val view: View) : RecyclerView.ViewHolder(view){

    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val location = locations[position]
        holder.cityTextView.text = location.name
        holder.countryTextView.text = location.country
    }

}
