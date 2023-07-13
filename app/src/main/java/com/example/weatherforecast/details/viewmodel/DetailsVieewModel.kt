package com.example.weatherforecast.home.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.weatherforecast.repo.Repo
import com.example.weatherforecast.network.ApiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DetailsViewModel (val repo: Repo) : ViewModel() {

    private val _apiState = MutableStateFlow<ApiState>(ApiState.Loading)
    val apiState: StateFlow<ApiState> get() = _apiState


    private val viewModelJob = Job()
    private val viewModelScope = CoroutineScope(Dispatchers.IO + viewModelJob)

    fun fetchAllWeather(lat: Double?, lon: Double?, lang: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getAllWeather(lat, lon, lang)
                .catch { e ->
                    Log.i("mazen", "error when fetching All Weather Data: $e")
                    _apiState.value = ApiState.Failure(e)
                }
                .collect { response ->
                    if (response.isSuccessful) {
                        val weatherData = response.body()
                        if (weatherData != null) {
                            _apiState.value = ApiState.Success(response.body()!!)
                            Log.i("mazen", "data inserted successfully in room ")
                        }

                    }
                }
        }
    }
}
