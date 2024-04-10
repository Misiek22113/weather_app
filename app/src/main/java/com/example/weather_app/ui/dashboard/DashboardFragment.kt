package com.example.weather_app.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weather_app.adapter.LocationAdapter
import com.example.weather_app.databinding.FragmentDashboardBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.weather_app.data.api.Location
import com.example.weather_app.data.api.RetrofitLocationClient

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val apiService = RetrofitLocationClient.create()

        val call = apiService.getLocation("Warsaw", 5, "4bf2d9ba39b3f65d6d56ced5607fee4b")

        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = LocationAdapter(emptyList())
        recyclerView.adapter = adapter

        call.enqueue(object : Callback<List<Location>> {
            override fun onResponse(
                call: Call<List<Location>>,
                response: Response<List<Location>>
            ) {
                if (response.isSuccessful) {
                    val location = response.body()
                    Log.i("Location", location.toString())
                    val locations: List<Location> = location ?: emptyList()
                    adapter.updateLocations(locations)
                }
            }

            override fun onFailure(call: Call<List<Location>>, t: Throwable) {
//                textView.text = "Error"
            }
        })

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}