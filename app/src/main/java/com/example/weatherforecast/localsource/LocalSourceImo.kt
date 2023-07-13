package com.example.weatherforecast.localsource

import com.example.weatherforecast.model.onecall.Alert
import com.example.weatherforecast.model.onecall.AllWeather
import kotlinx.coroutines.flow.Flow

interface LocalSourceInterface {
   suspend fun insertWeather(weatherData: AllWeather)
   fun getCashedData(): Flow<AllWeather?>
    suspend fun insertAlert(alarmItem: Alert)
    suspend fun deleteAlert(alert: Alert)
    suspend fun updateAlert(alert: Alert)
    suspend fun getAllAlertts() : Flow<List<Alert>>

    }

