package dev.rushia.verhaal_mobile.ui.story.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.rushia.verhaal_mobile.data.remote.response.StoryItem
import dev.rushia.verhaal_mobile.data.repository.StoryRepository
import javax.inject.Inject

@HiltViewModel
class HomeStoryViewModel @Inject constructor(
    private val storyRepository: StoryRepository
) : ViewModel() {

    private val _story = MutableLiveData<PagingData<StoryItem>>()
    val story: LiveData<PagingData<StoryItem>> = _story

    fun getStories() {
        storyRepository.getStory().cachedIn(viewModelScope).observeForever {
            _story.value = it
        }
    }
}