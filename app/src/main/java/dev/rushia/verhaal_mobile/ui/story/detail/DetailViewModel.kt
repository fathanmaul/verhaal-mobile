package dev.rushia.verhaal_mobile.ui.story.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.rushia.verhaal_mobile.data.remote.response.StoryResponse
import dev.rushia.verhaal_mobile.data.remote.retrofit.ApiConfig
import dev.rushia.verhaal_mobile.utils.Const
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.StringBuilder

class DetailViewModel : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading get() = _isLoading

    private val _story = MutableLiveData<StoryResponse>()
    val story get() = _story

    val error = MutableLiveData<Boolean>()

    fun getDetailStory(token: String, storyId: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService()
            .getStoryDetail(StringBuilder(Const.BEARER).append(token).toString(), storyId)
        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                if (response.isSuccessful) {
                    _isLoading.value = false
                    _story.value = response.body()
                } else {
                    _isLoading.value = false
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                _isLoading.value = false
                error.value = true
            }

        })
    }
}