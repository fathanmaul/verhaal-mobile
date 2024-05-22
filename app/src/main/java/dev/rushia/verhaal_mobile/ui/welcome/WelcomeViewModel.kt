package dev.rushia.verhaal_mobile.ui.welcome

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dev.rushia.verhaal_mobile.data.local.AuthPreferences

class WelcomeViewModel(
    private val pref: AuthPreferences
) : ViewModel() {

    fun getAuthToken(): LiveData<String?> {
        return pref.getAuthToken().asLiveData()
    }
}