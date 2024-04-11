package com.example.weather_app.ui.location

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weather_app.MainActivityViewModel
import com.example.weather_app.adapter.LocationAdapter
import com.example.weather_app.databinding.FragmentDashboardBinding

class LocationFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val viewModel: MainActivityViewModel by activityViewModels()
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerView = binding.LocationRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = LocationAdapter(emptyList())
        recyclerView.adapter = adapter

        viewModel.savedLocations.observe(viewLifecycleOwner) {
            adapter.updateLocations(it)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}