package com.example.weatherforecast.setting.viewmodel

import androidx.lifecycle.ViewModel
import com.example.weatherforecast.setting.view.SettingFragment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SharedViewModel : ViewModel() {
    private val _selectUnit = MutableStateFlow<SettingFragment.TemperatureUnit?>(null)
    val selectedUnit: StateFlow<SettingFragment.TemperatureUnit?> = _selectUnit

    private val _selectWind = MutableStateFlow<SettingFragment.WindSpeed?>(null)
    val selectWind : StateFlow<SettingFragment.WindSpeed?> = _selectWind


    fun setSelectedUnit(unit: SettingFragment.TemperatureUnit)
    {
        _selectUnit.value = unit
    }

    fun setSelectWind(windSpeed : SettingFragment.WindSpeed)
    {
        _selectWind.value = windSpeed
    }
}