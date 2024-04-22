package com.example.weather_app

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.weather_app.databinding.ActivityMainBinding
import com.example.weather_app.ui.fragments.LocationFragment


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainActivityViewModel

    private var handler = Handler(Looper.getMainLooper())
    private val updateLocationsRunnable = object : Runnable {
        override fun run() {
            if (viewModel.isInternetConnectionEstablished()) {
                viewModel.updateSavedLocationsData()
            }
            handler.postDelayed(this, 300000)
            Toast.makeText(applicationContext, "Data updated", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        handler.post(updateLocationsRunnable)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(updateLocationsRunnable)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        navView.setupWithNavController(navController)

        viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]

        viewModel.getLocationsFromStorage()
        viewModel.getCurrentLocation()
    }

    fun navigateToLocation() {
        // Create a new instance of the fragment you want to display
        val newFragment = LocationFragment()

        // Replace the current fragment with the new one
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_activity_main, newFragment)
            .commit()
    }
}