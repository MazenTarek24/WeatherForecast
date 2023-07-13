package com.example.weatherforecast.repo

import com.example.weatherforecast.model.onecall.Alert
import com.example.weatherforecast.model.onecall.AllWeather
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface RepoInterface {

    suspend fun getAllWeather(lat: Double?, lon: Double?, lang: String?): Flow<Response<AllWeather>>
    fun getCashedData(): Flow<AllWeather?>
    suspend fun insertWeather(weather: AllWeather)
    suspend fun insertAlert(alert: Alert)
     suspend fun deleteAlert(alert: Alert)
     suspend fun updateAlert(alert: Alert)
      suspend fun getAllAlertts() : Flow<List<Alert>>

}