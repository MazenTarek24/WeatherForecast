package com.example.weatherforecast.localsource

import androidx.room.TypeConverter
import com.example.weatherforecast.model.onecall.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class WeatherTypeConverters {
    @TypeConverter
    fun fromAlertList(alerts: List<Alert>?): String {
        val gson = Gson()
        return gson.toJson(alerts)
    }

    @TypeConverter
    fun toAlertList(json: String): List<Alert>? {
        val gson = Gson()
        val type = object : TypeToken<List<Alert>?>() {}.type
        return gson.fromJson(json, type)
    }

    @TypeConverter
    fun fromCurrent(current: Current?): String {
        val gson = Gson()
        return gson.toJson(current)
    }

    @TypeConverter
    fun toCurrent(json: String): Current ?{
        val gson = Gson()
        return gson.fromJson(json, Current::class.java)
    }

    @TypeConverter
    fun fromDailyList(dailyList: List<Daily>?): String {
        val gson = Gson()
        return gson.toJson(dailyList)
    }

    @TypeConverter
    fun toDailyList(json: String): List<Daily>? {
        val gson = Gson()
        val type = object : TypeToken<List<Daily>?>() {}.type
        return gson.fromJson(json, type)
    }

    @TypeConverter
    fun fromHourlyList(hourlyList: List<Hourly>?): String {
        val gson = Gson()
        return gson.toJson(hourlyList)
    }

    @TypeConverter
    fun toHourlyList(json: String): List<Hourly>? {
        val gson = Gson()
        val type = object : TypeToken<List<Hourly>?>() {}.type
        return gson.fromJson(json, type)
    }

    @TypeConverter
    fun fromMinutelyList(minutelyList: List<Minutely>?): String {
        val gson = Gson()
        return gson.toJson(minutelyList)
    }

    @TypeConverter
    fun toMinutelyList(json: String): List<Minutely>?{
        val gson = Gson()
        val type = object : TypeToken<List<Minutely>?>() {}.type
        return gson.fromJson(json, type)
    }


}