package com.example.weatherforecast.network

import com.example.weatherforecast.model.onecall.AllWeather

sealed class ApiState {
    object Loading : ApiState()
    data class Success(val data: AllWeather?) : ApiState()
    data class Failure(val msg: Throwable) : ApiState()
}