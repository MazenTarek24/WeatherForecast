package com.example.weatherforecast.setting.view

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.example.weatherforecast.databinding.FragmentSettingBinding
import java.util.*

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import com.example.weatherforecast.R
import com.example.weatherforecast.home.view.HomeFragment
import com.example.weatherforecast.setting.viewmodel.SharedViewModel
import com.example.weatherforecast.home.view.SharedlangViewModel

class SettingFragment :  Fragment() {

    private lateinit var binding: FragmentSettingBinding

    private var selectedLanguage: String = ""

    private val sharedViewModel : SharedViewModel by activityViewModels()

    private val langSharedViewModel : SharedlangViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSettingBinding.inflate(inflater,container,false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.groupLan.setOnCheckedChangeListener { group, checkId ->
            when (checkId) {
                R.id.english_id -> {
                    langSharedViewModel.updateSelectedLanguage("en")
                    setAppLang("en")
                }
                R.id.arabic_id -> {
                    langSharedViewModel.updateSelectedLanguage("ar")
                    setAppLang("ar")
                }
            }
        }

            binding.groupTemp.setOnCheckedChangeListener { group, checkId ->
                when (checkId) {
                    R.id.cel_id -> {
                        sharedViewModel.setSelectedUnit(TemperatureUnit.Celsius)

                    }
                    R.id.kelv_id -> {
                        sharedViewModel.setSelectedUnit(TemperatureUnit.Kelvin)
                    }
                    R.id.Faherinheit_id -> {
                        sharedViewModel.setSelectedUnit(TemperatureUnit.Fahrenheit)
                    }
                }
            }

        binding.groupWind.setOnCheckedChangeListener { group, checkId ->
            when (checkId) {
                R.id.meter_id -> {
                    sharedViewModel.setSelectWind(WindSpeed.Meter_Sec)
                }
                R.id.mile_id -> {
                    sharedViewModel.setSelectWind(WindSpeed.Mile_Hour)
                }
            }
        }

    }

    enum class TemperatureUnit {
        Celsius,
        Kelvin,
        Fahrenheit
    }

    enum class WindSpeed {
        Meter_Sec,
        Mile_Hour,
    }



    private fun setAppLang(selectedLanguage: String) {
        val locale = Locale(selectedLanguage)
       Locale.setDefault(locale)

     val resource = resources
        val configuration = resource.configuration
        configuration.setLocale(locale)
        resource.updateConfiguration(configuration,resource.displayMetrics)
        recreateFrag()

   }

    private fun recreateFrag() {
       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
       {
            activity?.let {
              it.finish()
              it.startActivity(it.intent)
           }
      }else{
           activity.let {
                it?.recreate()
          }
       }
  }

}
