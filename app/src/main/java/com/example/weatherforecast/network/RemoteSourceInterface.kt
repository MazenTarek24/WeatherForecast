package com.example.weatherforecast.network

import com.example.weatherforecast.model.onecall.AllWeather
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface RemoteSourceInterface {
    suspend fun getAllWeather(lat: Double?, lon: Double? ,lang : String) : Flow<Response<AllWeather>>

}