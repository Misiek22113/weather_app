package com.example.weather_app.ui.fragments

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
import com.example.weather_app.databinding.FragmentLocationBinding
import com.example.weather_app.databinding.FragmentSettingsBinding

class LocationFragment : Fragment() {

    private var _binding: FragmentLocationBinding? = null
    private val viewModel: MainActivityViewModel by activityViewModels()
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentLocationBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerView = binding.LocationRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = LocationAdapter(emptyList(), viewModel)
        recyclerView.adapter = adapter

        viewModel.savedLocations.observe(viewLifecycleOwner) { locations ->
            adapter.updateLocations(locations)
        }

        binding.settingsButton.setOnClickListener {
            createDialog()
        }

        return root
    }

    private fun createDialog() {
        var settingsBinding = FragmentSettingsBinding.inflate(layoutInflater)

        val alertDialog = AlertDialog.Builder(this.requireContext());
        alertDialog.setView(settingsBinding.root)

        settingsBinding.temperatureSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.setTemperatureUnit(isChecked)
        }

        alertDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}