package com.example.weatherforecast.home.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.ActivityHomeBinding
import com.example.weatherforecast.model.onecall.AllWeather
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity()   {
    lateinit var navController: NavController

    lateinit var binding : ActivityHomeBinding


    lateinit var weather: AllWeather

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)



        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?

        navController = navHostFragment!!.navController

        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        setupWithNavController(bottomNavView, navController)
    }



}