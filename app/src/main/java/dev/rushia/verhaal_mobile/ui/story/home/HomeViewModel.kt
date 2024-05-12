package dev.rushia.verhaal_mobile.ui.story.home

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.rushia.verhaal_mobile.data.remote.response.ListStoriesResponse
import dev.rushia.verhaal_mobile.data.remote.response.StoryItem
import dev.rushia.verhaal_mobile.data.remote.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel : ViewModel() {
    private val _listStories = MutableLiveData<List<StoryItem>>()
    val listStories get() = _listStories
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading get() = _isLoading


    fun setListStories(list: List<StoryItem>) {
        _listStories.value = list
    }

    fun getListStories(token: String?) {
        isLoading.value = true
        if (token != null) {
            val client = ApiConfig.getApiService().getStoriesList(
                "Bearer $token"
            )
            client.enqueue(object : Callback<ListStoriesResponse> {
                override fun onResponse(
                    call: Call<ListStoriesResponse>,
                    response: Response<ListStoriesResponse>
                ) {
                    if (response.isSuccessful) {
                        isLoading.value = false
                        setListStories(response.body()?.listStory ?: emptyList())
                    } else {
                        isLoading.value = false
                    }

                    Log.d("TOKEN IS ->", "Bearer $token")
                }

                override fun onFailure(call: Call<ListStoriesResponse>, t: Throwable) {
                    isLoading.value = false
                    Log.e("HomeViewModel", "onFailure: ${t.message}")
                }
            })
        } else {
            isLoading.value = false
        }
    }
}