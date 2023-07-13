package com.example.weatherforecast.home.view

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class SharedlangViewModel : ViewModel() {
    val selectedLanguage: MutableStateFlow<String> = MutableStateFlow("en")

    fun updateSelectedLanguage(language: String) {
        selectedLanguage.value = language
    }

}