package com.example.weatherforecast.fortesting

import com.example.weatherforecast.localsource.LocalSourceInterface
import com.example.weatherforecast.model.onecall.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeLoucalSource : LocalSourceInterface {
    private val alerts = mutableListOf<Alert>()

    var fakeWeatherData =AllWeather(
        id = 1,
        alerts = null,
        current = Current(clouds = 98, dew_point = 10.0, dt = 215562, feels_like = 12.0, humidity = 50, pressure = 100,
            sunrise = 12, sunset = 5, temp = 50.0, uvi = 25.0, visibility = 1, weather = listOf(), wind_deg = null,
            wind_gust = null,
            wind_speed = 150.01),
        daily = listOf()
        , hourly = listOf() ,
        1565325.044 , 14532.255, null,"23/7",20 )


    override suspend fun insertWeather(weatherData: AllWeather) {
        fakeWeatherData = weatherData
    }

    override fun getCashedData(): Flow<AllWeather?> {
        return flowOf(fakeWeatherData)
    }




    override suspend fun insertAlert(alarmItem: Alert) {
        alerts.add(alarmItem)
    }

    override suspend fun deleteAlert(alert: Alert) {
        alerts.remove(alert)
    }

    override suspend fun updateAlert(alert: Alert) {
        TODO("Not yet implemented")
    }

    override suspend fun getAllAlertts(): Flow<List<Alert>> {
        return flowOf( alerts )
    }
}