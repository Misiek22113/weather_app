package com.example.weather_app.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.weather_app.databinding.FragmentHomeBinding
import android.util.Log
import androidx.fragment.app.activityViewModels
import com.example.weather_app.MainActivityViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val viewModel: MainActivityViewModel by activityViewModels()

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val textView: TextView = binding.textHome
//        viewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }

//        val lat = 52.2319581
//        val lon = 21.0067249
        val apiKey = "4bf2d9ba39b3f65d6d56ced5607fee4b"

//        viewModel.fetchCurrentWeather(lat, lon, apiKey)
        viewModel.getCurrentLocation()

        viewModel.currentLocation.observe(viewLifecycleOwner) {
            viewModel.fetchCurrentWeather(it.lat, it.lon, apiKey)
        }

        viewModel.selectedLocationWeather.observe(viewLifecycleOwner) {
            binding.locationNameText.text = viewModel.selectedLocationWeather.value?.name ?: "Loading..."
        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}