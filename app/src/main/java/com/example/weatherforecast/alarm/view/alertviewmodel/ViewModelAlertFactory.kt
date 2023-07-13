package com.example.weatherforecast.alarm.view.alertviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.weatherforecast.repo.Repo

class ViewModelAlertFactory(private val repo: Repo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(ViewModelAlarm::class.java)) {
            return ViewModelAlarm(repo) as T
        }
        return super.create(modelClass, extras)
    }
}