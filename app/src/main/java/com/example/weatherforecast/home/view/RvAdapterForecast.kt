package com.example.weatherforecast.home.view

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.setting.view.SettingFragment
import com.example.weatherforecast.setting.viewmodel.SharedViewModel
import com.example.weatherforecast.databinding.ItemForecastBinding
import com.example.weatherforecast.model.onecall.Daily
import com.squareup.picasso.Picasso
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class RvAdapterForecast(var forecastList : ArrayList<Daily> , var sharedViewModel : SharedViewModel
) : RecyclerView.Adapter<RvAdapterForecast.ViewHolder>() {

    class ViewHolder(val binding: ItemForecastBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemForecastBinding.inflate(LayoutInflater
            .from(parent.context) ,parent ,false))
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentForecast = forecastList[position]

        holder.binding.apply {
            val imageIcon = currentForecast.weather[0].icon
            val imgUrl = "https://openweathermap.org/img/w/${imageIcon}.png"

            Picasso.get().load(imgUrl).into(imgItem)

            val tempSpeedUnit = sharedViewModel.selectedUnit.value ?: SettingFragment.TemperatureUnit.Celsius
            val temp = getFormattedTemp(currentForecast.temp.day, tempSpeedUnit)

            tvItemTemp.text =  temp

           // tvItemTemp.text = "{${currentForecast.temp.day.toInt()}} °C"

            tvItemStatus.text = "{${currentForecast.weather[0].description.toString()}}"
            tvItemTime.text = displayTime(currentForecast.dt.toString())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun displayTime(dtTxt: String): CharSequence? {
        val input = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val output = DateTimeFormatter.ofPattern("MM-dd HH:mm")

        val timestamp = dtTxt.toLong()
        val instant = Instant.ofEpochSecond(timestamp)
        val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        return output.format(dateTime)
    }

    override fun getItemCount(): Int {
      return  forecastList.size
    }

    private fun getFormattedTemp(temp: Double, unit: SettingFragment.TemperatureUnit): String {
        return when(unit){
            SettingFragment.TemperatureUnit.Celsius -> "${(temp/10).toInt()} °C"
            SettingFragment.TemperatureUnit.Kelvin -> "${temp.toInt()} K "
            SettingFragment.TemperatureUnit.Fahrenheit -> {val fahrenheit = (temp * 9 / 5) - 459.67
                "${fahrenheit.toInt()} °F"}
            else -> ({
            }).toString()
        }
    }



}