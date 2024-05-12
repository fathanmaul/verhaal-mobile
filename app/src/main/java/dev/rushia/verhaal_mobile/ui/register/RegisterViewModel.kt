package dev.rushia.verhaal_mobile.ui.register

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import dev.rushia.verhaal_mobile.data.remote.response.RegisterResponse
import dev.rushia.verhaal_mobile.data.remote.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RegisterViewModel : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: MutableLiveData<Boolean> = _isLoading

    private val registerResponse = MutableLiveData<RegisterResponse?>()

    var error = MutableLiveData<String>()

    fun register(
        name: String,
        email: String,
        password: String
    ) {
        isLoading.value = true
        val client = ApiConfig.getApiService().register(name, email, password)
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                if (response.isSuccessful) {
                    isLoading.value = false
                    registerResponse.value = response.body()
                    error.value = ""
                } else {
                    val errorMessage = Gson().fromJson(
                        response.errorBody()?.string(),
                        RegisterResponse::class.java
                    )

                    error.value = errorMessage.message
                    isLoading.value = false
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                isLoading.value = false
                registerResponse.value = null
            }

        })
    }
}