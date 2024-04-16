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


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val viewModel: MainActivityViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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

        val apiService = RetrofitWeatherClient.create()

        val lat = 52.2319581
        val lon = 21.0067249
        val apiKey = "4bf2d9ba39b3f65d6d56ced5607fee4b"

        val call = apiService.getWeatherData(lat, lon, apiKey)

        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(
                call: Call<WeatherResponse>,
                response: Response<WeatherResponse>
            ) {
                if (response.isSuccessful) {
                    val weatherResponse = response.body()
                    textView.text = weatherResponse?.list?.get(0).toString()
                    Log.i("Weather", "Temperature: ${weatherResponse?.list?.get(0)?.main?.temp}")
                } else {
                    response.errorBody()?.let {
                        val errorBodyString = it.string()
                        Log.i("Weather", errorBodyString)
                    }
                }

            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                // Obsłuż błąd zapytania
            }
        })

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}