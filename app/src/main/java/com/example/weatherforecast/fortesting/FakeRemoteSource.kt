package com.example.weatherforecast.fortesting

import com.example.weatherforecast.model.onecall.*
import com.example.weatherforecast.network.RemoteSourceInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import retrofit2.Response

class FakeRemoteSource (): RemoteSourceInterface {

    val fakeWeatherData = AllWeather(
        id = 1,
        alerts = null,
        current = Current(clouds = 98, dew_point = 10.0, dt = 215562, feels_like = 12.0, humidity = 50, pressure = 100,
            sunrise = 12, sunset = 5, temp = 50.0, uvi = 25.0, visibility = 1, weather = listOf(), wind_deg = null,
            wind_gust = null,
            wind_speed = 150.01),
        daily = listOf()
         , hourly = listOf() ,
         1565325.044 , 14532.255, null,"23/7",20 )


    override suspend fun getAllWeather(
        lat: Double?,
        lon: Double?,
        lang: String,
    ): Flow<Response<AllWeather>> {
        return flowOf(Response.success(fakeWeatherData))
    }
}