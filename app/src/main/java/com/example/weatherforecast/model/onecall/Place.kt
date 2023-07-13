package com.example.weatherforecast.model.onecall

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

data class Place(
    var latitude: Double,
    var longitude: Double
): Serializable