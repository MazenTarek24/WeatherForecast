package com.example.weatherforecast.alarm.view

import com.example.weatherforecast.model.onecall.Alert


interface AlertOnClickListener {
     fun onOptionClicked(alert: Alert)
}