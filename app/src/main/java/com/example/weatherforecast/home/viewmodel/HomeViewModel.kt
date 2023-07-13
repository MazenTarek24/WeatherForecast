package com.example.weatherforecast.home.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.repo.Repo
import com.example.weatherforecast.model.onecall.AllWeather
import com.example.weatherforecast.network.ApiState
import com.example.weatherforecast.repo.RepoInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(val repo: RepoInterface): ViewModel() {

    private val _currentWether = MutableStateFlow<AllWeather?>(null)
    val currentWeather: StateFlow<AllWeather?> get() = _currentWether


    private val _apiState = MutableStateFlow<ApiState>(ApiState.Loading)
    val apiState: StateFlow<ApiState> get() = _apiState


    fun fetchAllWeather(lat: Double?, lon: Double?, lang: String?): Flow<ApiState> = flow {

        try {
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
                            repo.insertWeather(weatherData)
                            Log.i("mazen", "data inserted successfully in room ")
                        }

                    } else {
                        _apiState.value = ApiState.Failure(Exception("API request failed"))
                    }
                }
        } catch (e: Exception) {
            _apiState.value = ApiState.Failure(e)
        }
    }



     fun getDataFromLocal(): Flow<AllWeather?> {
        return repo.getCashedData().map { it }
    }



    suspend fun insertDataToRoom(weather: AllWeather?) {
        weather?.let {
            repo.insertWeather(weather)
        }
    }

}


//        val response = repo.getAllWeather(lat,lon)
//        if (response.isSuccessful)
//        {
//            _allWeather.value = response.body()
//        }else{
//            Log.i("mazen", "error when ffetching All Weather Data")
//        }


//      fun fetchCurrentWeather(lat: Double?, lon: Double?)
//    {
//
//        viewModelScope.launch {
//            repo.getCurrentWeather(lat,lon)
//                .catch {
//                        e-> Log.i("mazen","error when fetching Current Weather Data: $e") }
//                .collect{ respons->
//                    _currentWether.value = respons.body()
//                }
//        }

//
//        val response = repo.getCurrentWeather(lat,lon)
//        if (response.isSuccessful)
//        {
//            _currentWether.value = response.body()
//        }else{
//            Log.i("mazen", "error when ffetching data")
//        }
//    }
//
//      fun fetchForecast(lat: Double?, lon: Double?)
//    {
//        viewModelScope.launch {
//            repo.getForecast(lat,lon)
//                .catch {
//                        e-> Log.i("mazen","error when fetching Forecast Weather Data: $e") }
//                .collect{ respons->
//                    _allWeather.value = respons.body()
//                }
//        }

//        val response = repo.getForecast(lat,lon)
//        if (response.isSuccessful)
//        {
//            _foreCast.value = response.body()
//        }else{
//            Log.i("mazen", "error when ffetching forecast")
//        }



//    suspend fun getDataFromLocal(): Flow<List<AllWeather>> {
//        return repo.getWeatherFroMLocal()
//    }
