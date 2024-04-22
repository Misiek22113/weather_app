package com.example.weather_app.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.weather_app.databinding.FragmentHomeBinding
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.example.weather_app.BuildConfig
import com.example.weather_app.MainActivityViewModel
import com.example.weather_app.R


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val viewModel: MainActivityViewModel by activityViewModels()
    private val binding get() = _binding!!
    private val apiKey = BuildConfig.API_KEY

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewModel.getCurrentLocation()
        viewModel.getCurrentLocationForecast()

        viewModel.currentLocation.observe(viewLifecycleOwner) {
            if(viewModel.isInternetConnectionEstablished() && it != null){
                try{
                    viewModel.fetchCurrentWeather(it.coord.lat, it.coord.lon, apiKey)
                    viewModel.fetchForecastWeather(it.coord.lat, it.coord.lon, apiKey)
                }
                catch (e: Exception){
                    Toast.makeText(context, "An error occurred", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
            }
        }

        binding.refreshButton.setOnClickListener {
            viewModel.currentLocation.value?.let {
                if(viewModel.isInternetConnectionEstablished()){
                    viewModel.fetchCurrentWeather(it.coord.lat, it.coord.lon, apiKey)
                }
                else {
                    Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
                }
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

        viewModel.selectedLocationForecast.observe(viewLifecycleOwner) { forecastData ->

            binding.forecastLinearLayout.removeAllViews()

            val dataList = forecastData?.list?.subList(0, forecastData.list.size.coerceAtMost(10))

            if (dataList != null) {
                for (data in dataList) {
                    val forecastCard = layoutInflater.inflate(
                        R.layout.forecast_card,
                        binding.forecastLinearLayout,
                        false
                    )

                    val dayOfTheWeekTextView =
                        forecastCard.findViewById<TextView>(R.id.dayOfTheWeek)
                    val forecastWeatherIconImageView =
                        forecastCard.findViewById<ImageView>(R.id.forecastWeatherIcon)
                    val forecastTemperatureTextView =
                        forecastCard.findViewById<TextView>(R.id.forecastTemperature)

                    dayOfTheWeekTextView.text = viewModel.getHour(data.dt)
                    forecastWeatherIconImageView.setImageResource(
                        viewModel.getWeatherIcon(
                            data.weather[0].main ?: "Clear",
                            data.weather[0].description ?: "Clear"
                        )
                    )
                    forecastTemperatureTextView.text =
                        viewModel.getTemperature(data.main.temp ?: 0.0).toString()
                            .plus("°" + viewModel.getTemperatureUnit().slice(0..0).uppercase())

                    binding.forecastLinearLayout.addView(forecastCard)
                }
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}