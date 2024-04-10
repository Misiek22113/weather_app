package com.example.weather_app.ui.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weather_app.adapter.LocationAdapter
import com.example.weather_app.data.api.Location
import com.example.weather_app.data.api.RetrofitLocationClient
import com.example.weather_app.databinding.FragmentSearchBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val searchViewModel =
            ViewModelProvider(this).get(SearchViewModel::class.java)

        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = LocationAdapter(emptyList())
        recyclerView.adapter = adapter

        val apiService = RetrofitLocationClient.create()

        //TODO move to coroutine
        binding.locationSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    val call = apiService.getLocation(query, 5, "4bf2d9ba39b3f65d6d56ced5607fee4b")
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
//                            textView.text = "Error"
                        }
                    })
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}