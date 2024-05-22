package dev.rushia.verhaal_mobile.ui.maps

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.rushia.verhaal_mobile.data.remote.response.ListStoriesResponse
import dev.rushia.verhaal_mobile.data.remote.response.StoryItem
import dev.rushia.verhaal_mobile.data.remote.retrofit.ApiConfig
import dev.rushia.verhaal_mobile.utils.Const
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MapsViewModel : ViewModel() {

    private val _listStories = MutableLiveData<List<StoryItem>>()
    val listStories get() = _listStories

    private val _error = MutableLiveData<String>()
    val error get() = _error

    fun getStoryWithLocation(token: String) {
        val client = ApiConfig.getApiService().getStoriesWithLocation("${Const.BEARER} $token")
        client.enqueue(object : Callback<ListStoriesResponse> {
            override fun onResponse(
                call: Call<ListStoriesResponse>,
                response: Response<ListStoriesResponse>
            ) {
                if (response.isSuccessful) {
                    _listStories.value = response.body()?.listStory ?: emptyList()
                } else {
                    _error.value = response.message()
                }
            }

            override fun onFailure(call: Call<ListStoriesResponse>, t: Throwable) {
                _error.value = t.message
            }
        })
    }

}