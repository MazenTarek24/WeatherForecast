package com.example.weatherforecast.network


import com.example.weatherforecast.model.onecall.AllWeather
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import retrofit2.Response

class RemoteSource( val allWeatherApiService: AllWeatherApiService) : RemoteSourceInterface {


    companion object {
        private const val apiKey = "34a112137b3f93965d53498818fa64fa"
    }

    override suspend fun getAllWeather(lat: Double?, lon: Double?, lang: String, ): Flow<Response<AllWeather>> {
        val response  = allWeatherApiService.getAllWeather(lat,lon , lang ,apiKey)
        return flowOf(response)
    }

}
