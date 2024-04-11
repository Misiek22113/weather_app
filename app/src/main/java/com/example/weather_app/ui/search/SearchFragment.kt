package com.example.weather_app.ui.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weather_app.MainActivityViewModel
import com.example.weather_app.adapter.SearchLocationAdapter
import com.example.weather_app.adapter.LocationCardClickListener
import com.example.weather_app.data.api.RetrofitCurrentWeatherClient
import com.example.weather_app.data.api.RetrofitLocationClient
import com.example.weather_app.data_classes.Location
import com.example.weather_app.databinding.FragmentSearchBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchFragment : Fragment(), LocationCardClickListener {

    private var _binding: FragmentSearchBinding? = null
    private val viewModel: MainActivityViewModel by activityViewModels()

    private val binding get() = _binding!!

    val adapter = SearchLocationAdapter(emptyList(), this)

    private fun fetchCurrentWeather(lat: Double, lon: Double, apiKey: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = RetrofitCurrentWeatherClient.create().getLocation(lat, lon, apiKey)
            if (response.isSuccessful) {
                val locationResponse = response.body()
                withContext(Dispatchers.Main) {
                    Log.i("Location", locationResponse.toString())
                    viewModel.addLocation(locationResponse!!)
                }
            } else {
                response.errorBody()?.let {
                    val errorBodyString = it.string()
                    Log.i("Location", errorBodyString)
                }
            }
        }
    }

    override fun onLocationCardClick(location: Location) {
        Log.i("Location", location.toString())
        fetchCurrentWeather(location.lat, location.lon, "4bf2d9ba39b3f65d6d56ced5607fee4b")
    }

    private fun fetchLocation(query: String, limit: Int, apiKey: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = RetrofitLocationClient.create().getLocation(query, limit, apiKey)
            if (response.isSuccessful) {
                val locationResponse = response.body()
                withContext(Dispatchers.Main) {
                    adapter.updateLocations(locationResponse!!)
                }
            } else {
                response.errorBody()?.let {
                    val errorBodyString = it.string()
                    Log.i("Location", errorBodyString)
                }
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        binding.locationSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchLocation(query, 5, "4bf2d9ba39b3f65d6d56ced5607fee4b")
                }
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })



        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}