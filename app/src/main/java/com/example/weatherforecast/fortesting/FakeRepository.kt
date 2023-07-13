package com.example.weatherforecast.fortesting

import com.example.weatherforecast.model.onecall.Alert
import com.example.weatherforecast.model.onecall.AllWeather
import com.example.weatherforecast.model.onecall.Current
import com.example.weatherforecast.repo.RepoInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import retrofit2.Response

class FakeRepository : RepoInterface{
     val fakeCachedData = MutableStateFlow<AllWeather?>(null)

   var fakeResponse: Flow<Response<AllWeather>>? = null


    override suspend fun getAllWeather(
        lat: Double?,
        lon: Double?,
        lang: String?,
    ): Flow<Response<AllWeather>> {
        return fakeResponse ?: flow { throw Exception("Fake response not set") }
    }


    override fun getCashedData(): Flow<AllWeather?> {
        return fakeCachedData
    }


    override suspend fun insertWeather(weather: AllWeather) {
        fakeCachedData.value = weather
    }



    override suspend fun insertAlert(alert: Alert) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAlert(alert: Alert) {
        TODO("Not yet implemented")
    }

    override suspend fun updateAlert(alert: Alert) {
        TODO("Not yet implemented")
    }

    override suspend fun getAllAlertts(): Flow<List<Alert>> {
        TODO("Not yet implemented")
    }
}