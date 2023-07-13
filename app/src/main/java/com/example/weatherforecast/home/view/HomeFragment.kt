package com.example.weatherforecast.home.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.productappkotlin.checkconnection.NetworkConnection
import com.example.weatherforecast.R
import com.example.weatherforecast.setting.view.SettingFragment
import com.example.weatherforecast.home.viewmodel.HomeViewModel
import com.example.weatherforecast.home.viewmodel.HomeViewModelFactory
import com.example.weatherforecast.setting.viewmodel.LocationViewModel
import com.example.weatherforecast.setting.viewmodel.SharedViewModel
import com.example.weatherforecast.databinding.BottomSheetBinding
import com.example.weatherforecast.databinding.FragmentHomeBinding
import com.example.weatherforecast.repo.Repo
import com.example.weatherforecast.localsource.LocalSource
import com.example.weatherforecast.localsource.WeatherDataBase
import com.example.weatherforecast.model.onecall.AllWeather
import com.example.weatherforecast.model.onecall.Daily
import com.example.weatherforecast.model.onecall.Hourly
import com.example.weatherforecast.network.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetDialog

import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {


    lateinit var binding: FragmentHomeBinding
    var apiKey: String = "34a112137b3f93965d53498818fa64fa"

    lateinit var sheetLayoutBinding: BottomSheetBinding
    lateinit var dialog: BottomSheetDialog

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var  viewModel: HomeViewModel

    private lateinit var forecastAdapter: RvAdapterForecast
    private lateinit var rvHoursForecastAdapter: RvHoursForecastAdapter


    var forecastList: ArrayList<Daily> = ArrayList()
    var forecasHourstList: ArrayList<Hourly> = ArrayList()

    var lat: Double? = null
    var lon: Double? = null


    lateinit var fusedClient: FusedLocationProviderClient

    val PERSMISSION_ID = 20

    private var geoCoder: Geocoder? = null


    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val locatioViewModel: LocationViewModel by activityViewModels()
    private val langSharedlangViewModel: SharedlangViewModel by activityViewModels()

    private lateinit var localDataSource: LocalSource

    lateinit var repo: Repo

    lateinit var progressIndicator: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        sheetLayoutBinding = BottomSheetBinding.inflate(layoutInflater)
        dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetTheme)
        dialog.setContentView(sheetLayoutBinding.root)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(DelicateCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        GlobalScope.launch(Dispatchers.Main) {
            sharedViewModel.selectedUnit.collect { unit ->
                viewModel.currentWeather.value?.let { weather ->
                    val temp = getFormattedTemp(weather.current.temp, unit)
                    binding.temp.text = temp
                }
            }
        }


        viewModel = ViewModelProvider(requireActivity(),
            HomeViewModelFactory(Repo.getInstance(RemoteSource(AllWeatherRetrofitHelper.retrofitInstance),
                    LocalSource(WeatherDataBase.getInstance(requireContext()).getDao())),
                )).get(HomeViewModel::class.java)

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())

        langSharedlangViewModel.selectedLanguage.onEach { lang ->
            GlobalScope.launch {
                langSharedlangViewModel.updateSelectedLanguage(lang)
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)



        binding.tvForecast.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                openDialog()
            }

        }


        forecastAdapter = RvAdapterForecast(forecastList, sharedViewModel)
        binding.recyclerDays.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(requireContext(), 1, RecyclerView.HORIZONTAL, false)
            adapter = forecastAdapter

        }

        rvHoursForecastAdapter = RvHoursForecastAdapter(forecasHourstList, sharedViewModel)
        binding.recyclerHour.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(requireContext(), 1,
                RecyclerView.HORIZONTAL, false)
            adapter = rvHoursForecastAdapter
        }


        lifecycleScope.launch {
            viewModel.apiState.collect { state ->
                when (state) {
                    is ApiState.Loading -> {
                        withContext(Dispatchers.Main) {
                            binding.progressCircular.visibility = View.VISIBLE
                        }
                    }
                    is ApiState.Success -> {

                        withContext(Dispatchers.Main) {
                            binding.progressCircular.visibility = View.GONE
                            val weatherList = state.data

                            updateCurrentWeatherUI(weatherList)
                            if (weatherList != null) {
                                updateHoursForecast(weatherList)
                            }
                            if (weatherList != null) {
                                updateForecastUI(weatherList)
                            }
                        }
                    }
                    is ApiState.Failure -> {
                        withContext(Dispatchers.Main) {
                            binding.progressCircular.visibility = View.GONE
                            Log.i("TAG", "error when getting data ")
                        }
                    }
                    else -> {}
                }
            }
        }


    }

    @RequiresApi(Build.VERSION_CODES.O)
     fun fetchWeatherData(lang: String) {
        viewModel.fetchAllWeather(lat, lon, lang)
            .onEach { apiState ->
                when (apiState) {
                    is ApiState.Loading -> {
                        // Handle loading state
                        binding.progressCircular.visibility = View.VISIBLE
                    }
                    is ApiState.Success -> {
                        // Handle success state
                        binding.progressCircular.visibility = View.GONE
                        val weatherList = apiState.data

                        // Update UI with the new weather data
                        updateCurrentWeatherUI(weatherList)
                        if (weatherList != null) {
                            updateHoursForecast(weatherList)
                        }
                        if (weatherList != null) {
                            updateForecastUI(weatherList)
                        }
                    }
                    is ApiState.Failure -> {
                        // Handle failure state
                        binding.progressCircular.visibility = View.GONE
                        Toast.makeText(requireContext(), "Error when getting data", Toast.LENGTH_LONG).show()
                    }
                    else -> {}
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        if (NetworkConnection.checkConnection(requireContext())) {
            // Network is available, fetch data from the server
            getLastLocation()
        } else {
            // Network is not available, fetch data from the local database
                viewModel.getDataFromLocal().onEach { weather ->
                    if (weather != null) {
                        updateCurrentWeatherUI(weather)
                        updateHoursForecast(weather)
                        updateForecastUI(weather)
                    }
                }.launchIn(viewLifecycleOwner.lifecycleScope)
            }
    }



    private fun getFormattedWind(windSpeed: Double, wind: SettingFragment.WindSpeed?): String? {
        return when (wind) {
            SettingFragment.WindSpeed.Meter_Sec -> "${windSpeed} m/s"
            SettingFragment.WindSpeed.Mile_Hour -> "${windSpeed} m/h"
            else -> ({
            }).toString()
        }
    }

    private fun getFormattedTemp(temp: Double, unit: SettingFragment.TemperatureUnit?): String {
        return when (unit) {
            SettingFragment.TemperatureUnit.Celsius -> "${(temp/10).toInt()} °C"
            SettingFragment.TemperatureUnit.Kelvin -> "${temp.toInt()} K "
            SettingFragment.TemperatureUnit.Fahrenheit -> {
                val fahrenheit = (temp * 9 / 5) - 459.67
                "${fahrenheit.toInt()} °F"
            }
            else -> ({
            }).toString()
        }
    }


    private suspend fun fetchForecast(lat: Double?, lon: Double?, lang: String) {
        this.lat = lat
        this.lon = lon
        val lang = if (langSharedlangViewModel.selectedLanguage.value == "ar") "ar" else "en"
        viewModel.fetchAllWeather(lat, lon, lang)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun updateCurrentWeatherUI(weather: AllWeather?) {
        // Update the UI with the current weather data
        val iconId = weather!!.current.weather[0].icon
        val imgUrl = "https://openweathermap.org/img/w/$iconId.png"
        Picasso.get().load(imgUrl).into(binding.iconWeather)
        // binding.locationName.text = weather.timezone

        binding.apply {
            // locationName.text = weather.timezone
            val dateTime =
                LocalDateTime.ofInstant(Instant.ofEpochSecond(weather.current.dt!!.toLong()),
                    ZoneId.systemDefault())
            val formatter = DateTimeFormatter.ofPattern("MMM dd, hh:mm a", Locale.ENGLISH)
            val formattedDate = dateTime.format(formatter)
            binding.date.text = formattedDate

            description.text = weather.current.weather[0].description


            val tempUnit =
                sharedViewModel.selectedUnit.value ?: SettingFragment.TemperatureUnit.Celsius
            val temp = getFormattedTemp(weather.current.temp, tempUnit)
            binding.temp.text = temp

            val windSpeedUnit =
                sharedViewModel.selectWind.value ?: SettingFragment.WindSpeed.Meter_Sec
            val wind = getFormattedWind(weather.current.wind_speed, windSpeedUnit)
            binding.windText.text = wind


            cloudText.text = weather.current.clouds.toString() + "%"
            pressureText.text = weather.current.pressure.toString() + "hpa"
            humidityText.text = weather.current.humidity.toString() + "%"
            sunriseText.text =
                SimpleDateFormat("hh:mm a",
                    Locale.ENGLISH).format(weather.current.sunrise * 1000)
            sunsetText.text =
                SimpleDateFormat("hh:mm a",
                    Locale.ENGLISH).format(weather.current.sunset * 1000)

        }
    }


    private fun updateHoursForecast(allWeather: AllWeather) {
        val hoursList: List<Hourly> = allWeather.hourly
        rvHoursForecastAdapter =
            RvHoursForecastAdapter(hoursList as ArrayList<Hourly>, sharedViewModel)
        binding.recyclerHour.adapter = rvHoursForecastAdapter
    }

    private fun updateForecastUI(allWeather: AllWeather) {

        sheetLayoutBinding.tvSheet.text = "Five days Forecast ${allWeather.timezone}"

        val forecastList: List<Daily> = allWeather.daily
        val adapterForecast =
            RvAdapterForecast(forecastList as ArrayList<Daily>, sharedViewModel)
        sheetLayoutBinding.rvForecast.adapter = adapterForecast
    }


    val mLocationCallBack: LocationCallback = object : LocationCallback() {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)
            val fragmentActivity = activity
            if (fragmentActivity != null) {

                val mLastLocation: Location? = locationResult?.lastLocation
                lat = mLastLocation?.latitude!!
                lon = mLastLocation.longitude!!

                geoCoder = Geocoder(requireActivity(), Locale.getDefault())
                val addresses: List<Address>? =
                    geoCoder?.getFromLocation(lat!!, lon!!, 1)
                if (addresses != null && addresses.isNotEmpty()) {
                    val address: Address = addresses[0]
                    val cityName: String = address.locality ?: ""
                    val stateName: String = address.adminArea ?: ""
                    val countryName: String = address.countryName ?: ""
                    val completeAddress = "$cityName, $countryName"

                    // Set the address in the locationName TextView
                    binding.locationName.text = completeAddress

                    lifecycleScope.launch(Dispatchers.IO) {
                        val lang = langSharedlangViewModel.selectedLanguage.value ?: "en"
                        viewModel.fetchAllWeather(lat!!,
                            lon!!, lang).collect { state ->
                                when (state) {
                                    is ApiState.Loading -> {
                                        withContext(Dispatchers.Main) {
                                            binding.progressCircular.visibility =
                                                View.VISIBLE
                                        }
                                    }
                                    is ApiState.Success -> {
                                        withContext(Dispatchers.Main) {
                                            binding.progressCircular.visibility =
                                                View.GONE
                                            val weatherList = state.data

                                            withContext(Dispatchers.IO) {
                                                viewModel.insertDataToRoom(weatherList)

                                            }
                                            updateCurrentWeatherUI(weatherList)
                                            if (weatherList != null) {
                                                updateHoursForecast(weatherList)
                                            }
                                            if (weatherList != null) {
                                                updateForecastUI(weatherList)
                                            }
                                        }
                                    }
                                    is ApiState.Failure -> {
                                        withContext(Dispatchers.IO) {
                                            viewModel.getDataFromLocal().onEach { weather->
                                                if (weather != null) {
                                                    withContext(Dispatchers.Main) {
                                                        updateCurrentWeatherUI(weather)
                                                        updateHoursForecast(weather)
                                                        updateForecastUI(weather)
                                                    }
                                                }
                                            }
                                            binding.progressCircular.visibility =
                                                View.GONE
                                            Toast.makeText(
                                                requireContext(),
                                                "error when get data",
                                                Toast.LENGTH_LONG,
                                            ).show()
                                            // Get data from Room database
                                        }
                                    }
                                    else -> {}
                                }
                            }
                    }
                }

            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERSMISSION_ID) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            } else {
                Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }


    private fun getLastLocation() {
        if (checkPermission()) {
            if (isLocationEnabled()) {
                requestNewLocationData()
            } else {
                Toast.makeText(requireContext(), "Please turn on location", Toast.LENGTH_LONG)
                    .show()
                val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermission()
        }
    }


    private fun requestNewLocationData() {
        val mLocationRequest = com.google.android.gms.location.LocationRequest()
        mLocationRequest.priority = LocationRequest.QUALITY_HIGH_ACCURACY
        mLocationRequest.setInterval(0)
        fusedClient = LocationServices.getFusedLocationProviderClient(requireContext())
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedClient.requestLocationUpdates(
            mLocationRequest, mLocationCallBack,

            Looper.myLooper()
        )
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ),
            PERSMISSION_ID
        )
    }

    private fun checkPermission(): Boolean {
        val result = ActivityCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        return result
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }


    private suspend fun openDialog() {
        val lang = langSharedlangViewModel.selectedLanguage.value ?: "en"
        fetchForecast(lat, lon, lang)

        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.show()
    }


}
