package com.example.weatherforecast.alarm.view.alertviewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.weatherforecast.model.onecall.Alert
import com.example.weatherforecast.repo.Repo
import kotlinx.coroutines.flow.Flow

class ViewModelAlarm (val  repo: Repo): ViewModel() {

    suspend fun insertAlert(alert: Alert) {
        repo.insertAlert(alert)
    }

      suspend fun deleteAlert(alert: Alert) {
        repo.deleteAlert(alert)
    }


     suspend fun getAllAlerts(): Flow<List<Alert>> {
        return repo.getAllAlertts()
    }
}