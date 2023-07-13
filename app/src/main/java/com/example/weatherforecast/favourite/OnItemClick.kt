package com.example.weatherforecast.favourite

import com.example.weatherforecast.localsource.FavoriteLocationEntity

interface OnItemClick {
    fun onItemClick(locationEntity: FavoriteLocationEntity)
}