package com.example.weatherforecast.localsource

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.weatherforecast.model.onecall.Alert
import com.example.weatherforecast.model.onecall.AllWeather
import kotlinx.coroutines.flow.Flow


@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: FavoriteLocationEntity)

    @Query("SELECT * FROM favorite_locations")
      fun getAllFav() : LiveData<List<FavoriteLocationEntity>>

    @Delete
    suspend fun deleteFav(location: FavoriteLocationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(alarmItem: Alert)

    @Delete
    suspend fun deleteAlarm(alarmItem: Alert)

    @Query("SELECT * FROM alerts")
    fun getAllAlarms(): Flow<List<Alert>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherData(weather: AllWeather)

    @Query("SELECT * FROM weather")
    fun getCashedData()  : Flow<AllWeather?>
}
