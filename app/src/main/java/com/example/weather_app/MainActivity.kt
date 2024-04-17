package com.example.weather_app

import SharedPreferences
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.weather_app.databinding.ActivityMainBinding
import com.example.weather_app.ui.fragments.LocationFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPref: SharedPreferences
    private lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        navView.setupWithNavController(navController)

        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)

        viewModel.setLocations()
    }

    private fun setUpSettingsDialog(){

    };

    fun navigateToLocation() {
        // Create a new instance of the fragment you want to display
        val newFragment = LocationFragment()

        // Replace the current fragment with the new one
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_activity_main, newFragment)
            .commit()
    }
}