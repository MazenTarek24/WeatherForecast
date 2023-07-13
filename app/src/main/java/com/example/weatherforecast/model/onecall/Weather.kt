package com.example.weatherforecast.model.onecall

data class Weather(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
)