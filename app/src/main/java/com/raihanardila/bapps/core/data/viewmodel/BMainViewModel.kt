package com.raihanardila.bapps.core.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.raihanardila.bapps.core.data.local.repository.BMainRepository
import com.raihanardila.bapps.core.data.model.StoriesBModel
import kotlinx.coroutines.flow.Flow

class BMainViewModel(
    private val repository: BMainRepository,
) : ViewModel() {

    val storiesFlow: Flow<PagingData<StoriesBModel>> by lazy {
        repository.getStories(1, 20).cachedIn(viewModelScope)
    }

    fun getStoriesMap(token: String): LiveData<List<StoriesBModel>> =
        repository.getStoriesMap(token)
}
