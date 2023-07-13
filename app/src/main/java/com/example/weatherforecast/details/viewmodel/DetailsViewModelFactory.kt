package com.example.weatherforecast.details.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforecast.home.viewmodel.DetailsViewModel
import com.example.weatherforecast.repo.Repo

class DetailsViewModelFactory(private val repository: Repo): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailsViewModel::class.java)) {
            return DetailsViewModel( repository ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}