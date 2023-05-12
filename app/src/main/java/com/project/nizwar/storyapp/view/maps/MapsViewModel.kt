package com.project.nizwar.storyapp.view.maps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.project.nizwar.storyapp.data.Repository
import kotlinx.coroutines.Dispatchers

class MapsViewModel(private val repository: Repository) : ViewModel() {
    fun getToken() = repository.getToken().asLiveData(Dispatchers.IO)

    fun getAllStoriesWithLocation(token: String) = repository.getAllStoriesWithLocation(token)
}