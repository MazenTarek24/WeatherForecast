package com.example.weatherforecast.repo

import com.example.weatherforecast.localsource.LocalSource
import com.example.weatherforecast.localsource.LocalSourceInterface
import com.example.weatherforecast.model.onecall.Alert
import com.example.weatherforecast.model.onecall.AllWeather
import com.example.weatherforecast.network.AllWeatherRetrofitHelper
import com.example.weatherforecast.network.RemoteSource
import com.example.weatherforecast.network.RemoteSourceInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import retrofit2.Response

class Repo(val remoteSource: RemoteSourceInterface , val localSource: LocalSourceInterface) : RepoInterface {

    companion object {
    @Volatile
    private var instance: Repo? = null
    fun getInstance(
        remoteSource: RemoteSource,
        localSource: LocalSource,
    ): Repo {
        return instance ?: synchronized(this){
            instance ?: Repo(remoteSource,localSource).also {
                instance = it
            }
        }
    }
 }

    override suspend fun getAllWeather(
        lat: Double?,
        lon: Double?,
        lang: String?
    ): Flow<Response<AllWeather>> {
        return remoteSource.getAllWeather(lat, lon, lang!!)
    }

   override fun getCashedData(): Flow<AllWeather?> {
        return localSource.getCashedData()
        println("data in repo ")
    }


   override  suspend fun insertWeather(weather: AllWeather)
    {
        localSource.insertWeather(weather)
    }

    override suspend fun insertAlert(alert: Alert) {
       localSource.insertAlert(alert)
    }

    override suspend fun deleteAlert(alert: Alert) {
        localSource.deleteAlert(alert)
    }

    override suspend fun updateAlert(alert: Alert) {
        localSource.updateAlert(alert)
    }

    override suspend fun getAllAlertts(): Flow<List<Alert>> {
       return localSource.getAllAlertts()
    }


}