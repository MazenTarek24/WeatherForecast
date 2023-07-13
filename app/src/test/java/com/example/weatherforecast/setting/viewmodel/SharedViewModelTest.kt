package com.example.weatherforecast.setting.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.weatherforecast.setting.view.SettingFragment
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SharedViewModelTest{

    @get:Rule
    val instantExecuteRule  = InstantTaskExecutorRule()

    private lateinit var viewModel: SharedViewModel

    @Before
    fun setUp(){
        viewModel = SharedViewModel()
    }

    @Test
    fun setSelectedUnit_updateSelectedUnit()
    {
        val testUnit = SettingFragment.TemperatureUnit.Celsius

        viewModel.setSelectedUnit(testUnit)

        val selectedUnit = viewModel.selectedUnit.value

        assertEquals(testUnit , selectedUnit)
    }

    @Test
    fun setSelectedWind_updateWind()
    {
        val testWind = SettingFragment.WindSpeed.Mile_Hour

        viewModel.setSelectWind(testWind)

        val selectWind = viewModel.selectWind.value

        assertEquals(testWind,selectWind)


    }
}