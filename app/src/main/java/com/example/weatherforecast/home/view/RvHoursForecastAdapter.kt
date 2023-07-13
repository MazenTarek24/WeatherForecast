package com.example.weatherforecast.home.view

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.setting.view.SettingFragment
import com.example.weatherforecast.setting.viewmodel.SharedViewModel
import com.example.weatherforecast.databinding.ItemHourlyBinding
import com.example.weatherforecast.model.onecall.Hourly
import com.squareup.picasso.Picasso
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class RvHoursForecastAdapter(val hoursForecast: ArrayList<Hourly> ,val sharedViewModel : SharedViewModel
) : RecyclerView.Adapter<RvHoursForecastAdapter.ViewHolder>() {


    class ViewHolder( val binding: ItemHourlyBinding) : RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemHourlyBinding.inflate
            (LayoutInflater.from(parent.context) , parent , false))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentHoursForecast = hoursForecast[position]
        holder.binding.apply {
            val imageIcon = currentHoursForecast.weather[0].icon
            val imgUrl = "https://openweathermap.org/img/w/${imageIcon}.png"
            Picasso.get().load(imgUrl).into(dayIconTemp)

            val tempSpeedUnit = sharedViewModel.selectedUnit.value ?: SettingFragment.TemperatureUnit.Celsius
            val temp = getFormattedTemp(currentHoursForecast.temp, tempSpeedUnit)
            hourTemp.text =  temp

            hourDate.text = displayTime(currentHoursForecast.dt)
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun displayTime(dtTxt: Int): CharSequence? {
        val output = DateTimeFormatter.ofPattern("hh a")

        val instant = Instant.ofEpochSecond(dtTxt.toLong())
        val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        return output.format(dateTime)
    }

    override fun getItemCount(): Int {
      return hoursForecast.size
    }

    private fun getFormattedTemp(temp: Double, unit: SettingFragment.TemperatureUnit): String {
        return when(unit){
            SettingFragment.TemperatureUnit.Celsius -> "${(temp/10).toInt()} °C "
            SettingFragment.TemperatureUnit.Kelvin -> "$temp K "
            SettingFragment.TemperatureUnit.Fahrenheit -> {val fahrenheit = (temp * 9 / 5) - 459.67
                "${fahrenheit.toInt()} °F"}
            else -> ({
            }).toString()
        }
    }

}