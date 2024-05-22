package dev.rushia.verhaal_mobile.ui.story.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.rushia.verhaal_mobile.data.repository.StoryRepository

class HomeViewModelFactory(
    private val storyRepository: StoryRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeStoryViewModel(storyRepository) as T
    }
}