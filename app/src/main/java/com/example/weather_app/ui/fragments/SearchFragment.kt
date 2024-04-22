package com.example.weather_app.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weather_app.BuildConfig
import com.example.weather_app.MainActivity
import com.example.weather_app.MainActivityViewModel
import com.example.weather_app.adapter.SearchLocationAdapter
import com.example.weather_app.adapter.SearchLocationCardClickListener
import com.example.weather_app.data_classes.Location
import com.example.weather_app.databinding.FragmentSearchBinding

class SearchFragment : Fragment(), SearchLocationCardClickListener {
    private var _binding: FragmentSearchBinding? = null
    private val viewModel: MainActivityViewModel by activityViewModels()
    private val binding get() = _binding!!
    private lateinit var searchLocationsAdapter: SearchLocationAdapter
    private val apiKey = BuildConfig.API_KEY

    //TODO Fix bug that navbar don't know where it is
    private fun onNavigate() {
        (activity as MainActivity).navigateToLocation()
    }

    override fun onSearchLocationCardClick(location: Location) {
        if(viewModel.isInternetConnectionEstablished()){
            viewModel.createNewCombinedLocationData(location.lat, location.lon)
            onNavigate()
        }
        else {
            Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val root: View = binding.root

        searchLocationsAdapter = SearchLocationAdapter(emptyList(), this, viewModel)

        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = searchLocationsAdapter

        binding.locationSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null && viewModel.isInternetConnectionEstablished()) {
                    viewModel.fetchLocation(
                        query,
                        5,
                        apiKey,
                        searchLocationsAdapter
                    )
                }
                else {
                    Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
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