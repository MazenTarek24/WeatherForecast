package com.example.weatherforecast.model.onecall

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "alerts")
data class Alert(
    val description: String,
    val startDate: String,
    val endDate: String,
    val startTime: String,
    val endTime: String,
    val event: String="Rain",
    val sender_name: String="Mazen",
){

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}