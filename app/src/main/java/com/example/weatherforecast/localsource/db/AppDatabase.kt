package com.example.weatherforecast.localsource

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weatherforecast.model.onecall.Alert
import com.example.weatherforecast.model.onecall.AllWeather

@Database(entities = arrayOf(FavoriteLocationEntity::class  , AllWeather::class , Alert::class ) , version = 7)
abstract class WeatherDataBase() : RoomDatabase()
{
    abstract fun getDao() : WeatherDao
    companion object{
        @Volatile
        private var INSTANCE: WeatherDataBase? = null
        fun getInstance (ctx: Context): WeatherDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    ctx.applicationContext, WeatherDataBase::class.java, "weather_database")
                    .build()

                INSTANCE = instance
                instance }
        }
    }
}
