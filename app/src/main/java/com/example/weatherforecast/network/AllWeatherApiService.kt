package com.example.weatherforecast.network

import com.example.weatherforecast.model.onecall.AllWeather
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface AllWeatherApiService {
    @GET("onecall?")
    suspend fun getAllWeather(
        @Query("lat") lat: Double?,
        @Query("lon") long: Double?,
        @Query("lang") language: String? = "en",
        @Query("appid") apiKey: String? = "34a112137b3f93965d53498818fa64fa",
    ): Response<AllWeather>
}

object AllWeatherRetrofitHelper{
    var gson : Gson = GsonBuilder().create()
    const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    val retrofitInstance : AllWeatherApiService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
        .create(AllWeatherApiService::class.java)
}

