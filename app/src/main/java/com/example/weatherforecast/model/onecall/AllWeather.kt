package com.example.weatherforecast.model.onecall

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.weatherforecast.localsource.WeatherTypeConverters

@Entity(tableName = "weather")
@TypeConverters(WeatherTypeConverters::class)

data class AllWeather(
    @PrimaryKey
    val id : Int = 1,

    val alerts: List<Alert>?,
    val current: Current,
    val daily: List<Daily>,
    val hourly: List<Hourly>,
    val lat: Double,
    val lon: Double,
    val minutely: List<Minutely>?,
    val timezone: String,
    val timezone_offset: Int
)