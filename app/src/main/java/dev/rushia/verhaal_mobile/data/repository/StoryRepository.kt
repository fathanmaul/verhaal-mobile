package dev.rushia.verhaal_mobile.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import dev.rushia.verhaal_mobile.data.StoryRemoteMediator
import dev.rushia.verhaal_mobile.data.local.database.StoryDatabase
import dev.rushia.verhaal_mobile.data.paging.StoryPagingSource
import dev.rushia.verhaal_mobile.data.remote.response.StoryItem
import dev.rushia.verhaal_mobile.data.remote.retrofit.ApiService
import javax.inject.Inject

class StoryRepository @Inject constructor(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService,
    val token: String
) {


    @OptIn(ExperimentalPagingApi::class)
    fun getStory(): LiveData<PagingData<StoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, token),
            pagingSourceFactory = {
                StoryPagingSource(apiService, token)
            }
        ).liveData
    }


}