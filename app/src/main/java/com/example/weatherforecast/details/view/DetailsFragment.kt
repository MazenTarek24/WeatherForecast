package com.example.weatherforecast.details.view

import android.content.ContentValues.TAG
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.R
import com.example.weatherforecast.setting.view.SettingFragment
import com.example.weatherforecast.setting.viewmodel.LocationViewModel
import com.example.weatherforecast.home.view.RvAdapterForecast
import com.example.weatherforecast.home.view.RvHoursForecastAdapter
import com.example.weatherforecast.home.view.SharedlangViewModel
import com.example.weatherforecast.home.viewmodel.*
import com.example.weatherforecast.databinding.BottomSheetBinding
import com.example.weatherforecast.databinding.FragmentDetailsBinding
import com.example.weatherforecast.details.viewmodel.DetailsViewModelFactory
import com.example.weatherforecast.repo.Repo
import com.example.weatherforecast.localsource.LocalSource
import com.example.weatherforecast.localsource.WeatherDataBase
import com.example.weatherforecast.model.onecall.AllWeather
import com.example.weatherforecast.model.onecall.Daily
import com.example.weatherforecast.model.onecall.Hourly
import com.example.weatherforecast.network.AllWeatherRetrofitHelper
import com.example.weatherforecast.network.ApiState
import com.example.weatherforecast.network.RemoteSource
import com.example.weatherforecast.setting.viewmodel.SharedViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


class DetailsFragment : Fragment() {


    var lat: Double?= null
    var lon: Double? = null
    var nameLocation : String? = null

    lateinit var binding: FragmentDetailsBinding
    var apiKey: String = "34a112137b3f93965d53498818fa64fa"

    lateinit var sheetLayoutBinding: BottomSheetBinding
    lateinit var dialog: BottomSheetDialog

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    lateinit var viewModel: DetailsViewModel

    private lateinit var forecastAdapter: RvAdapterForecast
    private lateinit var rvHoursForecastAdapter: RvHoursForecastAdapter


    var forecastList: ArrayList<Daily> = ArrayList()
    var forecasHourstList: ArrayList<Hourly> = ArrayList()


    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val locatioViewModel: LocationViewModel by activityViewModels()
    private val langSharedlangViewModel: SharedlangViewModel by activityViewModels()

    private lateinit var localDataSource: LocalSource

    lateinit var repo: Repo



    lateinit var geoCoder : Geocoder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        sheetLayoutBinding = BottomSheetBinding.inflate(layoutInflater)
        dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetTheme)
        dialog.setContentView(sheetLayoutBinding.root)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        localDataSource = LocalSource(WeatherDataBase.getInstance(requireContext()).getDao())
        repo = Repo(RemoteSource(AllWeatherRetrofitHelper.retrofitInstance), localDataSource)

        viewModel = ViewModelProvider(requireActivity(),
            DetailsViewModelFactory(repo)).get(DetailsViewModel::class.java)


        val place = DetailsFragmentArgs.fromBundle(requireArguments()).cordinates


        viewModel.fetchAllWeather(place.latitude , place.longitude , "en")

        lifecycleScope.launch {
            viewModel.apiState.collectLatest {
                when(it)
                {
                    is ApiState.Success ->{
                        binding.progressCircular.visibility = View.GONE

                        Log.d(TAG, "onViewCreated: ${it.data}")
                        withContext(Dispatchers.Main)
                        {
                            geoCoder = Geocoder(requireActivity(), Locale.getDefault())
                            val addresses: List<Address>? =
                                geoCoder?.getFromLocation(place.latitude!!, place.longitude!!, 1)
                            if (addresses != null && addresses.isNotEmpty()) {
                                val address: Address = addresses[0]
                                val cityName: String = address.locality ?: ""
                                val stateName: String = address.adminArea ?: ""
                                val countryName: String = address.countryName ?: ""
                                val completeAddress = "$cityName, $countryName"

                                // Set the address in the locationName TextView
                                binding.locationName.text = completeAddress
                            }

                            updateCurrentWeatherUI(it.data)
                            updateForecastUI(it.data)
                            updateHoursForecast(it.data)
                        }
                    }
                    is ApiState.Loading ->
                    {
                        withContext(Dispatchers.Main)
                        {
                            binding.progressCircular.visibility = View.VISIBLE
                        }
                    }
                else -> {
                    withContext(Dispatchers.Main)
                    {
                        binding.progressCircular.visibility = View.GONE
                    }
                }
                }
            }
        }

//        GlobalScope.launch(Dispatchers.Main) {
//            sharedViewModel.selectedUnit.collect { unit ->
//                viewModel.currentWeather.value?.let { weather ->
//                    val temp = getFormattedTemp(weather.current.temp, unit)
//                    binding.temp.text = temp
//                }
//            }
//        }


        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())



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



    }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateCurrentWeatherUI(weather: AllWeather?) {
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

    private fun updateHoursForecast(allWeather: AllWeather?) {
        val hoursList: List<Hourly> = allWeather!!.hourly
        rvHoursForecastAdapter =
            RvHoursForecastAdapter(hoursList as ArrayList<Hourly>, sharedViewModel)
        binding.recyclerHour.adapter = rvHoursForecastAdapter
    }

    private fun updateForecastUI(allWeather: AllWeather?) {

        sheetLayoutBinding.tvSheet.text = "Five days Forecast ${allWeather!!.timezone}"

        val forecastList: List<Daily> = allWeather.daily
        val adapterForecast =
            RvAdapterForecast(forecastList as ArrayList<Daily>, sharedViewModel)
        sheetLayoutBinding.rvForecast.adapter = adapterForecast
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
        langSharedlangViewModel.selectedLanguage.value
        viewModel.fetchAllWeather(lat, lon, langSharedlangViewModel.selectedLanguage.value)
    }



    private suspend fun openDialog() {
        val lang = langSharedlangViewModel.selectedLanguage.value
        fetchForecast(lat, lon, lang)

        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.show()
    }
}
