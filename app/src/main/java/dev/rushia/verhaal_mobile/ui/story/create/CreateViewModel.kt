package dev.rushia.verhaal_mobile.ui.story.create

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.rushia.verhaal_mobile.data.remote.response.UploadResponse
import dev.rushia.verhaal_mobile.data.remote.retrofit.ApiConfig
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.lang.StringBuilder

class CreateViewModel : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: MutableLiveData<Boolean> = _isLoading

    private val _isSuccess = MutableLiveData<Boolean>()
    val isSuccess: MutableLiveData<Boolean> = _isSuccess

    fun upload(description: String, imageFile: File, token: String) {
        _isLoading.value = true
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )
        val client = ApiConfig.getApiService().uploadStory(
            token = StringBuilder("Bearer ").append(token).toString(),
            file = multipartBody,
            description = requestBody
        )
        client.enqueue(object : Callback<UploadResponse> {
            override fun onResponse(
                call: Call<UploadResponse>,
                response: Response<UploadResponse>
            ) {
                if (response.isSuccessful) {
                    _isLoading.value = false
                    _isSuccess.value = true
                } else {
                    _isLoading.value = false
                    _isSuccess.value = false
                }
            }

            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                _isLoading.value = false
                _isSuccess.value = false
            }

        })
    }

    fun uploadWithLocation(
        description: String,
        imageFile: File,
        token: String,
        lon: Float,
        lat: Float
    ) {
        _isLoading.value = true
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )
        val client = ApiConfig.getApiService().uploadStoryWithLocation(
            token = StringBuilder("Bearer ").append(token).toString(),
            file = multipartBody,
            description = requestBody,
            lat = lat,
            long = lon
        )
        client.enqueue(object : Callback<UploadResponse> {
            override fun onResponse(
                call: Call<UploadResponse>,
                response: Response<UploadResponse>
            ) {
                if (response.isSuccessful) {
                    _isLoading.value = false
                    _isSuccess.value = true
                } else {
                    _isLoading.value = false
                    _isSuccess.value = false
                }
            }

            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                _isLoading.value = false
                _isSuccess.value = false
            }

        })
    }
}