package com.example.weather_app.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.weather_app.databinding.FragmentHomeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Log
import androidx.fragment.app.activityViewModels
import com.example.weather_app.MainActivityViewModel
import com.example.weather_app.data.api.RetrofitWeatherClient
import com.example.weather_app.data_classes.WeatherResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val viewModel: MainActivityViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private fun fetchCurrentWeather(lat: Double, lon: Double, apiKey: String){
        CoroutineScope(Dispatchers.IO).launch {
            val response = viewModel.retrofit.getCurrentWeather(lat, lon, apiKey)
            if (response.isSuccessful) {
                val locationResponse = response.body()
                withContext(Dispatchers.Main) {
//                    textView.text = weatherResponse?.list?.get(0).toString()
                    Log.i("Location", locationResponse.toString())
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

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        viewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        val lat = 52.2319581
        val lon = 21.0067249
        val apiKey = "4bf2d9ba39b3f65d6d56ced5607fee4b"

        fetchCurrentWeather(lat, lon, apiKey)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}