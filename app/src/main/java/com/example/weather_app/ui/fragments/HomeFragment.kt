package com.example.weather_app.ui.fragments

import android.annotation.SuppressLint
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

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val apiKey = "4bf2d9ba39b3f65d6d56ced5607fee4b"

        viewModel.getCurrentLocation()

        viewModel.currentLocation.observe(viewLifecycleOwner) {
            viewModel.fetchCurrentWeather(it.lat, it.lon, apiKey)
        }

        viewModel.selectedLocationWeather.observe(viewLifecycleOwner) {
            val temp = viewModel.getTemperature(it?.main?.temp ?: 0.0)
            val feelsLikeTemp = viewModel.getTemperature(it?.main?.feelsLike ?: 0.0)
            binding.locationNameText.text =
                viewModel.selectedLocationWeather.value?.name ?: "Loading..."
            binding.temperature.text =
                temp.toString().plus("°" + viewModel.getTemperatureUnit().slice(0..0).uppercase())
            binding.weatherDescription.text =
                viewModel.selectedLocationWeather.value?.weather?.get(0)?.description
            binding.weatherIcon.setImageResource(
                viewModel.getWeatherIcon(
                    it?.weather?.get(0)?.main ?: "Clear",
                    it?.weather?.get(0)?.description ?: "Clear"
                )
            )
            binding.clouds.dataDescription.text = "clouds"
            binding.clouds.dataValue.text = it?.clouds?.all.toString().plus("%")
            binding.wind.dataDescription.text = "wind"
            binding.wind.dataValue.text =
                viewModel.getSpeed(it?.wind?.speed ?: 0.0).toString().plus(viewModel.getSpeedUnit())
            binding.humidity.dataDescription.text = "humidity"
            binding.humidity.dataValue.text = it?.main?.humidity.toString().plus("%")
            binding.pressure.dataDescription.text = "pressure"
            binding.pressure.dataValue.text = it?.main?.pressure.toString().plus(" hPa")
            binding.sunrise.dataDescription.text = "sunrise"
            binding.sunrise.dataValue.text = viewModel.getSunriseSunset(it?.sys?.sunrise ?: 0)
            binding.sunset.dataDescription.text = "sunset"
            binding.sunset.dataValue.text = viewModel.getSunriseSunset(it?.sys?.sunset ?: 0)
            binding.feelsLike.dataDescription.text = "feels like"
            binding.feelsLike.dataValue.text = feelsLikeTemp.toString()
                .plus("°" + viewModel.getTemperatureUnit().slice(0..0).uppercase())
            binding.visibility.dataDescription.text = "visibility"
            binding.visibility.dataValue.text = it?.visibility.toString().plus(" m")
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}