package com.example.weatherforecast.localsource

import com.example.weatherforecast.model.onecall.Alert
import com.example.weatherforecast.model.onecall.AllWeather
import kotlinx.coroutines.flow.Flow


class LocalSource (val weatherDao : WeatherDao) : LocalSourceInterface
{

    override suspend fun insertWeather(weatherData: AllWeather) {
        weatherDao.insertWeatherData(weatherData)
    }

    override fun getCashedData(): Flow<AllWeather?> {
        return weatherDao.getCashedData()
    }

    override suspend fun insertAlert(alarmItem: Alert) {
        weatherDao.insertAlarm(alarmItem)
    }

    override suspend fun deleteAlert(alert: Alert) {
        weatherDao.deleteAlarm(alert)
    }

    override suspend fun updateAlert(alert: Alert) {
    }

    override suspend fun getAllAlertts(): Flow<List<Alert>> {
      return  weatherDao.getAllAlarms()
    }


//    fun getWeatherFromLocal() : Flow<AllWeather> {
//       return weatherDao.getAllWeatherData()
//    }


}