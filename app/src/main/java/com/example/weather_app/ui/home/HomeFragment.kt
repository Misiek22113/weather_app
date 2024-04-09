package com.example.weather_app.ui.home

import Location
import android.os.Bundle
import android.util.Log
import retrofit2.Call
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.weather_app.databinding.FragmentHomeBinding
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.log

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        val apiService = RetrofitClient.create()

        val call = apiService.getLocation("Warsaw", 5, "4bf2d9ba39b3f65d6d56ced5607fee4b")

        call.enqueue(object : Callback<List<Location>> {
            override fun onResponse(
                call: Call<List<Location>>,
                response: Response<List<Location>>
            ) {
                if (response.isSuccessful) {
                    val location = response.body()?.get(0)
                    textView.text = location?.name
                    Log.i("Location", location.toString())
                }
            }

            override fun onFailure(call: Call<List<Location>>, t: Throwable) {
                textView.text = "Error"
            }
        })

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}