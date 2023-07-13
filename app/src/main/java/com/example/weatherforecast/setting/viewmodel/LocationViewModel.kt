package com.example.weatherforecast.setting.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LocationViewModel : ViewModel() {

    private var _locationSelected = MutableStateFlow<Pair<Double,Double>?>(null)
    val locationSelected: StateFlow<Pair<Double,Double>?> = _locationSelected


    private var _locationSelectedwithName = MutableStateFlow<Triple<Double,Double,String>?>(null)
    val locationSelectedwithName: StateFlow<Triple<Double,Double,String>?> = _locationSelectedwithName

    fun selectedLocation(lat : Double , lon : Double )
    {
        _locationSelected.value = Pair(lat,lon)

    }

    fun selectedLocationwithName(lat: Double, lon: Double, name: String  )
    {
        _locationSelectedwithName.value = Triple(lat,lon , name )

    }

}