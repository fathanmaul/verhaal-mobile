package dev.rushia.verhaal_mobile.ui.login

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dev.rushia.verhaal_mobile.data.local.AuthPreferences
import dev.rushia.verhaal_mobile.data.local.dataStore
import dev.rushia.verhaal_mobile.data.remote.response.LoginResponse
import dev.rushia.verhaal_mobile.data.remote.response.LoginResult
import dev.rushia.verhaal_mobile.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(
    private val context: Context
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: MutableLiveData<Boolean> = _isLoading

    private val loginResponse = MutableLiveData<LoginResponse?>()

    var error = MutableLiveData<Boolean>()
    var loginResult = MutableLiveData<LoginResult>()
    fun login(email: String, password: String) {
        isLoading.value = true
        val client = ApiConfig.getApiService().login(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    isLoading.value = false
                    loginResponse.value = response.body()
                    error.value = false
                    loginResult.value.let {
                        loginResult.value = response.body()?.loginResult
                    }
                    saveAuthToken(loginResult.value!!)
                } else {
                    error.value = true
                    val errorMessage = Gson().fromJson(
                        response.errorBody()?.string(),
                        LoginResponse::class.java
                    )
                    isLoading.value = false
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                isLoading.value = false
                loginResponse.value = null
            }
        })
    }

    fun saveAuthToken(user: LoginResult) {
        viewModelScope.launch {
            AuthPreferences.getInstance(
                context.dataStore
            ).saveAuthToken(user)
        }
    }

}