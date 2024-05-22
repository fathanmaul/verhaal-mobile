package dev.rushia.verhaal_mobile.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dev.rushia.verhaal_mobile.data.local.AuthPreferences

class AuthViewModel(
    private val authPreferences: AuthPreferences
) : ViewModel() {
    val authToken: LiveData<String?> = authPreferences.getAuthToken().asLiveData()
    val userName: LiveData<String?> = authPreferences.getUserName().asLiveData()
}