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
import com.example.weather_app.MainActivityViewModel
import com.example.weather_app.R


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

        viewModel.getCurrentLocation()

        binding.refreshButton.setOnClickListener {
            if (viewModel.isInternetConnectionEstablished()) {
                viewModel.updateCurrentLocationData()
                viewModel.updateSavedLocationsData()
                Toast.makeText(context, "Data updated", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.currentLocation.observe(viewLifecycleOwner) {
            val temp = viewModel.getTemperature(it?.weatherData?.main?.temp ?: 0.0)
            val feelsLikeTemp = viewModel.getTemperature(it?.weatherData?.main?.feelsLike ?: 0.0)
            binding.locationNameText.text = it.weatherData.name
            binding.temperature.text =
                temp.toString().plus("°" + viewModel.getTemperatureUnit().slice(0..0).uppercase())
            binding.weatherDescription.text = it.weatherData.weather[0].description
            binding.weatherIcon.setImageResource(
                viewModel.getWeatherIcon(
                    it?.weatherData?.weather?.get(0)?.main ?: "Clear",
                    it?.weatherData?.weather?.get(0)?.description ?: "Clear"
                )
            )
            binding.clouds.dataDescription.text = "clouds"
            binding.clouds.dataValue.text = it?.weatherData?.clouds?.all.toString().plus("%")
            binding.wind.dataDescription.text = "wind"
            binding.wind.dataValue.text =
                viewModel.getSpeed(it?.weatherData?.wind?.speed ?: 0.0)
                    .plus(" " + viewModel.getSpeedUnit())
            binding.humidity.dataDescription.text = "humidity"
            binding.humidity.dataValue.text = it?.weatherData?.main?.humidity.toString().plus("%")
            binding.pressure.dataDescription.text = "pressure"
            binding.pressure.dataValue.text =
                it?.weatherData?.main?.pressure.toString().plus(" hPa")
            binding.sunrise.dataDescription.text = "sunrise"
            binding.sunrise.dataValue.text =
                viewModel.getSunriseSunset(it?.weatherData?.sys?.sunrise ?: 0)
            binding.sunset.dataDescription.text = "sunset"
            binding.sunset.dataValue.text =
                viewModel.getSunriseSunset(it?.weatherData?.sys?.sunset ?: 0)
            binding.feelsLike.dataDescription.text = "feels like"
            binding.feelsLike.dataValue.text = feelsLikeTemp.toString()
                .plus("°" + viewModel.getTemperatureUnit().slice(0..0).uppercase())
            binding.visibility.dataDescription.text = "visibility"
            binding.visibility.dataValue.text = it?.weatherData?.visibility.toString().plus(" m")
            binding.updateDataText.text = "Updated ".plus(
                viewModel.getUpdateTime(
                    viewModel.currentLocation.value?.lastUpdate ?: 0
                )
            ).plus(" min ago")

            val newElement = binding.root.findViewById<View>(R.id.seaLevel)

            if(newElement != null) {
                binding.seaLevel?.dataDescription?.text = "sea level"
                binding.seaLevel?.dataValue?.text = it?.weatherData?.main?.seaLevel.toString().plus(" hPa")
                binding.tempMin?.dataDescription?.text = "temp min"
                binding.tempMin?.dataValue?.text = viewModel.getTemperature(it?.weatherData?.main?.tempMin ?: 0.0).toString().plus("°" + viewModel.getTemperatureUnit().slice(0..0).uppercase())
                binding.tempMax?.dataDescription?.text = "temp max"
                binding.tempMax?.dataValue?.text = viewModel.getTemperature(it?.weatherData?.main?.tempMax ?: 0.0).toString().plus("°" + viewModel.getTemperatureUnit().slice(0..0).uppercase())
                binding.grndLevel?.dataDescription?.text = "grnd level"
                binding.grndLevel?.dataValue?.text = it?.weatherData?.main?.groundLevel.toString().plus(" hPa")
            }
        }

        viewModel.currentLocation.observe(viewLifecycleOwner) { forecastData ->

            binding.forecastLinearLayout.removeAllViews()

            val dataList = forecastData?.forecastData?.list?.subList(
                0,
                forecastData.forecastData.list.size.coerceAtMost(10)
            )

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
                            data.weather[0].main,
                            data.weather[0].description
                        )
                    )
                    forecastTemperatureTextView.text =
                        viewModel.getTemperature(data.main.temp).toString()
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