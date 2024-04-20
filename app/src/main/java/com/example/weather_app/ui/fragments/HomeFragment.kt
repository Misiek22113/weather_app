package com.example.weather_app.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.weather_app.databinding.FragmentHomeBinding
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.example.weather_app.MainActivityViewModel


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
        viewModel.getCurrentLocationForecast()

        viewModel.currentLocation.observe(viewLifecycleOwner) {
            viewModel.fetchCurrentWeather(it.lat, it.lon, apiKey)
            viewModel.fetchForecastWeather(it.lat, it.lon, apiKey)
        }

        binding.refreshButton.setOnClickListener {
            viewModel.currentLocation.value?.let {
                viewModel.fetchCurrentWeather(it.lat, it.lon, apiKey)
            }
            Toast.makeText(context, "Weather has been updated", Toast.LENGTH_SHORT).show()
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
                viewModel.getSpeed(it?.wind?.speed ?: 0.0).plus(" " + viewModel.getSpeedUnit())
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

        viewModel.selectedLocationForecast.observe(viewLifecycleOwner) {
            binding.forecastCard1.dayOfTheWeek.text = viewModel.getHour(it?.list?.get(0)?.dt ?: 0)
            binding.forecastCard1.forecastWeatherIcon.setImageResource(
                viewModel.getWeatherIcon(
                    it?.list?.get(0)?.weather?.get(0)?.main ?: "Clear",
                    it?.list?.get(0)?.weather?.get(0)?.description ?: "Clear"
                )
            )
            binding.forecastCard1.forecastTemperature.text =
                viewModel.getTemperature(it?.list?.get(0)?.main?.temp ?: 0.0).toString()
                    .plus("°" + viewModel.getTemperatureUnit().slice(0..0).uppercase())
            binding.forecastCard2.dayOfTheWeek.text = viewModel.getHour(it?.list?.get(1)?.dt ?: 0)
            binding.forecastCard2.forecastWeatherIcon.setImageResource(
                viewModel.getWeatherIcon(
                    it?.list?.get(1)?.weather?.get(0)?.main ?: "Clear",
                    it?.list?.get(1)?.weather?.get(0)?.description ?: "Clear"
                )
            )
            binding.forecastCard2.forecastTemperature.text =
                viewModel.getTemperature(it?.list?.get(1)?.main?.temp ?: 0.0).toString()
                    .plus("°" + viewModel.getTemperatureUnit().slice(0..0).uppercase())
            binding.forecastCard3.dayOfTheWeek.text = viewModel.getHour(it?.list?.get(2)?.dt ?: 0)
            binding.forecastCard3.forecastWeatherIcon.setImageResource(
                viewModel.getWeatherIcon(
                    it?.list?.get(2)?.weather?.get(0)?.main ?: "Clear",
                    it?.list?.get(2)?.weather?.get(0)?.description ?: "Clear"
                )
            )
            binding.forecastCard3.forecastTemperature.text =
                viewModel.getTemperature(it?.list?.get(2)?.main?.temp ?: 0.0).toString()
                    .plus("°" + viewModel.getTemperatureUnit().slice(0..0).uppercase())
            binding.forecastCard4.dayOfTheWeek.text = viewModel.getHour(it?.list?.get(3)?.dt ?: 0)
            binding.forecastCard4.forecastWeatherIcon.setImageResource(
                viewModel.getWeatherIcon(
                    it?.list?.get(3)?.weather?.get(0)?.main ?: "Clear",
                    it?.list?.get(3)?.weather?.get(0)?.description ?: "Clear"
                )
            )
            binding.forecastCard4.forecastTemperature.text =
                viewModel.getTemperature(it?.list?.get(3)?.main?.temp ?: 0.0).toString()
                    .plus("°" + viewModel.getTemperatureUnit().slice(0..0).uppercase())
            binding.forecastCard5.dayOfTheWeek.text = viewModel.getHour(it?.list?.get(4)?.dt ?: 0)
            binding.forecastCard5.forecastWeatherIcon.setImageResource(
                viewModel.getWeatherIcon(
                    it?.list?.get(4)?.weather?.get(0)?.main ?: "Clear",
                    it?.list?.get(4)?.weather?.get(0)?.description ?: "Clear"
                )
            )
            binding.forecastCard5.forecastTemperature.text =
                viewModel.getTemperature(it?.list?.get(4)?.main?.temp ?: 0.0).toString()
                    .plus("°" + viewModel.getTemperatureUnit().slice(0..0).uppercase())
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}