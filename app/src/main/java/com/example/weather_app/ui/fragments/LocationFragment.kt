package com.example.weather_app.ui.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weather_app.MainActivityViewModel
import com.example.weather_app.R
import com.example.weather_app.adapter.LocationAdapter
import com.example.weather_app.adapter.LocationCardClickListener
import com.example.weather_app.data_classes.Location
import com.example.weather_app.data_classes.SavedLocation
import com.example.weather_app.databinding.FragmentLocationBinding
import com.example.weather_app.databinding.FragmentSettingsBinding

class LocationFragment : Fragment(), LocationCardClickListener {

    private var _binding: FragmentLocationBinding? = null
    private val viewModel: MainActivityViewModel by activityViewModels()
    private val binding get() = _binding!!

    override fun onLocationCardClick(location: SavedLocation) {
        viewModel.setCurrentLocation(location)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentLocationBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerView = binding.LocationRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = LocationAdapter(emptyList(), viewModel, this)
        recyclerView.adapter = adapter

        viewModel.savedLocations.observe(viewLifecycleOwner) { locations ->
            adapter.updateLocations(locations)
        }

        binding.settingsButton.setOnClickListener {
            createDialog()
        }

        return root
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun createDialog() {
        val settingsBinding = FragmentSettingsBinding.inflate(layoutInflater)

        val alertDialog = AlertDialog.Builder(this.requireContext());
        alertDialog.setView(settingsBinding.root)

        val sharedPreferences = viewModel.getTemperatureUnit()

        settingsBinding.temperatureSwitch.isChecked = sharedPreferences == "celsius"

        settingsBinding.temperatureSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.setTemperatureUnit(isChecked)
            binding.LocationRecyclerView.adapter?.notifyDataSetChanged()
        }

        settingsBinding.speedSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.setSpeedUnit(isChecked)
            binding.LocationRecyclerView.adapter?.notifyDataSetChanged()
        }

        alertDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}